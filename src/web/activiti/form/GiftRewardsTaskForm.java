/**
 * 
 */
package web.activiti.form;

import java.io.Serializable;

import com.lehecai.admin.web.activiti.entity.GiftRewardsTask;

/**
 * @author chirowong
 *
 */
public class GiftRewardsTaskForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -778947275448176702L;
	private GiftRewardsTask giftRewardsTask;
	
	private String taskId;
	private String processId;
	private String taskName;
	private String processName;
	private String code;//用于提示处理冲突
	
	public GiftRewardsTask getGiftRewardsTask() {
		return giftRewardsTask;
	}
	public void setGiftRewardsTask(GiftRewardsTask giftRewardsTask) {
		this.giftRewardsTask = giftRewardsTask;
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
	public void setCode(String code) {
		this.code = code;
	}
	public String getCode() {
		return code;
	}
}
