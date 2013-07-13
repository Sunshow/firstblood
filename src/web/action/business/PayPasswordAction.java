package web.action.business;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.business.PayPassword;
import com.lehecai.admin.web.domain.business.PayPasswordLog;
import com.lehecai.admin.web.domain.business.PayPasswordType;
import com.lehecai.admin.web.service.business.PayPasswordService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 会员支付密码信息
 * @author likunpeng
 * 2013-06-28
 */
public class PayPasswordAction extends BaseAction {

    private static final long serialVersionUID = 15010522174118486L;
    
    private Long uid;
    private Date startDate;
    private Date endDate;
    private Integer passType;
	private String message;
	private String userName;
    private PayPassword payPassword;
    private MemberService memberService;
    private List<PayPasswordType> payPasswordTypeList;
	private PayPasswordService payPasswordService;
	private List<PayPasswordLog> payPasswordLogList;
	
	public String handle() {
        logger.info("进入支付密码信息查询");
        return "list";
	}
	
	public String query() {
        logger.info("进入个人支付密码信息查询");
        Member member = null;
        if (uid == null && StringUtils.isEmpty(userName)) {
			message = "用户id和用户名两者必须输入一个！";
			logger.error(message);
			super.setErrorMessage(message);
			super.setForwardUrl("/business/payPassword.do");
			return "failure";
        } else {
        	if (uid == null){
        		try {
					member = memberService.get(userName);
				} catch (ApiRemoteCallFailedException e) {
					logger.error(e.getMessage(), e);
					super.setErrorMessage("根据用户名查询用户失败，原因" + e.getMessage());
					return "failure";
				}
				if (member != null && member.getUid() != 0) {
					try {
						payPassword = payPasswordService.get(member.getUid());
					} catch (ApiRemoteCallFailedException e) {
						logger.error(e.getMessage(), e);
						super.setErrorMessage("根据用户名查询支付信息错误,原因" + e.getMessage());
						return "failure";
					}
				} else {
					message = "该用户不存在！";
					logger.error(message);
					super.setErrorMessage(message);
					super.setForwardUrl("/business/payPassword.do");
					return "failure";
				}
        	} else {
        		try {
					payPassword = payPasswordService.get(uid);
				} catch (ApiRemoteCallFailedException e) {
					logger.error(e.getMessage(), e);
					super.setErrorMessage("根据用户id查询支付信息错误,原因" + e.getMessage());
					return "failure";
				}
        	}
        }
        
		logger.info("个人支付密码信息查询结束");
        return "list";
	}
	
	/**
	 * 查看日志
	 */
	@SuppressWarnings("unchecked")
	public String queryLog() {
		logger.info("进入日志展示页面");
		HttpServletRequest request = ServletActionContext.getRequest();
		Member member = null;
		if (uid == null) {
			if (StringUtils.isNotEmpty(userName)) {
				try {
					member = memberService.get(userName);
				} catch (ApiRemoteCallFailedException e) {
					logger.error(e.getMessage(), e);
					super.setErrorMessage(e.getMessage());
					return "failure";
				}
				if (member != null && member.getUid() != 0) {
					Map<String, Object> map = null;
					try {
						map = payPasswordService.searchLog(member.getUid(), startDate, endDate, passType, super.getPageBean());
						if (map == null) {
							message = "查询日志列表，结果为空";
							logger.error(message);
							super.setErrorMessage(message);
							return "failure";
						}
					} catch (ApiRemoteCallFailedException e) {
						logger.error(e.getMessage(), e);
						super.setErrorMessage(e.getMessage());
						return "failure";
					}
					payPasswordLogList = (List<PayPasswordLog>) map.get(Global.API_MAP_KEY_LIST);
					PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
					super.setPageString(PageUtil.getPageString(request, pageBean));
					super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
				} else {
					message = "对不起，你输入的会员不存在！";
					logger.error(message);
					super.setErrorMessage(message);
					return "failure";
				}
			} else {
				message = "用户id和用户名两者必须输入一个！";
				logger.error(message);
				super.setErrorMessage(message);
				return "failure";
			}
		} else {
			try {
				member = memberService.get(uid);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage(e.getMessage());
				return "failure";
			}
			if (member != null) {
				Map<String, Object> map = null;
				try {
					map = payPasswordService.searchLog(member.getUid(), startDate, endDate, passType, super.getPageBean());
					if (map == null) {
						message = "查询日志列表，结果为空";
						logger.error(message);
						super.setErrorMessage(message);
						return "failure";
					}
				} catch (ApiRemoteCallFailedException e) {
					logger.error(e.getMessage(), e);
					super.setErrorMessage(e.getMessage());
					return "failure";
				}
				payPasswordLogList = (List<PayPasswordLog>) map.get(Global.API_MAP_KEY_LIST);
				PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
				super.setPageString(PageUtil.getPageString(request, pageBean));
				super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
			} else {
				message = "对不起，你输入的会员不存在！";
				logger.error(message);
				super.setErrorMessage(message);
				return "failure";
			}
		}
		logger.info("查询日志结束");
        return "input";
    }
	
	public PayPasswordService getPayPasswordService() {
		return payPasswordService;
	}

	public void setPayPasswordService(PayPasswordService payPasswordService) {
		this.payPasswordService = payPasswordService;
	}
	
	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public PayPassword getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(PayPassword payPassword) {
		this.payPassword = payPassword;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public MemberService getMemberService() {
		return memberService;
	}
	
    public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setPayPasswordTypeList(List<PayPasswordType> payPasswordTypeList) {
		this.payPasswordTypeList = payPasswordTypeList;
	}

	public List<PayPasswordType> getPayPasswordTypeList() {
		payPasswordTypeList = PayPasswordType.getItemsForQuery();
		return payPasswordTypeList;
	}

	public void setPassType(Integer passType) {
		this.passType = passType;
	}

	public Integer getPassType() {
		return passType;
	}

	public void setPayPasswordLogList(List<PayPasswordLog> payPasswordLogList) {
		this.payPasswordLogList = payPasswordLogList;
	}

	public List<PayPasswordLog> getPayPasswordLogList() {
		return payPasswordLogList;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
