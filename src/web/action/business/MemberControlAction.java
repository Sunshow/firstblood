package web.action.business;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.MemberStatus;
import com.opensymphony.xwork2.Action;

public class MemberControlAction extends BaseAction{

	private static final long serialVersionUID = 1L;
	protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private static final int RETURN_SUCCESS_CODE = 0;
	private static final int RETURN_ERROR_CODE = 1;
	
	private String username;
	private Long userId;
	private Member member;
	private MemberService memberService;
	
	public String handle() {
		logger.info("进入用户状态控制");
		return "view";
	}
	
	public String get() {
		logger.info("进入查询用户信息");
		
		if ((username == null || "".equals(username)) && (userId == null || userId == 0L)) {
			logger.error("用户名和用户ID不能都为空");
			super.setErrorMessage("用户名和用户ID不能都为空");
			return "failure";
		}
		
		if (userId == null || userId == 0L) {
			try {
				member = memberService.get(username);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("通过用户名获取用户信息,api调用异常" + e.getMessage());
				super.setErrorMessage("通过用户名获取用户信息失败,api调用异常" + e.getMessage());
				return "failure";
			}
		} else {
			try {
				member = memberService.get(userId);
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
		logger.info("查询用户信息结束");
		return "view";
	}
	
	public String recover() {
		logger.info("进入更新用户状态为正常状态");
		int code = RETURN_SUCCESS_CODE;
		String msg = "";
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		if(!checkMemberIsSystem()){
			code = RETURN_ERROR_CODE;
			msg = "用户为系统用户，不能修改用户状态";
			json.put("code", code);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		try {
			memberService.update(userId, Member.SET_STATUS, null, MemberStatus.NORMAL);
		} catch (ApiRemoteCallFailedException e) {
			code = RETURN_ERROR_CODE;
			msg = "更新用户状态,api调用异常";
			logger.error("更新用户状态,api调用异常" + e.getMessage());
			super.setErrorMessage("更新用户状态,api调用异常" + e.getMessage());
			json.put("code", code);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		msg = "更新用户状态成功";
		json.put("code", code);
		json.put("msg", msg);
		writeRs(response, json);
		logger.info("结束更新用户状态");
		return Action.NONE;
	}
	
	public String lock() {
		logger.info("进入更新用户状态为锁定状态");
		int code = RETURN_SUCCESS_CODE;
		String msg = "";
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		if(!checkMemberIsSystem()){
			code = RETURN_ERROR_CODE;
			msg = "用户为系统用户，不能修改用户状态";
			json.put("code", code);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		try {
			memberService.update(userId, Member.SET_STATUS, null, MemberStatus.LOCKED);
		} catch (ApiRemoteCallFailedException e) {
			code = RETURN_ERROR_CODE;
			msg = "更新用户状态,api调用异常";
			logger.error("更新用户状态,api调用异常" + e.getMessage());
			super.setErrorMessage("更新用户状态,api调用异常" + e.getMessage());
			json.put("code", code);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		msg = "更新用户状态成功";
		json.put("code", code);
		json.put("msg", msg);
		writeRs(response, json);
		logger.info("结束更新用户状态");
		return Action.NONE;
	}
	
	public String disable() {
		logger.info("进入更新用户状态为禁用状态");
		int code = RETURN_SUCCESS_CODE;
		String msg = "";
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		if(!checkMemberIsSystem()){
			code = RETURN_ERROR_CODE;
			msg = "用户为系统用户，不能修改用户状态";
			json.put("code", code);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		try {
			memberService.update(userId, Member.SET_STATUS, null, MemberStatus.DISABLED);
		} catch (ApiRemoteCallFailedException e) {
			code = RETURN_ERROR_CODE;
			msg = "更新用户状态,api调用异常";
			logger.error("更新用户状态,api调用异常" + e.getMessage());
			super.setErrorMessage("更新用户状态,api调用异常" + e.getMessage());
			json.put("code", code);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		msg = "更新用户状态成功";
		json.put("code", code);
		json.put("msg", msg);
		writeRs(response, json);
		logger.info("结束更新用户状态");
		return Action.NONE;
	}
	
	public String logoff() {
		logger.info("进入更新用户状态为注销状态");
		int code = RETURN_SUCCESS_CODE;
		String msg = "";
		JSONObject json = new JSONObject();
		HttpServletResponse response = ServletActionContext.getResponse();
		if(!checkMemberIsSystem()){
			code = RETURN_ERROR_CODE;
			msg = "用户为系统用户，不能修改用户状态";
			json.put("code", code);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		try {
			memberService.update(userId, Member.SET_STATUS, null, MemberStatus.DELETED);
		} catch (ApiRemoteCallFailedException e) {
			code = RETURN_ERROR_CODE;
			msg = "更新用户状态,api调用异常";
			logger.error("更新用户状态,api调用异常" + e.getMessage());
			super.setErrorMessage("更新用户状态,api调用异常" + e.getMessage());
			json.put("code", code);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		msg = "更新用户状态成功";
		json.put("code", code);
		json.put("msg", msg);
		writeRs(response, json);
		logger.info("结束更新用户状态");
		return Action.NONE;
	}
	
	public String system() {
		logger.info("进入更新用户状态为系统用户");
		int code = RETURN_SUCCESS_CODE;
		String msg = "";
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		
		try {
			memberService.update(userId, Member.SET_STATUS, null, MemberStatus.SYSTEM);
		} catch (ApiRemoteCallFailedException e) {
			code = RETURN_ERROR_CODE;
			msg = "更新用户状态,api调用异常";
			logger.error("更新用户状态,api调用异常" + e.getMessage());
			super.setErrorMessage("更新用户状态,api调用异常" + e.getMessage());
			json.put("code", code);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		msg = "更新用户状态成功";
		json.put("code", code);
		json.put("msg", msg);
		writeRs(response, json);
		logger.info("结束更新用户状态");
		return Action.NONE;
	}
	
	private boolean checkMemberIsSystem() {
		Member member = null;
		try {
			member = memberService.get(userId);
		}catch(ApiRemoteCallFailedException e){
			return false;
		}
		if(member != null){
			if(member.getStatus().getValue() == MemberStatus.SYSTEM.getValue()){
				return false;
			}else{
				return true;
			}
		}else{
			return false;
		}
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public MemberStatus getMemberStatusNormal() {
		return MemberStatus.NORMAL;
	}
	
	public MemberStatus getMemberStatusLocked() {
		return MemberStatus.LOCKED;
	}
	
	public MemberStatus getMemberStatusDisabled() {
		return MemberStatus.DISABLED;
	}
	
	public MemberStatus getMemberStatusSystem(){
		return MemberStatus.SYSTEM;
	}
}
