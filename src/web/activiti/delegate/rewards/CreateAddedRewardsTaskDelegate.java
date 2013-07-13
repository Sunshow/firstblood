/**
 * 
 */
package web.activiti.delegate.rewards;

import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.activiti.delegate.AbstractDelegate;
import com.lehecai.admin.web.activiti.entity.AddedRewardsTask;
import com.lehecai.admin.web.activiti.form.AddedRewardsTaskForm;
import com.lehecai.admin.web.activiti.service.AddedRewardsTaskService;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.core.api.lottery.Plan;

/**
 * @author qatang
 *
 */
public class CreateAddedRewardsTaskDelegate extends AbstractDelegate {
	@Autowired
	private AddedRewardsTaskService addedRewardsTaskService;
	@Autowired
	private LotteryPlanService lotteryPlanService;
	
	@Override
	protected void doExecution(DelegateExecution execution) throws Exception {
		AddedRewardsTaskForm addedRewardsTaskForm = (AddedRewardsTaskForm)execution.getVariable("addedRewardsTaskForm");
		
		AddedRewardsTask task = addedRewardsTaskForm.getAddedRewardsTask();
		Plan plan = lotteryPlanService.get(task.getPlanId());
		
		task.setLotteryType(plan.getLotteryType().getValue());
		task.setLotteryName(plan.getLotteryType().getName());
		task.setPhaseType(plan.getPhaseType().getValue());
		task.setPhase(plan.getPhase());
		task.setPlanCreatedTime(plan.getCreatedTime());
		task.setUid(plan.getUid());
		task.setUsername(plan.getUsername());
		task.setPlanStatus(plan.getPlanStatus().getValue());
		task.setAmount(plan.getAmount());
		task.setCreatedTime(new Date());
		//存储processId目的为history查询能够对应数据
		task.setProcessId(execution.getProcessInstanceId());
		
		addedRewardsTaskService.create(task);
		execution.setVariable("addedRewardsTaskForm", addedRewardsTaskForm);
	}
}
