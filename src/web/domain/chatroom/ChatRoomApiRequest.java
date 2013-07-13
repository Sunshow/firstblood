package web.domain.chatroom;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.sf.json.JSONObject;

import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.BaseApiRequest;
import com.lehecai.core.util.CharsetConstant;

public class ChatRoomApiRequest extends BaseApiRequest {
	private String actionType;
	private ChatRoom chatRoom;
	private Integer page;
	private Integer pageSize;
	
	@Override
	public String toQueryString() {
		StringBuffer sb = new StringBuffer(ApiConstant.API_REQUEST_PARAMETER_NAME);

		JSONObject json = new JSONObject();
		json.put(ChatRoomApiConstant.API_REQUEST_ACTION_TYPE,actionType);
		if(page != null && pageSize != null){
			JSONObject jsonData = new JSONObject();
			jsonData.put(ApiConstant.API_REQUEST_PAGE_NAME, page);
			jsonData.put(ChatRoomApiConstant.API_REQUEST_PAGESIZE_NAME, pageSize == 0 ? ApiConstant.API_REQUEST_PAGESIZE_DEFAULT : pageSize);
			json.put(ChatRoomApiConstant.API_REQUEST_DATA, jsonData);
		}
		if(chatRoom != null){
			json.put(ChatRoomApiConstant.API_REQUEST_DATA, ChatRoom.covertToString(chatRoom));
		}
		
		try {
			sb.append("=").append(URLEncoder.encode(json.toString(), CharsetConstant.CHARSET_UTF8));
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		
		return sb.toString();
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public ChatRoom getChatRoom() {
		return chatRoom;
	}

	public void setChatRoom(ChatRoom chatRoom) {
		this.chatRoom = chatRoom;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
