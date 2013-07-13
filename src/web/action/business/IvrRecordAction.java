package web.action.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.member.UserBankCardService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.api.user.UserBankCard;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.BankType;
import com.lehecai.core.lottery.WithIvrType;

public class IvrRecordAction extends BaseAction {
	private static final long serialVersionUID = 4181777008905094487L;
	private Logger logger = LoggerFactory.getLogger(IvrRecordAction.class);
	
	private UserBankCardService userBankCardService;
	private MemberService memberService;
	
	private List<UserBankCard> ivrRecords;
	
	private UserBankCard ivrRecord;
	
	private Long uid;
	private String username;
	private int bankTypeValue;

	/**
	 * 查询用户银行卡
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String handle () {
		Member member = null;
		if (uid != null && uid != 0L) {
			try {
				member = memberService.get(uid);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API根据用户编码查询用户异常，{}", e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!");
				return "failure";
			}
		} else if (username != null && !username.equals("")) {
			try {
				member = memberService.get(username);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API根据用户名查询用户异常，{}", e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!");
				return "failure";
			}
		} else {
			return "list";
		}
		
		if (member == null) {
			logger.error("查询会员不存在");
			super.setErrorMessage("查询会员不存在");
			return "failure";
		}
		
		uid = Long.valueOf(member.getUid());
		username = member.getUsername();
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = userBankCardService.queryBankCardList(member, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API根据查询用户银行卡异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!");
			return "failure";
		}
		if (map == null || map.size() == 0) {
			logger.error("API根据查询用户银行卡异常为空");
			super.setErrorMessage("API根据查询用户银行卡异常为空");
			return "failure";
		}
		ivrRecords = (List<UserBankCard>) map.get(Global.API_MAP_KEY_LIST);
		
		return "list";
	}
	
	/**
	 * 解绑用户银行卡
	 * @return
	 */
	public String unlock () {
		
		JSONObject json = new JSONObject();
		json.put("code", 0);
		json.put("msg", "解绑用户银行卡成功");
		
		if (ivrRecord == null ){
			json.put("code", 1);
			json.put("msg", "用户银行卡为空");
			
			super.writeRs(ServletActionContext.getResponse(), json);
			return null;
		}
		if (ivrRecord.getId() == 0L) {
			json.put("code", 1);
			json.put("msg", "用户编码为空");
			
			super.writeRs(ServletActionContext.getResponse(), json);
			return null;
		} 
		
		boolean unlockResult = false;
		try {
			unlockResult = userBankCardService.unlock(ivrRecord);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API解绑用户银行卡异常，{}", e.getMessage());
			json.put("code", 1);
			json.put("msg", "API调用异常，请联系技术人员!" + e.getMessage());
			
			super.writeRs(ServletActionContext.getResponse(), json);
			return null;
		}
		
		if (!unlockResult) {
			logger.error("API解绑用户银行卡失败");
			json.put("code", 1);
			json.put("msg", "解绑用户银行卡失败");
			super.writeRs(ServletActionContext.getResponse(), json);
			return null;
		}
		
		super.writeRs(ServletActionContext.getResponse(), json);
		return null;
		
	}
	
	/**
	 * 修改用户银行卡
	 * @return
	 */
	public String update () {
		JSONObject json = new JSONObject();
		json.put("code", 0);
		json.put("msg", "修改用户银行卡成功");
		
		if (ivrRecord == null ){
			json.put("code", 1);
			json.put("msg", "用户银行卡为空");
			
			super.writeRs(ServletActionContext.getResponse(), json);
			return null;
		}
		if (ivrRecord.getId() == 0L) {
			json.put("code", 1);
			json.put("msg", "用户编码为空");
			
			super.writeRs(ServletActionContext.getResponse(), json);
			return null;
		}
		if (bankTypeValue == 0 || bankTypeValue == BankType.ALL.getValue()) {
			json.put("code", 1);
			json.put("msg", "银行卡类型为空");
			
			super.writeRs(ServletActionContext.getResponse(), json);
			return null;
		}
		
		ivrRecord.setBankType(BankType.getItem(bankTypeValue));
		
		boolean updateResult = false;
		try {
			updateResult = userBankCardService.manageBankCard(ivrRecord);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改用户银行卡异常，{}", e.getMessage());
			json.put("code", 1);
			json.put("msg", "API调用异常，请联系技术人员!");
			
			super.writeRs(ServletActionContext.getResponse(), json);
			return null;
		}
		
		if (!updateResult) {
			logger.error("API修改用户银行卡失败");
			json.put("code", 1);
			json.put("msg", "修改用户银行卡失败");
			super.writeRs(ServletActionContext.getResponse(), json);
			return null;
		}
		
		super.writeRs(ServletActionContext.getResponse(), json);
		return null;
		
	}
	
	public List<BankType> getBankTypes () {
		return BankType.getItems();
	}
	
	public YesNoStatus getYesStatus () {
		return YesNoStatus.YES;
	}
	
	public YesNoStatus getNoStatus () {
		return YesNoStatus.NO;
	}
	
	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
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

	public int getBankTypeValue() {
		return bankTypeValue;
	}

	public void setBankTypeValue(int bankTypeValue) {
		this.bankTypeValue = bankTypeValue;
	}

	public UserBankCardService getUserBankCardService() {
		return userBankCardService;
	}

	public void setUserBankCardService(UserBankCardService userBankCardService) {
		this.userBankCardService = userBankCardService;
	}

	public List<UserBankCard> getIvrRecords() {
		return ivrRecords;
	}

	public void setIvrRecords(List<UserBankCard> ivrRecords) {
		this.ivrRecords = ivrRecords;
	}

	public UserBankCard getIvrRecord() {
		return ivrRecord;
	}

	public void setIvrRecord(UserBankCard ivrRecord) {
		this.ivrRecord = ivrRecord;
	}
	
	public WithIvrType getWithIvrTypeBind() {
		return WithIvrType.BIND;
	}
}
