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
import javax.persistence.Transient;

import com.lehecai.admin.web.activiti.constant.ProcessStatusType;

/**
 * @author chirowong
 *
 */
@Entity
@Table(name = "TASK_GIFTCARDS")
public class GiftCardsTask implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    
    //彩金卡面额
    @Column(name = "CARD_MONEY")
    private Double cardMoney;
    
    //彩金卡数量
    @Column(name = "CARD_AMOUNT")
    private Integer cardAmount;
    
    //彩金卡类型
    @Column(name = "COUPON_TYPE")
    private Integer couponType;
    
    //活动编码
    @Column(name = "EVENT_ID")
    private Integer eventId;
    
    //用户编码
    @Column(name = "USER_ID")
    private Long userId;
    
    //使用期限
    @Column(name = "LIVE_TIME")
    private String liveTime;
    
    //活动内容
    @Column(name = "ACTIVITY_CONTENT", length=2000)
    private String activityContent;
    
    //申请理由
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
    
    @Column(name = "CREATED_TIME", updatable = false, nullable = false)
    private Date createdTime;
    
    //拒绝理由
    @Column(name = "MEMO")
    private String memo;
    
    //处理状态
    @Column(name = "STATUS")
    private Integer status;
    
    @Transient
    private ProcessStatusType statusType;
    
    //是否完成标识
    @Transient
    private boolean completeFlag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getCardMoney() {
		return cardMoney;
	}

	public void setCardMoney(Double cardMoney) {
		this.cardMoney = cardMoney;
	}

	public Integer getCardAmount() {
		return cardAmount;
	}

	public void setCardAmount(Integer cardAmount) {
		this.cardAmount = cardAmount;
	}

	public String getLiveTime() {
		return liveTime;
	}

	public void setLiveTime(String liveTime) {
		this.liveTime = liveTime;
	}

	public String getActivityContent() {
		return activityContent;
	}

	public void setActivityContent(String activityContent) {
		this.activityContent = activityContent;
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

	public Integer getCouponType() {
		return couponType;
	}

	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
	}

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatusType(ProcessStatusType statusType) {
		this.statusType = statusType;
	}

	public ProcessStatusType getStatusType() {
		return statusType;
	}

	public void setCompleteFlag(boolean completeFlag) {
		this.completeFlag = completeFlag;
	}

	public boolean isCompleteFlag() {
		return completeFlag;
	}
}
