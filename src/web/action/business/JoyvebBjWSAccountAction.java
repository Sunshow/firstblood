package web.action.business;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.BIService;

public class JoyvebBjWSAccountAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private BIService bIService;

	private String wagerCardNum;// 投注卡号
	private String certificateNum;// 身份证号
	private String phoneNum;// 联系电话

	public String handle() {
		return "input";
	}

	/** 注册投注卡 */
	public String regist() {

		StringBuffer log = new StringBuffer();
		log.append(String.format("投注卡号 : %s ; ", wagerCardNum));
		log.append(String.format("身份证号 : %s ; ", certificateNum));
		log.append(String.format("电话号码 : %s ; ", phoneNum));

		if (wagerCardNum == null || "".equals(wagerCardNum) || certificateNum == null || "".equals(certificateNum) || phoneNum == null || "".equals(phoneNum)) {
			log.append(" --> 必要参数为空!");
			logger.error(log.toString());
			writeMessage(false, "注册失败! 原因 : 必要参数为空");
			return null;
		}

		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put("ProcessCode", BIService.JoyvebBjWSAccount);
		requestMap.put("wagercardnum", wagerCardNum);
		requestMap.put("certificatenum", certificateNum);
		requestMap.put("phonenum", phoneNum);

		Map<String, String> responseMap = null;
		try {
			responseMap = bIService.request(requestMap);
		} catch (Exception e) {
			log.append(" --> 请求engine出现异常!");
			logger.error(log.toString(), e);
			writeMessage(false, "注册失败! 原因 : 链接异常");
			return null;
		}

		if (responseMap == null) {
			log.append(" --> engine响应为空!");
			logger.error(log.toString());
			writeMessage(false, "注册失败! 原因 : 响应为空");
			return null;
		}

		String respCode = responseMap.get("RespCode");
		log.append(String.format("engine返回码 : %s ; ", respCode));

		String respMesg = responseMap.get("RespMesg");
		log.append(String.format("engine返回信息 : %s ; ", respMesg));

		if ("0000".equals(respCode)) {
			log.append(" --> 注册成功!");
			logger.info(log.toString());
			writeMessage(true, respMesg);
		} else {
			log.append(" --> 注册失败!");
			logger.error(log.toString());
			writeMessage(false, respMesg);
		}

		return null;
	}

	/** 生成返回的json信息 */
	private void writeMessage(boolean status, String message) {
		JSONObject json = new JSONObject();
		json.put("message", message);

		if (status) {
			json.put("result", "succeed");
		} else {
			json.put("result", "fail");
		}

		super.writeRs(ServletActionContext.getResponse(), json);
	}

	public BIService getbIService() {
		return bIService;
	}

	public void setbIService(BIService bIService) {
		this.bIService = bIService;
	}

	public String getWagerCardNum() {
		return wagerCardNum;
	}

	public void setWagerCardNum(String wagerCardNum) {
		this.wagerCardNum = wagerCardNum;
	}

	public String getCertificateNum() {
		return certificateNum;
	}

	public void setCertificateNum(String certificateNum) {
		this.certificateNum = certificateNum;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

}