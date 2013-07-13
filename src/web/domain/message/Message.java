/**
 * 
 */
package web.domain.message;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chirowong
 * 短消息主体类
 */
public class Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5622850171478051816L;
	private Long messageID;
	private String messageSender;//发送人
	private String messageTitle;//主题
	private String messageContent;//内容
	private String messageFlag;//标记
	private Date messageTime;//发送时间
	private String messageType;//类型
	
	public Long getMessageID() {
		return messageID;
	}
	public void setMessageID(Long messageID) {
		this.messageID = messageID;
	}
	public String getMessageSender() {
		return messageSender;
	}
	public void setMessageSender(String messageSender) {
		this.messageSender = messageSender;
	}
	public String getMessageTitle() {
		return messageTitle;
	}
	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}
	public String getMessageContent() {
		return messageContent;
	}
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	public String getMessageFlag() {
		return messageFlag;
	}
	public void setMessageFlag(String messageFlag) {
		this.messageFlag = messageFlag;
	}
	public Date getMessageTime() {
		return messageTime;
	}
	public void setMessageTime(Date messageTime) {
		this.messageTime = messageTime;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
}
