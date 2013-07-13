/**
 * 
 */
package web.action.chatroom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.chatroom.BanReason;
import com.lehecai.admin.web.domain.chatroom.ChatRoom;
import com.lehecai.admin.web.domain.chatroom.ChatRoomAccountBanState;
import com.lehecai.admin.web.domain.chatroom.ChatRoomBan;
import com.lehecai.admin.web.domain.chatroom.ChatRoomConstants;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.service.chatroom.ChatRoomService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.user.PermissionService;
import com.lehecai.admin.web.service.user.UserLevelService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.api.user.MemberLevel;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author chirowong
 *
 */
public class ChatRoomBanAction extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<ChatRoomBan> chatRoomBanList;
	private ChatRoom chatRoom;
	private ChatRoomAccountBanState chatRoomAccountBanState;
	
	private ChatRoomService chatRoomService;
	private MemberService memberService;
	private UserLevelService userLevelService;
	private PermissionService permissionService;
	private Member member;
	private MemberLevel memberLevel;
	private String userName;
	private Integer reason;
	private String otherReason;
	
	@SuppressWarnings("unchecked")
	public String handle(){
		logger.info("进入聊天室禁言列表查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map = null;
		try {
			if(chatRoom == null){
				chatRoom = new ChatRoom();
			}
			chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_BAN_SPECIFICATION_LIST);
			map = chatRoomService.queryChatRoomBanList(chatRoom,super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询聊天室,api调用异常" + e.getMessage());
			super.setErrorMessage("查询聊天室,api调用异常" + e.getMessage());
			return "failure";
		}
		if (map == null || map.size() == 0) {
			logger.error("API查询聊天室为空");
			super.setErrorMessage("API查询聊天室为空");
			return "failure";
		}
		Map<Long, User> userMap = permissionService.userMapping();
		chatRoomBanList = (List<ChatRoomBan>) map.get(Global.API_MAP_KEY_LIST);
		if (chatRoomBanList != null) {
			for (ChatRoomBan chatRoomBan : chatRoomBanList) {
				if (!StringUtils.isEmpty(chatRoomBan.getOperatorId()) && userMap.get(Long.valueOf(chatRoomBan.getOperatorId())) != null) {
					chatRoomBan.setOperatorName(userMap.get(Long.valueOf(chatRoomBan.getOperatorId())).getName());					
				}
			}
		}
		PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		logger.info("查询聊天室结束");
		return "banSpecificationList";
	}
	
	@SuppressWarnings("unchecked")
	public String query(){
		try {
			if(!StringUtils.isEmpty(userName)){
				member = memberService.get(userName);
			}else{
				logger.error("用户名为空");
				super.setErrorMessage("用户名为空");
				return "failure";
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询聊天室,api调用异常" + e.getMessage());
			super.setErrorMessage("查询聊天室,api调用异常" + e.getMessage());
			return "failure";
		}
		
		if(member != null){
			List<MemberLevel> mlList = null;
			List<String> uids = new ArrayList<String>();
			uids.add(String.valueOf(member.getUid()));
			Map<String, Object> levelMap;
			try {
				levelMap = userLevelService.getUsersLevel(new ArrayList<String>(uids));
				mlList = (List<MemberLevel>) levelMap.get(Global.API_MAP_KEY_LIST);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
			if(mlList != null && mlList.size() > 0){
				memberLevel = mlList.get(0);
			}
		}
		return "queryChatRoomBan";
	}
	
	public String banAccount(){
		return "banAccount";
	}
	
	public String banAccountManage(){
		logger.info("进入禁言用户保存开始");
		if(chatRoom == null){
			logger.error("表单为空");
			super.setErrorMessage("表单为空");
			return "failure";
		}
		if(chatRoom.getUsername() == null){
			logger.error("禁言用户名为空");
			super.setErrorMessage("禁言用户名为空");
			return "failure";
		}
		if(reason == null){
			logger.error("没有选择禁言原因");
			super.setErrorMessage("没有选择禁言原因");
			return "failure";
		}
		if(reason.intValue() == 99 && StringUtils.isEmpty(otherReason)){
			logger.error("其他原因必需填写");
			super.setErrorMessage("其他原因必需填写");
			return "failure";
		}
		try {
			chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_BAN_ACCOUNT);
			if(reason.intValue() == BanReason.OTHER.getValue()){
				chatRoom.setReason(otherReason);
			}else{
				chatRoom.setReason(BanReason.getItem(reason.intValue()).getName());
			}
			HttpServletRequest request = ServletActionContext.getRequest();
			UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
			chatRoom.setOperatorId(userSessionBean.getUser().getId() + "");
			ResultBean rb = chatRoomService.manageChatRoom(chatRoom);
			if(!rb.isResult()){
				logger.error("查询聊天室,api调用异常" + rb.getMessage());
				super.setErrorMessage("查询聊天室,api调用异常" + rb.getMessage());
				return "failure";
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询聊天室,api调用异常" + e.getMessage());
			super.setErrorMessage("查询聊天室,api调用异常" + e.getMessage());
			return "failure";
		}
		logger.info("进入禁言用户保存结束");
		return "success";
	}
	
	public String abortBanSpecification(){
		logger.info("进入聊天室保存开始");
		try {
			if(chatRoom == null){
				chatRoom = new ChatRoom();
			}
			chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_ABORT_BAN_SPECIFICATION);
			ResultBean rb = chatRoomService.manageChatRoom(chatRoom);
			if(!rb.isResult()){
				logger.error("查询聊天室,api调用异常" + rb.getMessage());
				super.setErrorMessage("查询聊天室,api调用异常" + rb.getMessage());
				return "failure";
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询聊天室,api调用异常" + e.getMessage());
			super.setErrorMessage("查询聊天室,api调用异常" + e.getMessage());
			return "failure";
		}
		super.setForwardUrl("/chatroom/chatRoomBan.do");
		logger.info("进入聊天室保存结束");
		return "success";
	}
	
	public String accountBanStateInput(){
		return "accountBanStateList";
	}
	
	public String accountBanState(){
		logger.info("进入聊天室禁言列表查询");
		Map<String, Object> map = null;
		try {
			if(chatRoom == null){
				chatRoom = new ChatRoom();
			}
			chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_ACCOUNT_BAN_STATE);
			map = chatRoomService.queryChatRoomAccountBanState(chatRoom,super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询聊天室,api调用异常" + e.getMessage());
			super.setErrorMessage("查询聊天室,api调用异常" + e.getMessage());
			return "failure";
		}
		if (map == null || map.size() == 0) {
			logger.error("API查询聊天室为空");
			super.setErrorMessage("API查询聊天室为空");
			return "failure";
		}
		chatRoomAccountBanState = (ChatRoomAccountBanState) map.get(Global.API_MAP_KEY_LIST);
		logger.info("查询聊天室结束");
		return "accountBanStateList";
	}

	public List<ChatRoomBan> getChatRoomBanList() {
		return chatRoomBanList;
	}

	public void setChatRoomBanList(List<ChatRoomBan> chatRoomBanList) {
		this.chatRoomBanList = chatRoomBanList;
	}

	public ChatRoom getChatRoom() {
		return chatRoom;
	}

	public void setChatRoom(ChatRoom chatRoom) {
		this.chatRoom = chatRoom;
	}

	public ChatRoomService getChatRoomService() {
		return chatRoomService;
	}

	public void setChatRoomService(ChatRoomService chatRoomService) {
		this.chatRoomService = chatRoomService;
	}
	
	public List<YesNoStatus> getYesNoStatuses(){
		return YesNoStatus.getItems();
	}

	public ChatRoomAccountBanState getChatRoomAccountBanState() {
		return chatRoomAccountBanState;
	}

	public void setChatRoomAccountBanState(
			ChatRoomAccountBanState chatRoomAccountBanState) {
		this.chatRoomAccountBanState = chatRoomAccountBanState;
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

	public UserLevelService getUserLevelService() {
		return userLevelService;
	}

	public void setUserLevelService(UserLevelService userLevelService) {
		this.userLevelService = userLevelService;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public MemberLevel getMemberLevel() {
		return memberLevel;
	}

	public void setMemberLevel(MemberLevel memberLevel) {
		this.memberLevel = memberLevel;
	}
	
	public List<BanReason> getBanReasonList(){
		return BanReason.getItems();
	}

	public String getOtherReason() {
		return otherReason;
	}

	public void setOtherReason(String otherReason) {
		this.otherReason = otherReason;
	}

	public Integer getReason() {
		return reason;
	}

	public void setReason(Integer reason) {
		this.reason = reason;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}
}
