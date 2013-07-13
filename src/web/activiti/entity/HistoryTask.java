package web.activiti.entity;

import java.util.Date;
import java.util.Map;
/**
 * 封装流程任务查询对象
 * @author chirowong
 *
 */
public class HistoryTask {
	private String taskName;
	private String userName;
	private Date startTime;
	private Date endTime;
	private Map<String,Object> task;
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Map<String, Object> getTask() {
		return task;
	}
	public void setTask(Map<String, Object> task) {
		this.task = task;
	}
}
