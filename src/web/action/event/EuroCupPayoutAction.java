/**
 * 
 */
package web.action.event;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
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
public class EuroCupPayoutAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7616527963436641558L;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private EuroCupService euroCupService;
	
	public String handle(){
		logger.info("进入获取欧洲杯派奖信息开始");
		return "payout";
	}
	
	public String payout(){
		logger.info("进入获取欧洲杯派奖信息开始");
		ResultBean resultBean = null;
		JSONObject object = new JSONObject();
		try{
			resultBean = euroCupService.payout();
			object.put("result", resultBean.isResult());
			object.put("msg", resultBean.getMessage());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取欧洲杯派奖信息，api调用异常，{}", e.getMessage());
			object.put("result", false);
			object.put("msg", e.getMessage());;
		}		
		writeRs(ServletActionContext.getResponse(), object);
		logger.info("进入获取欧洲杯派奖信息结束");
		return null;
	}
	
	public EuroCupService getEuroCupService() {
		return euroCupService;
	}

	public void setEuroCupService(EuroCupService euroCupService) {
		this.euroCupService = euroCupService;
	}
}
