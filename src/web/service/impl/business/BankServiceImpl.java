package web.service.impl.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.BankService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.BranchBank;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class BankServiceImpl implements BankService{

	private static final Logger logger = LoggerFactory.getLogger(BankServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	
	@Override
	public Map<String, Object> getBankInfoByBankId(Set<String> bankIdSet) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询银行信息");
		if (bankIdSet == null || bankIdSet.size() == 0) {
			logger.info("银行id为空");
			return null;
		} 
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_BANK_GET_BANKINFO);
		
		List<String> list = new ArrayList<String>(bankIdSet);
		request.setParameterIn(BranchBank.QUERY_BANK_ID, list);
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API查询银行信息失败");
			throw new ApiRemoteCallFailedException("API查询银行信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API查询银行信息请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return null;
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("调用API查询银行信息数据为空, message={}", response.getMessage());
			return null;
		}
		List<BranchBank> bankList = BranchBank.convertFromJSONArray(response.getData());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_LIST, bankList);
		logger.info("结束调用API查询银行信息");
		return map;
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}


}
