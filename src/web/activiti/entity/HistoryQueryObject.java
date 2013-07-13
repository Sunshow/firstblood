/**
 * 
 */
package web.activiti.entity;

import java.util.Date;

/**
 * 历史数据查询对象
 * @author chirowong
 *
 */
public class HistoryQueryObject {
	private String taskType;//任务类型
	private Date taskStartTime;//任务开始时间
	private Date taskEndTime;//任务结束时间
	private String initiator;//工单发起人
	private Date createdTime;//创建时间
	private String processId;//流程编码
	private Long handleUser;//处理人员
	
	private String processTable;//数据表名
	private String processName;//流程名称
	
	private GiftCardsTask giftCardsTask;//彩金卡,用于储存查询信息
	
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public Date getTaskStartTime() {
		return taskStartTime;
	}
	public void setTaskStartTime(Date taskStartTime) {
		this.taskStartTime = taskStartTime;
	}
	public Date getTaskEndTime() {
		return taskEndTime;
	}
	public void setTaskEndTime(Date taskEndTime) {
		this.taskEndTime = taskEndTime;
	}
	public String getInitiator() {
		return initiator;
	}
	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public Long getHandleUser() {
		return handleUser;
	}
	public void setHandleUser(Long handleUser) {
		this.handleUser = handleUser;
	}
	public String getProcessTable() {
		return processTable;
	}
	public void setProcessTable(String processTable) {
		this.processTable = processTable;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public void setGiftCardsTask(GiftCardsTask giftCardsTask) {
		this.giftCardsTask = giftCardsTask;
	}
	public GiftCardsTask getGiftCardsTask() {
		return giftCardsTask;
	}
}
