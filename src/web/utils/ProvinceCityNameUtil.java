package web.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProvinceCityNameUtil {
	private static List<String> ESPECIAL_PROVINCE = new ArrayList<String>();
	static {
		ESPECIAL_PROVINCE.add("北京");
		ESPECIAL_PROVINCE.add("上海");
		ESPECIAL_PROVINCE.add("重庆");
		ESPECIAL_PROVINCE.add("天津");
	}
	
	protected final static Logger logger = LoggerFactory.getLogger(ProvinceCityNameUtil.class.getName());
	
	public static String reviseProvince(String provinceName) {
		if (provinceName != null && !provinceName.equals("")) {
			if (ESPECIAL_PROVINCE.contains(provinceName)) {
				return provinceName + "市";
			} else {
				return provinceName + "省";
			}
		} else {
			return "";
		}
	}
	public static String reviseCity(String cityName) {
		if (cityName != null && !cityName.equals("")) {
			return cityName + "市";
		} else {
			return "";
		}
	}
}