/**
 * 
 */
package web.domain.finance;

import java.io.Serializable;
import java.util.Date;

import com.lehecai.core.lottery.LotteryType;

/**
 * 出票商点位管理，一种彩种对应一种点位
 * @author chirowong
 *
 */
public class TerminalCompanyPoint implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7616795176882994735L;
	private Long id;//编码
	private Long companyId;//出票商编码
	private String companyName;//出票商名字
	private Double point;//点位
	private LotteryType lotteryType;//出票彩种
	private String memo;//备注
	private Date createTime;//创建时间
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public Double getPoint() {
		return point;
	}
	public void setPoint(Double point) {
		this.point = point;
	}
	public LotteryType getLotteryType() {
		return lotteryType;
	}
	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
