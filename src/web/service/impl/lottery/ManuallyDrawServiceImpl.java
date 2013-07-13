/**
 * 
 */
package web.service.impl.lottery;

import com.lehecai.admin.web.service.config.EngineAddressConfigService;
import com.lehecai.admin.web.service.lottery.ManuallyDrawService;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreHttpUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author qatang
 *
 */
public class ManuallyDrawServiceImpl implements ManuallyDrawService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private EngineAddressConfigService engineAddressConfigService;
	
	private final static String SERVLET_URL = "/BIServlet";
	private final static String PROCESS_CODE = "50001";
	
	private final static String KEY_CODE = "code";
	private final static String KEY_MESSAGE = "msg";
	private final static String KEY_DATA = "data";

	@Override
	public JSONObject draw(String planId) {
		if (StringUtils.isEmpty(planId)) {
			logger.error("手动开奖获取planId参数不能为空");
			return null;
		}
		String engineUrl = null;
		try {
			engineUrl = engineAddressConfigService.getDefaultAddress();
		} catch (Exception e) {
			logger.error("手动开奖获取engine调用地址失败");
			logger.error(e.getMessage(), e);
		}
		if (StringUtils.isEmpty(engineUrl)) {
			return null;
		}
		logger.info("engine调用地址：{}", engineUrl);
		
		List<String> list = null;
		try {
			list = CoreHttpUtils.getUrl(engineUrl + SERVLET_URL, "ProcessCode=" + PROCESS_CODE + "&planNo=" + planId, CharsetConstant.CHARSET_UTF8, 120000);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		if (list == null || list.size() == 0) {
			logger.error("手动开奖planId={}的方案时，返回值为空", planId);
			return null;
		}
		Map<String, String> resultMap = CoreHttpUtils.parseQueryString(list.get(0), CharsetConstant.CHARSET_UTF8);
		if (resultMap == null || !resultMap.containsKey(KEY_CODE) || !resultMap.containsKey(KEY_MESSAGE)) {
			logger.error("手动开奖planId={}的方案时，返回值数据结构错误", planId, list.get(0));
			return null;
		}
		if (Integer.parseInt(resultMap.get(KEY_CODE)) != 0) {
			logger.error("手动开奖planId={}的方案失败，原因：{}", planId, resultMap.get(KEY_MESSAGE));
			return null;
		}
		String data = resultMap.get(KEY_DATA);
		
		return JSONObject.fromObject(data);
	}

	public EngineAddressConfigService getEngineAddressConfigService() {
		return engineAddressConfigService;
	}

	public void setEngineAddressConfigService(
			EngineAddressConfigService engineAddressConfigService) {
		this.engineAddressConfigService = engineAddressConfigService;
	}

}
