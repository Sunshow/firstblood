/**
 * 
 */
package web.activiti.task.recharge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.activiti.form.RechargeTaskForm;
import com.lehecai.admin.web.activiti.task.CommonProcessTask;
import com.lehecai.admin.web.service.process.AmountSettingService;

/**
 * @author qatang
 *
 */
public class HandleRechargeTask extends CommonProcessTask {
	
	private static final String RECHARGE_PROCESS = "rechargeProcess";
	private static final String CLAIM_RECHARGE_TASK = "claimRechargeTask";
	
	@Autowired
	private AmountSettingService amountSettingService;
	
	public void minusAmount(Long operateId, Double amount) {
		amountSettingService.manageAmountMinus(RECHARGE_PROCESS, CLAIM_RECHARGE_TASK, operateId, amount);
	}
	
	public void addAmount(Long operateId, Double amount) {
		amountSettingService.manageAmountAdd(RECHARGE_PROCESS, CLAIM_RECHARGE_TASK, operateId, amount);
	}
	
	public Double getRestAmount(Long operateId,Long roleId) {
		return amountSettingService.auditAmount(RECHARGE_PROCESS, CLAIM_RECHARGE_TASK, operateId,roleId);
	}
	
	public List<RechargeTaskForm> listByAssignee(String assignee) {
		if (StringUtils.isEmpty(assignee)) {
			logger.error("处理汇款充值工单异常：按用户查询任务列表时，assignee为空");
			return null;
		}
		logger.info("处理汇款充值工单：查询userId={}用户的任务列表", assignee);
		List<Task> userTaskList = taskService.createTaskQuery().processDefinitionKey(RECHARGE_PROCESS).taskDefinitionKey(CLAIM_RECHARGE_TASK).taskAssignee(assignee).list();
		List<RechargeTaskForm> rechargeTaskFormList = new ArrayList<RechargeTaskForm>();
		for (Task task : userTaskList) {
			RechargeTaskForm rechargeTaskForm = (RechargeTaskForm)this.getVariable(task.getProcessInstanceId(), "rechargeTaskForm");
			rechargeTaskForm.setTaskId(task.getId());
			rechargeTaskForm.setProcessId(task.getProcessInstanceId());
			rechargeTaskFormList.add(rechargeTaskForm);
		}
		return rechargeTaskFormList;
	}
	
	public void completeAndClaimNextTask(boolean enough, String taskId, String processId, String userId) {
		Map<String, Object> variables = new HashMap<String, Object>();
	    variables.put("enough", enough);
	    this.complete(taskId, variables);
		
		logger.info("认领下一步任务");
		Task nextTask = taskService.createTaskQuery().processInstanceId(processId).singleResult();
		this.claim(nextTask.getId(), userId);
	}

}
