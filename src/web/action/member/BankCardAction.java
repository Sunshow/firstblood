package web.action.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.member.UserBankCardService;
import com.lehecai.admin.web.service.user.ProvinceCityService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.user.City;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.api.user.Province;
import com.lehecai.core.api.user.UserBankCard;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.BankType;
import com.lehecai.core.lottery.WithIvrType;
import com.opensymphony.xwork2.Action;

public class BankCardAction extends BaseAction {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	
	private String username;
	private Long uid;
	private Long bankCardId;

	private Member member;
	private MemberService memberService;
	private List<UserBankCard> bankCardList;
	
	private UserBankCard bankCard;
	
	private UserBankCardService userBankCardService;
	
	private ProvinceCityService provinceCityService;
	private List<Province> provinces;
	private List<City> cities;
	private Integer provinceId;
	
	private String bankCardno;
	private Integer provinceValue;
	private Integer cityValue;
	private Integer bankTypeValue;
	private String bankBranch;
	
	public String handle() {
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String get() {
		logger.info("进入查询用户银行卡信息");
		
		HttpServletRequest request = ServletActionContext.getRequest();
		
		if ((username == null || "".equals(username)) && (uid == null || uid == 0L)) {
			logger.error("用户名和用户ID不能都为空");
			super.setErrorMessage("用户名和用户ID不能都为空");
			return "failure";
		}
		
		if (uid == null || uid == 0L) {
			try {
				member = memberService.get(username);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("通过用户名获取用户信息,api调用异常" + e.getMessage());
				super.setErrorMessage("通过用户名获取用户信息失败,api调用异常" + e.getMessage());
				return "failure";
			}
		} else {
			try {
				member = memberService.get(uid);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("通过用户ID获取用户信息失败,api调用异常" + e.getMessage());
				super.setErrorMessage("通过用户ID获取用户信息失败,api调用异常" + e.getMessage());
				return "failure";
			}
		}
		if (member == null) {
			logger.error("用户不存在");
			super.setErrorMessage("用户不存在");
			return "failure";
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = userBankCardService.queryBankCardList(member, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API调用失败" + e.getMessage());
			super.setErrorMessage("API调用失败");
			return "failure";
		}
		if (map == null || map.size() == 0) {
			logger.error("API获取会员银行卡信息为空");
			super.setErrorMessage("API获取会员银行卡信息为空");
			return "failure";
		}
		bankCardList = (List<UserBankCard>) map.get(Global.API_MAP_KEY_LIST);
		PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		
		logger.info("查询用户银行卡信息结束");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String input() {
		if (bankCardId == null || bankCardId == 0) {
			logger.error("ID为空");
			super.setErrorMessage("ID为空");
			return "failure";
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = userBankCardService.queryBankCardListById(bankCardId);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API调用失败" + e.getMessage());
			super.setErrorMessage("API调用失败");
			return "failure";
		}
		if (map == null || map.size() == 0) {
			logger.error("API获取会员银行卡信息为空");
			super.setErrorMessage("API获取会员银行卡信息为空");
			return "failure";
		}
		bankCardList = (List<UserBankCard>) map.get(Global.API_MAP_KEY_LIST);
		
		if (bankCardList != null && bankCardList.size() != 0) {
			bankCard = bankCardList.get(0);
		}
		
		Map<String,Object> provinceMap = null;
		try {
			provinceMap = provinceCityService.getProvinceList();
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询省信息异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
		}
		if (provinceMap != null) {
			if (provinceMap.get(Global.API_MAP_KEY_LIST)!= null) {
				provinces =(List<Province>)provinceMap.get(Global.API_MAP_KEY_LIST) ;
			}
		}

		Map<String,Object> cityMap = null;
		try {
			int p = bankCard.getProvince() == 0 ? 11 : bankCard.getProvince();
			cityMap = provinceCityService.getCityListByProvince(p);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询省信息异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
		}
		if (cityMap != null) {
			if (cityMap.get(Global.API_MAP_KEY_LIST)!= null) {
				cities =(List<City>)cityMap.get(Global.API_MAP_KEY_LIST) ;
			}
		}
		if (provinces == null || provinces.size() == 0) {
			logger.error("查询省市信息异常");
			super.setErrorMessage("查询省市信息异常");
			return "failure";
		}
		
		return "inputForm";
	}
	
	@SuppressWarnings("unchecked")
	public String getCitiesByProvince () {
		logger.info("进入根据省信息查询市信息");
		int rc = 0;
		String msg = "";
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		if (provinceId == null || provinceId == 0) {
			logger.error("查询市信息异常，省ID为空 ");
			rc = 1;
			msg = "查询市信息异常，省ID为空 ";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		} 
		
		Map<String,Object> cityMap = null;
		try {
			cityMap = provinceCityService.getCityListByProvince(provinceId);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询省信息异常，{}", e.getMessage());
			rc = 1;
			msg = "API调用异常，请联系技术人员! ";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		if (cityMap != null) {
			if (cityMap.get(Global.API_MAP_KEY_LIST)!= null) {
				cities =(List<City>)cityMap.get(Global.API_MAP_KEY_LIST) ;
			}
		}
		JSONArray jsonArray = new JSONArray();
		for (City c : cities) {
			JSONObject j = new JSONObject();
			j.put("key", c.getCityId());
			j.put("name", c.getCityName());
			jsonArray.add(j);
		}
		json.put("code", rc);
		json.put("msg", msg);
		json.put("data", jsonArray.toString());
		writeRs(response, json);
		return Action.NONE;
	}

	public String manage() {
		if (bankCardId == null || bankCardId == 0) {
			logger.error("银行卡信息ID为空");
			super.setErrorMessage("银行卡信息ID为空");
			return "failure";
		}
		
		UserBankCard ubc = new UserBankCard();
		ubc.setId(bankCardId);
		
		BankType b = bankTypeValue == null ? null : BankType.getItem(bankTypeValue);
		
		
		if (b != null && b.getValue() != BankType.ALL.getValue()) {
			ubc.setBankType(b);
		}
		if (bankBranch != null && !"".equals(bankBranch)) {
			ubc.setBankBranch(bankBranch);
		}
		if (provinceValue != null && provinceValue != 0) {
			ubc.setProvince(provinceValue);
		}
		if (cityValue != null && cityValue != 0) {
			ubc.setCity(cityValue);
		}
		
		boolean up = false;
		try {
			up = userBankCardService.manageBankCard(ubc);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API调用失败" + e.getMessage());
			super.setErrorMessage("API调用失败");
			return "failure";
		}
		if (!up) {
			logger.error("更新会员银行卡信息失败，id={}", bankCardId + "");
			super.setErrorMessage("更新会员银行卡信息失败，id=" + bankCardId);
			return "failure";
		}
		super.setForwardUrl("/member/bankCard.do?action=get&uid=" + uid);
		return "success";
	}
	
	public String unlock() {
		logger.info("进入解绑用户银行卡信息");
		if (bankCardId == null || bankCardId == 0L) {
			logger.error("记录ID为空");
			logger.error("记录ID不能为空");
			return "failure";
		}
		UserBankCard usb = new UserBankCard();
		usb.setId(bankCardId);
		boolean unlock = false;
		try {
			unlock = userBankCardService.unlock(usb);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API解绑用户银行卡失败" + e.getMessage());
			super.setErrorMessage("API解绑用户银行卡失败");
			return "failure";
		}
		if (!unlock) {
			logger.error("API解绑用户银行卡失败,id={}" + bankCardId);
			super.setErrorMessage("API解绑用户银行卡失败,id=" + bankCardId);
			return "failure";
		}
		super.setSuccessMessage("解绑用户银行卡成功");
		super.setForwardUrl("/member/bankCard.do?action=get&uid=" + uid);
		return "success";
	}
	
	public String lock() {
		logger.info("进入绑定用户银行卡信息");
		if (bankCardId == null || bankCardId == 0L) {
			logger.error("记录ID为空");
			logger.error("记录ID不能为空");
			return "failure";
		}
		UserBankCard usb = new UserBankCard();
		usb.setId(bankCardId);
		boolean lock = false;
		try {
			lock = userBankCardService.lock(usb);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API绑定用户银行卡失败" + e.getMessage());
			super.setErrorMessage("API绑定用户银行卡失败");
			return "failure";
		}
		if (!lock) {
			logger.error("API绑定用户银行卡失败,id={}" + bankCardId);
			super.setErrorMessage("API绑定用户银行卡失败,id=" + bankCardId);
			return "failure";
		}
		super.setSuccessMessage("绑定用户银行卡成功");
		super.setForwardUrl("/member/bankCard.do?action=get&uid=" + uid);
		return "success";
	}
	
	public String del() {
		logger.info("进入删除用户银行卡信息");
		if (bankCardId == null || bankCardId == 0L) {
			logger.error("记录ID为空");
			logger.error("记录ID不能为空");
			return "failure";
		}
		boolean del = false;
		try {
			del = userBankCardService.delBankCard(bankCardId);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API删除银行卡记录失败" + e.getMessage());
			super.setErrorMessage("API删除银行卡记录失败");
			return "failure";
		}
		if (!del) {
			logger.error("API删除银行卡记录失败,id={}" + bankCardId);
			super.setErrorMessage("API删除银行卡记录失败,id=" + bankCardId);
			return "failure";
		}
		super.setSuccessMessage("删除银行卡记录成功");
		super.setForwardUrl("/member/bankCard.do?action=get&uid=" + uid);
		return "success";
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public Long getBankCardId() {
		return bankCardId;
	}

	public void setBankCardId(Long bankCardId) {
		this.bankCardId = bankCardId;
	}

	public List<BankType> getBankTypes() {
		List<BankType> returnList = new ArrayList<BankType>();
		for (BankType bt : BankType.getItems()) {
			if (bt.getValue() != BankType.ALL.getValue() && bt.getValue() != BankType.DEFAULT.getValue()) {
				returnList.add(bt);
			}
		}
		return returnList;
	}

	public ProvinceCityService getProvinceCityService() {
		return provinceCityService;
	}

	public void setProvinceCityService(ProvinceCityService provinceCityService) {
		this.provinceCityService = provinceCityService;
	}

	public List<Province> getProvinces() {
		return provinces;
	}

	public void setProvinces(List<Province> provinces) {
		this.provinces = provinces;
	}

	public List<City> getCities() {
		if (cities == null || cities.size() == 0) {
			cities = new ArrayList<City>();
		}
		return this.cities;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}
	
	public YesNoStatus getYesStatus() {
		return YesNoStatus.YES;
	}

	public String getBankCardno() {
		return bankCardno;
	}

	public void setBankCardno(String bankCardno) {
		this.bankCardno = bankCardno;
	}

	public Integer getProvinceValue() {
		return provinceValue;
	}

	public void setProvinceValue(Integer provinceValue) {
		this.provinceValue = provinceValue;
	}

	public Integer getCityValue() {
		return cityValue;
	}

	public void setCityValue(Integer cityValue) {
		this.cityValue = cityValue;
	}

	public Integer getBankTypeValue() {
		return bankTypeValue;
	}

	public void setBankTypeValue(Integer bankTypeValue) {
		this.bankTypeValue = bankTypeValue;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public UserBankCardService getUserBankCardService() {
		return userBankCardService;
	}

	public void setUserBankCardService(UserBankCardService userBankCardService) {
		this.userBankCardService = userBankCardService;
	}

	public List<UserBankCard> getBankCardList() {
		return bankCardList;
	}

	public void setBankCardList(List<UserBankCard> bankCardList) {
		this.bankCardList = bankCardList;
	}

	public UserBankCard getBankCard() {
		return bankCard;
	}

	public void setBankCard(UserBankCard bankCard) {
		this.bankCard = bankCard;
	}
	
	public WithIvrType getBind() {
		return WithIvrType.BIND;
	}

}
