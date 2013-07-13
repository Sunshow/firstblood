package web.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

public class JsonUtil{
	/**
	* 从一个JSON 对象字符格式中得到一个java对象
	* @param jsonString
	* @param pojoCalss
	* @return
	*/
	@SuppressWarnings("unchecked")
	public static Object getObject4JsonString(String jsonString,Class pojoCalss) throws Exception{
		Object pojo;
		JSONObject jsonObject = JSONObject.fromObject( jsonString );
		pojo = JSONObject.toBean(jsonObject,pojoCalss);
		return pojo;
	}
	/**
	* 从json HASH表达式中获取一个map，改map支持嵌套功能
	* @param jsonString
	* @return
	*/
	@SuppressWarnings("unchecked")
	public static Map getMap4Json(String jsonString) throws Exception{
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map valueMap = new HashMap();
		return getMap4JsonObject(jsonObject, valueMap);
	}
	@SuppressWarnings("unchecked")
	public static Map getMap4JsonObject(JSONObject jsonObject,Map valueMap) throws Exception{
		Iterator keyIter = jsonObject.keys();
		String key;
		Object value;
		while(keyIter.hasNext()){
			key = (String)keyIter.next();
			value = jsonObject.get(key);		
			if(value instanceof JSONArray){
				JSONArray jsonArray = (JSONArray)value;
				List mapList = new ArrayList();
				for(Object obj:jsonArray){
					if(obj instanceof JSONObject){
						JSONObject jsonObj = (JSONObject)obj;
						Map map = getMap4JsonObject(jsonObj,new HashMap());
						mapList.add(map);
					}else{
						mapList = jsonArray;
						break;
					}	
				}
				valueMap.put(key, mapList);
			}else if(value instanceof JSONObject){
				JSONObject jsonObj = (JSONObject)value;
				Map map = getMap4JsonObject(jsonObj,new HashMap());
				valueMap.put(key, map);
			}else if (value instanceof JSONNull){
				valueMap.put(key, null);
			}else{
				valueMap.put(key, value);
			}
		}
		return valueMap;
	}
	
//	public static void main(String[] args) throws Exception{
//		
//		for (int i = 0; i < 50; i ++) {
//			String json = "{\"lottery_type_200\":{\"result\":[{\"key\":\"ball\",\"data\":[\"9\",\"1\",\"1\",\"3\",\"2\"]}],\"phase\":\"20110311023\",\"lottery_type\":200,\"draw_time\":\"2011-03-11 01:56:15\",\"pool_amount\":\"0\",\"sale_amount\":\"\",\"result_detail\":[{\"key\":\"prize1\",\"bet\":\"0\",\"prize\":100000},{\"key\":\"prize2\",\"bet\":\"0\",\"prize\":20000},{\"key\":\"prize3\",\"bet\":\"0\",\"prize\":200},{\"key\":\"prize4\",\"bet\":\"0\",\"prize\":20},{\"key\":\"prize5\",\"bet\":\"0\",\"prize\":1000},{\"key\":\"prize6\",\"bet\":\"0\",\"prize\":320},{\"key\":\"prize7\",\"bet\":\"0\",\"prize\":160},{\"key\":\"prize8\",\"bet\":\"0\",\"prize\":100},{\"key\":\"prize9\",\"bet\":\"0\",\"prize\":50},{\"key\":\"prize10\",\"bet\":\"0\",\"prize\":10},{\"key\":\"prize11\",\"bet\":\"0\",\"prize\":4}],\"fc3d_sjh\":null},\"lottery_type_201\":{\"result\":[{\"key\":\"ball\",\"data\":[\"1\",\"8\",\"3\"]}],\"phase\":\"2011031023\",\"lottery_type\":201,\"draw_time\":\"2011-03-10 21:33:30\",\"pool_amount\":\"0\",\"sale_amount\":\"0\",\"result_detail\":[{\"key\":\"prize1\",\"bet\":\"0\",\"prize\":980},{\"key\":\"prize2\",\"bet\":\"0\",\"prize\":320},{\"key\":\"prize3\",\"bet\":\"0\",\"prize\":160},{\"key\":\"prize4\",\"bet\":\"0\",\"prize\":98},{\"key\":\"prize5\",\"bet\":\"0\",\"prize\":98},{\"key\":\"prize6\",\"bet\":\"0\",\"prize\":10},{\"key\":\"prize7\",\"bet\":\"0\",\"prize\":10}],\"fc3d_sjh\":null},\"lottery_type_22\":{\"result\":[{\"key\":\"ball\",\"data\":[\"01\",\"08\",\"10\",\"11\",\"05\"]}],\"phase\":\"2011031065\",\"lottery_type\":22,\"draw_time\":\"2011-03-10 22:00:00\",\"pool_amount\":\"0\",\"sale_amount\":\"0\",\"result_detail\":[{\"key\":\"prize1\",\"bet\":\"0\",\"prize\":13},{\"key\":\"prize2\",\"bet\":\"0\",\"prize\":6},{\"key\":\"prize3\",\"bet\":\"0\",\"prize\":19},{\"key\":\"prize4\",\"bet\":\"0\",\"prize\":78},{\"key\":\"prize5\",\"bet\":\"0\",\"prize\":540},{\"key\":\"prize6\",\"bet\":\"0\",\"prize\":90},{\"key\":\"prize7\",\"bet\":\"0\",\"prize\":26},{\"key\":\"prize8\",\"bet\":\"0\",\"prize\":9},{\"key\":\"prize9\",\"bet\":\"0\",\"prize\":130},{\"key\":\"prize10\",\"bet\":\"0\",\"prize\":65},{\"key\":\"prize11\",\"bet\":\"0\",\"prize\":1170},{\"key\":\"prize12\",\"bet\":\"0\",\"prize\":195}],\"fc3d_sjh\":null},\"lottery_type_20\":{\"result\":[{\"key\":\"ball\",\"data\":[\"02\",\"10\",\"08\",\"06\",\"07\"]}],\"phase\":\"11031065\",\"lottery_type\":20,\"draw_time\":\"2011-03-10 21:55:00\",\"pool_amount\":\"0\",\"sale_amount\":\"0\",\"result_detail\":[{\"key\":\"prize1\",\"bet\":\"0\",\"prize\":13},{\"key\":\"prize2\",\"bet\":\"0\",\"prize\":6},{\"key\":\"prize3\",\"bet\":\"0\",\"prize\":19},{\"key\":\"prize4\",\"bet\":\"0\",\"prize\":78},{\"key\":\"prize5\",\"bet\":\"0\",\"prize\":540},{\"key\":\"prize6\",\"bet\":\"0\",\"prize\":90},{\"key\":\"prize7\",\"bet\":\"0\",\"prize\":26},{\"key\":\"prize8\",\"bet\":\"0\",\"prize\":9},{\"key\":\"prize9\",\"bet\":\"0\",\"prize\":130},{\"key\":\"prize10\",\"bet\":\"0\",\"prize\":65},{\"key\":\"prize11\",\"bet\":\"0\",\"prize\":1170},{\"key\":\"prize12\",\"bet\":\"0\",\"prize\":195}],\"fc3d_sjh\":null},\"lottery_type_543\":{\"result\":[{\"key\":\"ball\",\"data\":[\"09\",\"11\",\"14\",\"16\",\"19\",\"23\",\"28\",\"35\",\"36\",\"37\",\"41\",\"49\",\"51\",\"52\",\"53\",\"55\",\"65\",\"66\",\"67\",\"68\"]}],\"phase\":\"427017\",\"lottery_type\":543,\"draw_time\":\"2011-03-10 23:59:51\",\"pool_amount\":\"\",\"sale_amount\":\"\",\"result_detail\":[],\"fc3d_sjh\":null},\"lottery_type_547\":{\"result\":[{\"key\":\"ball\",\"data\":[\"2\",\"2\",\"8\",\"7\"]}],\"phase\":\"11022479\",\"lottery_type\":547,\"draw_time\":\"2011-02-24 00:00:00\",\"pool_amount\":\"\",\"sale_amount\":\"\",\"result_detail\":[],\"fc3d_sjh\":null},\"lottery_type_544\":{\"result\":[{\"key\":\"ball\",\"data\":[\"14\",\"12\",\"20\",\"07\",\"18\",\"01\",\"15\",\"17\"]}],\"phase\":\"2011031084\",\"lottery_type\":544,\"draw_time\":\"2011-03-10 23:13:00\",\"pool_amount\":\"\",\"sale_amount\":\"\",\"result_detail\":[],\"fc3d_sjh\":null},\"lottery_type_545\":{\"result\":[{\"key\":\"ball\",\"data\":[\"5\",\"14\",\"7\",\"20\",\"19\"]}],\"phase\":\"201106250\",\"lottery_type\":545,\"draw_time\":\"2011-03-11 00:00:00\",\"pool_amount\":\"\",\"sale_amount\":\"\",\"result_detail\":[],\"fc3d_sjh\":null}}";
//			//String json = "{     \"a\":\"a\",     \"c\":[1,2,3],     \"d\":[         {\"x\":\"y0\",\"m\":\"n\"},         {\"x\":\"y1\",\"m\":\"n\"},     ],     \"m\":{\"b\":\"x\"} }";
//			Map map = JsonUtil.getMap4Json(json);
//			System.out.println(i + ":");
//		}
//	}
	/**
	* 从json数组中得到相应java数组
	* @param jsonString
	* @return
	*/
	public static Object[] getObjectArray4Json(String jsonString) throws Exception{
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		return jsonArray.toArray();
	}
	/**
	* 从json对象集合表达式中得到一个java对象列表
	* @param jsonString
	* @param pojoClass
	* @return
	*/
	@SuppressWarnings("unchecked")
	public static List getList4Json(String jsonString, Class pojoClass) throws Exception{
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		JSONObject jsonObject;
		Object pojoValue;
		List list = new ArrayList();
		for ( int i = 0 ; i<jsonArray.size(); i++){
			jsonObject = jsonArray.getJSONObject(i);
			pojoValue = JSONObject.toBean(jsonObject,pojoClass);
			list.add(pojoValue);
		}
		return list;
	}
	/**
	* 从json数组中解析出java字符串数组
	* @param jsonString
	* @return
	*/
	public static String[] getStringArray4Json(String jsonString) throws Exception{
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		String[] stringArray = new String[jsonArray.size()];
		for( int i = 0 ; i<jsonArray.size() ; i++ ){
			stringArray[i] = jsonArray.getString(i);
		}
		return stringArray;
	}
	/**
	* 从json数组中解析出javaLong型对象数组
	* @param jsonString
	* @return
	*/
	public static Long[] getLongArray4Json(String jsonString) throws Exception{
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Long[] longArray = new Long[jsonArray.size()];
		for( int i = 0 ; i<jsonArray.size() ; i++ ){
			longArray[i] = jsonArray.getLong(i);
		}
		return longArray;
	}
	/**
	* 从json数组中解析出java Integer型对象数组
	* @param jsonString
	* @return
	*/
	public static Integer[] getIntegerArray4Json(String jsonString) throws Exception{
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Integer[] integerArray = new Integer[jsonArray.size()];
		for( int i = 0 ; i<jsonArray.size() ; i++ ){
			integerArray[i] = jsonArray.getInt(i);
		}
		return integerArray;
	}
	/**
	* 从json数组中解析出java Date 型对象数组，使用本方法必须保证
	* @param jsonString
	* @return
	*/
	public static Date[] getDateArray4Json(String jsonString,String DataFormat) throws Exception{
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Date[] dateArray = new Date[jsonArray.size()];
		String dateString;
		Date date;
		for( int i = 0 ; i<jsonArray.size() ; i++ ){
			dateString = jsonArray.getString(i);
			date = DateUtil.parseDate(dateString, DataFormat);
			dateArray[i] = date;
		}
		return dateArray;
	}
	/**
	* 从json数组中解析出java Integer型对象数组
	* @param jsonString
	* @return
	*/
	public static Double[] getDoubleArray4Json(String jsonString) throws Exception{
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Double[] doubleArray = new Double[jsonArray.size()];
		for( int i = 0 ; i<jsonArray.size() ; i++ ){
			doubleArray[i] = jsonArray.getDouble(i);
		}
		return doubleArray;
	}
	/**
	* 将java对象转换成json字符串
	* @param javaObj
	* @return
	*/
	public static String getJsonString4JavaPOJO(Object javaObj) throws Exception{
		JSONObject json;
		json = JSONObject.fromObject(javaObj);
		return json.toString();
	}
	/**
	* 将java对象转换成json字符串,并设定日期格式
	* @param javaObj
	* @param dataFormat
	* @return
	*/
	public static String getJsonString4JavaPOJO(Object javaObj , String dataFormat) throws Exception{
		JSONObject json;
		JsonConfig jsonConfig = configJson(dataFormat);
		json = JSONObject.fromObject(javaObj,jsonConfig);
		return json.toString();
	}
	/**
	* JSON 时间解析器具
	* @param datePattern
	* @return
	*/
	public static JsonConfig configJson(String datePattern) throws Exception{
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[]{""});
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.registerJsonValueProcessor(Date.class,
		new DateJsonValueProcessor(datePattern)); 
		return jsonConfig;
	}
	/**
	*
	* @param excludes
	* @param datePattern
	* @return
	*/
	public static JsonConfig configJson(String[] excludes, String datePattern) throws Exception {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(excludes);
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.registerJsonValueProcessor(Date.class,
		new DateJsonValueProcessor(datePattern)); 
		return jsonConfig;
	}
}

