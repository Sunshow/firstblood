/**
 * 
 */
package web.action.chatroom;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.chatroom.ChatRoom;
import com.lehecai.admin.web.domain.chatroom.ChatRoomConstants;
import com.lehecai.admin.web.domain.chatroom.ChatRoomNotice;
import com.lehecai.admin.web.service.chatroom.ChatRoomService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author chirowong
 *
 */
public class ChatRoomNoticeAction extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<ChatRoomNotice> chatRoomNoticeList;
	private ChatRoom chatRoom;
	private ChatRoomNotice chatRoomNotice;
	
	private ChatRoomService chatRoomService;
	
	@SuppressWarnings("unchecked")
	public String handle(){
		logger.info("进入聊天室公告列表查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map = null;
		try {
			if(chatRoom == null){
				chatRoom = new ChatRoom();
			}
			chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_NOTICE_LIST);
			map = chatRoomService.queryChatRoomNoticeList(chatRoom,super.getPageBean());
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
		chatRoomNoticeList = (List<ChatRoomNotice>) map.get(Global.API_MAP_KEY_LIST);
		PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		logger.info("查询聊天室结束");
		return "noticeList";
	}
	
	public String addNotice(){
		if(chatRoom != null && chatRoom.getNoticeId() != null){
			try {
				chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_FIND_NOTICE);
				chatRoomNotice = chatRoomService.getChatRoomNoticeInfo(chatRoom);
				chatRoom.setLevel(chatRoomNotice.getLevel());
				chatRoom.setNoticeType(chatRoomNotice.getNoticeType());
				chatRoom.setChatRoomName(chatRoomNotice.getChatRoom().getChatRoomName());
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询聊天室,api调用异常" + e.getMessage());
				super.setErrorMessage("查询聊天室,api调用异常" + e.getMessage());
				return "failure";
			}
		}
		return "addNotice";
	}
	
	public String addNoticeManage(){
		logger.info("进入聊天室保存开始");
		try {
			if(chatRoom == null){
				chatRoom = new ChatRoom();
			}
			if(chatRoom.getNoticeId() != null){
				chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_UPDATE_NOTICE);
			}else{
				chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_ADD_NOTICE);
			}
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
		logger.info("进入聊天室保存结束");
		return "success";
	}
	
	public String removeNotice(){
		logger.info("删除公告开始");
		if(chatRoom.getNoticeId() == null){
			logger.error("公告编码不能为空");
			super.setErrorMessage("公告编码不能为空");
			return "failure";
		}
		try {
			chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_REMOVE_NOTICE);
			chatRoomService.manageChatRoom(chatRoom);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("删除公告,api调用异常" + e.getMessage());
			super.setErrorMessage("删除公告,api调用异常" + e.getMessage());
			return "failure";
		}
		super.setForwardUrl("/chatroom/chatRoomNotice.do");
		logger.info("删除公告结束");
		return "forward";
	}

	public List<ChatRoomNotice> getChatRoomNoticeList() {
		return chatRoomNoticeList;
	}

	public void setChatRoomNoticeList(List<ChatRoomNotice> chatRoomNoticeList) {
		this.chatRoomNoticeList = chatRoomNoticeList;
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

	public ChatRoomNotice getChatRoomNotice() {
		return chatRoomNotice;
	}

	public void setChatRoomNotice(ChatRoomNotice chatRoomNotice) {
		this.chatRoomNotice = chatRoomNotice;
	}
	
	@SuppressWarnings("unchecked")
	public List<ChatRoom> getChatRoomList(){
		Map<String, Object> map = null;
		try {
			if(chatRoom == null){
				chatRoom = new ChatRoom();
			}
			chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_CHAT_ROOM_LIST);
			map = chatRoomService.queryChatRoomList(chatRoom,super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询聊天室,api调用异常" + e.getMessage());
		}
		return (List<ChatRoom>) map.get(Global.API_MAP_KEY_LIST);
	}
}
