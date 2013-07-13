package web.action.member;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.MailUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.admin.web.utils.SmsUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.api.user.Wallet;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.WalletType;
import com.opensymphony.xwork2.Action;

public class MemberAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(MemberAction.class);
	
	private MemberService memberService;
	
	private Member member;
	
	private List<Member> members;
	private List<Wallet> wallets;
	
	private String userName;
	private String name;
	private String email;
	private String phone;
	private String idData;
	private Date rbeginDate;
	private Date rendDate;
	private Date lbeginDate;
	private Date lendDate;
	private String source;
	private String orderStr;
	private String orderView;
	private String rechargered;
	
	private Long uid;
	private Integer walletType;
	private Double amount;
	private String remark;
	
	
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	
	private Long memberId;
	private Integer seconds;
	private boolean haveNewMessage;
	
	public String handle() {
		logger.info("进入查询会员列表");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询会员列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map;
		
		try {
			map = memberService.fuzzyQueryResult(uid, userName, name, phone, email,
					idData, rbeginDate, rendDate, lbeginDate,
					lendDate, source, getOrderStr(), getOrderView(), false, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			members = (List<Member>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
			
			for (Member m : members) {
				if (m.getPhone() != null && !"".equals(m.getPhone())) {
					String strPhone = null;
					if (SmsUtil.checkPhoneNo(m.getPhone())) {
						strPhone = m.getPhone().substring(0, 3) + "****" + m.getPhone().substring(7);
						m.setPhone(strPhone);
					}
				}
				if (m.getEmail() != null && !"".equals(m.getEmail())) {
					if (MailUtil.checkEmail(m.getEmail())) {
						String[] strEmail = m.getEmail().split("@");
						if (strEmail[0].length() > 4) {
							strEmail[0] = strEmail[0].substring(0, strEmail[0].length() - 4) + "****";
						} else {
							strEmail[0] = "****";
						}
						m.setEmail(strEmail[0] + "@" + strEmail[1]);
					}
				}
			}
		}	
		logger.info("查询会员列表结束");
		return "list";
	}
	
	public String view() {
		logger.info("进入查询会员详情");
		if (member != null && member.getUid() != 0) {
			try {
				member = memberService.get(member.getUid());
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
		} else {
			return "failure";
		}
		logger.info("查询会员详情结束");
		return "view";
	}
	
	public String viewWallet() {
		logger.info("进入查看钱包明细");
		if (member != null && member.getUid() != 0) {
			try {
				wallets = new ArrayList<Wallet>();
				List<Wallet> allWalletList = memberService.getWallets(member.getUid());
				for (Wallet wallet : allWalletList) {
					if (wallet.getType().getValue() == WalletType.LEHECAI.getValue()) {
						continue;
					}
					wallets.add(wallet);
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
		} else {
			return "failure";
		}
		logger.info("查看钱包明细结束");
		return "viewWallet";
	}
	
	public String freezeWallet() {
		logger.info("进入冻结钱包");
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject obj = new JSONObject();
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);
		Long frozenUid = null;
		if (userSessionBean.getUser() != null) {
			frozenUid = userSessionBean.getUser().getId();
		}
		if (uid != null && walletType != null && WalletType.getItem(walletType) != null && amount != null && amount > 0.00D && remark != null && !"".equals(remark) && frozenUid != null ) {
			try {
				Long deduct_id = memberService.freezeWallet(uid, WalletType.getItem(walletType), amount, remark, frozenUid);
				if (deduct_id != null && deduct_id != 0L) {
					obj.put("rs", "1");
					obj.put("message", "success");
					obj.put("deduct_id", deduct_id);
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				obj.put("rs", "0");
				obj.put("message", e.getMessage());
				obj.put("deduct_id", "0");
				return Action.NONE;
			}
		} else {
			obj.put("rs", "0");
			obj.put("message", "参数错误");
			obj.put("deduct_id", "0");
			return Action.NONE;
		}
		writeRs(response, obj);
		logger.info("冻结钱包结束");
		return Action.NONE;
	}
	
	@SuppressWarnings("unchecked")
	public String noticeMember() {
		logger.info("进入会员注册通知");
		HttpServletRequest request = ServletActionContext.getRequest();
		boolean flag = false;
		if ("on".equals(rechargered)) {
			flag = true;
		}
		if (super.getSession().get("flag") != null && !String.valueOf(flag).equals(String.valueOf(super.getSession().get("flag")))) {
			super.getSession().remove("memberId");
		}
		super.getSession().put("flag", flag);
		Map<String, Object> map;
		try {
			map = memberService.getResult(null, null, null, null, null, null,
					null, null,
					null, Member.ORDER_REG_TIME, ApiConstant.API_REQUEST_ORDER_DESC, flag, super
							.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map == null) {
			logger.error("查询会员列表，查询结果为空");
			super.setErrorMessage("list查询结果为空!");
			return "failure";
		}
		members = (List<Member>)map.get(Global.API_MAP_KEY_LIST);
		PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		
		Object obj = super.getSession().get("memberId");
		if (obj != null) {
			memberId = (Long)obj;
			long id =  members.get(0).getUid();
			if (memberId != id) {
				haveNewMessage = true;
				super.getSession().put("memberId", id);
			} else {
				haveNewMessage = false;
			}
		} else {
			haveNewMessage = false;
			memberId = members.get(0).getUid();
			super.getSession().put("memberId", memberId);
		}
		logger.info("会员注册通知结束");
		return "notice";
	}
	
	public MemberService getMemberService() {
		return memberService;
	}
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public List<Member> getMembers() {
		return members;
	}
	public void setMembers(List<Member> members) {
		this.members = members;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getRbeginDate() {
		return rbeginDate;
	}
	public void setRbeginDate(Date rbeginDate) {
		this.rbeginDate = rbeginDate;
	}
	public Date getRendDate() {
		return rendDate;
	}
	public void setRendDate(Date rendDate) {
		this.rendDate = rendDate;
	}
	public Date getLbeginDate() {
		return lbeginDate;
	}
	public void setLbeginDate(Date lbeginDate) {
		this.lbeginDate = lbeginDate;
	}
	public Date getLendDate() {
		return lendDate;
	}
	public void setLendDate(Date lendDate) {
		this.lendDate = lendDate;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getOrderStr() {
		if (orderStr == null && !"".equals(orderStr)) {
			orderStr = Member.ORDER_REG_TIME;
		}
		return orderStr;
	}
	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}
	public String getOrderView() {
		if (orderView == null && !"".equals(orderView)) {
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		return orderView;
	}
	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}
	public List<Wallet> getWallets() {
		return wallets;
	}
	public void setWallets(List<Wallet> wallets) {
		this.wallets = wallets;
	}
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(Member.ORDER_UID, "编码");
		orderStrMap.put(Member.ORDER_REG_TIME, "注册时间");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public Integer getSeconds() {
		if (seconds == null) {
			seconds = 30;
		}
		return seconds;
	}
	public void setSeconds(Integer seconds) {
		this.seconds = seconds;
	}
	public boolean isHaveNewMessage() {
		return haveNewMessage;
	}
	public void setHaveNewMessage(boolean haveNewMessage) {
		this.haveNewMessage = haveNewMessage;
	}
	public String getRechargered() {
		return rechargered;
	}
	public void setRechargered(String rechargered) {
		this.rechargered = rechargered;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	public Integer getWalletType() {
		return walletType;
	}
	public void setWalletType(Integer walletType) {
		this.walletType = walletType;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getIdData() {
		return idData;
	}
	public void setIdData(String idData) {
		this.idData = idData;
	}
}
