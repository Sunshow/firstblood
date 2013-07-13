package web.action.reward;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.service.lottery.RewardService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.PlanType;
import com.lehecai.core.lottery.ResultStatus;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.core.lottery.task.impl.LotteryRewardQueueTask;
import com.lehecai.core.queue.simple.SimpleQueueService;
import com.lehecai.core.service.memcached.MemcachedService;
import com.lehecai.core.util.lottery.FetcherLotteryDrawConverter;
/**
 * 彩票派奖
 * @author qatang
 *
 */
public class RewardAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private PhaseService phaseService;
	private SimpleQueueService simpleQueueService;
	private LotteryPlanService lotteryPlanService;
	private MemcachedService memcachedService;
	private RewardService rewardService;
	
	private String rewardQueuePrefix;//派奖队列前缀
	
	private int rewardPlanPageSize = 50;
	
	private Integer lotteryTypeValue;
	private String taskId;
	private Integer prizeScopeReward;
	private Double prizeScope = 10000D;
	private String phaseNo;
	private String phaseNoText;
	private Integer planTypeId;
	private String planId;
	private Date rbeginDate;
	private Date rendDate;
	
	private List<Plan> plans;
	private Phase phase;
	
	public String handle(){
		if (rbeginDate == null) {
			rbeginDate = getDefaultQueryBeginDate();
		}
		return "reward";
	}
	
	@SuppressWarnings("unchecked")
	public String search() {
		logger.info("进入派奖方案查询");
		if (lotteryTypeValue == null) {
			logger.error("lotteryTypeValue参数不能为空");
			super.setErrorMessage("lotteryTypeValue参数不能为空");
			return "failure";
		}
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		if (lotteryType == null) {
			logger.error("lotteryTypeValue={}彩种不存在", lotteryTypeValue);
			super.setErrorMessage("lotteryTypeValue=" + lotteryTypeValue + "彩种不存在");
			return "failure";
		}
		if (!StringUtils.isEmpty(phaseNoText)) {
			phaseNo = phaseNoText;
		}
		
		if (rbeginDate != null && rendDate != null) {
			if (!DateUtil.isSameMonth(rbeginDate, rendDate)) {
				logger.error("开始时间和结束时间不在同一年同一月");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询");
				return "failure";
			}
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		
		PhaseType phaseType = PhaseType.getItem(lotteryType);
		PlanType planType = planTypeId != null ? PlanType.getItem(planTypeId) : null;
		
		Map<String, Object> map = null;
		try {
			super.getPageBean().setPageSize(rewardPlanPageSize);
			map = lotteryPlanService.find4RewardPlan(lotteryType, phaseNo, planId, planType, ResultStatus.WON, rbeginDate, rendDate, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map == null) {
			logger.error("list查询结果为空");
			super.setErrorMessage("list查询结果为空");
			return "failure";
		}
		plans = (List<Plan>)map.get(Global.API_MAP_KEY_LIST);
		
		try {
			phase = phaseService.getPhaseByPhaseTypeAndPhaseNo(phaseType, phaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage());
			setErrorMessage(e.getMessage());
			return "failure";
		}
		if (phase == null) {
			logger.error("phase查询结果为空:phaseType={}, phaseNo={}", phaseType.getValue(), phaseNo);
			super.setErrorMessage("phase查询结果为空:phaseType=" + phaseType.getName() + ", phaseNo=" + phaseNo + "");
			return "failure";
		}
		phase.setResult(FetcherLotteryDrawConverter.convertResultJsonString2ShowString(phase.getResult()));
		PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
		//ResultBean resultBean = (ResultBean)map.get(Global.API_MAP_KEY_RESULTBEAN);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		logger.info("查询派奖方案结束");
		return "reward";
	}
	
	//派奖
	public String reward() {
		logger.info("进入派奖");
		HttpServletResponse response = ServletActionContext.getResponse();
		
		if (lotteryTypeValue == null) {
			logger.error("派奖接收参数lotteryTypeValue为空");
			JSONObject obj = new JSONObject();
			obj.put("rs", false);
			obj.put("msg", "派奖接收参数lotteryTypeValue为空");
			super.writeRs(response, obj);
			logger.info("派奖结束");
			return null;
		}
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		if (lotteryType == null) {
			logger.error("派奖接收参数lotteryTypeValue={}对应的彩种为空", lotteryTypeValue);
			JSONObject obj = new JSONObject();
			obj.put("rs", false);
			obj.put("msg", "派奖接收参数lotteryTypeValue=" + lotteryTypeValue + "对应的彩种为空");
			super.writeRs(response, obj);
			logger.info("派奖结束");
			return null;
		}
		if (prizeScopeReward == null || YesNoStatus.getItem(prizeScopeReward) == null) {
			logger.error("派奖接收参数是否为指定奖金范围自动派奖方式prizeScopeReward为空", lotteryTypeValue);
			JSONObject obj = new JSONObject();
			obj.put("rs", false);
			obj.put("msg", "派奖接收参数是否为指定奖金范围自动派奖方式prizeScopeReward为空");
			super.writeRs(response, obj);
			logger.info("派奖结束");
			return null;
		}
		if (StringUtils.isEmpty(phaseNo)) {
			logger.error("派奖接收参数彩期phaseNo为空");
			JSONObject obj = new JSONObject();
			obj.put("rs", false);
			obj.put("msg", "派奖接收参数彩期phaseNo为空");
			super.writeRs(response, obj);
			logger.info("派奖结束");
			return null;
		}
		if (prizeScopeReward == YesNoStatus.YES.getValue()) {
			if (prizeScope <= 0.00D) {
				logger.error("派奖任务为指定奖金范围自动派奖方式，奖金范围不能小于0");
				JSONObject obj = new JSONObject();
				obj.put("rs", false);
				obj.put("msg", "派奖任务为指定奖金范围自动派奖方式，奖金范围不能小于0");
				super.writeRs(response, obj);
				logger.info("派奖结束");
				return null;
			}
		} else {
			if (plans == null || plans.size() == 0) {
				logger.error("派奖接收方案列表为空");
				JSONObject obj = new JSONObject();
				obj.put("rs", false);
				obj.put("msg", "派奖接收方案列表为空");
				super.writeRs(response, obj);
				logger.info("派奖结束");
				return null;
			}
		}
		
		//发送派奖任务
		LotteryRewardQueueTask lotteryRewardQueueTask = new LotteryRewardQueueTask();
		lotteryRewardQueueTask.setLotteryType(lotteryType);
		lotteryRewardQueueTask.setPhaseNo(phaseNo);
		lotteryRewardQueueTask.setPrizeScopeReward(YesNoStatus.getItem(prizeScopeReward));
		lotteryRewardQueueTask.setPrizeScope(prizeScope);
		
		List<String> planNoList = new ArrayList<String>();
		if (plans != null) {
			for (Plan plan : plans) {
				planNoList.add(plan.getId());
			}
		}
		lotteryRewardQueueTask.setPlanNoList(planNoList);
		String initTaskId =  "lottery_reward_" + new Date().getTime();
		lotteryRewardQueueTask.setTaskId(initTaskId);
		
		//队列名称
		boolean resultFlag = false;
		try {
			resultFlag = simpleQueueService.putString(rewardQueuePrefix + String.valueOf(lotteryTypeValue) + "_new", LotteryRewardQueueTask.toJSON(lotteryRewardQueueTask).toString());
		} catch (Exception e) {
			logger.error("放入派奖任务到队列失败, {}, {}", new Object[] {
					lotteryType.getName(), 	phaseNo
			});
			logger.error(e.getMessage(), e);
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("lotteryTypeValue", lotteryTypeValue);
		jsonObject.put("taskId", initTaskId);
		
		if (resultFlag) {
			JSONObject obj = new JSONObject();
			obj.put("rs", true);
			obj.put("data", jsonObject);
			obj.put("msg", "[" + lotteryType.getName() + "]第<" + phaseNo + ">期的派奖结果:发送派奖任务到派奖队列成功");
			super.writeRs(response, obj);
			logger.info("派奖结束");
			return null;
		}else{
			JSONObject obj = new JSONObject();
			obj.put("rs", false);
			obj.put("msg", "[" + lotteryType.getName() + "]第<" + phaseNo + ">期的派奖结果:发送派奖任务到派奖队列失败");
			super.writeRs(response, obj);
			logger.info("派奖结束");
			return null;
		}
	}
	
	//派奖状态
	public String rewardStatus() {
		logger.info("进入查询正在执行的派奖状态");
		HttpServletResponse response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			logger.error("查询派奖状态接收参数lotteryTypeValue为空");
			JSONObject obj = new JSONObject();
			obj.put("rs", false);
			obj.put("msg", "查询派奖状态接收参数lotteryTypeValue为空");
			super.writeRs(response, obj);
			logger.info("查询派奖状态结束");
			return null;
		}
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		if (lotteryType == null) {
			logger.error("查询派奖状态接收参数lotteryTypeValue={}对应的彩种为空", lotteryTypeValue);
			JSONObject obj = new JSONObject();
			obj.put("rs", false);
			obj.put("msg", "查询派奖状态接收参数lotteryTypeValue=" + lotteryTypeValue + "对应的彩种为空");
			super.writeRs(response, obj);
			logger.info("查询派奖状态结束");
			return null;
		}
		
		if (StringUtils.isEmpty(taskId)) {
			logger.error("查询派奖状态接收参数彩期taskId为空");
			JSONObject obj = new JSONObject();
			obj.put("rs", false);
			obj.put("msg", "查询派奖状态接收参数彩期taskId为空");
			super.writeRs(response, obj);
			logger.info("查询派奖状态结束");
			return null;
		}
		
		JSONObject jsonObject = rewardService.getRewardStatus(lotteryType, taskId);
		if (jsonObject == null) {
			JSONObject obj = new JSONObject();
			obj.put("rs", false);
			obj.put("msg", "查询派奖状态接收参数lotteryTypeValue=" + lotteryTypeValue + "返回结果为空");
			super.writeRs(response, obj);
			logger.info("查询派奖状态结束");
			return null;
		}
		JSONObject obj = new JSONObject();
		obj.put("rs", true);
		obj.put("data", jsonObject);
		obj.put("msg", "查询派奖状态成功");
		super.writeRs(response, obj);
		logger.info("查询正在执行的派奖状态结束");
		return null;
	}
	
	public List<LotteryType> getLotteryTypeList() {
		return OnSaleLotteryList.get();
	}
	
	public List<PlanType> getPlanTypes() {
		return PlanType.getItems();
	}
	
	public YesNoStatus getYesStatus() {
		return YesNoStatus.YES;
	}
	
	public YesNoStatus getNoStatus() {
		return YesNoStatus.NO;
	}

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}

	public SimpleQueueService getSimpleQueueService() {
		return simpleQueueService;
	}

	public void setSimpleQueueService(SimpleQueueService simpleQueueService) {
		this.simpleQueueService = simpleQueueService;
	}

	public LotteryPlanService getLotteryPlanService() {
		return lotteryPlanService;
	}

	public void setLotteryPlanService(LotteryPlanService lotteryPlanService) {
		this.lotteryPlanService = lotteryPlanService;
	}

	public String getRewardQueuePrefix() {
		return rewardQueuePrefix;
	}

	public void setRewardQueuePrefix(String rewardQueuePrefix) {
		this.rewardQueuePrefix = rewardQueuePrefix;
	}

	public int getRewardPlanPageSize() {
		return rewardPlanPageSize;
	}

	public void setRewardPlanPageSize(int rewardPlanPageSize) {
		this.rewardPlanPageSize = rewardPlanPageSize;
	}

	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}

	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}

	public String getPhaseNo() {
		return phaseNo;
	}

	public void setPhaseNo(String phaseNo) {
		this.phaseNo = phaseNo;
	}

	public String getPhaseNoText() {
		return phaseNoText;
	}

	public void setPhaseNoText(String phaseNoText) {
		this.phaseNoText = phaseNoText;
	}

	public Integer getPlanTypeId() {
		return planTypeId;
	}

	public void setPlanTypeId(Integer planTypeId) {
		this.planTypeId = planTypeId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public Date getRbeginDate() {
		return rbeginDate;
	}

	public void setRbeginDate(Date rbeginDate) {
		this.rbeginDate = rbeginDate;
	}

	public Date getRendDate() {
		return rendDate;
	}

	public void setRendDate(Date rendDate) {
		this.rendDate = rendDate;
	}

	public List<Plan> getPlans() {
		return plans;
	}

	public void setPlans(List<Plan> plans) {
		this.plans = plans;
	}

	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public Integer getPrizeScopeReward() {
		return prizeScopeReward;
	}

	public void setPrizeScopeReward(Integer prizeScopeReward) {
		this.prizeScopeReward = prizeScopeReward;
	}

	public Double getPrizeScope() {
		return prizeScope;
	}

	public void setPrizeScope(Double prizeScope) {
		this.prizeScope = prizeScope;
	}

	public MemcachedService getMemcachedService() {
		return memcachedService;
	}

	public void setMemcachedService(MemcachedService memcachedService) {
		this.memcachedService = memcachedService;
	}

	public RewardService getRewardService() {
		return rewardService;
	}

	public void setRewardService(RewardService rewardService) {
		this.rewardService = rewardService;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
}
