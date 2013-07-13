package web.action.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class UsernameUseridConvertAction extends BaseAction {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	
	private String usernameStr;
	private String useridStr;
	private MemberService memberService;
	private List<Member> members;
	
	public String handle() {
		return "list";
	}
	
	public String convert() {
		logger.info("进入用户名用户ID转换");
		
		if (useridStr != null && !useridStr.equals("")) {
			String[] uidArray = useridStr.split("\\,");
			List<String> uidList = new ArrayList<String>();
			for (int i = 0; i < uidArray.length; i++) {
				uidList.add(uidArray[i]);
			}
			try {
				members = memberService.getMembersByUids(uidList);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("通过用户名获取用户信息,api调用异常" + e.getMessage());
				super.setErrorMessage("通过用户名获取用户信息失败,api调用异常" + e.getMessage());
				return "failure";
			}
		}
		if (members == null) {
			members = new ArrayList<Member>();
		}
		if (usernameStr != null && !usernameStr.equals("")) {
			String[] usernameArray = usernameStr.split("\\,");
			List<String> usernameList = new ArrayList<String>();
			for (int i = 0; i < usernameArray.length; i++) {
				usernameList.add(usernameArray[i]);
			}
			Map<String, String> map;
			try {
				map = memberService.getUidsByUsernamesForConvert(usernameList);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("通过用户名获取用户id,api调用异常" + e.getMessage());
				super.setErrorMessage("通过用户名获取用户id失败,api调用异常" + e.getMessage());
				return "failure";
			}
			if (map != null && map.size() > 0) {
				for(String key : map.keySet()) {  
					if (Long.parseLong(map.get(key)) != 0) {
						Member m = new Member();
						m.setUsername(key);
						m.setUid(Long.parseLong(map.get(key)));
						members.add(m);
					}
				}
			}
		}
		
		logger.info("结束用户名用户ID转换");
		return "list";
	}

	public String getUsernameStr() {
		return usernameStr;
	}

	public void setUsernameStr(String usernameStr) {
		this.usernameStr = usernameStr;
	}

	public String getUseridStr() {
		return useridStr;
	}

	public void setUseridStr(String useridStr) {
		this.useridStr = useridStr;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

}
