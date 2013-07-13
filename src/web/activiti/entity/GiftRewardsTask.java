/**
 * 
 */
package web.activiti.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author chirowong
 *
 */
@Entity
@Table(name = "TASK_GIFTREWARDS")
public class GiftRewardsTask implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
	
	//赠予用户名
    @Column(name = "USER_NAME")
    private String username;
    
    //赠予金额
    @Column(name = "AMOUNT")
    private Double amount;
    
    //赠予原因
    @Column(name = "REASON", length=2000)
    private String reason;
    
    //发起人
    @Column(name = "INITIATOR")
    private String initiator;
    
    //处理人员
    @Column(name = "HANDLE_USER")
    private Long handleUser;
    
    @Column(name = "PROCESS_ID")
    private String processId;
    
    @Column(name = "memo")
    private String memo;
    
    @Column(name = "CREATED_TIME", updatable = false, nullable = false)
    private Date createdTime;
    
    //账户类型
    @Column(name = "WALLET_TYPE")
    private Integer walletType;
    
    //派送结果信息
    @Column(name = "RESULT")
    private String result;
    
    @Column(name = "FINISHED")
    private boolean finished;
    
    //工单类型 @see GiftRewardsType
    @Column(name = "GIFT_REWARDS_TYPE")
    private Integer giftRewardsType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public Long getHandleUser() {
		return handleUser;
	}

	public void setHandleUser(Long handleUser) {
		this.handleUser = handleUser;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getWalletType() {
		return walletType;
	}

	public void setWalletType(Integer walletType) {
		this.walletType = walletType;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public Integer getGiftRewardsType() {
		return giftRewardsType;
	}

	public void setGiftRewardsType(Integer giftRewardsType) {
		this.giftRewardsType = giftRewardsType;
	}
}
