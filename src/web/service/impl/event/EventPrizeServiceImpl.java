package web.service.impl.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.event.EventPrizeService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.event.EventPrize;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 奖项业务逻辑层实现类，用于添加、修改、删除奖项
 * @author yanweijie
 *
 */
public class EventPrizeServiceImpl implements EventPrizeService {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private ApiRequestService apiWriteRequestService;
	
	/**
	 * 添加奖项
	 * @param eventPrize
	 */
	public boolean addEventPrize(EventPrize eventPrize) throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加奖项");
		
		if (eventPrize == null) {
			logger.error("添加的奖项为空");
			return false;
		}
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EVENT_PRIZE_ADD);

		request.setParameterForUpdate(EventPrize.SET_EVENT_ID, eventPrize.getEventId() + "");
		request.setParameterForUpdate(EventPrize.SET_PRIZE_NAME, eventPrize.getPrizeName());
		request.setParameterForUpdate(EventPrize.SET_PRIZE_QUANTITY, eventPrize.getPrizeQuantity() + "");
		request.setParameterForUpdate(EventPrize.SET_PRIZE_MONEY, eventPrize.getPrizeMoney() + "");
		request.setParameterForUpdate(EventPrize.SET_PRIZE_LEVEL, eventPrize.getPrizeLevel() + "");
		request.setParameterForUpdate(EventPrize.SET_PRIZE_TYPE, eventPrize.getPrizeType().getValue() + "");
		request.setParameterForUpdate(EventPrize.SET_IMG_SRC, eventPrize.getImgSrc());
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API添加奖项失败");
			throw new ApiRemoteCallFailedException("API添加奖项失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API添加奖项请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		
		return true;
	}
	
	/**
	 * 修改奖项
	 * @param eventPrize
	 */
	public boolean updateEventPrize(EventPrize eventPrize) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改奖项");
		
		if (eventPrize == null) {
			logger.error("修改的奖项为空");
			return false;
		}
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EVENT_PRIZE_UPDATE);
		
		if (eventPrize.getEventId() == 0) {
			logger.error("修改的奖项活动编码为空");
			return false;
		}
		if (eventPrize.getPrizeId() == 0) {
			logger.error("修改的奖项编码为空");
			return false;
		}
		request.setParameter(EventPrize.QUERY_EVENT_ID, String.valueOf(eventPrize.getEventId()));
		request.setParameter(EventPrize.QUERY_PRIZE_ID, String.valueOf(eventPrize.getPrizeId()));
		
		request.setParameterForUpdate(EventPrize.SET_PRIZE_NAME, eventPrize.getPrizeName());
		request.setParameterForUpdate(EventPrize.SET_PRIZE_QUANTITY, String.valueOf(eventPrize.getPrizeQuantity()));
		request.setParameterForUpdate(EventPrize.SET_PRIZE_MONEY, String.valueOf(eventPrize.getPrizeMoney()));
		request.setParameterForUpdate(EventPrize.SET_PRIZE_LEVEL, String.valueOf(eventPrize.getPrizeLevel()));
		request.setParameterForUpdate(EventPrize.SET_PRIZE_TYPE, eventPrize.getPrizeType().getValue() + "");
		request.setParameterForUpdate(EventPrize.SET_IMG_SRC, eventPrize.getImgSrc());
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API修改奖项失败");
			throw new ApiRemoteCallFailedException("API修改奖项失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API修改奖项请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		
		return true;
	}
	
	/**
	 * 删除奖项
	 * @param eventId 活动编码
	 * @param prizeId 奖项编码
	 */
	public boolean delEventPrize(int eventId,int prizeId) throws ApiRemoteCallFailedException{
		logger.info("进入调用API删除奖项");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EVENT_PRIZE_DELETE);
		
		if (eventId == 0) {
			logger.error("删除的奖项活动编码为空");
			return false;
		}
		if (prizeId == 0) {
			logger.error("删除的奖项编码为空");
			return false;
		}
		
		request.setParameter(EventPrize.QUERY_PRIZE_ID, String.valueOf(prizeId));
		request.setParameter(EventPrize.QUERY_EVENT_ID, String.valueOf(eventId));
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API删除奖项失败");
			throw new ApiRemoteCallFailedException("API删除奖项失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除奖项请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		
		return true;
	}

	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}
}
