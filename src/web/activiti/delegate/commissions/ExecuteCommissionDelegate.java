package web.activiti.delegate.commissions;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.activiti.delegate.AbstractDelegate;
import com.lehecai.admin.web.activiti.entity.CommissionTask;
import com.lehecai.admin.web.activiti.form.CommissionTaskForm;
import com.lehecai.admin.web.activiti.service.CommissionTaskService;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.service.lottery.ManuallyRechargeService;
import com.lehecai.admin.web.service.process.AmountSettingService;
import com.lehecai.admin.web.service.user.PermissionService;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ManuallyRechargeType;
import com.lehecai.core.lottery.WalletType;
import com.lehecai.core.util.CoreNumberUtil;

public class ExecuteCommissionDelegate extends AbstractDelegate {

	@Autowired
	private ManuallyRechargeService manuallyRechargeService;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private AmountSettingService amountSettingService;
	@Autowired
	private CommissionTaskService taskService;
	
	@Override
	protected void doExecution(DelegateExecution execution) throws Exception {
		logger.info("执行佣金派发");
		CommissionTaskForm form = (CommissionTaskForm)execution.getVariable("commissionTaskForm");
		CommissionTask task = form.getCommissionTask();
		CommissionTask sk = taskService.get(task.getId());
		Map<String, Object> variables = execution.getVariables();
		String taskName = (String)variables.get("taskName");
		String processName = (String)variables.get("processName");
		Long opUserId = (Long)variables.get("opUserId");
		String account = form.getCommissionTask().getUsername();//账户
		Double amount = form.getCommissionTask().getCommissionAmount();//金额
		String opUserName = form.getCommissionTask().getInitiator();//工单发起人
		User opUser = permissionService.getByUserName(opUserName);
		amountSettingService.manageAmountMinus(processName, taskName, opUserId, amount);
		WalletType wt = WalletType.CASH;
		ManuallyRechargeType mrt = ManuallyRechargeType.COMMISSION;
		try {
			manuallyRechargeService.recharge(account, amount,null, null, opUser, wt, mrt, null, sk.getStatement(), null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("佣金派发，api调用异常，{}", e.getMessage());
			amountSettingService.manageAmountAdd(processName, taskName, opUserId, amount);
		} finally{
			taskService.merge(sk);
		}
		form.setCommissionTask(sk);
		execution.setVariable("commissionTaskForm", form);
		logger.info("{}佣金派发{}成功", account, CoreNumberUtil.formatNumBy2Digits(amount));
	}

}
