package web.domain.business;

import java.io.Serializable;

public class SmsMailMember implements Serializable{
	private static final long serialVersionUID = 5219527163468935094L;
	
	private Long id;				//短信邮件会员编号
	private Long uid;				//短信邮件会员会员编码
	private String userName;		//短信邮件会员用户名
	private Long groupId;			//短信邮件会员组编号
	
	public SmsMailMember(){
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
}
