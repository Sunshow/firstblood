package web.service.impl.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.business.GeneralLotteryRecalcService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.StatisticApiConstant;
import com.lehecai.core.api.DirectApiRequest;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 普通彩种过关统计重算业务层实现类
 * @author jinsheng
 *
 */
public class GeneralLotteryRecalcServiceImpl implements GeneralLotteryRecalcService {
	private Logger logger = LoggerFactory.getLogger(GeneralLotteryRecalcServiceImpl.class);
	
	private static final String QUERY_LOTTERYTYPE = "lottery_type";	//彩种
	private static final String QUERY_PHASE = "phase";				//彩期 
	private static final String QUERY_EVENTTYPE = "event_type";		//事件	

	private ApiRequestService statisticApiRequestService;
	
	/**
	 * 按彩期重算普通彩种过关统计
	 * @param lotteryType 彩种
	 * @param phase 彩期
	 * @param eventType 事件
	 */
	public boolean recalcByGeneralLotteryPhase(Integer lotteryType, String phase, Integer eventType) throws ApiRemoteCallFailedException{
		logger.info("进入调用API普通彩种按彩期重算足彩过关统计");
		
		DirectApiRequest request = new DirectApiRequest();
		request.setUrl(StatisticApiConstant.API_URL_ORDINARY_RECALC_BY_PHASE);
		
		if(lotteryType != null && lotteryType != 0) {
			request.setParameter(QUERY_LOTTERYTYPE, lotteryType + "");
		} else {
			logger.error("普通彩种过关统计彩种为空");
			throw new ApiRemoteCallFailedException("普通彩种过关统计彩种为空");
		}
		if(phase != null && !phase.equals("")) {
			request.setParameter(QUERY_PHASE, phase);
		} else {
			logger.error("普通彩种过关统计彩期为空");
			throw new ApiRemoteCallFailedException("普通彩种过关统计彩期为空");
		}
		if(eventType != null && eventType != 0) {
			request.setParameter(QUERY_EVENTTYPE, eventType + "");
		} else {
			logger.error("普通彩种过关统计事件为空");
			throw new ApiRemoteCallFailedException("普通彩种过关统计事件为空");
		}
		
		ApiResponse response = null;
		try {
			response = statisticApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("普通彩种API按彩期重算足彩过关统计异常，{}", e.getMessage());
			throw new ApiRemoteCallFailedException("普通彩种API按彩期重算足彩过关统计异常");
		}
		if (response == null) {
			logger.error("普通彩种API按彩期重算足彩过关统计失败");
			throw new ApiRemoteCallFailedException("普通彩种API按彩期重算足彩过关统计失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("普通彩种API按彩期重算足彩过关统计失败");
			return false;
		}
		logger.info("普通彩种API按彩期重算足彩过关统计成功");
		return true;
	}

	public ApiRequestService getStatisticApiRequestService() {
		return statisticApiRequestService;
	}

	public void setStatisticApiRequestService(ApiRequestService statisticApiRequestService) {
		this.statisticApiRequestService = statisticApiRequestService;
	}
}
