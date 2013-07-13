package web.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.config.EngineAddressConfigService;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreHttpUtils;

public class BIService {

	private EngineAddressConfigService engineAddressConfigService;

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	final static public String BALANCE = "Balance";

	final static public String PROCESS_CODE = "ProcessCode";

	final static public String RESP_CODE = "RespCode";

	final static public String RESP_MESG = "RespMesg";
	
	final static public String RESP_DATA = "RespData";

	final static public String Helper_Account_Synchronous = "20001";
	
	final static public String DYJ_Account_Synchronous = "20002";
	
	final static public String HZH_Account_Synchronous = "20003";	
	
	final static public String ZHCW_Account_Synchronous = "20004";
	
	final static public String JXFC_Account_Synchronous = "20007";

	final static public String CAITONG_Account_Synchronous = "20008";

	final static public String AiCaiPiao_Account_Synchronous = "20009";

	final static public String RuiCai_Account_Synchronous = "20010";

	final static public String YingCai_Account_Synchronous = "20011";

	final static public String HZH_Phase_Synchronous = "30001";

	final static public String JXFC_Phase_Synchronous = "30002";

	final static public String SDTC_Phase_Synchronous = "30003";
	
	//中湘
	final static public String ZHX_Phase_Synchronous = "30004";
	
	//凯米信息
	final static public String KMInfo_Phase_Synchronous = "30005";
	
	//泰和彩
	final static public String THCai_Phase_Synchronous = "30006";

    //上海锦贺添
    final static public String SHJHT_Phase_Synchronous = "30007";

	final static public String Ticket_Jc_Sp_Synchronous = "10001";

	final static public String Specify_PhaseEvent_Process = "40001";

	final static public String Specify_PhaseEventTaskClear_Process = "40002";

	final static public String JoyvebBjWSAccount = "70001";

	public Map<String, String> request(Map<String, String> requestMap, LotteryType lotteryType) {
		URL url = null;
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		try {
			String writeString = CoreHttpUtils.getQueryString(requestMap,
					CharsetConstant.CHARSET_UTF8);
			logger.info("向Engine BI请求:" + writeString);

			String engineUrl = null;
			if (lotteryType != null) {
				engineUrl = getEngineAddressConfigService().getLotteryAddress(lotteryType);
				if (engineUrl == null) {
					logger.error("{}的engine地址未配置", lotteryType.getName());
					throw new Exception(lotteryType.getName() + "的engine地址未配置");
				}
			} else {
				engineUrl = getEngineAddressConfigService().getDefaultAddress();
				if (engineUrl == null) {
					logger.error("默认engine地址未配置");
					throw new Exception("默认engine地址未配置");
				}
			}
			engineUrl += "/BIServlet";

			url = new URL(engineUrl + "?" + writeString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(135000);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			connection.connect();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("BIService:从地址({})获取响应失败：{}", url, connection
						.getResponseMessage());
				throw new Exception("读取地址:" + url + " 错误:"
						+ connection.getResponseCode());
			}

			reader = new BufferedReader(new InputStreamReader(connection
					.getInputStream(), "GBK"));

			StringBuffer sb = new StringBuffer();

			String temp = null;
			while ((temp = reader.readLine()) != null) {
				sb.append(temp);
			}

			Map<String, String> retMap = new HashMap<String, String>();
			retMap = CoreHttpUtils.parseQueryString(sb.toString(),
					CharsetConstant.CHARSET_UTF8);
			return retMap;
		} catch (Exception e) {
			HashMap<String, String> retMap = new HashMap<String, String>();
			retMap.put("RespCode", "9999");
			retMap.put("RespMesg", "通迅过程故障:" + e.getMessage());
			logger.error("BIService:请求地址({})发生异常:", url, e);
			return retMap;
		}

	}

	public Map<String, String> request(Map<String, String> requestMap) {
		return request(requestMap, null);
	}

	public EngineAddressConfigService getEngineAddressConfigService() {
		return engineAddressConfigService;
	}

	public void setEngineAddressConfigService(
			EngineAddressConfigService engineAddressConfigService) {
		this.engineAddressConfigService = engineAddressConfigService;
	}


}
