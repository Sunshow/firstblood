package web.domain.chatroom;

import java.util.HashMap;
import java.util.Map;

public class ChatRoomConstants {
	public static final Map<String,String> errorCode = new HashMap<String,String>();
	static {
		errorCode.put("ERROR_CODE_0001", "没有找到此id的聊天室");
		errorCode.put("ERROR_CODE_0002", "聊天室是关着的");
		errorCode.put("ERROR_CODE_0003", "没有找到此accountId的账户");
		errorCode.put("ERROR_CODE_0004", "不能发送消息，账户没有登录");
		errorCode.put("ERROR_CODE_0005", "消息发送失败，账户被禁言");
		errorCode.put("ERROR_CODE_0006", "没有找到此banSpecificationId的禁令");
		errorCode.put("ERROR_CODE_0007", "没有找到此messageId的消息");
		errorCode.put("ERROR_CODE_0008", "发送消息失败，用户彩贝等级没有达到房间要求");
		errorCode.put("ERROR_CODE_0009", "过滤敏感词失败");
		errorCode.put("ERROR_CODE_0010", "数据库中已有同名的聊天室存在");
		errorCode.put("ERROR_CODE_0011", "json数组错误导致增加公告失败");
		errorCode.put("ERROR_CODE_0012", "此敏感词已经存在于数据库中，不能重复提交");
		errorCode.put("ERROR_CODE_0013", "消息中的网址不是有效的网址");
		errorCode.put("ERROR_CODE_0014", "禁言理由不能为空");
		errorCode.put("ERROR_CODE_0015", "没有找到此noticeId的公告");
		errorCode.put("ERROR_CODE_0016", "页码参数必须大于1");
	}
	
	public static final String ACTION_TYPE_CHAT_INITIAL_CHAT_ROOM = "CHAT_INITIAL_CHAT_ROOM";//用户从服务器打开房间
	public static final String ACTION_TYPE_CHAT_LOGIN = "CHAT_LOGIN";//用户登录
	public static final String ACTION_TYPE_CHAT_SEND_MESSAGE = "CHAT_SEND_MESSAGE";//用户发送消息
	public static final String ACTION_TYPE_CHAT_FETCH_MESSAGE = "CHAT_FETCH_MESSAGE";//用户抓取消息
	public static final String ACTION_TYPE_CHAT_FETCH_NOTICE = "CHAT_FETCH_NOTICE";//用户抓取公告
	public static final String ACTION_TYPE_CHAT_HEARTBEAT = "CHAT_HEARTBEAT";//记录用户痕迹
	public static final String ACTION_TYPE_CHAT_LOGOUT = "CHAT_LOGOUT";//用户登出
	public static final String ACTION_TYPE_API_CHAT_ROOM_LIST = "API_CHAT_ROOM_LIST";//管理员获取聊天室列表
	public static final String ACTION_TYPE_API_ADD_CHAT_ROOM = "API_ADD_CHAT_ROOM";//管理员增加聊天室
	public static final String ACTION_TYPE_API_UPDATE_CHAT_ROOM = "API_UPDATE_CHAT_ROOM";//管理员更新聊天室
	public static final String ACTION_TYPE_API_FIND_CHAT_ROOM = "API_FIND_CHAT_ROOM";//获取聊天室信息
	public static final String ACTION_TYPE_API_CLOSE_CHAT_ROOM = "API_CLOSE_CHAT_ROOM";//管理员关闭聊天室
	public static final String ACTION_TYPE_API_OPEN_CHAT_ROOM = "API_OPEN_CHAT_ROOM";//管理员开启聊天室
	public static final String ACTION_TYPE_API_REMOVE_MESSAGE = "API_REMOVE_MESSAGE";//管理员删除用户消息
	public static final String ACTION_TYPE_API_BAN_ACCOUNT = "API_BAN_ACCOUNT";//管理员对用户禁言
	public static final String ACTION_TYPE_API_BAN_SPECIFICATION_LIST = "API_BAN_SPECIFICATION_LIST";//管理员获取黑名单
	public static final String ACTION_TYPE_API_ABORT_BAN_SPECIFICATION = "API_ABORT_BAN_SPECIFICATION";//管理员对用户终止禁言
	public static final String ACTION_TYPE_API_NOTICE_LIST = "API_NOTICE_LIST";//管理员获取公告列表
	public static final String ACTION_TYPE_API_ADD_NOTICE = "API_ADD_NOTICE";//管理员增加公告
	public static final String ACTION_TYPE_API_UPDATE_NOTICE = "API_UPDATE_NOTICE";//管理员更新公告
	public static final String ACTION_TYPE_API_REMOVE_NOTICE = "API_REMOVE_NOTICE";//管理员移除公告
	public static final String ACTION_TYPE_API_FIND_NOTICE = "API_FIND_NOTICE";//获取公告信息
	public static final String ACTION_TYPE_API_MESSAGE_LIST = "API_MESSAGE_LIST";//获取信息列表
	public static final String ACTION_TYPE_API_ACCOUNT_BAN_STATE = "API_ACCOUNT_BAN_STATE";//获取禁言用户列表
	public static final String ACTION_TYPE_API_SEND_MESSAGE = "API_SEND_MESSAGE";//管理员发送消息
}
