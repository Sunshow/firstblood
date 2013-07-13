package web.domain.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.lehecai.core.YesNoStatus;
import com.lehecai.core.util.CoreDateUtils;

public class PayPassword implements Serializable {

	private static final long serialVersionUID = 1L;
	
    public static final String QUERY_ID_START = "id_start";
    public static final String QUERY_ID_END = "id_end";
    public static final String QUERY_UID = "uid";
    public static final String QUERY_TYPE = "type";
    public static final String QUERY_TIMELINE = "timeline";
    
	private Long uid;
	private YesNoStatus status;
	private String pass;
	private int expire;  
	private Date updateDate;
	private Date expireDate;
	
	public static PayPassword convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		PayPassword payPassword = new PayPassword();
		payPassword.setStatus(YesNoStatus.getItem(object.getInt("status")));
		payPassword.setPass(object.getString("pass"));
		int expire = object.getInt("expire");
		payPassword.setExpire(expire);
		String update_at = object.getString("update_at");
		Date date = CoreDateUtils.parseDate(update_at, "yyyy-MM-dd HH:mm:ss");
		payPassword.setUpdateDate(date);
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, expire);
		payPassword.setExpireDate(calendar.getTime());
		return payPassword;
	}
	  
	public static List<PayPassword> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<PayPassword> list = new ArrayList<PayPassword>();
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
	public YesNoStatus getStatus() {
		return status;
	}
	public void setStatus(YesNoStatus status) {
		this.status = status;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public int getExpire() {
		return expire;
	}
	public void setExpire(int expire) {
		this.expire = expire;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public Date getExpireDate() {
		return expireDate;
	}
}
