package web.activiti.form;

import java.io.Serializable;

import com.lehecai.admin.web.activiti.entity.CommissionTask;

public class CommissionTaskForm implements Serializable {
	private static final long serialVersionUID = -778947275448176702L;
	
	private CommissionTask commissionTask;
	
	private String taskId;
	private String processId;
	private String taskName;
	private String processName;
	private String userName;//用于重复提示
	public CommissionTask getCommissionTask() {
		return commissionTask;
	}
	public void setCommissionTask(CommissionTask commissionTask) {
		this.commissionTask = commissionTask;
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
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	
	

}
