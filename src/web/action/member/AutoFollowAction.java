package web.action.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.AutoFollowService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.user.AutoFollow;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.AutoFollowType;
import com.lehecai.core.lottery.LotteryType;

public class AutoFollowAction extends BaseAction {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	
	private String username;
	private Long uid;
	private Member member;
	private MemberService memberService;
	private AutoFollowService autoFollowService;
	private List<AutoFollow> autoFollowList;
	
	private String pageFrom;
	
	private Long fuid;
	private Long tuid;
	private String fusername;
	private String tusername;
	private Integer autoFollowTypeValue;
	private Integer numPerphase;
	private Double unitAmount;
	private Double cancelBelowAmount;
	private Member fmember;
	private Member tmember;
	
	private Integer lotteryTypeValue;
	
	private AutoFollow autoFollow;

	public String handle() {
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String get() {
		logger.info("进入查询用户跟单信息");
		
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
			map = autoFollowService.queryAutoFollowList(member, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API调用失败" + e.getMessage());
			super.setErrorMessage("API调用失败");
			return "failure";
		}
		if (map == null || map.size() == 0) {
			logger.error("API获取会员自动跟单信息为空");
			super.setErrorMessage("API获取会员自动跟单信息为空");
			return "failure";
		}
		autoFollowList = (List<AutoFollow>) map.get(Global.API_MAP_KEY_LIST);
		PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		
		logger.info("查询用户跟单信息结束");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String view() {
		logger.info("进入查询用户跟单详细信息");
		
		if (fuid == null || fuid == 0L || tuid == null || tuid == 0L) {
			logger.error("跟单用户ID或被跟单用户ID不能为空");
			super.setErrorMessage("跟单用户ID或被跟单用户ID不能为空");
			return "failure";
		}
		
		HttpServletRequest request = ServletActionContext.getRequest();
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = autoFollowService.queryAutoFollowInfoList(fuid, tuid, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询会员跟单详情异常" + e.getMessage());
			super.setErrorMessage("API查询会员跟单详情异常");
			return "failure";
		}
		if (map == null || map.size() == 0) {
			logger.error("API获取会员自动跟单详细信息为空");
			super.setErrorMessage("API获取会员自动跟单详细信息为空");
			return "failure";
		}
		
		autoFollowList = (List<AutoFollow>) map.get(Global.API_MAP_KEY_LIST);
		
		if (autoFollowList != null && autoFollowList.size() > 0) {
			fusername = autoFollowList.get(0).getFusername();
			tusername = autoFollowList.get(0).getTusername();
			fuid = autoFollowList.get(0).getFuid();
		}
		
		PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		
		return "view";
	}
	
	public String input() {
		if (fuid != null && fusername != null) {
			autoFollow = new AutoFollow();
			autoFollow.setFuid(fuid);
			autoFollow.setFusername(fusername);
		}
		return "inputForm";
	}

	public String manage() {
		if (fuid == null || fuid == 0L) {
			if (fusername == null || "".equals(fusername)) {
				logger.error("跟单人ID和用户名都为空");
				super.setErrorMessage("跟单人ID和用户名都为空");
				return "failure";
			} else {
				try {
					fuid = memberService.getIdByUserName(fusername);
					if (fuid == null || fuid == 0L) {
						logger.error("API根据跟单人用户名查询跟单人ID错误");
						super.setErrorMessage("API根据跟单人用户名查询跟单人ID错误");
						return "failure";
					}
				} catch (ApiRemoteCallFailedException e) {
					logger.error("API根据跟单人用户名查询跟单人ID错误" + e.getMessage());
					super.setErrorMessage("API根据跟单人用户名查询跟单人ID错误" + e.getMessage());
					return "failure";
				}
			}
		}
		if (tuid == null || tuid == 0L) {
			if (tusername == null || "".equals(tusername)) {
				logger.error("被跟单人ID和用户名都为空");
				super.setErrorMessage("被跟单人ID和用户名都为空");
				return "failure";
			} else {
				try {
					tuid = memberService.getIdByUserName(tusername);
					if (tuid == null || tuid == 0l) {
						logger.error("API根据被跟单人用户名查询跟单人ID错误");
						super.setErrorMessage("API根据被跟单人用户名查询跟单人ID错误");
						return "failure";
					}
				} catch (ApiRemoteCallFailedException e) {
					logger.error("API根据被跟单人用户名查询跟单人ID错误" + e.getMessage());
					super.setErrorMessage("API根据被跟单人用户名查询跟单人ID错误" + e.getMessage());
					return "failure";
				}
			}
		}
		LotteryType lotteryType = null;
		if (lotteryTypeValue != null) {
			if (lotteryTypeValue == LotteryType.ALL.getValue()) {
				logger.error("彩种类型不能为全部");
				super.setErrorMessage("彩种类型不能为全部");
				return "failure";
			}
			lotteryType = LotteryType.getItem(lotteryTypeValue);
		}
		AutoFollowType autoFollowType = null;
		if (autoFollowTypeValue != null) {
			if (autoFollowTypeValue == AutoFollowType.ALL.getValue()) {
				logger.error("跟单类型不能为全部");
				super.setErrorMessage("跟单类型不能为全部");
				return "failure";
			}
			autoFollowType = AutoFollowType.getItem(autoFollowTypeValue);
		}
		if (autoFollowTypeValue != null && autoFollowTypeValue == AutoFollowType.FOLLOW.getValue()
				&& (numPerphase == null || cancelBelowAmount == null || unitAmount == null)) {
			logger.error("跟单类型为“跟单”时，每次跟单金额、每期跟单次数、账户余额低于此值，放弃自动跟单不能为空");
			super.setErrorMessage("跟单类型为“跟单”时，每次跟单金额、每期跟单次数、账户余额低于此值，放弃自动跟单不能为空");
			return "failure";
		}
		if (autoFollowTypeValue == AutoFollowType.FOLLOW.getValue() && (numPerphase == null || numPerphase == 0)) {
			logger.error("跟单类型为“跟单”时，每期跟单次数不能为空或0");
			super.setErrorMessage("跟单类型为“跟单”时，每期跟单次数不能为空或0");
			return "failure";
		}
		if (autoFollowTypeValue == AutoFollowType.FOLLOW.getValue() && (unitAmount == null || unitAmount == 0d)) {
			logger.error("跟单类型为“跟单”时，每次跟单金额不能为空或0");
			super.setErrorMessage("跟单类型为“跟单”时，每次跟单金额不能为空或0");
			return "failure";
		}
		try {
			autoFollowService.addAutoFollow(fuid, tuid, lotteryType, autoFollowType, numPerphase, unitAmount, cancelBelowAmount);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API添加自动跟单信息失败" + e.getMessage());
			super.setErrorMessage("API添加自动跟单信息失败");
			return "failure";
		}
		super.setSuccessMessage("操作成功");
		if (pageFrom != null && pageFrom.equals("inputPage")) {
			super.setForwardUrl("/member/autoFollow.do?action=get&uid=" + fuid);
		} else {
			super.setForwardUrl("/member/autoFollow.do?action=view&fuid=" + fuid + "&tuid=" + tuid);
		}
		return "success";
	}
	
	public String del() {
		if (fuid == null || fuid == 0L) {
			logger.error("跟单人ID为空");
			logger.error("跟单人ID不能为空");
			return "failure";
		}
		if (tuid == null || tuid == 0L) {
			logger.error("被跟单人ID为空");
			logger.error("被跟单人ID不能为空");
			return "failure";
		}
		if (lotteryTypeValue == null || lotteryTypeValue == LotteryType.ALL.getValue()) {
			logger.error("彩种为空");
			logger.error("彩种不能为空");
			return "failure";
		}
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		try {
			autoFollowService.delAutoFollow(fuid, tuid, lotteryType);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API取消自动跟单信息失败" + e.getMessage());
			super.setErrorMessage("API取消自动跟单信息失败");
			return "failure";
		}
		super.setSuccessMessage("自动跟单取消成功");
		super.setForwardUrl("/member/autoFollow.do?action=get&uid=" + fuid);
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

	public AutoFollowService getAutoFollowService() {
		return autoFollowService;
	}

	public void setAutoFollowService(AutoFollowService autoFollowService) {
		this.autoFollowService = autoFollowService;
	}

	public List<AutoFollow> getAutoFollowList() {
		return autoFollowList;
	}

	public void setAutoFollowList(List<AutoFollow> autoFollowList) {
		this.autoFollowList = autoFollowList;
	}

	public Long getFuid() {
		return fuid;
	}

	public void setFuid(Long fuid) {
		this.fuid = fuid;
	}

	public Long getTuid() {
		return tuid;
	}

	public void setTuid(Long tuid) {
		this.tuid = tuid;
	}

	public Member getFmember() {
		return fmember;
	}

	public void setFmember(Member fmember) {
		this.fmember = fmember;
	}

	public Member getTmember() {
		return tmember;
	}

	public void setTmember(Member tmember) {
		this.tmember = tmember;
	}

	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}

	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}

	public AutoFollow getAutoFollow() {
		return autoFollow;
	}

	public void setAutoFollow(AutoFollow autoFollow) {
		this.autoFollow = autoFollow;
	}
	
	public List<AutoFollowType> getAutoFollowTypes() {
		List<AutoFollowType> autoFollowTypes = AutoFollowType.getItems();
		return autoFollowTypes;
	}
	
	public List<LotteryType> getLotteryTypes() {
		List<LotteryType> lotteryTypes = LotteryType.getItems();
		return lotteryTypes;
	}

	public String getFusername() {
		return fusername;
	}

	public void setFusername(String fusername) {
		this.fusername = fusername;
	}

	public String getTusername() {
		return tusername;
	}

	public void setTusername(String tusername) {
		this.tusername = tusername;
	}

	public Integer getAutoFollowTypeValue() {
		return autoFollowTypeValue;
	}

	public void setAutoFollowTypeValue(Integer autoFollowTypeValue) {
		this.autoFollowTypeValue = autoFollowTypeValue;
	}

	public Integer getNumPerphase() {
		return numPerphase;
	}

	public void setNumPerphase(Integer numPerphase) {
		this.numPerphase = numPerphase;
	}

	public Double getUnitAmount() {
		return unitAmount;
	}

	public void setUnitAmount(Double unitAmount) {
		this.unitAmount = unitAmount;
	}

	public Double getCancelBelowAmount() {
		return cancelBelowAmount;
	}

	public void setCancelBelowAmount(Double cancelBelowAmount) {
		this.cancelBelowAmount = cancelBelowAmount;
	}
	
	public AutoFollowType getAutoFollowTypeFollow(){
		return AutoFollowType.FOLLOW;
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

}
