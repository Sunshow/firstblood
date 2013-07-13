package web.service.impl.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.business.ComboOrderCancelService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.ComboOrder;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class ComboOrderCancelServiceImpl implements ComboOrderCancelService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public boolean comboOrderCancel(Long comboOrderId)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API取消套餐列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_COMBO_ORDER_CANCEL);
		if (comboOrderId != null || comboOrderId != 0) {
			request.setParameter(ComboOrder.QUERY_COMBO_ORDER_ID, comboOrderId + "");
		} 
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API取消套餐失败");
			throw new ApiRemoteCallFailedException("API取消套餐失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API取消套餐请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API取消套餐请求出错," + response.getMessage());
		}
		logger.info("结束调用API取消套餐列表");
		return true;
	}
	
	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}

}
