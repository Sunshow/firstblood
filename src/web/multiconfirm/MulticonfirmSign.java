package web.multiconfirm;

import java.util.HashMap;
import java.util.Map;


public class MulticonfirmSign {
	
	public static String getDefaultConfigString(String actionName, String methodName) {
		String key = actionName + MulticonfirmConstant.SPLIT_FLAG + methodName;
		return key;
	}
	
	public static String getSpecialConfigString(String actionName, String methodName, String specialValue) {
		String key = actionName + MulticonfirmConstant.SPLIT_FLAG + methodName + MulticonfirmConstant.SPLIT_FLAG + specialValue;
		return key;
	}
	
	public static String getConfigString(String actionName, String methodName, String specialValue) {
		String key = "";
		if (specialValue == null || specialValue.equals("")) {
			key = getDefaultConfigString(actionName, methodName);
		} else {
			key = getSpecialConfigString(actionName, methodName, specialValue);
		}
		return key;
	}
	
	public static Map<String, String> paraseConfigKey(String configKey) {
		if (configKey == null || configKey.equals("")) {
			return null;
		}
		String[] values = configKey.split(MulticonfirmConstant.SPLIT_FLAG);
		Map<String, String> map = new HashMap<String, String>();
		map.put(MulticonfirmConstant.MULITCONFIRM_ACTIONNAME, values[0]);
		map.put(MulticonfirmConstant.MULITCONFIRM_METHODNAME, values[1]);
		if (values.length > 2) {
			String specialValue = "";
			for (int i = 2; i < values.length; i++) {
				if (specialValue == null || specialValue.equals("")) {
					specialValue = values[i];
				} else {
					specialValue = specialValue + MulticonfirmConstant.SPLIT_FLAG + values[i];
				}
			}
			map.put(MulticonfirmConstant.MULTICONFIRM_SPECIAL_VALUE, specialValue);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public static String getConfigString(String actionName, String methodName,
			Map paramMap, MulticonfirmConfig multiconfirmConfig) {
		if (multiconfirmConfig.getSpecialSigns() == null || multiconfirmConfig.getSpecialSigns().equals("")) {
			return getDefaultConfigString(actionName, methodName);
		}
		if (paramMap == null || paramMap.size() == 0) {
			return getDefaultConfigString(actionName, methodName);
		}
		String[] specialNames = multiconfirmConfig.getSpecialSigns().split(MulticonfirmConstant.SPLIT_FLAG);
		boolean flag = true;
		String specialValue = "";
		for (int i = 0; i < specialNames.length; i++) {
			if (paramMap.containsKey(specialNames[i]) && paramMap.get(specialNames[i]) != null) {
				if (specialValue == null || specialValue.equals("")) {
					specialValue = ((String[]) paramMap.get(specialNames[i]))[0];
				} else {
					specialValue = specialValue + MulticonfirmConstant.SPLIT_FLAG + ((String[]) paramMap.get(specialNames[i]))[0];
				}
			} else {
				flag = false;
				break;
			}
		}
		if (flag) {
			return getSpecialConfigString(actionName, methodName, specialValue);
		} else {
			return getDefaultConfigString(actionName, methodName);
		}
	}

	public static String getXMLId(String actionName, String methodName) {
		String key = actionName + MulticonfirmConstant.XML_SPLIT_FLAG + methodName;
		return key;
	}

}
