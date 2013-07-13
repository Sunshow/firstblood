package web.multiconfirm;

import java.util.Date;

public class MulticonfirmTask {
	private Long id;
	private Long configId;
	private String taskKey;
	private Integer configConfirmCount;
	private MulticonfirmTaskStatus taskStatus;
	private Date createTime;
	private Date timeoutTime;
	private String result;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getConfigId() {
		return configId;
	}
	public void setConfigId(Long configId) {
		this.configId = configId;
	}
	public String getTaskKey() {
		return taskKey;
	}
	public void setTaskKey(String taskKey) {
		this.taskKey = taskKey;
	}
	public Integer getConfigConfirmCount() {
		return configConfirmCount;
	}
	public void setConfigConfirmCount(Integer configConfirmCount) {
		this.configConfirmCount = configConfirmCount;
	}
	public MulticonfirmTaskStatus getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(MulticonfirmTaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}
	public Date getCreateTime() {
		if (createTime == null) {
			createTime = new Date();
		}
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getTimeoutTime() {
		return timeoutTime;
	}
	public void setTimeoutTime(Date timeoutTime) {
		this.timeoutTime = timeoutTime;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
}
