package web.action.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 
 * 单方案查询
 * 
 * @author jinsheng
 *
 */

public class SingleLotteryPlanAction extends BaseAction{

	private static final long serialVersionUID = -327365672541321599L;
	private Logger logger = LoggerFactory.getLogger(SingleLotteryPlanAction.class);
	private String planNo;
	private LotteryPlanService lotteryPlanService;
	private Plan plan;
	
	public String handle() {
		logger.info("进入单方案查询");
		return "list";
	}
	
	public String query() {
		logger.info("开始查询单方案");
		
		try {
			plan = lotteryPlanService.getPlanById(planNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("按方案号查询方案，API调用失败，原因：" + e.getMessage());
			super.setErrorMessage("按方案号查询方案，API调用失败，原因：" + e.getMessage());	
			return "failure";
		}
		if (lotteryPlanService == null) {
			logger.error("查询结果为空！");
			super.setErrorMessage("查询结果为空！");
			return "failure";
		}
		logger.info("单方案查询结束");
		return "list";
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public String getPlanNo() {
		return planNo;
	}
	
	public void setPlanNo(String planNo) {
		this.planNo = planNo;
	}
	
	public LotteryPlanService getLotteryPlanService() {
		return lotteryPlanService;
	}

	public void setLotteryPlanService(LotteryPlanService lotteryPlanService) {
		this.lotteryPlanService = lotteryPlanService;
	}
	
	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}
}