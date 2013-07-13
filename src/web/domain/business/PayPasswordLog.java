package web.domain.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.lehecai.core.YesNoStatus;
import com.lehecai.core.lottery.PlatformType;
import com.lehecai.core.util.CoreDateUtils;

public class PayPasswordLog implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long uid;        //id
	private String username; //用户名
	private Date timeline;  //操作时间
	private PlatformType platform; //操作平台
	private YesNoStatus status;  //状态      是否成功
	private String information; //其他信息
	private PayPasswordType payPasswordType;

	public static PayPasswordLog convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		PayPasswordLog payPasswordLog = new PayPasswordLog();
		payPasswordLog.setStatus(YesNoStatus.getItem(object.getInt("is_success")));
		payPasswordLog.setUsername(object.getString("username"));
		payPasswordLog.setPlatform(PlatformType.getItem(object.getInt("platform")));
		payPasswordLog.setTimeline(CoreDateUtils.parseDate(object.getString("timeline"), "yyyy-MM-dd HH:mm:ss"));
		payPasswordLog.setInformation(object.getString("ext"));
		payPasswordLog.setPayPasswordType(PayPasswordType.getItem(object.getInt("type")));
		return payPasswordLog;
	}
	  
	public static List<PayPasswordLog> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<PayPasswordLog> list = new ArrayList<PayPasswordLog>();
		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			list.add(convertFromJSONObject(object));
		}
		return list;
	}
	
	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getTimeline() {
		return timeline;
	}

	public void setTimeline(Date timeline) {
		this.timeline = timeline;
	}

	public PlatformType getPlatform() {
		return platform;
	}

	public void setPlatform(PlatformType platform) {
		this.platform = platform;
	}

	public YesNoStatus getStatus() {
		return status;
	}

	public void setStatus(YesNoStatus status) {
		this.status = status;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public void setPayPasswordType(PayPasswordType payPasswordType) {
		this.payPasswordType = payPasswordType;
	}

	public PayPasswordType getPayPasswordType() {
		return payPasswordType;
	}
}
