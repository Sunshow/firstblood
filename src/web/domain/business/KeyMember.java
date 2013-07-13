package web.domain.business;

import java.io.Serializable;
import java.util.Date;

/**
 * 重点会员
 * @author yanweijie
 *
 */
public class KeyMember implements Serializable{
	private static final long serialVersionUID = 5219527163468935094L;
	
	private Long id;				//重点会员编号
	private Long uid;				//会员编号
	private String userName;		//会员用户名
	private Date registerTime;		//注册时间
	private Date lastLoginTime;		//最后登录时间
	private Date lastConsumeTime;	//最后消费时间
	private Date lastRechargeTime;	//最后充值时间
	private String memo;			//重点备注
	
	public KeyMember(){
		
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

	public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Date getLastConsumeTime() {
		return lastConsumeTime;
	}

	public void setLastConsumeTime(Date lastConsumeTime) {
		this.lastConsumeTime = lastConsumeTime;
	}

	public Date getLastRechargeTime() {
		return lastRechargeTime;
	}

	public void setLastRechargeTime(Date lastRechargeTime) {
		this.lastRechargeTime = lastRechargeTime;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
}
