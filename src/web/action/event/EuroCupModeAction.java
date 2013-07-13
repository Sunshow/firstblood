/**
 * 
 */
package web.action.event;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.service.event.EuroCupService;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author chirowong
 *
 */
public class EuroCupModeAction extends BaseAction {

	private static final long serialVersionUID = 8625207108649371005L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private HttpServletResponse response;
	
	private EuroCupService euroCupService;
	private String mode;
	
	public String handle(){
		logger.info("进入欧洲杯设置模式开始");
		ResultBean resultBean = null;
		try{
			resultBean = euroCupService.getMode();
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取欧洲杯模式，api调用异常，{}", e.getMessage());
			super.setErrorMessage("获取欧洲杯模式，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}		
		if (resultBean != null) {
			mode = resultBean.getData();
		}
		logger.info("进入获取欧洲杯设置模式结束");
		return "euroCupMode";
	}
	
	public String manage(){
		logger.info("更新欧洲杯模式开始");
		ResultBean resultBean;
		try {
			resultBean = euroCupService.setMode(mode);
		} catch (ApiRemoteCallFailedException e) {
			logger.info("更新模式结束，原因："+e.getMessage());
			super.setErrorMessage("更新模式失败，原因："+e.getMessage());
			return "failure";
		}
		if(resultBean.isResult()){
			logger.info("更新模式结束");
			super.setForwardUrl("/event/euroCupMode.do");
			return "success";
		}else{
			super.setErrorMessage(resultBean.getMessage());
			logger.info("更新模式结束");
			return "failure";
		}
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	public EuroCupService getEuroCupService() {
		return euroCupService;
	}
	public void setEuroCupService(EuroCupService euroCupService) {
		this.euroCupService = euroCupService;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
}
