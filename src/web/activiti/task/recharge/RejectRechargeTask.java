/**
 * 
 */
package web.activiti.task.recharge;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import com.lehecai.admin.web.activiti.form.RechargeTaskForm;
import com.lehecai.admin.web.activiti.task.CommonProcessTask;

/**
 * @author qatang
 *
 */
public class RejectRechargeTask extends CommonProcessTask {
	
	public List<RechargeTaskForm> listByAssignee(String assignee) {
		if (StringUtils.isEmpty(assignee)) {
			logger.error("需要判断到帐充值工单列表异常：按用户查询任务列表时，assignee为空");
			return null;
		}
		logger.info("查询未到帐充值工单列表失败：查询userId={}用户的任务列表", assignee);
		List<Task> rejectedTaskList = taskService.createTaskQuery().processDefinitionKey("rechargeProcess").taskDefinitionKey("rejectRechargeTask").taskAssignee(assignee).list();
		List<RechargeTaskForm> rechargeTaskFormList = new ArrayList<RechargeTaskForm>();
		for (Task task : rejectedTaskList) {
			RechargeTaskForm rechargeTaskForm = (RechargeTaskForm)this.getVariable(task.getProcessInstanceId(), "rechargeTaskForm");
			rechargeTaskForm.setTaskId(task.getId());
			rechargeTaskForm.setProcessId(task.getProcessInstanceId());
			rechargeTaskFormList.add(rechargeTaskForm);
		}
		return rechargeTaskFormList;
	}
	
}
