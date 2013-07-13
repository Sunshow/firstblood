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
public class ChatRoomNotice extends AbstractApiResultBean {
	
	private ChatRoom chatRoom;
	private Long id;
	private String level;
	private String noticeState;
	private String noticeType;
	private ChatRoomNoticeVo chatRoomNoticeVo;
	
	
	public static ChatRoomNotice convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		ChatRoomNotice chatRoomNotice = new ChatRoomNotice();
		chatRoomNotice.chatRoom = ChatRoom.convertFromJSONObject(JSONObject.fromObject(getString(object, "chatRoom")));
		chatRoomNotice.id = getLong(object, "id");
		chatRoomNotice.level = getString(object, "level");
		chatRoomNotice.noticeState = getString(object, "noticeState");
		chatRoomNotice.noticeType = getString(object, "noticeType");
		chatRoomNotice.chatRoomNoticeVo = ChatRoomNoticeVo.convertFromJSONObject(JSONObject.fromObject(getString(object, "noticeVo")));
		return chatRoomNotice;
	}
	
	public static List<ChatRoomNotice> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<ChatRoomNotice> list = new ArrayList<ChatRoomNotice>();
		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			list.add(convertFromJSONObject(object));
		}
		return list;
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

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getNoticeState() {
		return noticeState;
	}

	public void setNoticeState(String noticeState) {
		this.noticeState = noticeState;
	}

	public String getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}

	public ChatRoomNoticeVo getChatRoomNoticeVo() {
		return chatRoomNoticeVo;
	}

	public void setChatRoomNoticeVo(ChatRoomNoticeVo chatRoomNoticeVo) {
		this.chatRoomNoticeVo = chatRoomNoticeVo;
	}
}
