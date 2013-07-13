package web.service.impl.member;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.member.MemberCreditService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.MemberCredit;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class MemberCreditServiceImpl implements MemberCreditService {

	private final Logger logger = LoggerFactory.getLogger(CreditLogServiceImpl.class);
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public MemberCredit get(long uid) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员彩贝");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CREDIT_QUERY);
		request.setParameter(MemberCredit.QUERY_UID, String.valueOf(uid));
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取用户彩贝失败");
			throw new ApiRemoteCallFailedException("API获取用户彩贝失败");
		}
		if(response.getCode() != ApiConstant.RC_SUCCESS){
			logger.error("调用API获取用户彩贝数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取用户彩贝数据请求出错");
		}
		if (response.getData() == null || response.getData().size() == 0) {
			logger.warn("API获取用户彩贝数据为空");
			return null;
		}
		List<MemberCredit> memberCredits = MemberCredit.convertFromJSONArray(response.getData());
		return memberCredits.get(0);
	}

	@Override
	public boolean add(long uid, long amount, String remark)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API赠送会员积分");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CREDIT_ADD);
		
		request.setParameterForUpdate(MemberCredit.SET_CREDITS, amount + "");
		request.setParameterForUpdate(MemberCredit.SET_REMARK, remark + "");
		request.setParameter(MemberCredit.QUERY_UID, uid + "");
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用API赠送会员积分失败");
			throw new ApiRemoteCallFailedException("调用API赠送会员积分失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API赠送会员积分请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean deduct(long uid, long amount, String remark)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API扣除会员积分");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CREDIT_DEDUCT);
		
		request.setParameterForUpdate(MemberCredit.SET_CREDITS, amount + "");
		request.setParameterForUpdate(MemberCredit.SET_REMARK, remark + "");
		request.setParameter(MemberCredit.QUERY_UID, uid + "");
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用API扣除会员积分失败");
			throw new ApiRemoteCallFailedException("调用API扣除会员积分失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API扣除会员积分请求异常, rc={}, message={}", response.getCode(), response.getMessage());
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
