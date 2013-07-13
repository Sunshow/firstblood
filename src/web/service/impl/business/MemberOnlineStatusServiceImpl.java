package web.service.impl.business;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.business.MemberOnlineStatusService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.MemberOnlineStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 会员在线状态业务逻辑层实现类
 * @author yanweijie
 *
 */
public class MemberOnlineStatusServiceImpl implements MemberOnlineStatusService {
	private Logger logger = LoggerFactory.getLogger(MemberOnlineStatusServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	/**
	 * 根据会员编号查询会员在线状态
	 * @param uid 会员编号
	 * @throws ApiRemoteCallFailedException 
	 */
	public List<MemberOnlineStatus> findOnlineStatusByUid(long uid) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员在线状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_ONLINE_STATUS_SEARCH);
		request.setParameter(MemberOnlineStatus.QUERY_UID, uid + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员在线状态数据失败");
			throw new ApiRemoteCallFailedException("API获取会员在线状态数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员在线状态数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员在线状态数据请求出错");
		}
		if (response.getData() == null) {
			logger.error("API获取会员在线状态数据为空");
		}
		return MemberOnlineStatus.convertFromJSONArray(response.getData());
	}
	
	/**
	 * 根据会员编号删除会员所有登陆点
	 * @param uid 会员编号
	 */
	public boolean deleteAllLogin(long uid) throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除会员所有登陆点");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_ALL_LOGIN_OFFLINE);
		
		request.setParameter(MemberOnlineStatus.QUERY_UID, uid + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API删除会员所有登陆点数据失败");
			throw new ApiRemoteCallFailedException("API删除会员会员所有登陆点数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除会员所有登陆点数据请求异常!rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 删除会员某一登陆点
	 * @param key String
	 * @param type int 
	 */
	public boolean deleteLoing(String key,int type) throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除会员登陆点");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_LOGIN_OFFLINE);
		
		request.setParameter(MemberOnlineStatus.QUERY_KEY, key);
		request.setParameter(MemberOnlineStatus.QUERY_TYPE, type + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API删除会员登陆点数据失败");
			throw new ApiRemoteCallFailedException("API删除会员登陆点数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除会员登陆点数据请求异常!rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}
}
