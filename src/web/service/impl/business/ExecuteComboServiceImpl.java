package web.service.impl.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.ExecuteComboService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.ComboOrderDetail;
import com.lehecai.core.api.lottery.ComboOrderDetailWait;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;

public class ExecuteComboServiceImpl implements ExecuteComboService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ApiRequestService apiWriteRequestService;
	private ApiRequestService apiRequestService;
	
	@Override
	public String comboOrderExecute(List<String> comboOrderId,
			LotteryType lotteryType, String phase)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API执行套餐列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_COMBO_ORDER_EXECUTE);
		if (comboOrderId != null && comboOrderId.size() > 0) {
			request.setParameterIn(ComboOrderDetail.QUERY_ID, comboOrderId);
		} else {
			logger.error("套餐订单ID为空");
			return null;
		}
		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(ComboOrderDetail.QUERY_LOTTERY_TYPE, lotteryType.getValue() + "");
		} else {
			logger.error("彩种传递错误");
			return null;
		}
		if (phase != null && !phase.equals("")) {
			request.setParameter(ComboOrderDetail.QUERY_PHASE, phase);
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_LONG);
		if (response == null) {
			logger.error("API执行套餐失败");
			throw new ApiRemoteCallFailedException("API执行套餐失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API执行套餐请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API执行套餐请求出错," + response.getMessage());
		}
		if (response.getData() == null || response.getData().equals("")) {
			logger.error("调用API执行套餐请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API执行套餐请求出错," + response.getMessage());
		}
		return response.getData().get(0).toString();
	}
	
	@Override
	public Map<String, Object> queryWaitExcuteComboOrderList(Long uid, Long comboId,
			Long comboOrderId, LotteryType lotteryType, String phase, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询待执行套餐套餐列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_COMBO_ORDER_UNEXECUTED);
		if (uid != null && uid != 0) {
			request.setParameter(ComboOrderDetail.QUERY_UID, uid + "");
		}
		if (comboId != null && comboId != 0) {
			request.setParameter(ComboOrderDetail.QUERY_COMBO_ID, comboId + "");
		}
		if (comboOrderId != null && comboOrderId != 0) {
			request.setParameter(ComboOrderDetail.QUERY_ID, comboOrderId + "");
		}
		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(ComboOrderDetail.QUERY_LOTTERY_TYPE, lotteryType.getValue() + "");
		}
		if (phase != null && !phase.equals("")) {
			request.setParameter(ComboOrderDetail.QUERY_PHASE, phase);
		}
		if (pageBean!=null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_LONG);//特殊的设置为30s
		if (response == null) {
			logger.error("API执行套餐失败");
			throw new ApiRemoteCallFailedException("API执行套餐失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API执行套餐请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API执行套餐请求出错," + response.getMessage());
		}
		if (response.getData() == null || response.getData().equals("")) {
			logger.error("调用API执行套餐请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API执行套餐请求出错," + response.getMessage());
		}
		List<ComboOrderDetailWait> comboDetailList = ComboOrderDetailWait.convertFromJSONArray(response.getData());
		
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
		map.put(Global.API_MAP_KEY_LIST, comboDetailList);
		
		return map;
	}


	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

}
