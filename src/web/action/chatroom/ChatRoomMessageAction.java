/**
 * 
 */
package web.action.chatroom;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.chatroom.ChatRoom;
import com.lehecai.admin.web.domain.chatroom.ChatRoomConstants;
import com.lehecai.admin.web.domain.chatroom.ChatRoomMessage;
import com.lehecai.admin.web.service.chatroom.ChatRoomService;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author chirowong
 *
 */
public class ChatRoomMessageAction extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<ChatRoom> chatRoomList;
	private ChatRoom chatRoom;
	
	private ChatRoomService chatRoomService;
	private List<ChatRoomMessage> chatRoomMessageList;
	@SuppressWarnings("unchecked")
	public String handle(){
		logger.info("进入聊天室信息查询");
		Map<String, Object> map = null;
		try {
			if(chatRoom == null){
				chatRoom = new ChatRoom();
			}
			chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_CHAT_ROOM_LIST);
			map = chatRoomService.queryChatRoomList(chatRoom,super.getPageBean());
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
		logger.info("查询聊天室结束");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query(){
		logger.info("进入查询聊天室信息开始");
		HttpServletResponse response = ServletActionContext.getResponse();
		if (chatRoom == null || chatRoom.getChatRoomId() == null) {
			logger.error("房间信息错误");
			JSONObject obj = new JSONObject();
			obj.put("rs", false);
			obj.put("msg", "房间信息错误");
			super.writeRs(response, obj);
			logger.info("进入查询聊天室信息结束");
			return null;
		}
		
		Map<String, Object> map = null;
		try {
			chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_MESSAGE_LIST);
			map = chatRoomService.queryChatRoomMessageList(chatRoom,null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询聊天室信息列表,api调用异常" + e.getMessage());
			JSONObject obj = new JSONObject();
			obj.put("rs", false);
			obj.put("msg", "信息列表,api调用异常");
			super.writeRs(response, obj);
			logger.info("进入查询聊天室信息结束");
			return null;
		}
		
		if (map == null || map.size() == 0) {
			logger.error("API查询聊天室信息为空");
			JSONObject obj = new JSONObject();
			obj.put("rs", false);
			obj.put("msg", "API查询聊天室信息为空");
			super.writeRs(response, obj);
			logger.info("进入查询聊天室信息结束");
			return null;
		}
		
		
		chatRoomMessageList = (List<ChatRoomMessage>) map.get(Global.API_MAP_KEY_LIST);
		if(chatRoomMessageList != null && chatRoomMessageList.size() > 0){
			JSONArray array = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("rs", true);
			for(ChatRoomMessage chatRoomMessage : chatRoomMessageList){
				array.add(ChatRoomMessage.covertToString(chatRoomMessage));
			}
			obj.put("data", array.toString());
			super.writeRs(response, obj);
		}
		logger.info("进入查询聊天室信息结束");
		return null;
	}
	
	public String addMessage(){
		logger.info("进入发送消息");
		return "addMessage";
	}
	
	
	public String sendMessage(){
		logger.info("发送消息开始");
		if(chatRoom == null){
			logger.error("聊天室信息为空");
			super.setErrorMessage("聊天室信息为空");
			return "failure";
		}
		try {
			chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_SEND_MESSAGE);
			chatRoomService.manageMessage(chatRoom);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询聊天室,api调用异常" + e.getMessage());
			super.setErrorMessage("查询聊天室,api调用异常" + e.getMessage());
			return "failure";
		}
		logger.info("发送消息结束");
		super.setSuccessMessage("发送消息成功！");
		return "success";
	}
	
	public String messageManage(){
		logger.info("进入聊天室保存开始");
		try {
			if(chatRoom == null){
				chatRoom = new ChatRoom();
			}
			chatRoom.setActionType(ChatRoomConstants.ACTION_TYPE_API_REMOVE_MESSAGE);
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
	
	public List<ChatRoomMessage> getChatRoomMessageList() {
		return chatRoomMessageList;
	}

	public void setChatRoomMessageList(List<ChatRoomMessage> chatRoomMessageList) {
		this.chatRoomMessageList = chatRoomMessageList;
	}

	@SuppressWarnings("unchecked")
	public List<ChatRoom> getChatRooms(){
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
