package web.service.impl.business;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.business.CacheManageService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.DcRace;
import com.lehecai.core.api.lottery.JclqRace;
import com.lehecai.core.api.lottery.JczqRace;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.api.lottery.PlanOrder;
import com.lehecai.core.api.lottery.SfggRace;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 缓存业务逻辑层实现类
 * @author yanweijie
 *
 */
public class CacheManageServiceImpl implements CacheManageService {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private ApiRequestService apiWriteRequestService;
	
	/**
	 * 删除钱包缓存
	 * @param uid 
	 */
	public boolean deleteWalletCache(Long uid)
									throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除用户钱包缓存");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WALLET_CACHE_DELETE);
		request.setParameter(Member.QUERY_UID, String.valueOf(uid));
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API删除用户钱包缓存失败");
			throw new ApiRemoteCallFailedException("API执行删除用户钱包缓存失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除用户钱包缓存请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 删除单场缓存
	 * @param id
	 */
	public boolean deleteDCCache(Long id) 
								throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除单场缓存");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_DC_CACHE_DELETE);
		request.setParameter(DcRace.QUERY_ID, id + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API删除单场缓存失败");
			throw new ApiRemoteCallFailedException("API删除单场缓存失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除单场缓存请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	/**
	 * 删除胜负过关缓存
	 * @param id
	 */
	public boolean deleteSFGGCache(Long id) 
	throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除胜负过关缓存");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_SFGG_CACHE_DELETE);
		request.setParameter(SfggRace.QUERY_ID, id + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API删除胜负过关缓存失败");
			throw new ApiRemoteCallFailedException("API删除胜负过关缓存失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除胜负过关缓存请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 删除竞彩篮球缓存
	 * @param matchNum
	 */
	public boolean deleteJCLQCache(Long matchNum) 
									throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除竞彩篮球缓存");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_JCLQ_DELETE);
		request.setParameter(JclqRace.QUERY_MATCH_NUM, matchNum + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API删除竞彩篮球缓存失败");
			throw new ApiRemoteCallFailedException("API删除竞彩篮球缓存失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除竞彩篮球缓存请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	/**
	 * 删除竞彩足球缓存
	 * @param matchNum
	 */
	public boolean deleteJCZQCache(Long matchNum) throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除竞彩足球缓存");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_JCZQ_DELETE);
		request.setParameter(JczqRace.QUERY_MATCH_NUM, matchNum + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API删除竞彩足球缓存失败");
			throw new ApiRemoteCallFailedException("API删除竞彩足球缓存失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除竞彩足球缓存请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 删除方案缓存
	 */
	public boolean deletePlanCache(Long planId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除方案缓存");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PLAN_CACHE_DELETE);
		request.setParameter(Plan.QUERY_ID, planId + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API删除竞彩方案缓存失败");
			throw new ApiRemoteCallFailedException("API删除竞彩方案缓存失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除竞彩方案缓存请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 删除订单缓存
	 */
	public boolean deleteOrderCache(Long orderId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除订单缓存");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_ORDER_CACHE_DELETE);
		request.setParameter(PlanOrder.QUERY_ID, orderId + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API删除订单缓存失败");
			throw new ApiRemoteCallFailedException("API删除订单缓存失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除订单缓存请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}	
	
	/**
	 * 删除计数器缓存
	 */
	public boolean deleteCounterCache(List<Long> uids) throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除用户计数器缓存");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_COUNTER_CACHE_DELETE);
		List<String> counterUid = new ArrayList<String>();
		
		for (Long uid : uids) {
			counterUid.add(uid.toString());
		}
		request.setParameterIn(Member.QUERY_UID, counterUid);
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API删除用户计数器缓存失败");
			throw new ApiRemoteCallFailedException("API执行删除用户计数器缓存失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除用户计数器缓存请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 删除用户登录失败计数缓存
	 */
	public boolean deleteUserLoginFailureCountCache() throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除用户登录失败计数缓存");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_USER_LOGIN_FAILURE_COUNT_CACHE_DELETE);
		
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API删除用户登录失败计数缓存失败");
			throw new ApiRemoteCallFailedException("API执行删除用户登录失败计数缓存失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除用户登录失败计数缓存请求异常, rc={}, message={}", response.getCode(), response.getMessage());
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