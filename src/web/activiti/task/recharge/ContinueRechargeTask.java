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

import com.lehecai.admin.web.activiti.form.RechargeTaskForm;
import com.lehecai.admin.web.activiti.task.CommonProcessTask;

/**
 * @author qatang
 *
 */
public class ContinueRechargeTask extends CommonProcessTask {
	
	public List<RechargeTaskForm> listByAssignee(String assignee) {
		if (StringUtils.isEmpty(assignee)) {
			logger.error("需要判断到帐充值工单列表异常：按用户查询任务列表时，assignee为空");
			return null;
		}
		logger.info("需要判断到帐充值工单列表失败：查询userId={}用户的任务列表", assignee);
		List<Task> continueTaskList = taskService.createTaskQuery().processDefinitionKey("rechargeProcess").taskDefinitionKey("queryFinancialAccount").taskAssignee(assignee).list();
		List<RechargeTaskForm> rechargeTaskFormList = new ArrayList<RechargeTaskForm>();
		for (Task task : continueTaskList) {
			RechargeTaskForm rechargeTaskForm = (RechargeTaskForm)this.getVariable(task.getProcessInstanceId(), "rechargeTaskForm");
			rechargeTaskForm.setTaskId(task.getId());
			rechargeTaskForm.setProcessId(task.getProcessInstanceId());
			rechargeTaskFormList.add(rechargeTaskForm);
		}
		return rechargeTaskFormList;
	}
	
	public void completeAndClaimNextTask(boolean received, String taskId, String processId, String userId) {
		Map<String, Object> variables = new HashMap<String, Object>();
	    variables.put("received", received);
		taskService.complete(taskId, variables);
		
		logger.info("认领下一步任务");
		Task nextTask = taskService.createTaskQuery().processInstanceId(processId).singleResult();
		taskService.claim(nextTask.getId(), userId);
	}
}
