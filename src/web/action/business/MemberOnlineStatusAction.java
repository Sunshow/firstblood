package web.action.business;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.business.MemberOnlineStatusService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.api.user.MemberOnlineStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 会员在线状态Action
 * @author yanweijie
 *
 */
public class MemberOnlineStatusAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(MemberOnlineStatusAction.class);
	
	private MemberOnlineStatusService memberOnlineStatusService;
	private MemberService memberService;
	
	private List<MemberOnlineStatus> memberOnlineStatusList;
	
	private Long uid;			//会员编号
	private String userName;	//会员用户名
	private String key;			//
	private Integer type;		//
	
	/**
	 * 转向会员在线状态列表
	 * @return
	 */
	public String handle () {
		return "list";
	}
	
	/**
	 * 根据会员编号或者用户名查询会员在线状态
	 * @return
	 */
	public String findMemberOnlineStatus() {
		logger.info("进入查询会员在线状态列表");
		if (userName != null && !userName.equals("")) {
			try {
				uid = memberService.getIdByUserName(userName);//根据会员用户名查询会员编号
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询会员编码，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
		}
		
		if (uid == null) {
			logger.error("查询会员在线状态，必要参数为空");
			super.setErrorMessage("查询会员在线状态，必要参数为空");
			return "failure";
		}
		
		try {
			memberOnlineStatusList = memberOnlineStatusService.findOnlineStatusByUid(uid);//根据会员编号查询会员在线状态
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询会员在线状态，api调用异常");
			super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
			return "failure";
		}
		logger.info("查询会员在线状态列表结束");
		return "list";
	}
	

	/**
	 * 删除会员全部登陆点
	 * @return
	 */
	public String deleteAllLogin() {
		logger.info("进入删除会员全部登录点");
		if (uid == null) {
			logger.error("删除会员全部登录点，必要参数为空");
			super.setErrorMessage("删除会员全部登录点，必要参数为空");
			return "failure";
		}
		
		boolean deleteResult = false;
		try {
			deleteResult = memberOnlineStatusService.deleteAllLogin(uid);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("删除会员所有登陆点，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
			return "failure";
		}
		
		if (!deleteResult) {
			logger.error("删除会员登录点失败");
			super.setErrorMessage("删除会员登录点失败");
			return "failure";
		}
		
		super.setForwardUrl("/business/memberOnlineStatus.do?action=findMemberOnlineStatus&userName=" + userName + "&uid=" + uid);
		return "forward";
	}
	

	/**
	 * 删除会员登陆点
	 * @return
	 */
	public String deleteLogin() {
		logger.info("进入删除会员单一登录点");
		if ((key != null && !key.equals("")) && (type != null)) {
			boolean deleteResult = false;
			try {
				deleteResult = memberOnlineStatusService.deleteLoing(key, type);	//删除会员登录点
			} catch (ApiRemoteCallFailedException e) {
				logger.error("删除会员登陆点，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
			
			if (!deleteResult) {
				logger.error("删除会员登录点失败");
				super.setErrorMessage("删除会员登录点失败");
				return "failure";
			}
			
			super.setForwardUrl("/business/memberOnlineStatus.do?action=findMemberOnlineStatus&userName=" + userName +"&uid=" + uid);
			return "forward";
		} else {
			logger.error("删除会员登录点，必须的参数为空");
			super.setErrorMessage("删除会员登录点，必须的参数为空");
			return "failure";
		}
	}
	
	public MemberOnlineStatusService getMemberOnlineStatusService() {
		return memberOnlineStatusService;
	}

	public void setMemberOnlineStatusService(
			MemberOnlineStatusService memberOnlineStatusService) {
		this.memberOnlineStatusService = memberOnlineStatusService;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public List<MemberOnlineStatus> getMemberOnlineStatusList() {
		return memberOnlineStatusList;
	}

	public void setMemberOnlineStatusList(
			List<MemberOnlineStatus> memberOnlineStatusList) {
		this.memberOnlineStatusList = memberOnlineStatusList;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
