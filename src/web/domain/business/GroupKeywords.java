package web.domain.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.lehecai.core.util.CoreDateUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 站点支付平台比例
 * @author He Wang
 *
 */
public class GroupKeywords implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final String SET_KEYWORDS = "word";
	public static final String SET_GROUP_ID= "group_id";
	
	public static final String QUERY_KEYWORDS = "word";
	public static final String QUERY_GROUP_ID = "group_id";
	public static final String QUERY_ID = "id";
	public static final String QUERY_TIMELINE = "timeline";
	
	//分组
	private GroupType groupType;
	//关键词
	private String keywords;
	private Long id;
	private Date createTime;
	//id，用于批量确认后获取groupType
	private Integer groupId;
	
	  public static GroupKeywords convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		GroupKeywords groupKeywords = new GroupKeywords();
		groupKeywords.setGroupType(GroupType.getItem(object.getInt(QUERY_GROUP_ID)));
		groupKeywords.setKeywords(object.getString(QUERY_KEYWORDS));
		groupKeywords.setId(object.getLong(QUERY_ID));
		groupKeywords.setCreateTime(CoreDateUtils.parseDate(object.getString(QUERY_TIMELINE), CoreDateUtils.DATETIME));
		return groupKeywords;
	}
	  
	public static List<GroupKeywords> convertFromJSONArray(JSONArray array) {
		if (array == null) {
		return null;
		}
		List<GroupKeywords> list = new ArrayList<GroupKeywords>();
		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			list.add(convertFromJSONObject(object));
		}
		return list;
	}
	
	
	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
	}
	public GroupType getGroupType() {
		return groupType;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getKeywords() {
		return keywords;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getGroupId() {
		return groupId;
	}

}
