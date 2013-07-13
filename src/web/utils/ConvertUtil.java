/**
 * 
 */
package web.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qatang
 *
 */
public class ConvertUtil {
	
	private final Logger logger = LoggerFactory.getLogger(ConvertUtil.class);
	
	private static Map<String, String[]> HC1_MAP = new HashMap<String, String[]>();
	
	static {
		HC1_MAP.put("01", new String[]{"鼠", "春", "东"});
		HC1_MAP.put("02", new String[]{"牛", "春", "南"});
		HC1_MAP.put("03", new String[]{"虎", "春", "东"});
		HC1_MAP.put("04", new String[]{"兔", "春", "南"});
		HC1_MAP.put("05", new String[]{"龙", "春", "东"});
		HC1_MAP.put("06", new String[]{"蛇", "春", "南"});
		HC1_MAP.put("07", new String[]{"马", "春", "东"});
		HC1_MAP.put("08", new String[]{"羊", "春", "南"});
		HC1_MAP.put("09", new String[]{"猴", "春", "东"});
		HC1_MAP.put("10", new String[]{"鸡", "夏", "南"});
		HC1_MAP.put("11", new String[]{"狗", "夏", "东"});
		HC1_MAP.put("12", new String[]{"猪", "夏", "南"});
		HC1_MAP.put("13", new String[]{"鼠", "夏", "东"});
		HC1_MAP.put("14", new String[]{"牛", "夏", "南"});
		HC1_MAP.put("15", new String[]{"虎", "夏", "东"});
		HC1_MAP.put("16", new String[]{"兔", "夏", "南"});
		HC1_MAP.put("17", new String[]{"龙", "夏", "东"});
		HC1_MAP.put("18", new String[]{"蛇", "夏", "南"});
		HC1_MAP.put("19", new String[]{"马", "秋", "西"});
		HC1_MAP.put("20", new String[]{"羊", "秋", "北"});
		HC1_MAP.put("21", new String[]{"猴", "秋", "西"});
		HC1_MAP.put("22", new String[]{"鸡", "秋", "北"});
		HC1_MAP.put("23", new String[]{"狗", "秋", "西"});
		HC1_MAP.put("24", new String[]{"猪", "秋", "北"});
		HC1_MAP.put("25", new String[]{"鼠", "秋", "西"});
		HC1_MAP.put("26", new String[]{"牛", "秋", "北"});
		HC1_MAP.put("27", new String[]{"虎", "秋", "西"});
		HC1_MAP.put("28", new String[]{"兔", "冬", "北"});
		HC1_MAP.put("29", new String[]{"龙", "冬", "西"});
		HC1_MAP.put("30", new String[]{"蛇", "冬", "北"});
		HC1_MAP.put("31", new String[]{"马", "冬", "西"});
		HC1_MAP.put("32", new String[]{"羊", "冬", "北"});
		HC1_MAP.put("33", new String[]{"猴", "冬", "西"});
		HC1_MAP.put("34", new String[]{"鸡", "冬", "北"});
		HC1_MAP.put("35", new String[]{"狗", "冬", "西"});
		HC1_MAP.put("36", new String[]{"猪", "冬", "北"});
	}
	
	
	public String convertDf6j1(String num) {
		logger.info("转换东方6+1生肖");
		String convertStr = "";
		if(num != null){
			if("01".equals(num)){
				convertStr = "鼠";
			}else if("02".equals(num)){
				convertStr = "牛";
			}else if("03".equals(num)){
				convertStr = "虎";
			}else if("04".equals(num)){
				convertStr = "兔";
			}else if("05".equals(num)){
				convertStr = "龙";
			}else if("06".equals(num)){
				convertStr = "蛇";
			}else if("07".equals(num)){
				convertStr = "马";
			}else if("08".equals(num)){
				convertStr = "羊";
			}else if("09".equals(num)){
				convertStr = "猴";
			}else if("10".equals(num)){
				convertStr = "鸡";
			}else if("11".equals(num)){
				convertStr = "狗";
			}else if("12".equals(num)){
				convertStr = "猪";
			}
		}
		return convertStr;
	}
	
	public String converNyfchc1(int type,String num){
		logger.info("转换南粤风采好彩1");
		
		String[] str = HC1_MAP.get(num);
		
		return str[type];
	}
}
