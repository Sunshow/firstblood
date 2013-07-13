/**
 * 
 */
package web.activiti.delegate.giftrewards;

import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.activiti.delegate.AbstractDelegate;
import com.lehecai.admin.web.activiti.entity.GiftRewardsTask;
import com.lehecai.admin.web.activiti.form.GiftRewardsTaskForm;
import com.lehecai.admin.web.activiti.service.GiftRewardsTaskService;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.service.process.AmountSettingService;
import com.lehecai.admin.web.service.user.PermissionService;

/**
 * @author chirowong
 *
 */
public class CreateGiftRewardsTaskDelegate extends AbstractDelegate {
	private static final String GIFT_REWARDS_PROCESS = "giftRewardsProcess";
	private static final String CREATE_GIFT_REWARDS_TASK = "createGiftRewardsTask";
	@Autowired
	private GiftRewardsTaskService giftRewardsTaskService;
	@Autowired
	private AmountSettingService amountSettingService;
	@Autowired
	private PermissionService permissionService;
	
	@Override
	protected void doExecution(DelegateExecution execution) throws Exception {
		GiftRewardsTaskForm giftRewardsTaskForm = (GiftRewardsTaskForm)execution.getVariable("giftRewardsTaskForm");
		GiftRewardsTask task = giftRewardsTaskForm.getGiftRewardsTask();
		task.setCreatedTime(new Date());
		//存储processId目的为history查询能够对应数据
		task.setProcessId(execution.getProcessInstanceId());
		
		giftRewardsTaskService.create(task);
		
		Double userAmount = giftRewardsTaskForm.getGiftRewardsTask().getAmount();
		User opUser = permissionService.getByUserName(giftRewardsTaskForm.getGiftRewardsTask().getInitiator());
		Double rechargeAmount = amountSettingService.auditAmount(GIFT_REWARDS_PROCESS, CREATE_GIFT_REWARDS_TASK,opUser.getId(),opUser.getRoleID());
		if(rechargeAmount == null){
			rechargeAmount = 0.0D;
		}
		boolean enough = false;
		if (userAmount <= rechargeAmount) {
			enough = true;
		}
		execution.setVariable("giftRewardsTaskForm", giftRewardsTaskForm);
		execution.setVariable("enough", enough);
		execution.setVariable("taskName", CREATE_GIFT_REWARDS_TASK);
		execution.setVariable("processName", GIFT_REWARDS_PROCESS);
		execution.setVariable("opUserId", opUser.getId());
	}
}
