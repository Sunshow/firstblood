/**
 * 
 */
package web.domain.chatroom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.lehecai.core.api.AbstractApiResultBean;

/**
 * @author chirowong
 * 
 */
public class ChatRoomState extends AbstractApiResultBean {
	private Boolean enable;
	private List<ChatRoomStateSpecification> chatRoomStateSpecificationList;

	public static String chatRoomStateSpecificationListToString(List<ChatRoomStateSpecification> chatRoomStateSpecificationList) {
		StringBuilder stringBuilder = new StringBuilder();

		for (ChatRoomStateSpecification chatRoomStateSpecification : chatRoomStateSpecificationList) {
			String start = String.valueOf(chatRoomStateSpecification.getStart());
			String end = String.valueOf(chatRoomStateSpecification.getEnd());

			if (!StringUtils.isEmpty(start) && !StringUtils.isEmpty(end)) {
				if (start.length() == 3) {
					start = "0" + start;
				}
				if (end.length() == 3) {
					end = "0" + end;
				}
				stringBuilder.append(start.substring(0, 2) + ":" + start.substring(2, 4) + "|");
				stringBuilder.append(end.substring(0, 2) + ":" + end.substring(2, 4) + ",");
			}
		}
		if (stringBuilder.length() != 0) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		return stringBuilder.toString();
	}

	public static ChatRoomState convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		ChatRoomState state = new ChatRoomState();
		state.enable = getBoolean(object, "enable");
		state.chatRoomStateSpecificationList = ChatRoomStateSpecification.convertFromJSONArray(getArray(object, "chatRoomStateSpecificationList"));
		return state;
	}

	public static List<ChatRoomState> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<ChatRoomState> list = new ArrayList<ChatRoomState>();

		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			list.add(convertFromJSONObject(object));
		}
		return list;
	}

	public static String convertToJSON(ChatRoomState state) {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		Boolean enable = state.getEnable();

		if (enable != null) {
			jsonObject.put("enable", enable);
		}
		List<ChatRoomStateSpecification> ChatRoomStateSpecificationList = state.getChatRoomStateSpecificationList();

		if (ChatRoomStateSpecificationList != null) {
			for (ChatRoomStateSpecification chatRoomStateSpecification : ChatRoomStateSpecificationList) {
				if (chatRoomStateSpecification != null) {
					jsonArray.add(ChatRoomStateSpecification.convertToJSON(chatRoomStateSpecification));
				}
			}
			jsonObject.put("chatRoomStateSpecificationList", jsonArray);
		}
		return jsonObject.toString();
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public List<ChatRoomStateSpecification> getChatRoomStateSpecificationList() {
		return chatRoomStateSpecificationList;
	}

	public void setChatRoomStateSpecificationList(List<ChatRoomStateSpecification> chatRoomStateSpecificationList) {
		this.chatRoomStateSpecificationList = chatRoomStateSpecificationList;
	}
}