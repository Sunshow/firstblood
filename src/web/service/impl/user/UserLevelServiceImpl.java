package web.service.impl.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.user.UserLevelService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.MemberLevel;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class UserLevelServiceImpl implements UserLevelService {
	private final Logger logger = LoggerFactory.getLogger(UserLevelServiceImpl.class);
	
	
	private ApiRequestService apiRequestService;
	@Override
	public Map<String, Object> getUsersLevel(List<String> uids)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员奖牌战记数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_LEVEL_SEARCH);
		request.setParameterIn(MemberLevel.QUERY_UID, uids);

		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取奖牌战记数据失败");
			throw new ApiRemoteCallFailedException("API获取奖牌战记数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取奖牌战记数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取奖牌战记数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取奖牌战记数据为空, message={}", response.getMessage());
			return null;
		}
		List<MemberLevel> list = MemberLevel.convertFromJSONObjectMap(response.getData().getJSONObject(0));
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_LIST, list);
		return map;
	}
	
	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}
	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}
}
