package web.action.member;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.member.MemberCreditService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.api.user.MemberCredit;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.opensymphony.xwork2.Action;

public class MemberCreditAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(MemberCreditAction.class);
	
	private MemberCreditService memberCreditService;
	private MemberService memberService;
	
	private MemberCredit memberCredit;
	
	private long presentAmount;
	private long deductAmount;
	private String presentRemark;
	private String deductRemark;
	
	public String handle() {
		logger.info("进入查询会员彩贝页面");
		if (memberCredit == null || memberCredit.getUid() == 0) {
			logger.error("查询会员彩贝，uid不能空");
			super.setErrorMessage("uid不能空");
			return "failure";
		}
		try {
			MemberCredit memberCreditTmp = memberCreditService.get(memberCredit.getUid());
			if (memberCreditTmp != null) {
				memberCredit = memberCreditTmp;
			}
			String username = memberService.getUserNameById(memberCredit.getUid());
			memberCredit.setUsername(username);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		logger.info("查询会员列表结束");
		return "view";
	}
	
	public String add() {
		logger.info("进入赠送会员彩贝");
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject obj = new JSONObject();
		if (memberCredit != null && memberCredit.getUid() != 0 && presentAmount != 0) {
			try {
				boolean rs = memberCreditService.add(memberCredit.getUid(), presentAmount, presentRemark);
				if (rs) {
					obj.put("rs", "1");
					obj.put("message", "success");
					MemberCredit mc = memberCreditService.get(memberCredit.getUid());
					obj.put("credit", JSONObject.fromObject(mc));
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				obj.put("rs", "0");
				obj.put("message", e.getMessage());
			}
		} else {
			obj.put("rs", "0");
			obj.put("message", "参数错误");
		}
		writeRs(response, obj);
		logger.info("赠送会员彩贝结束");
		return Action.NONE;
	}
	
	public String deduct() {
		logger.info("进入扣除会员彩贝");
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject obj = new JSONObject();
		if (memberCredit != null && memberCredit.getUid() != 0 && deductAmount != 0) {
			try {
				boolean rs = memberCreditService.deduct(memberCredit.getUid(), deductAmount, deductRemark);
				if (rs) {
					obj.put("rs", "1");
					obj.put("message", "success");
					MemberCredit mc = memberCreditService.get(memberCredit.getUid());
					obj.put("credit", JSONObject.fromObject(mc));
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				obj.put("rs", "0");
				obj.put("message", e.getMessage());
			}
		} else {
			obj.put("rs", "0");
			obj.put("message", "参数错误");
		}
		writeRs(response, obj);
		logger.info("扣除会员彩贝结束");
		return Action.NONE;
	}
	
	public MemberCreditService getMemberCreditService() {
		return memberCreditService;
	}

	public void setMemberCreditService(MemberCreditService memberCreditService) {
		this.memberCreditService = memberCreditService;
	}

	public MemberCredit getMemberCredit() {
		return memberCredit;
	}

	public void setMemberCredit(MemberCredit memberCredit) {
		this.memberCredit = memberCredit;
	}

	public long getPresentAmount() {
		return presentAmount;
	}

	public void setPresentAmount(long presentAmount) {
		this.presentAmount = presentAmount;
	}

	public long getDeductAmount() {
		return deductAmount;
	}

	public void setDeductAmount(long deductAmount) {
		this.deductAmount = deductAmount;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public String getPresentRemark() {
		return presentRemark;
	}

	public void setPresentRemark(String presentRemark) {
		this.presentRemark = presentRemark;
	}

	public String getDeductRemark() {
		return deductRemark;
	}

	public void setDeductRemark(String deductRemark) {
		this.deductRemark = deductRemark;
	}
}
