package web.service.impl.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.domain.business.PayPlatformRate;
import com.lehecai.admin.web.service.business.PayPlatformService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.PayPlatformType;
import com.lehecai.core.service.setting.SettingService;

/**
 * 
 * @author He Wang
 *
 */
public class PayPlatformServiceImpl implements PayPlatformService {
	private final Logger logger = LoggerFactory.getLogger(PayPlatformServiceImpl.class);
	
	private SettingService settingService;
	private static final String USER_DEFAULT = "use_default";
	private static final String RATE = "rate";

	@Override
	public List<PayPlatformRate> getPayPlatFormRateList(String settingGroup, String itemId) throws ApiRemoteCallFailedException {
		String json = settingService.get(settingGroup, itemId);
		JSONObject jsonObj = JSONObject.fromObject(json);
		List<PayPlatformRate> rateList = new ArrayList<PayPlatformRate>();
		if (jsonObj != null && jsonObj.size() > 0) {
			if (itemId == null) {
				for (Iterator<?> i = jsonObj.keySet().iterator(); i.hasNext();) {
					String key = (String)i.next();
					if (key != null && !key.equals("") && jsonObj.get(key) instanceof JSONObject) {
						JSONObject tempObj = jsonObj.getJSONObject(key);
						PayPlatformRate payPlatformRate = new PayPlatformRate();
						payPlatformRate.setPlatformType(key);
						Boolean userDefaultBoolean = tempObj.getBoolean(USER_DEFAULT);
						YesNoStatus userDefault = YesNoStatus.YES;
						if (userDefaultBoolean != null && userDefaultBoolean == false) {
							userDefault = YesNoStatus.NO;
						}
						payPlatformRate.setUserDefault(userDefault);
						JSONObject rateObj = tempObj.getJSONObject(RATE);
						Map<PayPlatformType, Integer> payPlatformTypeMap = new LinkedHashMap<PayPlatformType, Integer>();
						for (Iterator<?> j = rateObj.keySet().iterator(); j.hasNext();) {  
							String tempKey = (String)j.next();
							Integer rate = 0;
							try{
								rate = rateObj.getInt(tempKey);
							} catch (Exception e) {
								logger.error("获取比例错误，原因{}", e.getMessage());
							}
							payPlatformTypeMap.put(PayPlatformType.getItemByName(tempKey), rate);
						}
						payPlatformRate.setPayPlatformTypeMap(payPlatformTypeMap);
						rateList.add(payPlatformRate);
					}
				}
			} else {
				PayPlatformRate payPlatformRate = new PayPlatformRate();
				payPlatformRate.setPlatformType(itemId);
				Map<PayPlatformType, Integer> payPlatformTypeMap = new LinkedHashMap<PayPlatformType, Integer>();
				Boolean userDefaultBoolean = jsonObj.getBoolean(USER_DEFAULT);
				YesNoStatus userDefault = YesNoStatus.YES;
				if (userDefaultBoolean != null && userDefaultBoolean == false) {
					userDefault = YesNoStatus.NO;
				}
				payPlatformRate.setUserDefault(userDefault);
				JSONObject jsonObjRate = jsonObj.getJSONObject(RATE);
				for (Iterator<?> j = jsonObjRate.keySet().iterator(); j.hasNext();) {  
					String tempKey = (String)j.next();
					Integer rate = 0;
					try{
						rate = jsonObjRate.getInt(tempKey);
					} catch (Exception e) {
						logger.error("获取比例错误，原因{}", e.getMessage());
					}
					payPlatformTypeMap.put(PayPlatformType.getItemByName(tempKey), rate);
				}
				payPlatformRate.setPayPlatformTypeMap(payPlatformTypeMap);
				rateList.add(payPlatformRate);
			}
		}
		return rateList;
	}
	
	@Override
	public boolean updateItemSettings(String group, String item, String value) {
		boolean result = false;
		try {
			result = settingService.add(group, item);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			result = false;
		}
		if (result) {
			try {
				result = settingService.update(group, item, value);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("通过setting service更新配置出错", e);
				logger.error("group={}, item={}, value={}", new Object[]{group, item, value});
			}
		}
		return result;
	}
	
	public SettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(SettingService settingService) {
		this.settingService = settingService;
	}

}
