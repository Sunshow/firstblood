package web.domain.chatroom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.lehecai.core.api.AbstractApiResultBean;

public class ChatRoomStateSpecification extends AbstractApiResultBean {
	private Long id;
	private Integer start;
	private Integer end;

	public static String convertToJSON(ChatRoomStateSpecification specification) {
		JSONObject object = new JSONObject();
		Integer start = specification.getStart();

		if (start != null) {
			object.put("start", start);
		}
		Integer end = specification.getEnd();

		if (end != null) {
			object.put("end", end);
		}
		return object.toString();
	}

	public static List<ChatRoomStateSpecification> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<ChatRoomStateSpecification> list = new ArrayList<ChatRoomStateSpecification>();

		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			list.add(convertFromJSONObject(object));
		}
		return list;
	}

	public static ChatRoomStateSpecification convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		ChatRoomStateSpecification state = new ChatRoomStateSpecification();
		state.id = getLong(object, "id");
		state.start = getInt(object, "start");
		state.end = getInt(object, "end");
		return state;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}
}