package web.activiti.delegate.commissions;

import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.activiti.delegate.AbstractDelegate;
import com.lehecai.admin.web.activiti.entity.CommissionTask;
import com.lehecai.admin.web.activiti.form.CommissionTaskForm;
import com.lehecai.admin.web.activiti.service.CommissionTaskService;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.service.process.AmountSettingService;
import com.lehecai.admin.web.service.user.PermissionService;

public class CreateCommissionTaskDelegate extends AbstractDelegate {
	
	private static final String COMMISSION_PROCESS = "commissionTaskProcess";
	private static final String CREATE_COMMISSION_TASK = "createCommissionTask";
	@Autowired
	private CommissionTaskService commissionTaskService;
	@Autowired
	private AmountSettingService amountSettingService;
	@Autowired
	private PermissionService permissionService;

	@Override
	protected void doExecution(DelegateExecution execution) throws Exception {
		
		CommissionTaskForm commissionTaskForm =(CommissionTaskForm)execution.getVariable("commissionTaskForm");
		CommissionTask task =commissionTaskForm.getCommissionTask();
		task.setCreatedTime(new Date());
		
		task.setProcessId(execution.getProcessInstanceId());
		commissionTaskService.create(task);
		Double userAmount = commissionTaskForm.getCommissionTask().getCommissionAmount();
		User opUser = permissionService.getByUserName(commissionTaskForm.getCommissionTask().getInitiator());
		Double rechargeAmount = amountSettingService.auditAmount(COMMISSION_PROCESS, CREATE_COMMISSION_TASK,opUser.getId(),opUser.getRoleID());
		if(rechargeAmount == null){
			rechargeAmount = 0.0D;
		}
		boolean enough = false;
		if (userAmount <= rechargeAmount) {
			enough = true;
		}
		execution.setVariable("commissionTaskForm", commissionTaskForm);
		execution.setVariable("enough", enough);
		execution.setVariable("taskName", CREATE_COMMISSION_TASK);
		execution.setVariable("processName", COMMISSION_PROCESS);
		execution.setVariable("opUserId", opUser.getId());

	}


	
}
