/**
 * 
 */
package web.service.impl.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.CreditExchangeQueryService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.CreditExchangeLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author chirowong
 *
 */
public class CreditExchangeQueryServiceImpl implements CreditExchangeQueryService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ApiRequestService apiRequestService;

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

	@Override
	public Map<String, Object> queryCreditExchangeList(
			CreditExchangeLog creditExchangeLog, Date beginTime, Date endTime,
			String orderStr, String orderView, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用积分互换平台日志查询API");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CREDIT_EXCHANGE_QUERY);
		String _id = creditExchangeLog.getId(); 
		if(_id != null && !"".equals(_id)){
			request.setParameter(CreditExchangeLog.QUERY_ID, creditExchangeLog.getId());
		}
		Long _uid = Long.valueOf(creditExchangeLog.getUid()); 
		if(_uid != null && _uid != 0L){
			request.setParameter(CreditExchangeLog.QUERY_UID, creditExchangeLog.getUid()+"");
		}
		Long _partner_id = Long.valueOf(creditExchangeLog.getPartnerId());
		if(_partner_id != null && _partner_id != 0L){
			request.setParameter(CreditExchangeLog.QUERY_PARTNERID, creditExchangeLog.getPartnerId()+"");
		}
		YesNoStatus _status = creditExchangeLog.getStatus(); 
		if(_status != null && _status != YesNoStatus.ALL){
			request.setParameter(CreditExchangeLog.QUERY_STATUS, _status.getValue()+"");
		}
		Date _create_at = creditExchangeLog.getCreateTime(); 
		if(_create_at != null){
			request.setParameterBetween(CreditExchangeLog.QUERY_CREATETIME, DateUtil.formatDate(_create_at,DateUtil.DATETIME),null);
		}
		if (beginTime != null) {
			request.setParameterBetween(CreditExchangeLog.QUERY_CREATETIME, DateUtil.formatDate(beginTime,DateUtil.DATETIME),null);
		}
		if (endTime != null) {
			request.setParameterBetween(CreditExchangeLog.QUERY_CREATETIME, null,DateUtil.formatDate(endTime,DateUtil.DATETIME));
		}
		if (orderStr != null && !"".equals(orderView) && orderView != null
				&& !"".equals(orderView)) {
			request.addOrder(orderStr,orderView);
		}else{
			request.addOrder(CreditExchangeLog.ORDER_CREATETIME,ApiConstant.API_REQUEST_ORDER_DESC);
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}

		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用积分互换平台日志查询API失败");
			throw new ApiRemoteCallFailedException("调用积分互换平台日志查询API失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用积分互换平台日志查询API请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用积分互换平台日志查询API请求出错," + response.getMessage());
		}
		logger.info("结束调用积分互换平台日志查询API");
		List<CreditExchangeLog> creditExchangerLogList = CreditExchangeLog.convertFromJSONArray(response.getData());
		
		if (pageBean != null && pageBean.isPageFlag()) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			
			int pageCount = 0;//页数
			if ( pageBean.getPageSize() != 0 ) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, creditExchangerLogList);
		
		return map;
	}
}
