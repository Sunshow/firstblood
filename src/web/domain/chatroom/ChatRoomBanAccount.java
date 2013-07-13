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
public class ChatRoomBanAccount extends AbstractApiResultBean {
	
	private Long id;
	private ChatRoomBanAccountInfo chatRoomBanAccountInfo;
	
	public static ChatRoomBanAccount convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		ChatRoomBanAccount chatRoomAccount = new ChatRoomBanAccount();
		chatRoomAccount.id = getLong(object, "id");
		chatRoomAccount.chatRoomBanAccountInfo = ChatRoomBanAccountInfo.convertFromJSONObject(JSONObject.fromObject(getString(object, "accountInfo")));
		return chatRoomAccount;
	}
	
	public static List<ChatRoomBanAccount> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<ChatRoomBanAccount> list = new ArrayList<ChatRoomBanAccount>();
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
