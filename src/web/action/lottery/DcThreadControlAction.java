package web.action.lottery;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.config.EngineAddressConfigService;
import com.lehecai.core.api.setting.ThreadWorkType;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreFetcherUtils;
import com.lehecai.core.util.CoreHttpUtils;
import com.lehecai.core.util.CoreStringUtils;

/**
 * 单场线程控制
 * @author leiming
 *
 */
public class DcThreadControlAction extends BaseAction{
	private final Logger logger = LoggerFactory.getLogger(CancelChaseAction.class);
	private static final long serialVersionUID = 2436161530465382824L;
	
	@SuppressWarnings("unused")
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	private EngineAddressConfigService engineAddressConfigService;
	
	private String workType;//线程工作类型
	private String phaseNo;//彩期号

	private ThreadWorkType startWorkType = ThreadWorkType.START_THREAD;
	private ThreadWorkType stopWorkType = ThreadWorkType.STOP_THREAD;
	
	private String dcServiceServlet;//单场服务servlet
	
	private LotteryType lotteryType = LotteryType.DC_SFP;
	
	private static Object __lock__ = new Object();
	
	public String handle(){
		logger.info("进入单场启动");
		return "control";
	}
	
	private String getEngineAddress () {
		try {
			return engineAddressConfigService.getLotteryAddress(lotteryType);
		} catch (Exception e) {
			logger.error("获取engine调用地址出错", e);
			return null;
		}
	}
	
	/**
	 * 控制线程
	 * @return
	 */
	public String control(){
		logger.info("进入控制线程");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		if (workType == null) {
			rs.put("state","failed");
			rs.put("msg", "线程工作类型数值不存在,null");
			writeRs(response,rs);
			return null;
		}
		if (phaseNo == null || phaseNo.isEmpty()) {
			rs.put("state","failed");
			rs.put("msg", "单场期号不存在,null");
			writeRs(response,rs);
			return null;
		}
		
		String url = this.getEngineAddress();
		if (url == null) {
			rs.put("state","failed");
			rs.put("msg", "未获取到engine地址");
			writeRs(response,rs);
			return null;
		}
		
		String dcControlUrl = url + dcServiceServlet;//单场控制url
		logger.info("单场控制服务地址:{}",dcControlUrl);
		Map<String,String> params = new HashMap<String,String>();
		params.put("workType", workType);
		params.put("phaseNo", phaseNo);
		
		String result = null;
		try {
			// 同时只能有一次启动或停止操作
			synchronized (__lock__) {
				List<String> list = CoreHttpUtils.getUrl(dcControlUrl, params, CharsetConstant.CHARSET_UTF8, 10000);
				result = CoreStringUtils.join(list, "");
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		
		if (result == null) {
			rs.put("state","failed");
			rs.put("msg", "调用服务出错");
			writeRs(response,rs);
			return null;
		}
		
		rs = JSONObject.fromObject(result);
		
		writeRs(response,rs);
		logger.info("控制线程结束");
		return null;
	}
	
	public String view(){
		logger.info("进入查看单场运行状态");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		
		String url = this.getEngineAddress();
		if (url == null) {
			rs.put("state","failed");
			rs.put("msg", "未获取到engine地址");
			writeRs(response,rs);
			return null;
		}
		
		String dcControlUrl = url + dcServiceServlet;//单场控制url
		logger.info("单场控制服务地址:{}",dcControlUrl);
		Map<String,String> params = new HashMap<String,String>();
		params.put("cmd", "view");
		String result = CoreFetcherUtils.URLGet(dcControlUrl, params, CharsetConstant.CHARSET_UTF8);
		
		rs = JSONObject.fromObject(result);
		
		writeRs(response,rs);
		logger.info("查看单场运行状态结束");
		return null;
		
	}
	
	
	public String getWorkType() {
		return workType;
	}
	public void setWorkType(String workType) {
		this.workType = workType;
	}
	public String getPhaseNo() {
		return phaseNo;
	}
	public void setPhaseNo(String phaseNo) {
		this.phaseNo = phaseNo;
	}
	
	public ThreadWorkType getStartWorkType() {
		return startWorkType;
	}
	public void setStartWorkType(ThreadWorkType startWorkType) {
		this.startWorkType = startWorkType;
	}
	public ThreadWorkType getStopWorkType() {
		return stopWorkType;
	}
	public void setStopWorkType(ThreadWorkType stopWorkType) {
		this.stopWorkType = stopWorkType;
	}
	public String getDcServiceServlet() {
		return dcServiceServlet;
	}
	public void setDcServiceServlet(String dcServiceServlet) {
		this.dcServiceServlet = dcServiceServlet;
	}
	public LotteryType getLotteryType() {
		return lotteryType;
	}
	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}

	public EngineAddressConfigService getEngineAddressConfigService() {
		return engineAddressConfigService;
	}

	public void setEngineAddressConfigService(
			EngineAddressConfigService engineAddressConfigService) {
		this.engineAddressConfigService = engineAddressConfigService;
	}
}
