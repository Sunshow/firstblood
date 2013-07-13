/**
 * 
 */
package web.domain.chatroom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.lehecai.core.api.AbstractApiResultBean;

/**
 * @author chirowong
 *
 */
public class ChatRoomMessage extends AbstractApiResultBean {
	
	private ChatRoom chatRoom;
	private Long id;
	private ChatRoomMessageVo chatRoomMessageVo;
	private ChatRoomMessageSender chatRoomMessageSender;
	
	
	public static ChatRoomMessage convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		ChatRoomMessage chatRoomMessage = new ChatRoomMessage();
		chatRoomMessage.chatRoom = ChatRoom.convertFromJSONObject(JSONObject.fromObject(getString(object, "chatRoom")));
		chatRoomMessage.id = getLong(object, "id");
		chatRoomMessage.chatRoomMessageVo = ChatRoomMessageVo.convertFromJSONObject(JSONObject.fromObject(getString(object, "messageVo")));
		chatRoomMessage.chatRoomMessageSender = ChatRoomMessageSender.convertFromJSONObject(JSONObject.fromObject(getString(object, "sender")));
		return chatRoomMessage;
	}
	
	public static List<ChatRoomMessage> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<ChatRoomMessage> list = new ArrayList<ChatRoomMessage>();
		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			list.add(convertFromJSONObject(object));
		}
		return list;
	}
	
	public static String covertToString(ChatRoomMessage chatRoomMessage){
		JSONObject object = new JSONObject();
		Long chatRoomId = chatRoomMessage.getChatRoom().getChatRoomId();
		if(chatRoomId != null && chatRoomId.longValue() != 0){
			object.put("chatRoomId", chatRoomId);
		}	
		String chatRoomName = chatRoomMessage.getChatRoom().getChatRoomName();
		if(!StringUtils.isEmpty(chatRoomName)){
			object.put("chatRoomName", chatRoomName);
		}
		Long messageId = chatRoomMessage.getId();
		if(messageId != null && messageId.longValue() != 0){
			object.put("messageId", messageId);
		}		
		String messageContent = chatRoomMessage.getChatRoomMessageVo().getContent();
		if(!StringUtils.isEmpty(messageContent)){
			object.put("messageContent", messageContent);
		}
		String messageCreate = chatRoomMessage.getChatRoomMessageVo().getCreatedDate();
		if(!StringUtils.isEmpty(messageCreate)){
			object.put("messageCreate", messageCreate);
		}
		String sender = chatRoomMessage.getChatRoomMessageSender().getChatRoomBanAccountInfo().getUsername();
		if(!StringUtils.isEmpty(sender)){
			object.put("sender", sender);
		}		
		String senderId = chatRoomMessage.getChatRoomMessageSender().getChatRoomBanAccountInfo().getUserId();
		if(!StringUtils.isEmpty(senderId)){
			object.put("senderId", senderId);
		}
		return object.toString();
	}
	
	public ChatRoom getChatRoom() {
		return chatRoom;
	}

	public void setChatRoom(ChatRoom chatRoom) {
		this.chatRoom = chatRoom;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ChatRoomMessageVo getChatRoomMessageVo() {
		return chatRoomMessageVo;
	}

	public void setChatRoomMessageVo(ChatRoomMessageVo chatRoomMessageVo) {
		this.chatRoomMessageVo = chatRoomMessageVo;
	}

	public ChatRoomMessageSender getChatRoomMessageSender() {
		return chatRoomMessageSender;
	}

	public void setChatRoomMessageSender(ChatRoomMessageSender chatRoomMessageSender) {
		this.chatRoomMessageSender = chatRoomMessageSender;
	}
}
