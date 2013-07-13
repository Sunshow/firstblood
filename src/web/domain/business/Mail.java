package web.domain.business;

import java.io.Serializable;
import java.util.Date;

import com.lehecai.admin.web.enums.StatusType;
import com.lehecai.admin.web.enums.TextType;

public class Mail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4799644412392568082L;
	
	private Long id;
	private String mailFrom;
	private String mailTo;//收件人，多个收件人用英文逗号隔开:aaa@aaa.com,bbb@bbb.com,ccc@ccc.com
	private String subject;
	private TextType textType;//1.纯文本，2.富文本
	private String content;
	private Date createTime;
	private Date sendTime;
	private StatusType status;//0.正在发送，1.成功，2失败
	private boolean valid;
	private String memo;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMailFrom() {
		return mailFrom;
	}
	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}
	public String getMailTo() {
		return mailTo;
	}
	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public TextType getTextType() {
		return textType;
	}
	public void setTextType(TextType textType) {
		this.textType = textType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Date getCreateTime() {
		if(createTime == null){
			createTime = new Date();
		}
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public StatusType getStatus() {
		return status;
	}
	public void setStatus(StatusType status) {
		this.status = status;
	}
}
