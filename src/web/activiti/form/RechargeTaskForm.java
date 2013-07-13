/**
 * 
 */
package web.activiti.form;

import java.io.Serializable;

import com.lehecai.admin.web.activiti.entity.RechargeTask;

/**
 * @author qatang
 *
 */
public class RechargeTaskForm implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private RechargeTask rechargeTask;
	
	private String taskId;
	private String processId;
	private String code;//编码，用于提示

	public RechargeTask getRechargeTask() {
		if (rechargeTask == null) {
			rechargeTask = new RechargeTask();
		}
		return rechargeTask;
	}

	public void setRechargeTask(RechargeTask rechargeTask) {
		this.rechargeTask = rechargeTask;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
}
