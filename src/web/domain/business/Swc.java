package web.domain.business;

import java.io.Serializable;
import java.util.Date;

import com.lehecai.core.EnabledStatus;

/**
 * 敏感词实体
 * @author yanweijie
 *
 */
public class Swc implements Serializable{
	private static final long serialVersionUID = 5219527163468935094L;
	
	public static final String SET_WORD = "word";
	public static final String SET_FLAG = "flag";
	
	private Long id;				//敏感词编号
    private String name;			//敏感词
	private Date createTime;		//创建时间
	private Date updateTime;		//修改时间
	private EnabledStatus status;	//状态
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreateTime() {
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
	public EnabledStatus getStatus() {
		return status;
	}
	public void setStatus(EnabledStatus status) {
		this.status = status;
	}
}
