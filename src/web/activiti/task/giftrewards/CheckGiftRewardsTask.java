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
public class CheckGiftRewardsTask extends CommonProcessTask {
	
	public static final String GIFT_REWARDS_PROCESS = "giftRewardsProcess";
	public static final String CHECK_GIFT_REWARDS_TASK = "checkGiftRewardsTask";
	
	public List<GiftRewardsTaskForm> listByRoleOrUser(String roleId, String userId) {
		if (StringUtils.isEmpty(roleId)) {
			logger.error("检查彩金增送工单异常：按角色查询任务列表时，角色id为空");
			return null;
		}
		if (StringUtils.isEmpty(userId)) {
			logger.error("检查彩金增送工单异常：按用户查询任务列表时，用户id为空");
			return null;
		}
		logger.info("检查彩金增送工单：查询roleId={}角色的任务列表", roleId);
		List<Task> allTaskList = taskService.createTaskQuery().processDefinitionKey(GIFT_REWARDS_PROCESS).taskDefinitionKey(CHECK_GIFT_REWARDS_TASK).taskCandidateGroup(roleId).orderByTaskCreateTime().desc().list();
		if (allTaskList == null || allTaskList.size() == 0) {
			logger.info("检查彩金增送工单：查询userID={}用户的任务列表", userId);
			allTaskList = taskService.createTaskQuery().processDefinitionKey(GIFT_REWARDS_PROCESS).taskDefinitionKey(CHECK_GIFT_REWARDS_TASK).taskCandidateUser(userId).orderByTaskCreateTime().desc().list();
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
	
	public void finishTask(String taskId){
		Map<String, Object> variables = new HashMap<String, Object>();
		this.complete(taskId, variables);
	}
}
