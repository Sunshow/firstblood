package web.domain.weibo;

import java.io.Serializable;
import java.util.Date;

import com.lehecai.core.lottery.WeiboType;

public class TokenInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String uid;
	private String token;
	private String tokenSecret;
	private WeiboType weiboType;
	private boolean valid;
	private Date createTime;
	private Date updateTime;
	private Long expiresIn;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getTokenSecret() {
		return tokenSecret;
	}
	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}
	public WeiboType getWeiboType() {
		return weiboType;
	}
	public void setWeiboType(WeiboType weiboType) {
		this.weiboType = weiboType;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public Date getCreateTime() {
		if (createTime == null) {
			createTime = new Date();
		}
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Long getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}
	
}
