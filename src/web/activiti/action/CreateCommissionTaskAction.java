package web.activiti.action;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.activiti.form.CommissionTaskForm;
import com.lehecai.admin.web.activiti.task.commission.StartCommissionTask;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class CreateCommissionTaskAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	private CommissionTaskForm commissionTaskForm;
	
	private StartCommissionTask startCommissionTask;
	
	@Autowired
	private MemberService memberService;
	
	public String handle() {
		logger.info("创建佣金派发工单");
		return "inputForm";
	}
	
	public String manage() {
		logger.info("提交彩金赠送工单，启动彩金赠送工作流程");
		if (commissionTaskForm == null) {
			logger.error("提交佣金派发工单失败，原因：commissionTaskForm为空");
			super.setErrorMessage("提交佣金派发工单失败，原因：commissionTaskForm为空");
			return "inputForm";
		}
		if (commissionTaskForm.getCommissionTask().getCommissionAmount() == null || commissionTaskForm.getCommissionTask().getCommissionAmount() <= 0.00D) {
			logger.error("提交佣金派发工单失败，原因：赠予金额amount为空");
			super.setErrorMessage("提交佣金派发工单失败，原因：赠予金额amount为空");
			return "inputForm";
		}
		if (StringUtils.isEmpty(commissionTaskForm.getCommissionTask().getStatement())) {
			logger.error("提交佣金派发工单失败，原因：赠予原因reason为空");
			super.setErrorMessage("提交佣金派发工单失败，原因：赠予原因reason为空");
			return "inputForm";
		}
		Member member = null;
		try {
			member = memberService.get(commissionTaskForm.getCommissionTask().getUsername());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		if (member == null) {
			logger.error("提交佣金派发工单失败，原因：[{}]用户名不存在", commissionTaskForm.getCommissionTask().getUsername());
			super.setErrorMessage("提交佣金派发工单失败，原因：[" + commissionTaskForm.getCommissionTask().getUsername() + "]用户名不存在");
			return "inputForm";
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
		commissionTaskForm.getCommissionTask().setInitiator((userSessionBean.getUser().getUserName()));
		Map<String, Object> variables = new HashMap<String, Object>();
	    variables.put("commissionTaskForm", commissionTaskForm);
		
	    startCommissionTask.start(variables);
	    
	    super.setSuccessMessage("创建佣金派发工单成功！");
		return "success";
	}

	public CommissionTaskForm getCommissionTaskForm() {
		return commissionTaskForm;
	}

	public void setCommissionTaskForm(CommissionTaskForm commissionTaskForm) {
		this.commissionTaskForm = commissionTaskForm;
	}

	public StartCommissionTask getStartCommissionTask() {
		return startCommissionTask;
	}

	public void setStartCommissionTask(StartCommissionTask startCommissionTask) {
		this.startCommissionTask = startCommissionTask;
	}

	
}
