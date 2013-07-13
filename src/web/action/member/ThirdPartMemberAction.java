package web.action.member;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.member.ThirdPartMemberService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.user.ThirdPartMember;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ThirdPartMemberType;

public class ThirdPartMemberAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(ThirdPartMemberAction.class);
	private static final long serialVersionUID = 2436161530465382824L;

	private ThirdPartMemberService thirdPartMemberService;
	
	private List<ThirdPartMember> thirdParthMemberList;
	
	private Long uid;
	private Long ruid;
	private Integer memberTypeValue;
	private String rusername;
	private String username;
	private MemberService memberService;
	
	public String handle(){
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query(){
		HttpServletRequest request = ServletActionContext.getRequest();
		
		ThirdPartMemberType mt = memberTypeValue == null ? null : ThirdPartMemberType.getItem(memberTypeValue);
		
		if ((uid == null || uid == 0) && username != null && !username.equals("")) {
			try {
				uid = memberService.getIdByUserName(username);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("根据用户名查询用户Id错误" + e.getMessage(), e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
			if (uid == null || uid == 0) {
				logger.error("不存在的用户名");
				super.setErrorMessage("不存在的用户名");
				return "failure";
			} 
		}
		
		Map<String, Object> map;
		try {
			map = thirdPartMemberService.queryResult(uid, ruid, rusername, mt, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if(map != null){
			thirdParthMemberList = (List<ThirdPartMember>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		
		return "list";
	}
	
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Long getRuid() {
		return ruid;
	}
	public void setRuid(Long ruid) {
		this.ruid = ruid;
	}
	public Integer getMemberTypeValue() {
		return memberTypeValue;
	}
	public void setMemberTypeValue(Integer memberTypeValue) {
		this.memberTypeValue = memberTypeValue;
	}
	public String getRusername() {
		return rusername;
	}
	public void setRusername(String rusername) {
		this.rusername = rusername;
	}
	public List<ThirdPartMember> getThirdParthMemberList() {
		return thirdParthMemberList;
	}
	public void setThirdParthMemberList(List<ThirdPartMember> thirdParthMemberList) {
		this.thirdParthMemberList = thirdParthMemberList;
	}

	public ThirdPartMemberService getThirdPartMemberService() {
		return thirdPartMemberService;
	}

	public void setThirdPartMemberService(
			ThirdPartMemberService thirdPartMemberService) {
		this.thirdPartMemberService = thirdPartMemberService;
	}
	public List<ThirdPartMemberType> getMemberTypes(){
		return ThirdPartMemberType.getItems();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

}
