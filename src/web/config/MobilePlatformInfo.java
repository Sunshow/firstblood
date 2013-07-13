package web.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.lottery.LotteryType;

public class MobilePlatformInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String mobilePlatformId;
	private String mobilePlatformName;
	private String versionNum;
	private String group;
	private YesNoStatus status;
	private String operators;
	private String operatorsValue;
	private Integer[] lotteryTypeArray;
	private Map<Integer, String> lotteryTypeMap;
	
	public static MobilePlatformInfo convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		String operatorsValue = "";
		MobilePlatformInfo mobilePlatformInfo = new MobilePlatformInfo();
		Map <String,String> operatorsMap = SettingConstant.getOperatorsMap();
   		for (Iterator<String> i = operatorsMap.keySet().iterator(); i.hasNext(); ) {
			String key = (String) i.next();
			if (object.getString("op").equals(key)) {
				operatorsValue = operatorsMap.get(key);
			}
		}
   		mobilePlatformInfo.setOperatorsValue(operatorsValue);
		mobilePlatformInfo.setVersionNum(object.getString("app_version"));
		mobilePlatformInfo.setOperators(object.getString("op"));
		mobilePlatformInfo.setStatus(YesNoStatus.getItem(object.getInt("status")));
		Map<Integer, String> typeMap = new HashMap<Integer, String>();
		if (object.containsKey("lottery_type")) {
			JSONArray jsonArray = object.getJSONArray("lottery_type");
			Object[] objArray =jsonArray.toArray();
			Integer obj[] = new Integer[objArray.length];
			for (int i = 0; i < objArray.length; i++ ) {
				int value = (Integer)objArray[i];
				obj[i] = value;
				if (obj[i] == 0) {
					typeMap.put(value, "全部");
				} else {
					typeMap.put(value, LotteryType.getItem(value).getName());
				}
			}
			mobilePlatformInfo.setLotteryTypeMap(typeMap);
			mobilePlatformInfo.setLotteryTypeArray(obj);
		}
		return mobilePlatformInfo;
	}
	  
	public static List<MobilePlatformInfo> convertFromJSONArray(JSONArray array) {
		if (array == null) {
			return null;
		}
		List<MobilePlatformInfo> list = new ArrayList<MobilePlatformInfo>();
		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			list.add(convertFromJSONObject(object));
		}
		return list;
	}

	public String getMobilePlatformName() {
		return mobilePlatformName;
	}

	public void setMobilePlatformName(String mobilePlatformName) {
		this.mobilePlatformName = mobilePlatformName;
	}

	public String getVersionNum() {
		return versionNum;
	}

	public void setVersionNum(String versionNum) {
		this.versionNum = versionNum;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getOperators() {
		return operators;
	}

	public void setOperators(String operators) {
		this.operators = operators;
	}

	public void setStatus(YesNoStatus status) {
		this.status = status;
	}

	public YesNoStatus getStatus() {
		return status;
	}

	public void setMobilePlatformId(String mobilePlatformId) {
		this.mobilePlatformId = mobilePlatformId;
	}

	public String getMobilePlatformId() {
		return mobilePlatformId;
	}

	public void setOperatorsValue(String operatorsValue) {
		this.operatorsValue = operatorsValue;
	}

	public String getOperatorsValue() {
		return operatorsValue;
	}

	public void setLotteryTypeArray(Integer[] lotteryTypeArray) {
		this.lotteryTypeArray = lotteryTypeArray;
	}

	public Integer[] getLotteryTypeArray() {
		return lotteryTypeArray;
	}

	public void setLotteryTypeMap(Map<Integer, String> lotteryTypeMap) {
		this.lotteryTypeMap = lotteryTypeMap;
	}

	public Map<Integer, String> getLotteryTypeMap() {
		return lotteryTypeMap;
	}
   
}
