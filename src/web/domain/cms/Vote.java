package web.domain.cms;
import java.io.Serializable;
import java.util.Date;

import com.lehecai.admin.web.enums.VoteType;


public class Vote implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7523487116027920700L;
	private Long id;
	private String title;
	private VoteType voteType;
	private Integer voteLimitAmount;
	private Date createTime;
	private Date beginTime;
	private Date endTime;
	private boolean valid;
	private String memo;
	
	public Date getCreateTime() {
		if(createTime == null){
			createTime = new Date();
		}
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public VoteType getVoteType() {
		return voteType;
	}
	public void setVoteType(VoteType voteType) {
		this.voteType = voteType;
	}
	public Integer getVoteLimitAmount() {
		return voteLimitAmount;
	}
	public void setVoteLimitAmount(Integer voteLimitAmount) {
		this.voteLimitAmount = voteLimitAmount;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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
}
