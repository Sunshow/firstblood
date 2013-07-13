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
 * @author qatang
 *
 */
@Entity
@Table(name = "TASK_RECHARGE")
public class RechargeTask implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
	
	//充值用户名
    @Column(name = "USER_NAME")
    private String username;
    
    //充值金额
    @Column(name = "AMOUNT")
    private Double amount;
    
    //真实姓名
    @Column(name = "REAL_NAME")
    private String realName;
    
    //用户银行
    @Column(name = "USER_BANK")
    private String userBank;
    
    //用户银行卡号
    @Column(name = "USER_CARD_NO")
    private String userCardNo;
    
    //汇款银行
    @Column(name = "RECHARGE_BANK")
    private String rechargeBank;
    
    //汇款银行
    @Column(name = "RECHARGE_BANK_ID")
    private Integer rechargeBankId;
    
    //汇款银行卡号
    @Column(name = "RECHARGE_CARD_NO")
    private String rechargeCardNo;
    
    //发起人
    @Column(name = "INITIATOR")
    private String initiator;
    
    //手续费
    @Column(name = "SERVICE_CHARGE")
    private Double serviceCharge;
    
    //处理人员
    @Column(name = "HANDLE_USER")
    private Long handleUser;
    
    //备注
    @Column(name = "MEMO", length=2000)
    private String memo;
    
    @Column(name = "PROCESS_ID")
    private String processId;
    
    @Column(name = "CREATED_TIME", updatable = false, nullable = false)
    private Date createdTime;

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

	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public Double getServiceCharge() {
		return serviceCharge;
	}

	public void setServiceCharge(Double serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	public Long getHandleUser() {
		return handleUser;
	}

	public void setHandleUser(Long handleUser) {
		this.handleUser = handleUser;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getUserBank() {
		return userBank;
	}

	public void setUserBank(String userBank) {
		this.userBank = userBank;
	}

	public String getUserCardNo() {
		return userCardNo;
	}

	public void setUserCardNo(String userCardNo) {
		this.userCardNo = userCardNo;
	}

	public String getRechargeBank() {
		return rechargeBank;
	}

	public void setRechargeBank(String rechargeBank) {
		this.rechargeBank = rechargeBank;
	}

	public String getRechargeCardNo() {
		return rechargeCardNo;
	}

	public void setRechargeCardNo(String rechargeCardNo) {
		this.rechargeCardNo = rechargeCardNo;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Integer getRechargeBankId() {
		return rechargeBankId;
	}

	public void setRechargeBankId(Integer rechargeBankId) {
		this.rechargeBankId = rechargeBankId;
	}
}
