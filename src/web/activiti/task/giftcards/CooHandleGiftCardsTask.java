/**
 * 
 */
package web.activiti.task.giftcards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import com.lehecai.admin.web.activiti.form.GiftCardsTaskForm;
import com.lehecai.admin.web.activiti.task.CommonProcessTask;

/**
 * @author chirowong
 *
 */
public class CooHandleGiftCardsTask extends CommonProcessTask {
	
	public static final String GIFT_CARDS_PROCESS = "giftCardsProcess";
	public static final String COO_HANDLE_GIFT_CARDS_TASK = "cooHandleGiftCardsTask";
	
	public List<GiftCardsTaskForm> listByRoleOrUser(String roleId, String userId) {
		if (StringUtils.isEmpty(roleId)) {
			logger.error("处理彩金卡申请工单异常：按角色查询任务列表时，角色id为空");
			return null;
		}
		if (StringUtils.isEmpty(userId)) {
			logger.error("处理彩金卡申请工单异常：按用户查询任务列表时，用户id为空");
			return null;
		}
		logger.info("处理彩金卡申请工单：查询roleId={}角色的任务列表", roleId);
		List<Task> allTaskList = taskService.createTaskQuery().processDefinitionKey(GIFT_CARDS_PROCESS).taskDefinitionKey(COO_HANDLE_GIFT_CARDS_TASK).taskCandidateGroup(roleId).orderByTaskCreateTime().desc().list();
		if (allTaskList == null || allTaskList.size() == 0) {
			logger.info("处理彩金卡申请工单：查询userID={}用户的任务列表", userId);
			allTaskList = taskService.createTaskQuery().processDefinitionKey(GIFT_CARDS_PROCESS).taskDefinitionKey(COO_HANDLE_GIFT_CARDS_TASK).taskCandidateUser(userId).orderByTaskCreateTime().desc().list();
		}
		List<GiftCardsTaskForm> giftCardsTaskFormList = new ArrayList<GiftCardsTaskForm>();
		for (Task task : allTaskList) {
			GiftCardsTaskForm giftCardsTaskForm = (GiftCardsTaskForm)this.getVariable(task.getProcessInstanceId(), "giftCardsTaskForm");
			giftCardsTaskForm.setTaskId(task.getId());
			giftCardsTaskForm.setProcessId(task.getProcessInstanceId());
			giftCardsTaskFormList.add(giftCardsTaskForm);
		}
		return giftCardsTaskFormList;
	}
	
	public void agreeTask(boolean enough, String taskId){
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("agree", true);
		variables.put("enough", enough);
		this.complete(taskId, variables);
	}
	
	public void disAgreeTask(String taskId){
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("agree", false);
		this.complete(taskId, variables);
	}
	
	public List<GiftCardsTaskForm> listByAssignee(String assignee) {
		if (StringUtils.isEmpty(assignee)) {
			logger.error("需要判断到帐充值工单列表异常：按用户查询任务列表时，assignee为空");
			return null;
		}
		logger.info("查询到帐充值工单列表失败：查询userId={}用户的任务列表", assignee);
		List<Task> approvedTaskList = taskService.createTaskQuery().processDefinitionKey(GIFT_CARDS_PROCESS).taskDefinitionKey(COO_HANDLE_GIFT_CARDS_TASK).taskAssignee(assignee).list();
		List<GiftCardsTaskForm> giftCardsTaskFormList = new ArrayList<GiftCardsTaskForm>();
		for (Task task : approvedTaskList) {
			GiftCardsTaskForm GiftCardsTaskForm = (GiftCardsTaskForm)this.getVariable(task.getProcessInstanceId(), "giftCardsTaskForm");
			GiftCardsTaskForm.setTaskId(task.getId());
			GiftCardsTaskForm.setProcessId(task.getProcessInstanceId());
			giftCardsTaskFormList.add(GiftCardsTaskForm);
		}
		return giftCardsTaskFormList;
	}
}
