/**
 * 
 */
package web.activiti.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.activiti.form.RechargeTaskForm;
import com.lehecai.admin.web.activiti.task.recharge.StartRechargeTask;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.BankType;

/**
 * @author qatang
 *
 */
public class CreateRechargeTaskAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private StartRechargeTask startRechargeTask;
	@Autowired
	private MemberService memberService;
	
	private RechargeTaskForm rechargeTaskForm;
	
	public String handle() {
		logger.info("创建汇款充值工单");
		return "inputForm";
	}
	
	public String manage() {
		logger.info("提交汇款充值工单，启动汇款充值工作流程");
		if (rechargeTaskForm == null) {
			logger.error("提交汇款充值工单失败，原因：rechargeTaskForm为空");
			super.setErrorMessage("提交汇款充值工单失败，原因：rechargeTaskForm为空");
			return "inputForm";
		}
		if (StringUtils.isEmpty(rechargeTaskForm.getRechargeTask().getUsername())) {
			logger.error("提交汇款充值工单失败，原因：充值用户名username为空");
			super.setErrorMessage("提交汇款充值工单失败，原因：充值用户名username为空");
			return "inputForm";
		}
		if (rechargeTaskForm.getRechargeTask().getAmount() == null || rechargeTaskForm.getRechargeTask().getAmount() <= 0.00D) {
			logger.error("提交汇款充值工单失败，原因：充值用户名username为空");
			super.setErrorMessage("提交汇款充值工单失败，原因：充值用户名username为空");
			return "inputForm";
		}
		if (StringUtils.isEmpty(rechargeTaskForm.getRechargeTask().getRealName())) {
			logger.error("提交汇款充值工单失败，原因：充值用户真实姓名realName为空");
			super.setErrorMessage("提交汇款充值工单失败，原因：充值用户真实姓名realName为空");
			return "inputForm";
		}
		if (StringUtils.isEmpty(rechargeTaskForm.getRechargeTask().getUserBank())) {
			logger.error("提交汇款充值工单失败，原因：充值用户银行userBank为空");
			super.setErrorMessage("提交汇款充值工单失败，原因：充值用户银行userBank为空");
			return "inputForm";
		}
		Integer rechargeBankId = rechargeTaskForm.getRechargeTask().getRechargeBankId();
		if (rechargeBankId == null || rechargeBankId.intValue() == -1  || rechargeBankId.intValue() == 0) {
			logger.error("提交汇款充值工单失败，原因：到账银行rechargeBank信息错误");
			super.setErrorMessage("提交汇款充值工单失败，原因：到账银行rechargeBank信息错误");
			return "inputForm";
		}else{
			rechargeTaskForm.getRechargeTask().setRechargeBank(BankType.getItem(rechargeBankId).getName());
		}
		Member member = null;
		try {
			member = memberService.get(rechargeTaskForm.getRechargeTask().getUsername());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		if (member == null) {
			logger.error("提交汇款充值工单失败，原因：[{}]用户名不存在", rechargeTaskForm.getRechargeTask().getUsername());
			super.setErrorMessage("提交汇款充值工单失败，原因：[" + rechargeTaskForm.getRechargeTask().getUsername() + "]用户名不存在");
			return "inputForm";
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		
		rechargeTaskForm.getRechargeTask().setInitiator(userSessionBean.getUser().getUserName());
		
		Map<String, Object> variables = new HashMap<String, Object>();
	    variables.put("rechargeTaskForm", rechargeTaskForm);
		
	    startRechargeTask.start(variables);
	    
	    super.setSuccessMessage("创建汇款充值工单成功！");
		return "success";
	}

	public RechargeTaskForm getRechargeTaskForm() {
		return rechargeTaskForm;
	}

	public void setRechargeTaskForm(RechargeTaskForm rechargeTaskForm) {
		this.rechargeTaskForm = rechargeTaskForm;
	}
	
	public List<BankType> getBankTypes(){
		List<BankType> bankTypes = new ArrayList<BankType>();
		for(BankType bt : BankType.getItems()){
			if(bt.getValue() > 0){
				bankTypes.add(bt);
			}
		}
		return bankTypes;
	}

}
