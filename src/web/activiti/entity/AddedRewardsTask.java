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
@Table(name = "TASK_ADDED_REWARDS")
public class AddedRewardsTask implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
	
	@Column(name = "PLAN_ID")
	private String planId;
	
	@Column(name = "LOTTERY_TYPE")
	private Integer lotteryType;
	
	@Column(name = "LOTTERY_NAME")
	private String lotteryName;
	
	@Column(name = "PHASE_TYPE")
	private Integer phaseType;	//彩期类型
	
	@Column(name = "PHASE")
	private	String phase;			//彩期编号
	
	@Column(name = "UID")
	private Long uid;          //方案发起人编码
	
	@Column(name = "USERNAME")
	private String username;   //方案发起人
	
	@Column(name = "PLAN_CREATED_TIME")
	private Date planCreatedTime;		//方案创建时间
	
	@Column(name = "PLAN_STATUS")
	private Integer planStatus;  //方案状态
	
	@Column(name = "RESULT_STATUS")
	private Integer resultStatus;	//结果状态，是否已中奖等
	
	@Column(name = "RESULT_STATUS_NAME")
	private String resultStatusName;	//结果状态，是否已中奖等
	
	@Column(name = "AMOUNT")
	private Long amount;				//方案金额
	
	@Column(name = "PRETAX_PRIZE")
	private Double pretaxPrize;		//税前奖金
	
	@Column(name = "POSTTAX_PRIZE")
	private Double posttaxPrize;	//税后奖金
    
    //发起人
    @Column(name = "INITIATOR")
    private String initiator;
    
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

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public Integer getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(Integer lotteryType) {
		this.lotteryType = lotteryType;
	}

	public Integer getPhaseType() {
		return phaseType;
	}

	public void setPhaseType(Integer phaseType) {
		this.phaseType = phaseType;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getPlanCreatedTime() {
		return planCreatedTime;
	}

	public void setPlanCreatedTime(Date planCreatedTime) {
		this.planCreatedTime = planCreatedTime;
	}

	public Integer getPlanStatus() {
		return planStatus;
	}

	public void setPlanStatus(Integer planStatus) {
		this.planStatus = planStatus;
	}

	public Integer getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(Integer resultStatus) {
		this.resultStatus = resultStatus;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Double getPretaxPrize() {
		return pretaxPrize;
	}

	public void setPretaxPrize(Double pretaxPrize) {
		this.pretaxPrize = pretaxPrize;
	}

	public Double getPosttaxPrize() {
		return posttaxPrize;
	}

	public void setPosttaxPrize(Double posttaxPrize) {
		this.posttaxPrize = posttaxPrize;
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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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

	public String getLotteryName() {
		return lotteryName;
	}

	public void setLotteryName(String lotteryName) {
		this.lotteryName = lotteryName;
	}

	public String getResultStatusName() {
		return resultStatusName;
	}

	public void setResultStatusName(String resultStatusName) {
		this.resultStatusName = resultStatusName;
	}
	
}
