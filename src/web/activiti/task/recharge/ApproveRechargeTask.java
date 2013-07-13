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
import com.lehecai.admin.web.domain.user.User;

/**
 * @author qatang
 * @author chirowong 增加同意、不同意方法
 *
 */
public class ApproveRechargeTask extends CommonProcessTask {
	
	public List<RechargeTaskForm> listByAssignee(String assignee) {
		if (StringUtils.isEmpty(assignee)) {
			logger.error("需要判断到帐充值工单列表异常：按用户查询任务列表时，assignee为空");
			return null;
		}
		logger.info("查询到帐充值工单列表失败：查询userId={}用户的任务列表", assignee);
		List<Task> approvedTaskList = taskService.createTaskQuery().processDefinitionKey("rechargeProcess").taskDefinitionKey("approveRechargeTask").taskAssignee(assignee).list();
		List<RechargeTaskForm> rechargeTaskFormList = new ArrayList<RechargeTaskForm>();
		for (Task task : approvedTaskList) {
			RechargeTaskForm rechargeTaskForm = (RechargeTaskForm)this.getVariable(task.getProcessInstanceId(), "rechargeTaskForm");
			rechargeTaskForm.setTaskId(task.getId());
			rechargeTaskForm.setProcessId(task.getProcessInstanceId());
			rechargeTaskFormList.add(rechargeTaskForm);
		}
		return rechargeTaskFormList;
	}
	
	public void agreeTask(String taskId,User opUser){
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("agree", true);
		variables.put("opUser", opUser);
		this.complete(taskId, variables);
	}
	
	public void disAgreeTask(String taskId){
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("agree", false);
		this.complete(taskId, variables);
	}
}
