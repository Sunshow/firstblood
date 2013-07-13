package web.service.impl.business;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.business.ZcRecalcService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 足彩过关统计重算业务层实现类
 * @author yanweijie
 *
 */
public class ZcRecalcServiceImpl implements ZcRecalcService {
	private Logger logger = LoggerFactory.getLogger(ZcRecalcServiceImpl.class);
	
	private static final String QUERY_LOTTERYTYPE = "lottery_type";	//彩种
	private static final String QUERY_PHASE = "phase";				//彩期
	private static final String QUERY_PLANID = "plan_id";			//方案编号
	private static final String QUERY_EXPIRE = "expire";			//停用时间，单位为秒
	private static final String QUERY_ITEM = "item";			//足彩和单场为彩期，竞彩足球为场次编号 
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	/**
	 * 按彩期重算足彩过关统计
	 * @param lotteryType 彩种
	 * @param phase 彩期
	 */
	public boolean reCalcByPhase(Integer lotteryType, String phase) throws ApiRemoteCallFailedException{
		logger.info("进入调用API按彩期重算足彩过关统计");
		
		ApiRequest apiRequest = new ApiRequest();
		apiRequest.setUrl(ApiConstant.API_URL_ZC_RECALC_BY_PHASE);
		
		if (lotteryType != null && lotteryType != 0) {
			apiRequest.setParameter(QUERY_LOTTERYTYPE, lotteryType + "");					//设置彩种
		} else {
			logger.error("彩种为空");
		}
		if (phase != null && !phase.equals("")) {
			apiRequest.setParameter(QUERY_PHASE, phase);									//设置彩期
		} else {
			logger.error("彩期为空");
		}
		
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(apiRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API按彩期重算足彩过关统计异常，{}", e.getMessage());
			throw new ApiRemoteCallFailedException("API按彩期重算足彩过关统计异常");
		}
		if (response == null) {
			logger.error("API按彩期重算足彩过关统计失败");
			throw new ApiRemoteCallFailedException("API按彩期重算足彩过关统计失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API按彩期重算足彩过关统计失败");
			return false;
		}
		logger.info("API按彩期重算足彩过关统计成功");
		return true;
	}
	
	/**
	 * 按方案编号重算足彩过关统计
	 */
	public boolean reCalcByPlanId(Integer lotteryType, String planId) throws ApiRemoteCallFailedException{
		logger.info("进入调用API按方案编号重算足彩过关统计");
		
		ApiRequest apiRequest = new ApiRequest();
		apiRequest.setUrl(ApiConstant.API_URL_ZC_RECALC_BY_PLANID);
		
		if (lotteryType != null && lotteryType != 0) {
			apiRequest.setParameter(QUERY_LOTTERYTYPE, lotteryType + "");			//设置彩种
		} else {
			logger.error("彩种为空");
		}
		if (planId != null && !planId.equals("")) {
			apiRequest.setParameter(QUERY_PLANID, planId);									//设置方案编号
		} else {
			logger.error("方案编号为空");
		}
		
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(apiRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API按方案编号重算足彩过关统计异常，{}", e.getMessage());
			throw new ApiRemoteCallFailedException("API按方案编号重算足彩过关统计异常");
		}
		if (response == null) {
			logger.error("API按方案编号重算足彩过关统计失败");
			throw new ApiRemoteCallFailedException("API按方案编号重算足彩过关统计失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API按方案编号重算足彩过关统计失败");
			return false;
		}
		logger.info("API按方案编号重算足彩过关统计成功");
		return true;
	}
	/**
	 * 按彩期同步足彩过关统计
	 * @param lotteryType 彩种
	 * @param phase 彩期
	 */
	public boolean syncByPhase(Integer lotteryType, String phase) throws ApiRemoteCallFailedException{
		logger.info("进入调用API按彩期同步足彩过关统计");
		
		ApiRequest apiRequest = new ApiRequest();
		apiRequest.setUrl(ApiConstant.API_URL_ZC_SYNC_BY_PHASE);
		
		if (lotteryType != null && lotteryType != 0) {
			apiRequest.setParameter(QUERY_LOTTERYTYPE, lotteryType + "");					//设置彩种
		} else {
			logger.error("彩种为空");
		}
		if (phase != null && !phase.equals("")) {
			apiRequest.setParameter(QUERY_PHASE, phase);									//设置彩期
		} else {
			logger.error("彩期为空");
		}
		
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(apiRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API按彩期同步足彩过关统计异常，{}", e.getMessage());
			throw new ApiRemoteCallFailedException("API按彩期同步足彩过关统计异常");
		}
		if (response == null) {
			logger.error("API按彩期同步足彩过关统计失败");
			throw new ApiRemoteCallFailedException("API按彩期同步足彩过关统计失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API按彩期同步足彩过关统计失败");
			return false;
		}
		logger.info("API按彩期同步足彩过关统计成功");
		return true;
	}
	/**
	 * 按彩期结束足彩过关统计
	 * @param lotteryType 彩种
	 * @param phase 彩期
	 */
	public boolean terminateByPhase(Integer lotteryType, String phase) throws ApiRemoteCallFailedException{
		logger.info("进入调用API按彩期结束足彩过关统计");
		
		ApiRequest apiRequest = new ApiRequest();
		apiRequest.setUrl(ApiConstant.API_URL_ZC_TERMINATE_BY_PHASE);
		
		if (lotteryType != null && lotteryType != 0) {
			apiRequest.setParameter(QUERY_LOTTERYTYPE, lotteryType + "");					//设置彩种
		} else {
			logger.error("彩种为空");
		}
		if (phase != null && !phase.equals("")) {
			apiRequest.setParameter(QUERY_PHASE, phase);									//设置彩期
		} else {
			logger.error("彩期为空");
		}
		
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(apiRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API按彩期结束足彩过关统计异常，{}", e.getMessage());
			throw new ApiRemoteCallFailedException("API按彩期结束足彩过关统计异常");
		}
		if (response == null) {
			logger.error("API按彩期结束足彩过关统计失败");
			throw new ApiRemoteCallFailedException("API按彩期结束足彩过关统计失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API按彩期结束足彩过关统计失败");
			return false;
		}
		logger.info("API按彩期结束足彩过关统计成功");
		return true;
	}
	
	/**
	 * 按方案编号同步足彩过关统计
	 */
	public boolean syncByPlanId(Integer lotteryType, String planId) throws ApiRemoteCallFailedException{
		logger.info("进入调用API按方案编号同步足彩过关统计");
		
		ApiRequest apiRequest = new ApiRequest();
		apiRequest.setUrl(ApiConstant.API_URL_ZC_SYNC_BY_PLANID);
		
		if (lotteryType != null && lotteryType != 0) {
			apiRequest.setParameter(QUERY_LOTTERYTYPE, lotteryType + "");			//设置彩种
		} else {
			logger.error("彩种为空");
		}
		if (planId != null && !planId.equals("")) {
			apiRequest.setParameter(QUERY_PLANID, planId);									//设置方案编号
		} else {
			logger.error("方案编号为空");
		}
		
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(apiRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API按方案编号同步足彩过关统计异常，{}", e.getMessage());
			throw new ApiRemoteCallFailedException("API按方案编号同步足彩过关统计异常");
		}
		if (response == null) {
			logger.error("API按方案编号同步足彩过关统计失败");
			throw new ApiRemoteCallFailedException("API按方案编号同步足彩过关统计失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API按方案编号同步足彩过关统计失败");
			return false;
		}
		logger.info("API按方案编号同步足彩过关统计成功");
		return true;
	}
	@Override
	public Integer getUpdateStatus() throws ApiRemoteCallFailedException {
		logger.info("进入调用API足彩过关统计获取结果更新状态");
		
		ApiRequest apiRequest = new ApiRequest();
		apiRequest.setUrl(ApiConstant.API_URL_ZC_RECALC_GET_STATUS);
		
		ApiResponse response = null;
		try {
			response = apiRequestService.request(apiRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API足彩过关统计获取结果更新状态异常，{}", e.getMessage());
			throw new ApiRemoteCallFailedException("API足彩过关统计获取结果更新状态异常");
		}
		if (response == null) {
			logger.error("API足彩过关统计获取结果更新状态失败");
			throw new ApiRemoteCallFailedException("API足彩过关统计获取结果更新状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API足彩过关统计获取结果更新状态失败");
			return null;
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.error("API足彩过关统计获取结果更新状态失败");
			return null;
		}
		
		logger.info("API足彩过关统计获取结果更新状态成功");
		return response.getData().getInt(0);
	}

	@Override
	public boolean pauseResultUpdate(long expire)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API足彩过关统计暂停结果更新 ");
		
		ApiRequest apiRequest = new ApiRequest();
		apiRequest.setUrl(ApiConstant.API_URL_ZC_RECALC_PAUSE);
		
		if (expire != 0) {
			apiRequest.setParameter(QUERY_EXPIRE, expire + "");
		}
		
		ApiResponse response = null;
		try {
			response = apiRequestService.request(apiRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API足彩过关统计暂停结果更新 状态异常，{}", e.getMessage());
			throw new ApiRemoteCallFailedException("API足彩过关统计暂停结果更新 状态异常");
		}
		if (response == null) {
			logger.error("API足彩过关统计暂停结果更新 状态失败");
			throw new ApiRemoteCallFailedException("API足彩过关统计暂停结果更新 状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API足彩过关统计暂停结果更新 状态失败");
			return false;
		}
		logger.info("API足彩过关统计暂停结果更新 状态成功");
		return true;
	}

	@Override
	public boolean removeResult(Integer lotteryType, List<String> items)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API足彩过关统计赛事结果删除  ");
		
		ApiRequest apiRequest = new ApiRequest();
		apiRequest.setUrl(ApiConstant.API_URL_ZC_RECALC_REMOVE);
		
		if (lotteryType == null || lotteryType == 0) {
			logger.error("API足彩过关统计赛事结果删除lotteryType不能为空");
			return false;
		}
		if (items == null || items.size() == 0) {
			logger.error("API足彩过关统计赛事结果删除items不能为空");
			return false;
		}
		apiRequest.setParameter(QUERY_LOTTERYTYPE, lotteryType + "");			//设置彩种
		apiRequest.setParameterIn(QUERY_ITEM, items);
		
		ApiResponse response = null;
		try {
			response = apiRequestService.request(apiRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API足彩过关统计赛事结果删除  状态异常，{}", e.getMessage());
			throw new ApiRemoteCallFailedException("API足彩过关统计赛事结果删除  状态异常");
		}
		if (response == null) {
			logger.error("API足彩过关统计赛事结果删除  状态失败");
			throw new ApiRemoteCallFailedException("API足彩过关统计赛事结果删除  状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API足彩过关统计赛事结果删除  状态失败");
			return false;
		}
		logger.info("API足彩过关统计赛事结果删除  状态成功");
		return true;
	}

	@Override
	public boolean resumeResultUpdate() throws ApiRemoteCallFailedException {
		logger.info("进入调用API足彩过关统计恢复结果更新 ");
		
		ApiRequest apiRequest = new ApiRequest();
		apiRequest.setUrl(ApiConstant.API_URL_ZC_RECALC_RESUME);
		
		ApiResponse response = null;
		try {
			response = apiRequestService.request(apiRequest, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API足彩过关统计恢复结果更新 状态异常，{}", e.getMessage());
			throw new ApiRemoteCallFailedException("API足彩过关统计恢复结果更新 状态异常");
		}
		if (response == null) {
			logger.error("API足彩过关统计恢复结果更新 状态失败");
			throw new ApiRemoteCallFailedException("API足彩过关统计恢复结果更新 状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API足彩过关统计恢复结果更新 状态失败");
			return false;
		}
		logger.info("API足彩过关统计恢复结果更新 状态成功");
		return true;
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
