/**
 * 
 */
package web.activiti.form;

import java.io.Serializable;

import com.lehecai.admin.web.activiti.entity.GiftCardsTask;

/**
 * @author chirowong
 *
 */
public class GiftCardsTaskForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -778947275448176702L;
	private GiftCardsTask giftCardsTask;
	
	private String taskId;
	private String processId;
	private String code;//用于处理工单冲突
	public GiftCardsTask getGiftCardsTask() {
		return giftCardsTask;
	}
	public void setGiftCardsTask(GiftCardsTask giftCardsTask) {
		this.giftCardsTask = giftCardsTask;
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
