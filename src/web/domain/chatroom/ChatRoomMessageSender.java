/**
 * 
 */
package web.domain.chatroom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.lehecai.core.api.AbstractApiResultBean;

/**
 * @author chirowong
 *
 */
public class ChatRoomMessageSender extends AbstractApiResultBean {
	
	private Long id;
	private ChatRoomBanAccountInfo chatRoomBanAccountInfo;
	
	public static ChatRoomMessageSender convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		ChatRoomMessageSender chatRoomMessageSender = new ChatRoomMessageSender();
		chatRoomMessageSender.id = getLong(object, "id");
		chatRoomMessageSender.chatRoomBanAccountInfo = ChatRoomBanAccountInfo.convertFromJSONObject(JSONObject.fromObject(getString(object, "accountInfo")));
		return chatRoomMessageSender;
	}
	
	public static List<ChatRoomMessageSender> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<ChatRoomMessageSender> list = new ArrayList<ChatRoomMessageSender>();
		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			list.add(convertFromJSONObject(object));
		}
		return list;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ChatRoomBanAccountInfo getChatRoomBanAccountInfo() {
		return chatRoomBanAccountInfo;
	}

	public void setChatRoomBanAccountInfo(
			ChatRoomBanAccountInfo chatRoomBanAccountInfo) {
		this.chatRoomBanAccountInfo = chatRoomBanAccountInfo;
	}
}
