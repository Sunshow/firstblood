/**
 * 
 */
package web.domain.message;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chirowong
 * 短消息接收类
 */
public class MessageReceiver implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9105158413824096737L;
	public static final String MESSAGE_NO_READ = "0";
	public static final String MESSAGE_READED = "1";
	private Long messageReceiverID;//消息编码
	private Long messageID;//消息编码
	private Long userID;//消息接收人
	private String flag;//阅读标记 0-未阅读 1-已阅读
	private Date readTime;//阅读时间

	public Long getMessageReceiverID() {
		return messageReceiverID;
	}
	public void setMessageReceiverID(Long messageReceiverID) {
		this.messageReceiverID = messageReceiverID;
	}
	public Long getMessageID() {
		return messageID;
	}
	public void setMessageID(Long messageID) {
		this.messageID = messageID;
	}
	public Long getUserID() {
		return userID;
	}
	public void setUserID(Long userID) {
		this.userID = userID;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public Date getReadTime() {
		return readTime;
	}
	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}
}
