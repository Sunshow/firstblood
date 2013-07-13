/**
 * 
 */
package web.activiti.task.rewards;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import com.lehecai.admin.web.activiti.form.AddedRewardsTaskForm;
import com.lehecai.admin.web.activiti.task.CommonProcessTask;

/**
 * @author qatang
 *
 */
public class ApproveAddedRewardsTask extends CommonProcessTask {
	
	public List<AddedRewardsTaskForm> listByAssignee(String assignee) {
		if (StringUtils.isEmpty(assignee)) {
			logger.error("处理补充派奖工单异常：按用户查询任务列表时，assignee为空");
			return null;
		}
		logger.info("处理补充派奖工单：查询userId={}用户的任务列表", assignee);
		List<Task> userTaskList = taskService.createTaskQuery().processDefinitionKey("addedRewardsProcess").taskDefinitionKey("approveAddedRewardsTask").taskAssignee(assignee).list();
		List<AddedRewardsTaskForm> addedRewardsTaskFormList = new ArrayList<AddedRewardsTaskForm>();
		for (Task task : userTaskList) {
			AddedRewardsTaskForm addedRewardsTaskForm = (AddedRewardsTaskForm)this.getVariable(task.getProcessInstanceId(), "addedRewardsTaskForm");
			addedRewardsTaskForm.setTaskId(task.getId());
			addedRewardsTaskForm.setProcessId(task.getProcessInstanceId());
			addedRewardsTaskFormList.add(addedRewardsTaskForm);
		}
		return addedRewardsTaskFormList;
	}
	
}
