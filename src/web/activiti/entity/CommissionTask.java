package web.activiti.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TASK_COMMISSION")
public class CommissionTask implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
	
	//赠予用户名
    @Column(name = "USER_NAME")
    private String username;
	
	@Column(name = "COMMISSION_AMOUNT")
	private Double commissionAmount;
	
	@Column(name = "COMMISSION_CHARGE")
	private Double commissionCharge;
	
	//说明
	@Column(name = "STATEMENT", length=2000)
	private String statement;
	
    //发起人
    @Column(name = "INITIATOR")
    private String initiator;
    
    //处理人员
    @Column(name = "HANDLE_USER")
    private Long handleUser;
	
    @Column(name = "CREATED_TIME", updatable = false, nullable = false)
    private Date createdTime;
	
	@Column(name = "COMMISSION_TIME_START")
	private Date commissionTimeStart;
	
	@Column(name = "COMMISSION_TIME_END")
	private Date commissionTimeEnd;
	
	@Column(name = "PROCESS_ID")
    private String processId;
	
	@Column(name = "MEMO")
    private String memo;

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

	public Double getCommissionAmount() {
		return commissionAmount;
	}

	public void setCommissionAmount(Double commissionAmount) {
		this.commissionAmount = commissionAmount;
	}

	public Double getCommissionCharge() {
		return commissionCharge;
	}

	public void setCommissionCharge(Double commissionCharge) {
		this.commissionCharge = commissionCharge;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
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

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getCommissionTimeStart() {
		return commissionTimeStart;
	}

	public void setCommissionTimeStart(Date commissionTimeStart) {
		this.commissionTimeStart = commissionTimeStart;
	}

	public Date getCommissionTimeEnd() {
		return commissionTimeEnd;
	}

	public void setCommissionTimeEnd(Date commissionTimeEnd) {
		this.commissionTimeEnd = commissionTimeEnd;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
}
