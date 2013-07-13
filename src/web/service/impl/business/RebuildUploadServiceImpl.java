package web.service.impl.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.business.RebuildUploadService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class RebuildUploadServiceImpl implements RebuildUploadService {

	private final String PLAN_ID = "plan_id";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public String rebuildUpload(Long id)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API重建上传文件缓存");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_REBUILD_UPLOAD);
		if (id == null || id == 0) {
			logger.error("文件方案号为空");
			return null;
		} else {
			request.setParameter(PLAN_ID, id + "");
		}
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API重建上传文件缓存失败");
			throw new ApiRemoteCallFailedException("API重建上传文件缓存失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API重建上传文件缓存表请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API重建上传文件缓存请求出错");
		}
		
		return response.getData().getString(0);
	}

	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}

}
