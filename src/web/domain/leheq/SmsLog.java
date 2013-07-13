package web.domain.leheq;

import java.util.Date;

/**
 * 短信日志
 * @author yanweijie
 *
 */
public class SmsLog {
	private Integer id;		//流水id
	private String smsto;	//接收人
	private Date sendTime;	//发送时间
	private String content;	//发送内容
	private int result;		//发送结果
	private String sender;	//发送人
	private String memo;	//备注
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSmsto() {
		return smsto;
	}
	public void setSmsto(String smsto) {
		this.smsto = smsto;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
}
