package web.domain.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.enums.StatusType;

public class Sms implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4799644412392568082L;
	
	private Long id;
	private String smsFrom;
	private String smsTo;//接收人，多个接收人用英文逗号隔开:13810122553，13810122554，13810122555
	private String content;
	private Date createTime;
	private Date sendTime;
	private StatusType status;
	private boolean valid;
	private String memo;
	private List<String> phoneNos;//添加多个接收人时参数，只为接受数据
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getSmsFrom() {
		return smsFrom;
	}
	public void setSmsFrom(String smsFrom) {
		this.smsFrom = smsFrom;
	}
	public String getSmsTo() {
		return smsTo;
	}
	public void setSmsTo(String smsTo) {
		this.smsTo = smsTo;
	}
	public List<String> getPhoneNos() {
		return phoneNos;
	}
	public void setPhoneNos(List<String> phoneNos) {
		this.phoneNos = phoneNos;
	}
}
