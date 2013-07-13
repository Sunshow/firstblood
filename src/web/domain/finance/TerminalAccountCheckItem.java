/**
 * 
 */
package web.domain.finance;

import java.io.Serializable;
import java.util.Date;

import com.lehecai.core.lottery.LotteryType;

/**
 * @author chirowong
 *
 */
/**
 * @author chirowong
 *
 */
public class TerminalAccountCheckItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1223711168890779715L;
	private Long id;//对账单条目编码
	private LotteryType lotteryType;//彩种编码
	private Long terminalCompanyId;//出票商编码
	private String terminalCompanyName;//出票商名
	private int accountCheckType;//对账类型0-期，1-天
	private String amountCheckDate;//对账期数或者日期
	private double terminalDrawMoney;//出票商出票金额
	private double terminalPrizeMoney;//出票商中奖金额
	private double lehecaiDrawMoney;//乐和彩出票金额
	private double lehecaiPrizeMoney;//乐和彩中奖金额
	private double drawMoneyDiff;//出票差额
	private String drawDiffReason;//出票差额原因
	private double prizeMoneyDiff;//中奖差额
	private String prizeDiffReason;//中奖差额原因
	private double commission;//佣金
	private String memo;//备注
	private Long userId;//创建人编码
	private Date createTime;//创建日期
	private boolean enterAccount;//是否入账
	
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
	public Long getTerminalCompanyId() {
		return terminalCompanyId;
	}
	public void setTerminalCompanyId(Long terminalCompanyId) {
		this.terminalCompanyId = terminalCompanyId;
	}
	public String getTerminalCompanyName() {
		return terminalCompanyName;
	}
	public void setTerminalCompanyName(String terminalCompanyName) {
		this.terminalCompanyName = terminalCompanyName;
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
	public double getTerminalDrawMoney() {
		return terminalDrawMoney;
	}
	public void setTerminalDrawMoney(double terminalDrawMoney) {
		this.terminalDrawMoney = terminalDrawMoney;
	}
	public double getTerminalPrizeMoney() {
		return terminalPrizeMoney;
	}
	public void setTerminalPrizeMoney(double terminalPrizeMoney) {
		this.terminalPrizeMoney = terminalPrizeMoney;
	}
	public double getLehecaiDrawMoney() {
		return lehecaiDrawMoney;
	}
	public void setLehecaiDrawMoney(double lehecaiDrawMoney) {
		this.lehecaiDrawMoney = lehecaiDrawMoney;
	}
	public double getLehecaiPrizeMoney() {
		return lehecaiPrizeMoney;
	}
	public void setLehecaiPrizeMoney(double lehecaiPrizeMoney) {
		this.lehecaiPrizeMoney = lehecaiPrizeMoney;
	}
	public double getDrawMoneyDiff() {
		return drawMoneyDiff;
	}
	public void setDrawMoneyDiff(double drawMoneyDiff) {
		this.drawMoneyDiff = drawMoneyDiff;
	}
	public String getDrawDiffReason() {
		return drawDiffReason;
	}
	public void setDrawDiffReason(String drawDiffReason) {
		this.drawDiffReason = drawDiffReason;
	}
	public double getPrizeMoneyDiff() {
		return prizeMoneyDiff;
	}
	public void setPrizeMoneyDiff(double prizeMoneyDiff) {
		this.prizeMoneyDiff = prizeMoneyDiff;
	}
	public String getPrizeDiffReason() {
		return prizeDiffReason;
	}
	public void setPrizeDiffReason(String prizeDiffReason) {
		this.prizeDiffReason = prizeDiffReason;
	}
	public double getCommission() {
		return commission;
	}
	public void setCommission(double commission) {
		this.commission = commission;
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
	public boolean isEnterAccount() {
		return enterAccount;
	}
	public void setEnterAccount(boolean enterAccount) {
		this.enterAccount = enterAccount;
	}
}
