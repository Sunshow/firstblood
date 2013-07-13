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
public class ChatRoomBan extends AbstractApiResultBean {
	
	private ChatRoomBanAccount chatRoomBanAccount;
	private String banSpecificationState;
	private String createdDate;
	private Long expiring;
	private Long id;
	private String reason;
	private String operatorId;//操作人id
	private String operatorName;//操作人名称
	
	public static ChatRoomBan convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		ChatRoomBan chatRoomBan = new ChatRoomBan();
		chatRoomBan.chatRoomBanAccount = ChatRoomBanAccount.convertFromJSONObject(JSONObject.fromObject(getString(object, "account")));
		chatRoomBan.banSpecificationState = getString(object, "banSpecificationState");
		JSONObject jsonCreatedDate = object.getJSONObject("createdDate");
		Long time = getLong(jsonCreatedDate,"time");
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		chatRoomBan.createdDate = sdf.format(ca.getTime());
		chatRoomBan.expiring = getLong(object, "expiring");
		chatRoomBan.id = getLong(object, "id");
		chatRoomBan.reason = getString(object, "reason");
		if (object.containsKey("operatorId")) {
			chatRoomBan.setOperatorId(getString(object, "operatorId"));
		}
		return chatRoomBan;
	}
	
	public static List<ChatRoomBan> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<ChatRoomBan> list = new ArrayList<ChatRoomBan>();
		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			list.add(convertFromJSONObject(object));
		}
		return list;
	}
	
	public ChatRoomBanAccount getChatRoomBanAccount() {
		return chatRoomBanAccount;
	}

	public void setChatRoomBanAccount(ChatRoomBanAccount chatRoomBanAccount) {
		this.chatRoomBanAccount = chatRoomBanAccount;
	}

	public String getBanSpecificationState() {
		return banSpecificationState;
	}

	public void setBanSpecificationState(String banSpecificationState) {
		this.banSpecificationState = banSpecificationState;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public Long getExpiring() {
		return expiring;
	}

	public void setExpiring(Long expiring) {
		this.expiring = expiring;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorId() {
		return operatorId;
	}

}
