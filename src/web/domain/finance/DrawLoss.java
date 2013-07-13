/**
 * 
 */
package web.domain.finance;

import java.io.Serializable;
import java.util.Date;

import com.lehecai.core.lottery.LotteryType;

/**
 * @author chirowong
 * 出票损失工单
 */
public class DrawLoss implements Serializable{

	private static final long serialVersionUID = 3539229939985207610L;
	private Long id;//对账单条目编码
	private LotteryType lotteryType;//彩种编码
	private int accountCheckType;//对账类型0-期，1-天
	private String amountCheckDate;//对账期数或者日期
	private double drawMoney;//出票金额
	private double drawPlanMoney;//出票方案金额
	private double drawLossMoney;//出票损失 = 出票金额 -出票方案金额
	private String memo;//备注
	private Long userId;//创建人编码
	private Date createTime;//创建日期
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LotteryType getLotteryType() {
		return lotteryType;
	}
	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}
	public int getAccountCheckType() {
		return accountCheckType;
	}
	public void setAccountCheckType(int accountCheckType) {
		this.accountCheckType = accountCheckType;
	}
	public String getAmountCheckDate() {
		return amountCheckDate;
	}
	public void setAmountCheckDate(String amountCheckDate) {
		this.amountCheckDate = amountCheckDate;
	}
	public double getDrawMoney() {
		return drawMoney;
	}
	public void setDrawMoney(double drawMoney) {
		this.drawMoney = drawMoney;
	}
	public double getDrawPlanMoney() {
		return drawPlanMoney;
	}
	public void setDrawPlanMoney(double drawPlanMoney) {
		this.drawPlanMoney = drawPlanMoney;
	}
	public double getDrawLossMoney() {
		return drawLossMoney;
	}
	public void setDrawLossMoney(double drawLossMoney) {
		this.drawLossMoney = drawLossMoney;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
