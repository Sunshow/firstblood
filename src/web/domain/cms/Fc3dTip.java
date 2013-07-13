package web.domain.cms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.constant.Global;

/**
 * 福彩3D小贴士实体类
 * @author yanweijie
 *
 */
public class Fc3dTip implements Serializable {
	private static final long serialVersionUID = 1794791536311656413L;
	private static final Logger logger = LoggerFactory.getLogger(Fc3dTip.class);
	
	private String target;			//指标
	private String currentOmit;	//当前遗漏
	private String historyValue;	//历史峰值
	
	public Fc3dTip() {
		
	}

	/**
	 * 把List集合转换成Json字符串
	 * @return
	 */
	public static String toJsonArrayStr(List<String> targets, List<String> currentOmits, 
			List<String> historyValues, String updateTime) {
		JSONObject tipJsonObject = new JSONObject();
		
		JSONObject titleJsonObject = new JSONObject();
		JSONArray contentJsonArray = new JSONArray();
		for (int i = 0;i<targets.size();i++) {
			JSONObject contentJsonObject = new JSONObject();
			contentJsonObject.put(Global.KEY_TARGET, targets.get(i));				//指标
			contentJsonObject.put(Global.KEY_CURRENT_OMIT, currentOmits.get(i));	//当前遗漏
			contentJsonObject.put(Global.KEY_HISTORY_VALUE, historyValues.get(i));	//历史峰值
			
			if (i == 0) {
				titleJsonObject = contentJsonObject;
			} else {
				contentJsonArray.add(contentJsonObject);
			}
		}
		tipJsonObject.put(Global.KEY_TITLE,titleJsonObject);							//标题
		tipJsonObject.put(Global.KEY_CONTENT, contentJsonArray);						//内容
		tipJsonObject.put(Global.KEY_UPDATE_TIME, updateTime);							//更新时间
		
		return tipJsonObject.toString();
	}
	
	/**
	 * 转换json字符串到List集合
	 * @param jsonObjectString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Fc3dTip> convertFromJsonObjectString(String jsonObjectString){
		if(jsonObjectString==null){
			return null;
		}
		try{
			JSONObject jsonObject = JSONObject.fromObject(jsonObjectString);
			
			List<Fc3dTip> fc3dTipList = new ArrayList<Fc3dTip>();
			Fc3dTip titleTip = convertFromJSONObject(jsonObject.getJSONObject(Global.KEY_TITLE));
			if (titleTip != null) {
				fc3dTipList.add(titleTip);
			}
			JSONArray jsonArray = jsonObject.getJSONArray(Global.KEY_CONTENT);
			for (Iterator iterator = jsonArray.iterator(); iterator.hasNext();) {
				JSONObject object = (JSONObject) iterator.next();
				Fc3dTip contentTip = convertFromJSONObject(object);
				if(contentTip != null){
					fc3dTipList.add(contentTip);
				}
			}
			return fc3dTipList;
		}catch(Exception e){
			logger.error("转换json字符串到List集合异常");
			return null;
		}
	}
	
	public static Fc3dTip convertFromJSONObject(JSONObject object) {
		if (object == null) {
			return null;
		}
		Fc3dTip tip = new Fc3dTip();
		tip.target = getString(object, Global.KEY_TARGET);
		tip.currentOmit = getString(object, Global.KEY_CURRENT_OMIT);
		tip.historyValue = getString(object, Global.KEY_HISTORY_VALUE);
		return tip;
	}
	
	protected static String getString(JSONObject object, String key) {
		try {
			Object valueObject = object.get(key);
			if (valueObject == null || valueObject instanceof JSONNull) {
				return null;
			}
			return valueObject.toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getCurrentOmit() {
		return currentOmit;
	}

	public void setCurrentOmit(String currentOmit) {
		this.currentOmit = currentOmit;
	}

	public String getHistoryValue() {
		return historyValue;
	}

	public void setHistoryValue(String historyValue) {
		this.historyValue = historyValue;
	}

}
