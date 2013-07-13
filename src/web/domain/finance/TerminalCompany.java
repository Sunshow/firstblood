/**
 * 
 */
package web.domain.finance;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chirowong
 *
 */
public class TerminalCompany implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7616795176882994735L;
	private Long id;//出票商编码
	private String name;//出票商名字
	private Date createTime;//创建时间
	private String contact;//联系人
	private String memo;//备注
	private String terminalTypes;//终端
	private String lotteryTypes;//出票彩种
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getTerminalTypes() {
		return terminalTypes;
	}
	public void setTerminalTypes(String terminalTypes) {
		this.terminalTypes = terminalTypes;
	}
	public String getLotteryTypes() {
		return lotteryTypes;
	}
	public void setLotteryTypes(String lotteryTypes) {
		this.lotteryTypes = lotteryTypes;
	}
}
