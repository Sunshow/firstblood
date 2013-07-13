/**
 * 
 */
package web.activiti.task.rewards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import com.lehecai.admin.web.activiti.form.AddedRewardsTaskForm;
import com.lehecai.admin.web.activiti.task.CommonProcessTask;

/**
 * @author qatang
 *
 */
public class PayAddedRewardsTask extends CommonProcessTask {
	
	public List<AddedRewardsTaskForm> listByRoleOrUser(String roleId, String userId) {
		if (StringUtils.isEmpty(roleId)) {
			logger.error("处理补充派奖工单异常：按角色查询任务列表时，角色id为空");
			return null;
		}
		if (StringUtils.isEmpty(userId)) {
			logger.error("处理补充派奖工单异常：按用户查询任务列表时，用户id为空");
			return null;
		}
		logger.info("处理补充派奖工单：查询roleId={}角色的任务列表", roleId);
		List<Task> allTaskList = taskService.createTaskQuery().processDefinitionKey("addedRewardsProcess").taskDefinitionKey("payAddedRewardsTask").taskCandidateGroup(roleId).orderByTaskCreateTime().desc().list();
		if (allTaskList == null || allTaskList.size() == 0) {
			logger.info("处理补充派奖工单：查询userID={}用户的任务列表", userId);
			allTaskList = taskService.createTaskQuery().processDefinitionKey("addedRewardsProcess").taskDefinitionKey("payAddedRewardsTask").taskCandidateUser(userId).orderByTaskCreateTime().desc().list();
		}
		List<AddedRewardsTaskForm> addedRewardsTaskFormList = new ArrayList<AddedRewardsTaskForm>();
		for (Task task : allTaskList) {
			AddedRewardsTaskForm addedRewardsTaskForm = (AddedRewardsTaskForm) this.getVariable(task.getProcessInstanceId(), "addedRewardsTaskForm");
			addedRewardsTaskForm.setTaskId(task.getId());
			addedRewardsTaskForm.setProcessId(task.getProcessInstanceId());
			addedRewardsTaskFormList.add(addedRewardsTaskForm);
		}
		return addedRewardsTaskFormList;
	}
	
	public List<AddedRewardsTaskForm> listByAssignee(String assignee) {
		if (StringUtils.isEmpty(assignee)) {
			logger.error("处理补充派奖工单异常：按用户查询任务列表时，assignee为空");
			return null;
		}
		logger.info("处理补充派奖工单：查询userId={}用户的任务列表", assignee);
		List<Task> userTaskList = taskService.createTaskQuery().processDefinitionKey("addedRewardsProcess").taskDefinitionKey("payAddedRewardsTask").taskAssignee(assignee).list();
		List<AddedRewardsTaskForm> addedRewardsTaskFormList = new ArrayList<AddedRewardsTaskForm>();
		for (Task task : userTaskList) {
			AddedRewardsTaskForm addedRewardsTaskForm = (AddedRewardsTaskForm)this.getVariable(task.getProcessInstanceId(), "addedRewardsTaskForm");
			addedRewardsTaskForm.setTaskId(task.getId());
			addedRewardsTaskForm.setProcessId(task.getProcessInstanceId());
			addedRewardsTaskFormList.add(addedRewardsTaskForm);
		}
		return addedRewardsTaskFormList;
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
