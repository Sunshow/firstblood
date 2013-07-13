package web.domain.process;
import java.io.Serializable;

import com.lehecai.admin.web.enums.TaskType;

public class WorkTask implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String taskId;
	private String taskName;
	private String processId;
	private TaskType taskType; 
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public TaskType getTaskType() {
		return taskType;
	}
	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}
}
