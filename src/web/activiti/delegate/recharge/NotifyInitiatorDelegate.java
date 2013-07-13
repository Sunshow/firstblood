/**
 * 
 */
package web.activiti.delegate.recharge;

import com.lehecai.admin.web.activiti.delegate.AbstractDelegate;
import org.activiti.engine.delegate.DelegateExecution;

/**
 * @author chirowong 
 * 给汇款工单发起人发送短信通知
 */
public class NotifyInitiatorDelegate extends AbstractDelegate {

    /*
	@Autowired
	private NotifyService notifyService;
	@Autowired
	private PermissionService permissionService;
	*/

	@Override
	protected void doExecution(DelegateExecution execution) throws Exception {
		/*logger.info("短信通知工单发起人");
		RechargeTaskForm rechargeTaskForm = (RechargeTaskForm)execution.getVariable("rechargeTaskForm");
		String userName = rechargeTaskForm.getRechargeTask().getInitiator();
		User user = userService.getByUserName(userName);
		if(user != null){
			boolean ifApprove = (Boolean)execution.getVariable("ifApprove");
			logger.info("短信工单发起人"+user.getName());
			String message = "";
			if(ifApprove){//批准
				logger.info("您发起的汇款工单已成功，充值用户名为[" + rechargeTaskForm.getRechargeTask().getUsername() + "]，充值金额为[" + rechargeTaskForm.getRechargeTask().getAmount() + "]元。");
				message = "您发起的汇款工单已成功，充值用户名为[" + rechargeTaskForm.getRechargeTask().getUsername() + "]，充值金额为[" + rechargeTaskForm.getRechargeTask().getAmount() + "]元。";
			}else{//拒绝
				logger.info("您发起的汇款工单已失败，充值用户名为[" + rechargeTaskForm.getRechargeTask().getUsername() + "]，充值金额为[" + rechargeTaskForm.getRechargeTask().getAmount() + "]元，失败原因为[" + rechargeTaskForm.getRechargeTask().getMemo() + "]。");
				message = "您发起的汇款工单已失败，充值用户名为[" + rechargeTaskForm.getRechargeTask().getUsername() + "]，充值金额为[" + rechargeTaskForm.getRechargeTask().getAmount() + "]元，失败原因为[" + execution.getVariable("memo") + "]。";
			}
			notifyService.sendSms(message, user.getTel());
			logger.info("发送短信："+user.getTel());
		}*/
	}
}
