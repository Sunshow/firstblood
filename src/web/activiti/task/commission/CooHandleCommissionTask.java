package web.activiti.task.commission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import com.lehecai.admin.web.activiti.form.CommissionTaskForm;
import com.lehecai.admin.web.activiti.task.CommonProcessTask;

public class CooHandleCommissionTask extends CommonProcessTask {
	
	public static final String COMMISSION_PROCESS = "commissionTaskProcess";
	public static final String COO_HANDLE_COMMISSION_TASK = "cooHandleCommissionTask";
	
	public List<CommissionTaskForm> listByRoleOrUser(String roleId, String userId) {
		if (StringUtils.isEmpty(roleId)) {
			logger.error("处理佣金派发工单异常：按角色查询任务列表时，角色id为空");
			return null;
		}
		if (StringUtils.isEmpty(userId)) {
			logger.error("处理佣金派发工单异常：按用户查询任务列表时，用户id为空");
			return null;
		}
		logger.info("处理佣金派发工单：查询roleId={}角色的任务列表", roleId);
		List<Task> allTaskList = taskService.createTaskQuery().processDefinitionKey(COMMISSION_PROCESS).taskDefinitionKey(COO_HANDLE_COMMISSION_TASK).taskCandidateGroup(roleId).orderByTaskCreateTime().desc().list();
		if (allTaskList == null || allTaskList.size() == 0) {
			logger.info("处理佣金派发工单：查询userID={}用户的任务列表", userId);
			allTaskList = taskService.createTaskQuery().processDefinitionKey(COMMISSION_PROCESS).taskDefinitionKey(COO_HANDLE_COMMISSION_TASK).taskCandidateUser(userId).orderByTaskCreateTime().desc().list();
		}
		List<CommissionTaskForm> commissionTaskFormList = new ArrayList<CommissionTaskForm>();
		for (Task task : allTaskList) {
			CommissionTaskForm commissionTaskForm = (CommissionTaskForm)this.getVariable(task.getProcessInstanceId(), "commissionTaskForm");
			commissionTaskForm.setTaskId(task.getId());
			commissionTaskForm.setProcessId(task.getProcessInstanceId());
			commissionTaskFormList.add(commissionTaskForm);
		}
		return commissionTaskFormList;
	}
	
	public void agreeTask(boolean enough, String taskId,Long opUserId){
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("agree", true);
		variables.put("enough", enough);
		variables.put("taskName", COO_HANDLE_COMMISSION_TASK);
		variables.put("processName", COMMISSION_PROCESS);
		variables.put("opUserId", opUserId);
		this.complete(taskId, variables);
	}
	
	public void disAgreeTask(String taskId){
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("agree", false);
		this.complete(taskId, variables);
	}
	
	public List<CommissionTaskForm> listByAssignee(String assignee) {
		if (StringUtils.isEmpty(assignee)) {
			logger.error("需要判断到帐充值工单列表异常：按用户查询任务列表时，assignee为空");
			return null;
		}
		logger.info("查询到帐充值工单列表失败：查询userId={}用户的任务列表", assignee);
		List<Task> approvedTaskList = taskService.createTaskQuery().processDefinitionKey(COMMISSION_PROCESS).taskDefinitionKey(COO_HANDLE_COMMISSION_TASK).taskAssignee(assignee).list();
		List<CommissionTaskForm> formList = new ArrayList<CommissionTaskForm>();
		for (Task task : approvedTaskList) {
			CommissionTaskForm form = (CommissionTaskForm)this.getVariable(task.getProcessInstanceId(), "commissionTaskForm");
			form.setTaskId(task.getId());
			form.setProcessId(task.getProcessInstanceId());
			formList.add(form);
		}
		return formList;
	}

}
