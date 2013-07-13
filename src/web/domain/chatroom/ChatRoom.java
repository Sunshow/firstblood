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
public class ChatRoom extends AbstractApiResultBean {
	public static final String QUERY_ACTION_TYPE = "actionType";

	private String actionType;// 请求类型
	private String chatRoomName;// 聊天室名称
	private Long chatRoomId;// 房间id
	private ChatRoomConfig config;// 聊天室配置
	private Long messageId;// 指定要在数据库中和缓存中删除的消息的id
	private Long userId;// 指定要禁言的用户的id（此id为plot分配的id）
	private String username;// 指定要禁言的用户名
	private Long expiring;// 指定对用户禁言的期限（此参数只有9000000、36000000、864000000、6048000000、-1五个值,分别代表禁言15分钟、1小时、24小时、7天与永久禁言）
	private String reason;// 指定对用户禁言的原因
	private Long banSpecificationId;// 指定要更改状态为终止的禁言的id
	private String content;// 指定公告的内容
	private String noticeType;// 指定公告的类型
	private String level;// 指定公告的级别
	private Long noticeId;// 指定要更新的公告的id
	private ChatRoomState chatRoomState;// 聊天室状态
	private String operatorId;//操作人id

	public static ChatRoom convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		ChatRoom chatRoom = new ChatRoom();
		chatRoom.actionType = getString(object, "actionType");
		chatRoom.chatRoomName = getString(object, "name");
		chatRoom.chatRoomId = getLong(object, "id");
		chatRoom.config = ChatRoomConfig.convertFromJSONObject(JSONObject.fromObject(getString(object, "chatRoomConfig")));

		if (object.containsKey("messageId")) {
			chatRoom.messageId = getLong(object, "messageId");
		}
		if (object.containsKey("userId")) {
			chatRoom.userId = getLong(object, "userId");
		}
		if (object.containsKey("username")) {
			chatRoom.username = getString(object, "username");
		}
		if (object.containsKey("expiring")) {
			chatRoom.expiring = getLong(object, "expiring");
		}
		chatRoom.reason = getString(object, "reason");

		if (object.containsKey("banSpecificationId")) {
			chatRoom.banSpecificationId = getLong(object, "banSpecificationId");
		}
		chatRoom.content = getString(object, "content");
		chatRoom.noticeType = getString(object, "noticeType");
		chatRoom.level = getString(object, "level");

		if (object.containsKey("noticeId")) {
			chatRoom.noticeId = getLong(object, "noticeId");
		}
		chatRoom.chatRoomState = ChatRoomState.convertFromJSONObject(JSONObject.fromObject(getString(object, "chatRoomState")));
		if (object.containsKey("operatorId")) {
			chatRoom.setOperatorId(getString(object, "operatorId"));
		}
		return chatRoom;
	}

	public static List<ChatRoom> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<ChatRoom> list = new ArrayList<ChatRoom>();

		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			list.add(convertFromJSONObject(object));
		}
		return list;
	}

	public static String covertToString(ChatRoom chatRoom) {
		JSONObject object = new JSONObject();
		String chatRoomName = chatRoom.getChatRoomName();

		if (!StringUtils.isEmpty(chatRoomName)) {
			object.put("name", chatRoomName);
		}
		Long chatRoomId = chatRoom.getChatRoomId();

		if (chatRoomId != null && chatRoomId.longValue() != 0) {
			object.put("chatRoomId", chatRoomId);
		}
		ChatRoomConfig config = chatRoom.getConfig();

		if (config != null) {
			object.put("chatRoomConfig", ChatRoomConfig.convertToJSON(config));
		}
		Long messageId = chatRoom.getMessageId();

		if (messageId != null && messageId.longValue() != 0) {
			object.put("messageId", messageId);
		}
		Long userId = chatRoom.getUserId();

		if (userId != null && userId.longValue() != 0) {
			object.put("userId", userId);
		}
		String userName = chatRoom.getUsername();

		if (!StringUtils.isEmpty(userName)) {
			object.put("username", userName);
		}
		Long expiring = chatRoom.getExpiring();

		if (expiring != null && expiring.longValue() != 0) {
			object.put("expiring", expiring);
		}
		String reason = chatRoom.getReason();

		if (!StringUtils.isEmpty(reason)) {
			object.put("reason", reason);
		}
		Long banSpecificationId = chatRoom.getBanSpecificationId();

		if (banSpecificationId != null && banSpecificationId.longValue() != 0) {
			object.put("banSpecificationId", banSpecificationId);
		}
		String content = chatRoom.getContent();

		if (!StringUtils.isEmpty(content)) {
			object.put("content", content);
		}
		String noticeType = chatRoom.getNoticeType();

		if (!StringUtils.isEmpty(noticeType)) {
			object.put("noticeType", noticeType);
		}
		String level = chatRoom.getLevel();

		if (!StringUtils.isEmpty(level)) {
			object.put("level", level);
		}
		Long noticeId = chatRoom.getNoticeId();

		if (noticeId != null && noticeId.longValue() != 0) {
			object.put("noticeId", noticeId);
		}
		ChatRoomState state = chatRoom.getChatRoomState();

		if (state != null) {
			object.put("chatRoomState", ChatRoomState.convertToJSON(state));
		}
		if (chatRoom.getOperatorId() != null) {
			object.put("operatorId", chatRoom.getOperatorId());
		}
		return object.toString();
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getChatRoomName() {
		return chatRoomName;
	}

	public void setChatRoomName(String chatRoomName) {
		this.chatRoomName = chatRoomName;
	}

	public Long getChatRoomId() {
		return chatRoomId;
	}

	public void setChatRoomId(Long chatRoomId) {
		this.chatRoomId = chatRoomId;
	}

	public ChatRoomConfig getConfig() {
		return config;
	}

	public void setConfig(ChatRoomConfig config) {
		this.config = config;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Long getBanSpecificationId() {
		return banSpecificationId;
	}

	public void setBanSpecificationId(Long banSpecificationId) {
		this.banSpecificationId = banSpecificationId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Long getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(Long noticeId) {
		this.noticeId = noticeId;
	}

	public ChatRoomState getChatRoomState() {
		return chatRoomState;
	}

	public void setChatRoomState(ChatRoomState chatRoomState) {
		this.chatRoomState = chatRoomState;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorId() {
		return operatorId;
	}


}