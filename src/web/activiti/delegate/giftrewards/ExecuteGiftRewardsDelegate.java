/**
 * 
 */
package web.activiti.delegate.giftrewards;

import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.activiti.delegate.AbstractDelegate;
import com.lehecai.admin.web.activiti.entity.GiftRewardsTask;
import com.lehecai.admin.web.activiti.form.GiftRewardsTaskForm;
import com.lehecai.admin.web.activiti.service.GiftRewardsTaskService;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.service.lottery.ManuallyRechargeService;
import com.lehecai.admin.web.service.process.AmountSettingService;
import com.lehecai.admin.web.service.user.PermissionService;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ManuallyRechargeType;
import com.lehecai.core.lottery.WalletType;
import com.lehecai.core.util.CoreNumberUtil;

/**
 * @author chirowong
 * 执行彩金赠送
 */
public class ExecuteGiftRewardsDelegate extends AbstractDelegate {
	@Autowired
	private ManuallyRechargeService manuallyRechargeService;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private AmountSettingService amountSettingService;
	@Autowired
	private GiftRewardsTaskService giftRewardsTaskService;
	
	@Override
	protected void doExecution(DelegateExecution execution) throws Exception {
		logger.info("执行彩金赠送");
		GiftRewardsTaskForm giftRewardsTaskForm = (GiftRewardsTaskForm)execution.getVariable("giftRewardsTaskForm");
		GiftRewardsTask grt = giftRewardsTaskForm.getGiftRewardsTask();
		GiftRewardsTask giftRewardsTask = giftRewardsTaskService.get(grt.getId());
		Map<String, Object> variables = execution.getVariables();
		String taskName = (String)variables.get("taskName");
		String processName = (String)variables.get("processName");
		Long opUserId = (Long)variables.get("opUserId");
		String account = giftRewardsTaskForm.getGiftRewardsTask().getUsername();//赠送彩金账户
		Double amount = giftRewardsTaskForm.getGiftRewardsTask().getAmount();//赠送金额
		String opUserName = giftRewardsTaskForm.getGiftRewardsTask().getInitiator();//工单发起人，赠送彩金的工作人员
		User opUser = permissionService.getByUserName(opUserName);
		amountSettingService.manageAmountMinus(processName, taskName, opUserId, amount);
		WalletType wt = WalletType.GIFT;
		ManuallyRechargeType mrt = ManuallyRechargeType.PRESENT_REFUND;
		Integer intWalletType = giftRewardsTaskForm.getGiftRewardsTask().getWalletType();
		if(intWalletType != null){
			if(intWalletType.intValue() == WalletType.CASH.getValue()){//如果是现金派送
				wt = WalletType.CASH;
				mrt = ManuallyRechargeType.OTHERS;
			}
		}
		try {
			manuallyRechargeService.recharge(account, amount,null, null, opUser, wt, mrt, null, giftRewardsTask.getReason(), null);
			giftRewardsTask.setResult(account+"账户派送成功，赠送账户为"+wt.getName()+"，赠送金额为"+CoreNumberUtil.formatNumBy2Digits(amount));
		} catch (ApiRemoteCallFailedException e) {
			logger.error("赠送彩金，api调用异常，{}", e.getMessage());
			amountSettingService.manageAmountAdd(processName, taskName, opUserId, amount);
			giftRewardsTask.setResult("赠送彩金，api调用异常，"+e.getMessage());
		} finally{
			giftRewardsTaskService.merge(giftRewardsTask);
		}
		giftRewardsTaskForm.setGiftRewardsTask(giftRewardsTask);
		execution.setVariable("giftRewardsTaskForm", giftRewardsTaskForm);
		logger.info("{}账户赠送彩金{}成功", account, CoreNumberUtil.formatNumBy2Digits(amount));
	}
}
