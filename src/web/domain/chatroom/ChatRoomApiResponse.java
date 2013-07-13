/**
 * 
 */
package web.domain.chatroom;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Sunshow
 *
 */
public class ChatRoomApiResponse {
	/* API执行结果 */
	private JSONObject data;
	
	private Integer count;
	private JSONArray arrayData;
	private Integer page;
	private Integer pageSize;
	
	/* 执行结果信息 */
	private String message;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public JSONArray getArrayData() {
		return arrayData;
	}

	public void setArrayData(JSONArray arrayData) {
		this.arrayData = arrayData;
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

	public JSONObject toJSONObject() {
		JSONObject object = new JSONObject();
		object.put(ChatRoomApiConstant.API_RESPONSE_DATA_NAME, this.getData());
		object.put(ChatRoomApiConstant.API_RESPONSE_MESSAGE_NAME, this.getMessage());
		return object;
	}
}
