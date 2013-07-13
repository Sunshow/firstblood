/**
 * 
 */
package web.activiti.task.giftrewards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import com.lehecai.admin.web.activiti.form.GiftRewardsTaskForm;
import com.lehecai.admin.web.activiti.task.CommonProcessTask;

/**
 * @author chirowong
 *
 */
public class CooHandleGiftRewardsTask extends CommonProcessTask {
	
	public static final String GIFT_REWARDS_PROCESS = "giftRewardsProcess";
	public static final String COO_HANDLE_GIFT_REWARDS_TASK = "cooHandleGiftRewardsTask";
	
	public List<GiftRewardsTaskForm> listByRoleOrUser(String roleId, String userId) {
		if (StringUtils.isEmpty(roleId)) {
			logger.error("处理彩金增送工单异常：按角色查询任务列表时，角色id为空");
			return null;
		}
		if (StringUtils.isEmpty(userId)) {
			logger.error("处理彩金增送工单异常：按用户查询任务列表时，用户id为空");
			return null;
		}
		logger.info("处理彩金增送工单：查询roleId={}角色的任务列表", roleId);
		List<Task> allTaskList = taskService.createTaskQuery().processDefinitionKey(GIFT_REWARDS_PROCESS).taskDefinitionKey(COO_HANDLE_GIFT_REWARDS_TASK).taskCandidateGroup(roleId).orderByTaskCreateTime().desc().list();
		if (allTaskList == null || allTaskList.size() == 0) {
			logger.info("处理彩金增送工单：查询userID={}用户的任务列表", userId);
			allTaskList = taskService.createTaskQuery().processDefinitionKey(GIFT_REWARDS_PROCESS).taskDefinitionKey(COO_HANDLE_GIFT_REWARDS_TASK).taskCandidateUser(userId).orderByTaskCreateTime().desc().list();
		}
		List<GiftRewardsTaskForm> giftRewardsTaskFormList = new ArrayList<GiftRewardsTaskForm>();
		for (Task task : allTaskList) {
			GiftRewardsTaskForm giftRewardsTaskForm = (GiftRewardsTaskForm)this.getVariable(task.getProcessInstanceId(), "giftRewardsTaskForm");
			giftRewardsTaskForm.setTaskId(task.getId());
			giftRewardsTaskForm.setProcessId(task.getProcessInstanceId());
			giftRewardsTaskFormList.add(giftRewardsTaskForm);
		}
		return giftRewardsTaskFormList;
	}
	
	public void agreeTask(boolean enough, String taskId,Long opUserId){
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("agree", true);
		variables.put("enough", enough);
		variables.put("taskName", COO_HANDLE_GIFT_REWARDS_TASK);
		variables.put("processName", GIFT_REWARDS_PROCESS);
		variables.put("opUserId", opUserId);
		this.complete(taskId, variables);
	}
	
	public void disAgreeTask(String taskId){
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("agree", false);
		this.complete(taskId, variables);
	}
	
	
	public List<GiftRewardsTaskForm> listByAssignee(String assignee) {
		if (StringUtils.isEmpty(assignee)) {
			logger.error("需要判断到帐充值工单列表异常：按用户查询任务列表时，assignee为空");
			return null;
		}
		logger.info("查询到帐充值工单列表失败：查询userId={}用户的任务列表", assignee);
		List<Task> approvedTaskList = taskService.createTaskQuery().processDefinitionKey(GIFT_REWARDS_PROCESS).taskDefinitionKey(COO_HANDLE_GIFT_REWARDS_TASK).taskAssignee(assignee).list();
		List<GiftRewardsTaskForm> giftRewardsTaskFormList = new ArrayList<GiftRewardsTaskForm>();
		for (Task task : approvedTaskList) {
			GiftRewardsTaskForm GiftRewardsTaskForm = (GiftRewardsTaskForm)this.getVariable(task.getProcessInstanceId(), "giftRewardsTaskForm");
			GiftRewardsTaskForm.setTaskId(task.getId());
			GiftRewardsTaskForm.setProcessId(task.getProcessInstanceId());
			giftRewardsTaskFormList.add(GiftRewardsTaskForm);
		}
		return giftRewardsTaskFormList;
	}
}
