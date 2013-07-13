/**
 * 
 */
package web.action.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.CreditExchangeQueryService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.user.CreditExchangeLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author chirowong
 *
 */
public class CreditExchangeQueryAction extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<CreditExchangeLog> creditExchangeLogList;
	private CreditExchangeLog creditExchangeLog;
	private Date beginTime;
	private Date endTime;
	private String orderStr;
	private String orderView;
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	private int result = YesNoStatus.ALL.getValue();
	
	private CreditExchangeQueryService creditExchangeQueryService;
	
	public String handle(){
		logger.info("进入积分互换平台查询");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query(){
		logger.info("进入积分互换平台查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map = null;
		try {
			if(creditExchangeLog == null) creditExchangeLog = new CreditExchangeLog();
			YesNoStatus yesNoStatus = YesNoStatus.getItem(result);
			creditExchangeLog.setStatus(yesNoStatus);
			map = creditExchangeQueryService.queryCreditExchangeList(creditExchangeLog,beginTime,endTime,orderStr,orderView,super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询积分互换平台日志,api调用异常" + e.getMessage());
			super.setErrorMessage("查询积分互换平台日志,api调用异常" + e.getMessage());
			return "failure";
		}
		if (map == null || map.size() == 0) {
			logger.error("API查询积分互换平台日志为空");
			super.setErrorMessage("API查询积分互换平台日志为空");
			return "failure";
		}
		creditExchangeLogList = (List<CreditExchangeLog>) map.get(Global.API_MAP_KEY_LIST);
		PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));

		logger.info("查询积分互换平台日志结束");
		return "list";
	}

	public CreditExchangeQueryService getCreditExchangeQueryService() {
		return creditExchangeQueryService;
	}

	public void setCreditExchangeQueryService(
			CreditExchangeQueryService creditExchangeQueryService) {
		this.creditExchangeQueryService = creditExchangeQueryService;
	}

	public List<CreditExchangeLog> getCreditExchangeLogList() {
		return creditExchangeLogList;
	}

	public void setCreditExchangeLogList(
			List<CreditExchangeLog> creditExchangeLogList) {
		this.creditExchangeLogList = creditExchangeLogList;
	}

	public CreditExchangeLog getCreditExchangeLog() {
		return creditExchangeLog;
	}

	public void setCreditExchangeLog(CreditExchangeLog creditExchangeLog) {
		this.creditExchangeLog = creditExchangeLog;
	}
	
	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getOrderStr() {
		return orderStr;
	}

	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}

	public String getOrderView() {
		if (StringUtils.isEmpty(orderView)) {
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		return orderView;
	}

	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}

	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(CreditExchangeLog.ORDER_CREATETIME, "发起交易时间");
		orderStrMap.put(CreditExchangeLog.ORDER_ID, "流水号");
		return orderStrMap;
	}

	public void setOrderStrMap(Map<String, String> orderStrMap) {
		this.orderStrMap = orderStrMap;
	}

	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		return orderViewMap;
	}

	public void setOrderViewMap(Map<String, String> orderViewMap) {
		this.orderViewMap = orderViewMap;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
