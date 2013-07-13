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
public class ChatRoomConfig extends AbstractApiResultBean {
	private Integer mostDisplayMessageCount;//显示消息的最大值
	private Integer loginedDisplayMessageCount;//登录时小时的消息数量
	private Integer sendMessageInterval;//发送消息的间隔
	private Integer sendMessageLevel;//发送消息的级别
	private Integer displayActivitiesNoticeCount;//显示活动公告的数量
	private Integer displaySystemNoticeCount;//显示系统公告的数量
	private Integer loginedTimeout;//登录超时的时间
	private Boolean showWinningList;//是否显示中奖名单
	
	public static ChatRoomConfig convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		ChatRoomConfig config = new ChatRoomConfig();
		config.mostDisplayMessageCount = getInt(object, "mostDisplayMessageCount");
		config.loginedDisplayMessageCount = getInt(object, "loginedDisplayMessageCount");
		config.sendMessageInterval = getInt(object, "sendMessageInterval");
		config.sendMessageLevel = getInt(object, "sendMessageLevel");
		config.displayActivitiesNoticeCount = getInt(object, "displayActivitiesNoticeCount");
		config.displaySystemNoticeCount = getInt(object, "displaySystemNoticeCount");
		config.loginedTimeout = getInt(object, "loginedTimeout");
		config.showWinningList = getBoolean(object, "showWinningList");
		return config;
	}
	
	public static List<ChatRoomConfig> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<ChatRoomConfig> list = new ArrayList<ChatRoomConfig>();
		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			list.add(convertFromJSONObject(object));
		}
		return list;
	}
	
	public static String convertToJSON(ChatRoomConfig config){
		JSONObject object = new JSONObject();
		Integer mostDisplayMessageCount = config.getMostDisplayMessageCount();
		if(mostDisplayMessageCount != null){
			object.put("mostDisplayMessageCount", mostDisplayMessageCount);
		}
		Integer loginedDisplayMessageCount = config.getLoginedDisplayMessageCount();
		if(loginedDisplayMessageCount != null){
			object.put("loginedDisplayMessageCount", loginedDisplayMessageCount);
		}
		Integer sendMessageInterval = config.getSendMessageInterval();
		if(sendMessageInterval != null){
			object.put("sendMessageInterval", sendMessageInterval);
		}
		Integer sendMessageLevel = config.getSendMessageLevel();
		if(sendMessageLevel != null){
			object.put("sendMessageLevel", sendMessageLevel);
		}
		Integer displayActivitiesNoticeCount = config.getDisplayActivitiesNoticeCount();
		if(displayActivitiesNoticeCount != null){
			object.put("displayActivitiesNoticeCount", displayActivitiesNoticeCount);
		}
		Integer displaySystemNoticeCount = config.getDisplaySystemNoticeCount();
		if(displaySystemNoticeCount != null){
			object.put("displaySystemNoticeCount", displaySystemNoticeCount);
		}
		Integer loginedTimeout = config.getLoginedTimeout();
		if(loginedTimeout != null){
			object.put("loginedTimeout", loginedTimeout);
		}
		Boolean showWinningList = config.getShowWinningList();
		if(showWinningList != null){
			object.put("showWinningList", showWinningList);
		}
		return object.toString();
	}

	public Integer getMostDisplayMessageCount() {
		return mostDisplayMessageCount;
	}

	public void setMostDisplayMessageCount(Integer mostDisplayMessageCount) {
		this.mostDisplayMessageCount = mostDisplayMessageCount;
	}

	public Integer getLoginedDisplayMessageCount() {
		return loginedDisplayMessageCount;
	}

	public void setLoginedDisplayMessageCount(Integer loginedDisplayMessageCount) {
		this.loginedDisplayMessageCount = loginedDisplayMessageCount;
	}

	public Integer getSendMessageInterval() {
		return sendMessageInterval;
	}

	public void setSendMessageInterval(Integer sendMessageInterval) {
		this.sendMessageInterval = sendMessageInterval;
	}

	public Integer getSendMessageLevel() {
		return sendMessageLevel;
	}

	public void setSendMessageLevel(Integer sendMessageLevel) {
		this.sendMessageLevel = sendMessageLevel;
	}

	public Integer getDisplayActivitiesNoticeCount() {
		return displayActivitiesNoticeCount;
	}

	public void setDisplayActivitiesNoticeCount(Integer displayActivitiesNoticeCount) {
		this.displayActivitiesNoticeCount = displayActivitiesNoticeCount;
	}

	public Integer getDisplaySystemNoticeCount() {
		return displaySystemNoticeCount;
	}

	public void setDisplaySystemNoticeCount(Integer displaySystemNoticeCount) {
		this.displaySystemNoticeCount = displaySystemNoticeCount;
	}

	public Integer getLoginedTimeout() {
		return loginedTimeout;
	}

	public void setLoginedTimeout(Integer loginedTimeout) {
		this.loginedTimeout = loginedTimeout;
	}

	public Boolean getShowWinningList() {
		return showWinningList;
	}

	public void setShowWinningList(Boolean showWinningList) {
		this.showWinningList = showWinningList;
	}
}
