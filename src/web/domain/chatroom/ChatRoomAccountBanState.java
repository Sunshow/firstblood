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
public class ChatRoomAccountBanState extends AbstractApiResultBean {
	
	private Long banId;
	private Long banState;
	private Long banCount;
	private String username;
	private Long userId;
	private String banTime;
	private String banReason;
	private Long expiring;
	
	public static ChatRoomAccountBanState convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		ChatRoomAccountBanState chatRoomAccountBanState = new ChatRoomAccountBanState();
		if(object.containsKey("banId")){
			chatRoomAccountBanState.banId = getLong(object, "banId");
		}
		if(object.containsKey("banState")){
			chatRoomAccountBanState.banState = getLong(object, "banState");
		}
		chatRoomAccountBanState.banCount = getLong(object, "banCount");
		chatRoomAccountBanState.username = getString(object,"username");
		chatRoomAccountBanState.userId = getLong(object,"userId");
		if(object.containsKey("banTime")){
			JSONObject jsonBanTime = object.getJSONObject("banTime");
			Long time = getLong(jsonBanTime,"time");
			Calendar ca = Calendar.getInstance();
			ca.setTimeInMillis(time);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			chatRoomAccountBanState.banTime = sdf.format(ca.getTime());
		}
		chatRoomAccountBanState.banReason = getString(object,"banReason");
		if(object.containsKey("expiring")){
			chatRoomAccountBanState.expiring = getLong(object,"expiring");
		}
		return chatRoomAccountBanState;
	}
	
	public static List<ChatRoomAccountBanState> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<ChatRoomAccountBanState> list = new ArrayList<ChatRoomAccountBanState>();
		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			list.add(convertFromJSONObject(object));
		}
		return list;
	}

	public Long getBanState() {
		return banState;
	}

	public void setBanState(Long banState) {
		this.banState = banState;
	}

	public Long getBanCount() {
		return banCount;
	}

	public void setBanCount(Long banCount) {
		this.banCount = banCount;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBanTime() {
		return banTime;
	}

	public void setBanTime(String banTime) {
		this.banTime = banTime;
	}

	public String getBanReason() {
		return banReason;
	}

	public void setBanReason(String banReason) {
		this.banReason = banReason;
	}

	public Long getBanId() {
		return banId;
	}

	public void setBanId(Long banId) {
		this.banId = banId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}	
	
	public Long getExpiring() {
		return expiring;
	}

	public void setExpiring(Long expiring) {
		this.expiring = expiring;
	}
}
