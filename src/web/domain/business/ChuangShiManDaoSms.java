package web.domain.business;

import java.io.Serializable;
import java.util.Date;

public class ChuangShiManDaoSms implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4799644412392568082L;
	
	private Long id;
	private String recvtel;
	private String sender;
	private String content;
	private Date recdate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRecvtel() {
		return recvtel;
	}
	public void setRecvtel(String recvtel) {
		this.recvtel = recvtel;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getRecdate() {
		return recdate;
	}
	public void setRecdate(Date recdate) {
		this.recdate = recdate;
	}
}
