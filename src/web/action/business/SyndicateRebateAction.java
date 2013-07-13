package web.action.business;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.PlanType;
import com.lehecai.core.lottery.ResultStatus;
/**
 * 
 * @author qatang
 *
 */
public class SyndicateRebateAction extends BaseAction {
	private static final long serialVersionUID = -6839161837259244571L;
	private final Logger logger = LoggerFactory.getLogger(SyndicateRebateAction.class);
	
	private LotteryPlanService lotteryPlanService;
	
	private String planNo;			    //方案编号
	private Plan plan;
	
	/**
	 * 查询方案
	 * @return
	 */
	public String handle(){
		logger.info("进入方案查询");
		if (planNo == null || "".equals(planNo)) {
			return "list";
		}
		try {
			plan = lotteryPlanService.get(planNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查看方案，api调用异常，{}", e.getMessage());
		}
		
		if (plan == null) {
			logger.error("未找到编码为{}的方案", planNo);
			super.setErrorMessage("未找到编码为" + planNo + "的方案");
			return "list";
		}
		if (plan.getPlanType().getValue() != PlanType.HM.getValue()) {
			plan = null;
			logger.error("方案编码为{}的方案不是合买方案", planNo);
			super.setErrorMessage("方案编码为" + planNo + "的方案不是合买方案");
			return "list";
		}
		if (plan.getResultStatus().getValue() == ResultStatus.REWARDED.getValue()) {
			plan = null;
			logger.error("方案编码为{}的方案结果处于已派奖状态,不能修改提成比例", planNo);
			super.setErrorMessage("方案编码为" + planNo + "的方案结果处于已派奖状态,不能修改提成比例");
			return "list";
		}
		return "list";
	}
	
	public String manage() {
		logger.info("进入修改合买提成比例");
		HttpServletResponse response = ServletActionContext.getResponse();
		String msg = "修改合买提成比例成功！";
		
		JSONObject obj = new JSONObject();
		if (plan == null) {
			msg = "修改合买提成比例失败！plan参数不能为空！";
			obj.put("msg", msg);
			writeRs(response, obj);
			return null;
		}
		try {
			lotteryPlanService.updateRebate(plan.getId(), plan.getRebate());
		} catch (ApiRemoteCallFailedException e) {
			msg = "修改合买提成比例失败！api调用异常，请联系管理员！";
			logger.error(e.getMessage(), e);
		}
		obj.put("msg", msg);
		writeRs(response, obj);
		return null;
	}

	public LotteryPlanService getLotteryPlanService() {
		return lotteryPlanService;
	}

	public void setLotteryPlanService(LotteryPlanService lotteryPlanService) {
		this.lotteryPlanService = lotteryPlanService;
	}

	public String getPlanNo() {
		return planNo;
	}

	public void setPlanNo(String planNo) {
		this.planNo = planNo;
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}
}
