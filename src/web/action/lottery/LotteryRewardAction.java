package web.action.lottery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.config.GlobalConfig;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.PlanType;
import com.lehecai.core.lottery.ResultStatus;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.core.lottery.lock.LotteryRewardMemCacheLock;
import com.lehecai.core.lottery.status.LotteryRewardPlanStatus;
import com.lehecai.core.lottery.task.LotteryRewardQueueTask;
import com.lehecai.core.queue.simple.SimpleQueueService;
import com.lehecai.core.service.memcached.MemcachedService;
import com.lehecai.core.util.lottery.FetcherLotteryDrawConverter;
import com.lehecai.core.util.lottery.LotteryUtil;
/**
 * 彩票派奖
 * @author leiming
 *
 */
public class LotteryRewardAction extends BaseAction {
	private static final long serialVersionUID = -367266851465374167L;
	private static final Logger logger = LoggerFactory.getLogger(LotteryRewardAction.class);
	
	@SuppressWarnings("unused")
	private HttpServletRequest request;
	private HttpServletResponse response;
	private PhaseService phaseService;
	private SimpleQueueService simpleQueueService;
	private MemcachedService memcachedService;
	private LotteryPlanService lotteryPlanService;
	
	private int rewardPlanPageSize = 500;
	
	private String rewardQueuePrefix;//派奖队列前缀
	
	private Integer lotteryTypeValue;
	private String phaseNo;
	private String phaseNoText;
	private List<Plan> plans;
	private Phase phase;
	private String rewardPlanData;
	@SuppressWarnings("unused")
	private Integer wonResultStatusValue ;//方案已中奖状态
	
	private String planId;
	private Integer planTypeId;


    @Override
    protected Date getDefaultQueryBeginDate() {
        return this.getDefaultQueryBeginDate(-7);
    }

    /**
	 * 结果状态
	 */
	private static List<ResultStatus> resultStatusList = new ArrayList<ResultStatus>();
	private Integer resultStatusId;
	
	private Date rbeginDate;
	private Date rendDate;
	
	static {
		resultStatusList.add(ResultStatus.WON);
		resultStatusList.add(ResultStatus.REWARDED);
	}
	
	@SuppressWarnings("unused")
	private List<LotteryType> lotteryTypeList;
	
	public String handle(){
		if (rbeginDate == null) {
			rbeginDate = getDefaultQueryBeginDate();
		}
		return "reward";
	}
	
	@SuppressWarnings("unchecked")
	public String search() {
		logger.info("进入派奖方案查询");
		if (phaseNoText!=null&&!"".equals(phaseNoText)) {
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
		//分页查询列表
		if (lotteryTypeValue == null) {
			logger.error("彩种类型数值不存在,null");
			super.setErrorMessage("彩种类型数值不存在,null");
			return "failure";
		}
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType phaseType = PhaseType.getItem(lotteryType);
		Map<String, Object> map;
		try {
			PlanType planType = planTypeId != null ? PlanType.getItem(planTypeId) : null;
			
			ResultStatus resultStatus = ResultStatus.getItem(resultStatusId);
			if (resultStatus == null) {
				resultStatus = ResultStatus.WON;
			}
			
			super.getPageBean().setPageSize(rewardPlanPageSize);
			map = lotteryPlanService.find4RewardPlan(lotteryType, phaseNo, planId, planType, resultStatus, rbeginDate, rendDate, super.getPageBean());
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
		if (phaseNoText!=null&&!"".equals(phaseNoText)) {
			phaseNo = phaseNoText;
		}
		logger.info("rewardPlanData:"+rewardPlanData);
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		String rewardLockKey = null;
		LotteryRewardMemCacheLock lotteryRewardMemCacheLock = null;
		if (lotteryTypeValue == null) {
			logger.error("彩种类型数值不存在,null");
			rs.put("state","failed");
			rs.put("msg", "彩种类型数值不存在,null");
			writeRs(response,rs);
			return null;
		}
		//校验派奖彩期的数据
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType phaseType = PhaseType.getItem(lotteryType.getValue());
		Phase phase = null;
		try {
			phase = phaseService.getPhaseByPhaseTypeAndPhaseNo(phaseType, phaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取彩期，api调用异常，{}", e.getMessage());
			rs.put("state","failed");
			rs.put("msg", e.getMessage());
			writeRs(response,rs);
			return null;
		}
		String msg = "";
		if(lotteryType==null){
			rs.put("state","failed");
			msg = "要派奖的彩期类型值:"+lotteryTypeValue+"在系统中不存在,派奖失败";
			rs.put("msg", msg);
			logger.error(msg);
			writeRs(response,rs);
			return null;
		}
		if(phaseNo==null){
			rs.put("state","failed");
			msg = "要派奖的彩票["+phaseType.getName()+"]的彩期号为null,派奖失败";
			rs.put("msg", msg);
			logger.error(msg);
			writeRs(response,rs);
			return null;
		}
		if(phase==null){
			rs.put("state","failed");
			msg = "要派奖的彩票["+phaseType.getName()+"]的派奖彩期:"+phaseNo+"在系统中不存在,派奖失败";
			rs.put("msg", msg);
			logger.error(msg);
			writeRs(response,rs);
			return null;
		}
		rewardLockKey = LotteryUtil.generateLotteryRewardLockKey(lotteryType, phaseNo);
		try {
			lotteryRewardMemCacheLock = (LotteryRewardMemCacheLock)memcachedService.get(rewardLockKey, LotteryUtil.REWARD_LOCK_TIMEOUT);
		} catch (Exception e) {
			logger.error("从缓存中读取派奖锁出错", e);
			rs.put("state","failed");
			rs.put("msg", "从缓存中读取派奖锁出错");
			writeRs(response,rs);
			return null;
		}
		String msgHeader = "["+lotteryType.getName()+"]第<"+phaseNo+">期的派奖结果:";
		if(lotteryRewardMemCacheLock!=null){
			rs.put("state","failed");
			msg = msgHeader+"已经有正在派奖的派奖任务,本次操作失败,需等本期派奖任务执行结束才可重新执行";
			rs.put("msg", msg);
			logger.error(msg);
			writeRs(response,rs);
			return null;
		}
		//发送派奖任务
		LotteryRewardQueueTask lotteryRewardQueueTask = new LotteryRewardQueueTask();
		lotteryRewardQueueTask.setLotteryTypeValue(String.valueOf(lotteryTypeValue));
		lotteryRewardQueueTask.setPhase(phaseNo);
		
		String[] plans = null;
		String[] posttaxPrizes = null;
		String[] rebateAmounts = null;
		String[] rewardPlanDataArray = null;
		String[] tmpArray = null;
		if(rewardPlanData!=null){
			rewardPlanDataArray = rewardPlanData.split(",");
		}
		if(rewardPlanDataArray!=null){
			plans = new String[rewardPlanDataArray.length];
			posttaxPrizes = new String[rewardPlanDataArray.length];
			rebateAmounts = new String[rewardPlanDataArray.length];
			
			for(int i=0;i<rewardPlanDataArray.length;i++){
				if(rewardPlanDataArray[i].indexOf("_")!=-1){
					tmpArray = rewardPlanDataArray[i].split("_");
					plans[i] = tmpArray[0];
					if(tmpArray.length>=2){
						posttaxPrizes[i] = tmpArray[1];
					}else{
						posttaxPrizes[i] = "";
					}
					if(tmpArray.length>=3){
						rebateAmounts[i] = tmpArray[2];
					}else{
						rebateAmounts[i] = "";
					}
				}else{
					plans[i] = "";
					posttaxPrizes[i] = "";
					rebateAmounts[i] = "";
				}
			}
		}
		lotteryRewardQueueTask.setPlans(plans);
		lotteryRewardQueueTask.setPosttaxPrizes(posttaxPrizes);
		lotteryRewardQueueTask.setRebateAmounts(rebateAmounts);
		
		//队列名称
		boolean resultFlag = false;
		try {
			resultFlag = simpleQueueService.putString(rewardQueuePrefix+String.valueOf(lotteryTypeValue), LotteryRewardQueueTask.toJSONString(lotteryRewardQueueTask));
		} catch (Exception e) {
			logger.error("放入派奖任务到队列失败, {}, {}", new Object[] {
					lotteryType.getName(), 	phaseNo
			});
			logger.error(e.getMessage(), e);
		}
		if(resultFlag){
			//加锁
			lotteryRewardMemCacheLock = new LotteryRewardMemCacheLock();
			lotteryRewardMemCacheLock.setLotteryTypeValue(lotteryTypeValue);
			lotteryRewardMemCacheLock.setPhase(phaseNo);
			boolean addLockFlag = false;
			try {
				addLockFlag = memcachedService.set(rewardLockKey, lotteryRewardMemCacheLock, GlobalConfig.MEMCACHE_LOTTERY_DRAW_LOCK_ALIVE_TIME, LotteryUtil.REWARD_LOCK_TIMEOUT);
			} catch (Exception e) {
				logger.error("派奖任务加同步锁失败", e);
			}
			if(addLockFlag){
				rs.put("state","success");
				msg = msgHeader+"发送派奖任务到派奖队列成功";
				rs.put("msg", msg);
				logger.info(msg);
			}else{
				rs.put("state","failed");
				msg = msgHeader+"发送派奖任务到派奖队列成功,但是派奖任务加同步锁失败";
				rs.put("msg", msg);
				logger.error(msg);
			}
		}else{
			rs.put("state","failed");
			msg = msgHeader+"发送派奖任务到派奖队列失败";
			rs.put("msg", msg);
			logger.error(msg);
		}
		writeRs(response,rs);
		logger.info("派奖结束");
		return null;
	}
	
	//派奖状态
	public String rewardStatus() {
		logger.info("进入查询正在执行的派奖状态");
		if (phaseNoText!=null&&!"".equals(phaseNoText)) {
			phaseNo = phaseNoText;
		}
		JSONObject rs = new JSONObject();
		String msg = "";
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			logger.error("彩种类型数值不存在,null");
			rs.put("state","failed");
			rs.put("msg", "彩种类型数值不存在,null");
			writeRs(response,rs);
			return null;
		}
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		String memCacheLotteryDrawKey = LotteryUtil.generateLotteryRewardKey(lotteryType, phaseNo);
		LotteryRewardPlanStatus lotteryRewardPlanStatus;
		try {
			lotteryRewardPlanStatus = (LotteryRewardPlanStatus)memcachedService.get(memCacheLotteryDrawKey);
		} catch (Exception e) {
			logger.error("从缓存中读取派奖锁出错", e);
			rs.put("state","failed");
			rs.put("msg", "从缓存中读取派奖锁出错");
			writeRs(response,rs);
			return null;
		}
		if(lotteryRewardPlanStatus==null){
			rs.put("state", "failed");
			msg = "无正在执行的派奖状态";
			rs.put("msg", msg);
			rs.put("rewardStatus", LotteryRewardPlanStatus.toJSON(lotteryRewardPlanStatus));
			logger.info(msg);
		}else{
			rs.put("state", "success");
			rs.put("rewardStatus", LotteryRewardPlanStatus.toJSON(lotteryRewardPlanStatus));
		}
		writeRs(response,rs);
		logger.info("查询正在执行的派奖状态结束");
		return null;
	}
	
	public PhaseService getPhaseService() {
		return phaseService;
	}
	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
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
	public Phase getPhase() {
		return phase;
	}
	public void setPhase(Phase phase) {
		this.phase = phase;
	}
	public List<LotteryType> getLotteryTypeList() {
		return OnSaleLotteryList.get();
	}
	public void setLotteryTypeList(List<LotteryType> lotteryTypeList) {
		this.lotteryTypeList = lotteryTypeList;
	}
	public SimpleQueueService getSimpleQueueService() {
		return simpleQueueService;
	}
	public void setSimpleQueueService(SimpleQueueService simpleQueueService) {
		this.simpleQueueService = simpleQueueService;
	}
	public MemcachedService getMemcachedService() {
		return memcachedService;
	}
	public void setMemcachedService(MemcachedService memcachedService) {
		this.memcachedService = memcachedService;
	}
	public LotteryPlanService getLotteryPlanService() {
		return lotteryPlanService;
	}
	public void setLotteryPlanService(LotteryPlanService lotteryPlanService) {
		this.lotteryPlanService = lotteryPlanService;
	}
	public List<Plan> getPlans() {
		return plans;
	}
	public void setPlans(List<Plan> plans) {
		this.plans = plans;
	}
	public String getRewardPlanData() {
		return rewardPlanData;
	}
	public void setRewardPlanData(String rewardPlanData) {
		this.rewardPlanData = rewardPlanData;
	}
	public String getRewardQueuePrefix() {
		return rewardQueuePrefix;
	}
	public void setRewardQueuePrefix(String rewardQueuePrefix) {
		this.rewardQueuePrefix = rewardQueuePrefix;
	}
	public Integer getWonResultStatusValue() {
		return ResultStatus.WON.getValue();
	}
	public void setWonResultStatusValue(Integer wonResultStatusValue) {
		this.wonResultStatusValue = wonResultStatusValue;
	}
	public List<PlanType> getPlanTypes(){
		return PlanType.getItems();
	}
	public String getPlanId() {
		return planId;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	public Integer getPlanTypeId() {
		return planTypeId;
	}
	public void setPlanTypeId(Integer planTypeId) {
		this.planTypeId = planTypeId;
	}
	public String getPhaseNoText() {
		return phaseNoText;
	}
	public void setPhaseNoText(String phaseNoText) {
		this.phaseNoText = phaseNoText;
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

	public Integer getResultStatusId() {
		return resultStatusId;
	}

	public void setResultStatusId(Integer resultStatusId) {
		this.resultStatusId = resultStatusId;
	}
	
	public List<ResultStatus> getResultStatus(){
		return resultStatusList;
	}
}
