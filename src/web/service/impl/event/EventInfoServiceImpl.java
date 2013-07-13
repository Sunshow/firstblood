package web.service.impl.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.event.EventInfoService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.event.EventInfo;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.util.CoreDateUtils;

/**
 * 抽奖活动信息管理业务逻辑层实现类，用于添加修改抽奖活动信息
 * @author yanweijie
 *
 */
public class EventInfoServiceImpl implements EventInfoService {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private ApiRequestService apiWriteRequestService;
	
	/**
	 * 添加抽奖活动信息
	 * @param eventInfo 抽奖活动信息
	 * @return
	 */
	@Override
	public boolean addEventInfo(EventInfo eventInfo) throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加抽奖活动");
		
		if (eventInfo == null) {
			logger.error("添加的抽奖活动信息为空");
			return false;
		}
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EVENT_INFO_ADD);
		
		request.setParameterForUpdate(EventInfo.SET_EVENT_NAME, eventInfo.getEventName());	//设置抽奖活动名称
		request.setParameterForUpdate(EventInfo.SET_EVENT_START_TIME, 
				CoreDateUtils.formatDate(eventInfo.getEventStartTime(),CoreDateUtils.DATETIME));//设置抽奖活动起始时间
		request.setParameterForUpdate(EventInfo.SET_EVENT_END_TIME, 
				CoreDateUtils.formatDate(eventInfo.getEventEndTime(),CoreDateUtils.DATETIME));	//设置抽奖活动结束时间
		request.setParameterForUpdate(EventInfo.SET_EVENT_DESCRIPTION, eventInfo.getEventDescription());//设置抽奖活动描述
		request.setParameterForUpdate(EventInfo.SET_EVENT_STATUS, String.valueOf(eventInfo.getStatus().getValue()));//设置活动状态
		request.setParameterForUpdate(EventInfo.SET_PRESET_HITS, String.valueOf(eventInfo.getPresetHits()));//设置预估参与人数
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API添加抽奖活动失败");
			throw new ApiRemoteCallFailedException("API添加抽奖活动失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API添加抽奖活动请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	
	/**
	 * 修改抽奖活动信息
	 * @param eventInfo 抽奖活动信息
	 * @return
	 */
	@SuppressWarnings("static-access")
	@Override
	public boolean updateEventInfo(EventInfo eventInfo) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改抽奖活动信息");
		
		if (eventInfo == null) {
			logger.error("修改的抽奖活动信息为空");
			return false;
		}
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EVENT_INFO_UPDATE);
		
		if (eventInfo.getEventId() == 0) {
			logger.error("修改的抽奖活动编号为空");
			return false;
		}
		
		request.setParameter(eventInfo.QUERY_EVENT_ID,String.valueOf(eventInfo.getEventId()));	//设置抽奖活动编号
		
		request.setParameterForUpdate(EventInfo.SET_EVENT_NAME, eventInfo.getEventName());	//设置抽奖活动名称
		request.setParameterForUpdate(EventInfo.SET_EVENT_START_TIME, 
				CoreDateUtils.formatDate(eventInfo.getEventStartTime(),CoreDateUtils.DATETIME));//设置抽奖活动起始时间
		request.setParameterForUpdate(EventInfo.SET_EVENT_END_TIME, 
				CoreDateUtils.formatDate(eventInfo.getEventEndTime(),CoreDateUtils.DATETIME));	//设置抽奖活动结束时间
		request.setParameterForUpdate(EventInfo.SET_EVENT_DESCRIPTION, eventInfo.getEventDescription());//设置抽奖活动描述
		request.setParameterForUpdate(EventInfo.SET_EVENT_STATUS, String.valueOf(eventInfo.getStatus().getValue()));//设置活动状态
		request.setParameterForUpdate(EventInfo.SET_PRESET_HITS, String.valueOf(eventInfo.getPresetHits()));//设置预估参与人数
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API修改抽奖活动信息失败");
			throw new ApiRemoteCallFailedException("API修改抽奖活动信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API修改抽奖活动信息请求异常, rc={}, message={}", response.getCode(), response.getMessage());
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
