/**
 * 
 */
package web.activiti.delegate.recharge;

import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.activiti.delegate.AbstractDelegate;
import com.lehecai.admin.web.activiti.entity.RechargeTask;
import com.lehecai.admin.web.activiti.form.RechargeTaskForm;
import com.lehecai.admin.web.activiti.service.RechargeTaskService;

/**
 * @author qatang
 *
 */
public class CreateRechargeTaskDelegate extends AbstractDelegate {
	@Autowired
	private RechargeTaskService rechargeTaskService;
	
	@Override
	protected void doExecution(DelegateExecution execution) throws Exception {
		RechargeTaskForm rechargeTaskForm = (RechargeTaskForm)execution.getVariable("rechargeTaskForm");
		
		RechargeTask task = rechargeTaskForm.getRechargeTask();
		task.setCreatedTime(new Date());
		//存储processId目的为history查询能够对应数据
		task.setProcessId(execution.getProcessInstanceId());
		
		rechargeTaskService.create(task);
		execution.setVariable("rechargeTaskForm", rechargeTaskForm);
	}
}
