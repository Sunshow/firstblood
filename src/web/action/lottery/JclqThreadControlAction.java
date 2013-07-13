package web.action.lottery;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * 竞彩篮球线程控制
 * @author qatang
 *
 */
public class JclqThreadControlAction extends BaseAction{
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(CancelChaseAction.class);
	
	private HttpServletResponse response;
	
	private String workType;//线程工作类型
	
	private EngineAddressConfigService engineAddressConfigService;

	private ThreadWorkType startWorkType = ThreadWorkType.START_THREAD;
	private ThreadWorkType stopWorkType = ThreadWorkType.STOP_THREAD;
	
	private String jclqServiceServlet;//竞彩篮球服务servlet
	
	private LotteryType lotteryType = LotteryType.JCLQ_SF;
	
	private static Object __lock__ = new Object();
	
	public String handle() {
		logger.info("进入篮彩启动");
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
	public String control() {
		logger.info("进入控制篮彩线程");
		JSONObject rs = new JSONObject();
		response = ServletActionContext.getResponse();
		if( workType == null ) {
			rs.put("state","failed");
			rs.put("msg", "线程工作类型数值不存在,null");
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
		
		
		String jclqControlUrl = url + jclqServiceServlet;//竞彩篮球控制url
		logger.info("竞彩篮球控制服务地址:{}",jclqControlUrl);
		Map<String,String> params = new HashMap<String,String>();
		params.put("workType", workType);
		
		String result = null;
		try {
			// 同时只能有一次启动或停止操作
			synchronized (__lock__) {
				List<String> list = CoreHttpUtils.getUrl(jclqControlUrl, params, CharsetConstant.CHARSET_UTF8, 10000);
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
		logger.info("控制篮彩线程结束");
		return null;
	}
	
	public String view() {
		logger.info("进入查看篮彩运行状态");
		JSONObject rs = new JSONObject();
		response = ServletActionContext.getResponse();
		
		String url = this.getEngineAddress();
		if (url == null) {
			rs.put("state","failed");
			rs.put("msg", "未获取到engine地址");
			writeRs(response,rs);
			return null;
		}
		
		String jclqControlUrl = url + jclqServiceServlet;//竞彩篮球控制url
		logger.info("竞彩篮球控制服务地址:{}",jclqControlUrl);
		Map<String,String> params = new HashMap<String,String>();
		params.put("cmd", "view");
		String result = CoreFetcherUtils.URLGet(jclqControlUrl, params, CharsetConstant.CHARSET_UTF8);
		
		rs = JSONObject.fromObject(result);
		
		writeRs(response,rs);
		logger.info("查看篮彩运行状态结束");
		return null;
		
	}
	
	
	public String getWorkType() {
		return workType;
	}
	public void setWorkType(String workType) {
		this.workType = workType;
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
	public String getJclqServiceServlet() {
		return jclqServiceServlet;
	}
	public void setJclqServiceServlet(String jclqServiceServlet) {
		this.jclqServiceServlet = jclqServiceServlet;
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
