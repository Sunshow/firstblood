package web.service.impl.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.business.ResetPlanDrawStatusService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class ResetPlanDrawStatusServiceImpl implements ResetPlanDrawStatusService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ApiRequestService apiWriteRequestService;
	
	@SuppressWarnings("unchecked")
	@Override
	public void reset(List<String> ids, List<String> successList, List<String> failList)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API重置开奖方案状态");
		if (successList == null) {
			successList = new ArrayList<String>();
		}
		if (failList == null) {
			failList = new ArrayList<String>();
		}
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_RESET_RESULT);
		if (ids != null && ids.size() != 0) {
			request.setParameterIn(Plan.QUERY_ID, ids);
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API重置开奖方案状态失败");
			throw new ApiRemoteCallFailedException("API重置开奖方案状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API重置开奖方案状态请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API重置开奖方案状态请求出错," + response.getMessage());
		}
		if (response.getData() == null || response.getData().equals("")) {
			logger.error("调用API重置开奖方案状态请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API重置开奖方案状态请求出错," + response.getMessage());
		}
		
		JSONObject object = (JSONObject) response.getData().get(0);
		
		JSONArray arraySuccess = object.getJSONArray("success");
		if (arraySuccess != null && arraySuccess.size() > 0) {
			for (Iterator iterSuccess = arraySuccess.iterator(); iterSuccess.hasNext();) {
				String successId = (String) iterSuccess.next();
				successList.add(successId);
			}
		}
		JSONArray arrayFailed = object.getJSONArray("fail");
		if (arrayFailed != null && arrayFailed.size() > 0) {
			for (Iterator iterFailed = arrayFailed.iterator(); iterFailed.hasNext();) {
				String failedId = (String) iterFailed.next();
				failList.add(failedId);
			}				
		}
		
	}


	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}

}
