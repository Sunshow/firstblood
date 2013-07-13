/**
 * 
 */
package web.service.impl.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.config.MobilePlatformInfo;
import com.lehecai.admin.web.service.config.MobileSettingService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.setting.Setting;
import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.service.setting.SettingService;

public class MobileSettingServiceImpl implements MobileSettingService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private ApiRequestService apiRequestService;
	private SettingService settingService;

	@Override
	public MobilePlatformInfo get(String group, String item) throws ApiRemoteCallFailedException {
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_SETTINGS_QUERY);
		request.setParameter(Setting.QUERY_GROUP, group);
		request.setParameter(Setting.QUERY_ITEM, item);
		MobilePlatformInfo info = new MobilePlatformInfo();
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API获取参数请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			return null;
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取配置集为空, message={}", response.getMessage());
			return null;
		}
		String result = response.getData().getString(0);
		JSONObject obj = JSONObject.fromObject(result);
		info = MobilePlatformInfo.convertFromJSONObject(obj);
		info.setGroup(group);
		info.setMobilePlatformId(item);
		return info;
	}
	
	@Override
	public List<MobilePlatformInfo> mget() {
		List<MobilePlatformInfo> mobilePlatformInfoList = new ArrayList<MobilePlatformInfo>();
		List<String> itemList = new ArrayList<String>();
		Map<String, String> mobilePlatformTypeMap = SettingConstant.getPlatformMap();
		for (Iterator<String> i = mobilePlatformTypeMap.keySet().iterator(); i.hasNext(); ) {
			String key = (String) i.next();
			itemList.add(key);
		}
		Map<String, String> resultMap;
		try {
			resultMap = settingService.mget(SettingConstant.APP_VERSION_LOTTERY_STOP_SELL, itemList);
			if (resultMap != null) {
				for (Iterator<String> it = resultMap.keySet().iterator(); it.hasNext(); ) {
	    			String key = (String) it.next();
	    			String values = resultMap.get(key);
	    			String value = mobilePlatformTypeMap.get(key);
	    			if (StringUtils.isNotEmpty(values) && !values.equals("null") && !values.equals("false")) {
	    				JSONObject obj = JSONObject.fromObject(values);
	    				MobilePlatformInfo info = MobilePlatformInfo.convertFromJSONObject(obj);
	    				info.setMobilePlatformName(value);
	    				info.setMobilePlatformId(key);
	    				mobilePlatformInfoList.add(info);
	    			}
	    		}
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("调用接口失败", e.getMessage());
		}
		return mobilePlatformInfoList;
	}
	
	@Override
	public boolean merge(MobilePlatformInfo mobilePlatformInfo) throws ApiRemoteCallFailedException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("app_version", mobilePlatformInfo.getVersionNum());
    	jsonObject.put("op", mobilePlatformInfo.getOperators());
    	jsonObject.put("status", mobilePlatformInfo.getStatus().getValue());
    	jsonObject.put("lottery_type",mobilePlatformInfo.getLotteryTypeArray());
		String value = jsonObject.toString();
		boolean flag = settingService.add(mobilePlatformInfo.getGroup(), mobilePlatformInfo.getMobilePlatformId());
		if (flag) {
			flag = settingService.update(mobilePlatformInfo.getGroup(), mobilePlatformInfo.getMobilePlatformId(), value);
		}
		return flag;
	}
	
	@Override
	public boolean update(MobilePlatformInfo mobilePlatformInfo) throws ApiRemoteCallFailedException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("app_version", mobilePlatformInfo.getVersionNum());
    	jsonObject.put("op", mobilePlatformInfo.getOperators());
    	jsonObject.put("status", mobilePlatformInfo.getStatus().getValue());
    	jsonObject.put("lottery_type", mobilePlatformInfo.getLotteryTypeArray());
		String value = jsonObject.toString();
		return settingService.update(mobilePlatformInfo.getGroup(), mobilePlatformInfo.getMobilePlatformId(), value);
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}
    
	public SettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(SettingService settingService) {
		this.settingService = settingService;
	}
}
