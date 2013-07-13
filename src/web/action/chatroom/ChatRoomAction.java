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
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.chatroom.ChatRoom;
import com.lehecai.admin.web.domain.chatroom.ChatRoomConstants;
import com.lehecai.admin.web.domain.chatroom.ChatRoomState;
import com.lehecai.admin.web.domain.chatroom.ChatRoomStateSpecification;
import com.lehecai.admin.web.service.chatroom.ChatRoomService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author chirowong
 * 
 */
public class ChatRoomAction extends BaseAction {

	private static final long serialVersionUID = 312393979027007942L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<ChatRoom> chatRoomList;
	private ChatRoom chatRoom;

	private ChatRoomService chatRoomService;
	private String closePeriod;

	@SuppressWarnings("unchecked")
	public String handle() {
		logger.info("进入聊天室查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map = null;

		try {
			if (chatRoom == null) {
				chatRoom = new ChatRoom();
			}
			chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_CHAT_ROOM_LIST);
			map = chatRoomService.queryChatRoomList(chatRoom, super.getPageBean());
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
		chatRoomList = (List<ChatRoom>) map.get(Global.API_MAP_KEY_LIST);
		PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		logger.info("查询聊天室结束");
		return "list";
	}

	public String addChatRoom() {
		if (chatRoom != null && chatRoom.getChatRoomId() != null) {
			try {
				chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_FIND_CHAT_ROOM);
				chatRoom = chatRoomService.getChatRoomInfo(chatRoom);
				closePeriod = ChatRoomState.chatRoomStateSpecificationListToString(chatRoom.getChatRoomState().getChatRoomStateSpecificationList());

				if (closePeriod.equals("false")) {
					logger.error("返回时间格式错误！");
					super.setErrorMessage("返回时间格式错误！");
					return "failure";
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询聊天室,api调用异常" + e.getMessage());
				super.setErrorMessage("查询聊天室,api调用异常" + e.getMessage());
				return "failure";
			}
		}
		return "input";
	}

	public String manage() {
		logger.info("进入聊天室保存开始");
		try {
			if (chatRoom == null) {
				chatRoom = new ChatRoom();
			}
			if (chatRoom.getChatRoomId() != null) {
				chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_UPDATE_CHAT_ROOM);
			} else {
				chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_ADD_CHAT_ROOM);
			}
			if (!StringUtils.isEmpty(closePeriod)) {
				String[] closePeriods = closePeriod.split(",");
				List<ChatRoomStateSpecification> chatRoomStateSpecificationList = new ArrayList<ChatRoomStateSpecification>();

				for (String closeTime : closePeriods) {
					String[] close = closeTime.split("\\|");
					ChatRoomStateSpecification chatRoomStateSpecification = new ChatRoomStateSpecification();
					chatRoomStateSpecification.setStart(Integer.parseInt(close[0].replace(":", "")));
					chatRoomStateSpecification.setEnd(Integer.parseInt(close[1].replace(":", "")));
					chatRoomStateSpecificationList.add(chatRoomStateSpecification);
				}
				chatRoom.getChatRoomState().setChatRoomStateSpecificationList(chatRoomStateSpecificationList);
			}
			ResultBean rb = chatRoomService.manageChatRoom(chatRoom);

			if (!rb.isResult()) {
				logger.error("查询聊天室,api调用异常" + rb.getMessage());
				super.setErrorMessage("查询聊天室,api调用异常" + rb.getMessage());
				return "failure";
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询聊天室,api调用异常" + e.getMessage());
			super.setErrorMessage("查询聊天室,api调用异常" + e.getMessage());
			return "failure";
		}
		logger.info("进入聊天室保存结束");
		return "success";
	}

	public String closeChatRoom() {
		logger.info("关闭聊天室开始");

		if (chatRoom.getChatRoomId() == null) {
			logger.error("聊天室编码不能为空");
			super.setErrorMessage("聊天室编码不能为空");
			return "failure";
		}
		try {
			chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_CLOSE_CHAT_ROOM);
			chatRoomService.manageChatRoom(chatRoom);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("关闭聊天室,api调用异常" + e.getMessage());
			super.setErrorMessage("关闭聊天室,api调用异常" + e.getMessage());
			return "failure";
		}
		super.setForwardUrl("/chatroom/chatRoom.do");
		logger.info("关闭聊天室结束");
		return "forward";
	}

	public String openChatRoom() {
		logger.info("开启聊天室开始");

		if (chatRoom.getChatRoomId() == null) {
			logger.error("聊天室编码不能为空");
			super.setErrorMessage("聊天室编码不能为空");
			return "failure";
		}
		try {
			chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_OPEN_CHAT_ROOM);
			chatRoomService.manageChatRoom(chatRoom);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("开启聊天室,api调用异常" + e.getMessage());
			super.setErrorMessage("关闭聊天室,api调用异常" + e.getMessage());
			return "failure";
		}
		super.setForwardUrl("/chatroom/chatRoom.do");
		logger.info("开启聊天室结束");
		return "forward";
	}

	public List<ChatRoom> getChatRoomList() {
		return chatRoomList;
	}

	public void setChatRoomList(List<ChatRoom> chatRoomList) {
		this.chatRoomList = chatRoomList;
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

	public List<YesNoStatus> getYesNoStatuses() {
		return YesNoStatus.getItems();
	}

	public String getClosePeriod() {
		return closePeriod;
	}

	public void setClosePeriod(String closePeriod) {
		this.closePeriod = closePeriod;
	}
}