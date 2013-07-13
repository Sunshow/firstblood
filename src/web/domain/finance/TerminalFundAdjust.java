/**
 * 
 */
package web.domain.finance;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chirowong
 * 款项调整
 */
public class TerminalFundAdjust implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1052759739015410680L;
	private Long id;//款项调整编码
	private Long userId;//创建人编码
	private Date createTime;//创建日期
	private Double amount;//调整金额
	private PayType payType;//赔偿方式
	private String reason;
	private Long terminalAccountCheckItemId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Double getAmount() {
		return amount;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public PayType getPayType() {
		return payType;
	}
	public void setPayType(PayType payType) {
		this.payType = payType;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Long getTerminalAccountCheckItemId() {
		return terminalAccountCheckItemId;
	}
	public void setTerminalAccountCheckItemId(Long terminalAccountCheckItemId) {
		this.terminalAccountCheckItemId = terminalAccountCheckItemId;
	}
}
