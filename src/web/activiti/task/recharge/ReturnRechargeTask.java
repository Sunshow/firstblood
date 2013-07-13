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
public class ReturnRechargeTask extends CommonProcessTask {
	
	public List<RechargeTaskForm> listByAssignee(String assignee) {
		if (StringUtils.isEmpty(assignee)) {
			logger.error("处理汇款充值工单异常：按用户查询任务列表时，assignee为空");
			return null;
		}
		logger.info("退回汇款充值工单：查询userId={}用户的任务列表", assignee);
		List<Task> returnTaskList = taskService.createTaskQuery().processDefinitionKey("rechargeProcess").taskDefinitionKey("returnRechargeTask").taskAssignee(assignee).list();
		List<RechargeTaskForm> rechargeTaskFormList = new ArrayList<RechargeTaskForm>();
		for (Task task : returnTaskList) {
			RechargeTaskForm rechargeTaskForm = (RechargeTaskForm)this.getVariable(task.getProcessInstanceId(), "rechargeTaskForm");
			rechargeTaskForm.setTaskId(task.getId());
			rechargeTaskForm.setProcessId(task.getProcessInstanceId());
			rechargeTaskFormList.add(rechargeTaskForm);
		}
		return rechargeTaskFormList;
	}
}
