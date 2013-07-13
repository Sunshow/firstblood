package web.action.business;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.IdType;
import com.opensymphony.xwork2.Action;

public class MemberInfoAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private long uid;
	
	private String username;
	
	private String modifyProp;
	
	private String newValue;

    private Integer newIdTypeValue;
	
	private Member member;
	
	private String checkedStr;
	private int checkedValue;
	
	private MemberService memberService;
	
	private String errorMessage;
	
	public String handle() {
		logger.info("进入用户信息修改");
		return "view";
	}
	
	public String get() {
		logger.info("进入查询用户信息");
		try {
			this.member = memberService.get(username);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询用户信息，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (this.member == null) {
			this.errorMessage = "用户名" + this.username + "不存在，获取用户信息失败";
			logger.error(this.errorMessage);
			super.setErrorMessage(errorMessage);
			return "failure";
		}
		logger.info("查询用户信息结束");
		return "view";
	}

	public String modify() {
		logger.info("进入更新用户信息");
		ResultBean resultBean;

        if (modifyProp.equals(Member.SET_ID_TYPE)) {
            newValue = newIdTypeValue.toString();
        }

		try {
			resultBean = this.memberService.update(this.uid, modifyProp, newValue, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("更新用户信息，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (resultBean.getCode()!= ApiConstant.RC_SUCCESS) {
			this.errorMessage = "修改用户名信息失败 ,原因：" + resultBean.getMessage() ;
			logger.error(this.errorMessage);
			super.setErrorMessage(errorMessage);
			return "failure";
		}
		try {
			this.member = this.memberService.get(uid);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询用户信息，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		super.setForwardUrl("/business/memberInfo.do?action=get&username="+this.member.getUsername());
		logger.info("更新用户信息结束");
		return "success";
	}
	
	public String updateChecked() {
		String prop = checkedStr.equals(Member.SET_PHONE_CHECKED) ? Member.SET_PHONE_CHECKED : Member.SET_EMAIL_CHECKED;
		
		JSONObject obj = new JSONObject();
		HttpServletResponse response = ServletActionContext.getResponse();
		YesNoStatus updateCheckedStatus = YesNoStatus.YES.getValue() == checkedValue ? YesNoStatus.NO: YesNoStatus.YES;
		try {
			memberService.update(this.uid, prop, updateCheckedStatus.getValue() + "", null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("更新用户信息，api调用异常，{}", e.getMessage());
			obj.put("rs", false);
			writeRs(response, obj);
			return Action.NONE;
		}
		obj.put("rs", true);
		obj.put("data", updateCheckedStatus);
		writeRs(response, obj);
		return Action.NONE;
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
	public String getModifyProp() {
		return modifyProp;
	}
	public void setModifyProp(String modifyProp) {
		this.modifyProp = modifyProp;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getCheckedStr() {
		return checkedStr;
	}

	public void setCheckedStr(String checkedStr) {
		this.checkedStr = checkedStr;
	}

	public int getCheckedValue() {
		return checkedValue;
	}

	public void setCheckedValue(int checkedValue) {
		this.checkedValue = checkedValue;
	}

    public List<IdType> getIdTypeList() {
        return IdType.getItems();
    }

    public Integer getNewIdTypeValue() {
        return newIdTypeValue;
    }

    public void setNewIdTypeValue(Integer newIdTypeValue) {
        this.newIdTypeValue = newIdTypeValue;
    }
}
