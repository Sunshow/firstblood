/**
 * 
 */
package web.domain.chatroom;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.lehecai.core.api.AbstractApiResultBean;

/**
 * @author chirowong
 *
 */
public class ChatRoomNoticeVo extends AbstractApiResultBean {
	
	private String content;
	private String createdDate;
	
	public static ChatRoomNoticeVo convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		ChatRoomNoticeVo chatRoomNoticeVo = new ChatRoomNoticeVo();
		chatRoomNoticeVo.content = getString(object, "content");
		JSONObject jsonCreatedDate = object.getJSONObject("createdDate");
		Long time = getLong(jsonCreatedDate,"time");
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		chatRoomNoticeVo.createdDate = sdf.format(ca.getTime());
		return chatRoomNoticeVo;
	}
	
	public static List<ChatRoomNoticeVo> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<ChatRoomNoticeVo> list = new ArrayList<ChatRoomNoticeVo>();
		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			list.add(convertFromJSONObject(object));
		}
		return list;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
}
