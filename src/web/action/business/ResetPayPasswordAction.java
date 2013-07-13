package web.action.business;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class ResetPayPasswordAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	
	private String username;
	
	private String newPwd;
	
	private String errorMessage;
	
	private MemberService memberService;
	
	public String handle(){
		return "view";
	}
	
	public String reset() {
		logger.info("进入重置密码");
		Member member;
		try {
			member = this.memberService.get(this.username);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取用户信息，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (member == null) {
			this.errorMessage = "用户名不存在，重置用户密码失败";
			logger.error(this.errorMessage);
			super.setErrorMessage(errorMessage);
			return "failure";
		}
		try {
			this.newPwd = this.memberService.resetPayPassword(member.getUid());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("重置会员密码，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，原因:" + e.getMessage());
			return "failure";
		}
		if (this.newPwd == null) {
			this.errorMessage = "重置用户名为"+this.username + "密码失败";
			logger.error(errorMessage);
			super.setErrorMessage(errorMessage);
			return "failure";
		}
		logger.info("重置密码结束");
		return "view";
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
