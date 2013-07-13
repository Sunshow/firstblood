package web.action.business;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;

/**
 * 合买方案Action
 * @author yanweijie
 *
 */
public class UpdateHmLotteryPlanAction extends BaseAction {
	private static final long serialVersionUID = -8733542056093371953L;
	private Logger logger = LoggerFactory.getLogger(UpdateHmLotteryPlanAction.class);
	
	private LotteryPlanService lotteryPlanService;
	
	private List<Plan> hmLotteryPlanList;
	
	private Plan plan;
	private String username;
	private String planId;
	private String phase;
	private Integer lotteryTypeId;

	/**
	 * 条件查询合买方案
	 * @return
	 */
	public String handle() {
		logger.info("进入查询合买方案列表");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询合买方案列表");
		LotteryType lotteryType = (lotteryTypeId == null || lotteryTypeId == 0) ? LotteryType.ALL : LotteryType.getItem(lotteryTypeId);
		Map<String, Object> map = null;
		try {
			map = lotteryPlanService.findHMPlan(username, planId, lotteryType, phase, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("调用API查询合买方案异常");
			super.setErrorMessage("调用API查询合买方案异常");
			return "failure";
		}
		if (map != null) {
			hmLotteryPlanList = (List<Plan>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			String pageStr = PageUtil.getPageString(ServletActionContext.getRequest(), pageBean);
			super.setPageString(pageStr);
		}
		
		return "list";
	}
	
	/**
	 * 修改合买方案标题和内容
	 * @return
	 */
	@SuppressWarnings("static-access")
	public String manage() {
		logger.info("进入修改合买方案标题和内容");
		if (plan == null || (plan.getId() == null || plan.getId().equals(""))) {
			logger.error("记录日志信息：合买方案为空！");
			super.setErrorMessage("合买方案不能为空！");
			return "failure";
		}
		if (plan.getTitle() == null || plan.getTitle().trim().equals("")) {
			logger.error("记录日志信息：合买方案的标题为空！");
			super.setErrorMessage("合买方案的标题不能为空！");
			return "failure";
		}
		if (plan.getTitle().trim().length() > 50) {
			logger.error("记录日志信息：合买方案的标题的长度超过50个字符！");
			super.setErrorMessage("合买方案的标题的长度不能超过50个字符！");
			return "failure";
		}
		
		boolean updateResult = false;
		try {
			updateResult = lotteryPlanService.updatePlan(plan);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("调用API修改合买方案异常");
			super.setErrorMessage("调用API修改合买方案异常");
			return "failure";
		}
		
		JSONObject rs = new JSONObject();
		if (updateResult) {
			rs.put("code", "0");
		} else {
			rs.put("code", "1");
		}
		rs.put(plan.QUERY_ID, plan.getId());
		rs.put(plan.SET_TITLE, plan.getTitle());
		rs.put(plan.SET_DESCRIPTION, plan.getDescription());
		
		super.writeRs(ServletActionContext.getResponse(), rs);
		
		return null;
		
	}
	
	public List<LotteryType> getLotteryTypes() {
		return LotteryType.getItems();
	}
	
	public LotteryPlanService getLotteryPlanService() {
		return lotteryPlanService;
	}

	public void setLotteryPlanService(LotteryPlanService lotteryPlanService) {
		this.lotteryPlanService = lotteryPlanService;
	}

	public List<Plan> getHmLotteryPlanList() {
		return hmLotteryPlanList;
	}

	public void setHmLotteryPlanList(List<Plan> hmLotteryPlanList) {
		this.hmLotteryPlanList = hmLotteryPlanList;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}

	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}
}
