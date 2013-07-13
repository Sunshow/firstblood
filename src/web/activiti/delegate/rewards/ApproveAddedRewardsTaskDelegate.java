/**
 * 
 */
package web.activiti.delegate.rewards;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.activiti.delegate.AbstractDelegate;
import com.lehecai.admin.web.activiti.form.AddedRewardsTaskForm;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.service.lottery.ManuallyRechargeService;
import com.lehecai.admin.web.service.process.AmountSettingService;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ManuallyRechargeType;
import com.lehecai.core.lottery.WalletType;

/**
 * @author chirowong
 *
 */
public class ApproveAddedRewardsTaskDelegate extends AbstractDelegate {
	private final static String PROCESS_ID = "addedRewardsProcess";
	private final static String TASK_ID = "payAddedRewardsTask";
	@Autowired
	private ManuallyRechargeService manuallyRechargeService;
	@Autowired
	private AmountSettingService amountSettingService;
	
	@Override
	protected void doExecution(DelegateExecution execution) throws Exception {
		AddedRewardsTaskForm addedRewardsTaskForm = (AddedRewardsTaskForm)execution.getVariable("addedRewardsTaskForm");
		Map<String, Object> variables = execution.getVariables();
		User opUser = (User)variables.get("opUser");
		try {
			manuallyRechargeService.rechargeAddPlanId(addedRewardsTaskForm.getAddedRewardsTask().getUsername(), addedRewardsTaskForm.getAddedRewardsTask().getPosttaxPrize(),
					null, null, opUser, WalletType.CASH, ManuallyRechargeType.REPLENISH_REWARD, null, null, null,addedRewardsTaskForm.getAddedRewardsTask().getPlanId());
		} catch (ApiRemoteCallFailedException e) {
			amountSettingService.manageAmountAdd(PROCESS_ID, TASK_ID, opUser.getId(), addedRewardsTaskForm.getAddedRewardsTask().getPosttaxPrize());
			logger.error(e.getMessage(), e);
		}
	}
}
