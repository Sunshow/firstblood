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
public class ChatRoomBanAccountInfo extends AbstractApiResultBean {
	
	private Long highFrequencyLevel;
	private String highFrequencyScore;
	private Long level;
	private String numericScore;
	private Long sportsLevel;
	private String sportsScore;
	private String userId;
	private String username;
	
	public static ChatRoomBanAccountInfo convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		ChatRoomBanAccountInfo chatRoomBanAccountInfo = new ChatRoomBanAccountInfo();
		chatRoomBanAccountInfo.highFrequencyLevel = getLong(object, "highFrequencyLevel");
		chatRoomBanAccountInfo.highFrequencyScore = getString(object, "highFrequencyScore");
		chatRoomBanAccountInfo.level = getLong(object,"level");
		chatRoomBanAccountInfo.numericScore = getString(object,"numericScore");
		chatRoomBanAccountInfo.sportsLevel = getLong(object,"sportsLevel");
		chatRoomBanAccountInfo.sportsScore = getString(object,"sportsScore");
		chatRoomBanAccountInfo.sportsScore = getString(object,"sportsScore");
		chatRoomBanAccountInfo.userId = getString(object,"userId");
		chatRoomBanAccountInfo.username = getString(object,"username");
		return chatRoomBanAccountInfo;
	}
	
	public static List<ChatRoomBanAccountInfo> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<ChatRoomBanAccountInfo> list = new ArrayList<ChatRoomBanAccountInfo>();
		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			list.add(convertFromJSONObject(object));
		}
		return list;
	}
	
	public Long getHighFrequencyLevel() {
		return highFrequencyLevel;
	}

	public void setHighFrequencyLevel(Long highFrequencyLevel) {
		this.highFrequencyLevel = highFrequencyLevel;
	}

	public String getHighFrequencyScore() {
		return highFrequencyScore;
	}

	public void setHighFrequencyScore(String highFrequencyScore) {
		this.highFrequencyScore = highFrequencyScore;
	}

	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	public String getNumericScore() {
		return numericScore;
	}

	public void setNumericScore(String numericScore) {
		this.numericScore = numericScore;
	}

	public Long getSportsLevel() {
		return sportsLevel;
	}

	public void setSportsLevel(Long sportsLevel) {
		this.sportsLevel = sportsLevel;
	}

	public String getSportsScore() {
		return sportsScore;
	}

	public void setSportsScore(String sportsScore) {
		this.sportsScore = sportsScore;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
