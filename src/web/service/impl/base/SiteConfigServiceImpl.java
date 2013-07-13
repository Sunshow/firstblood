/**
 * 
 */
package web.service.impl.base;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.base.SiteConfigService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.base.SiteConfig;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author Sunshow
 *
 */
public class SiteConfigServiceImpl implements SiteConfigService {
	
	protected transient final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private ApiRequestService apiRequestService;

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.base.SiteConfigService#getSiteConfig()
	 */
	@Override
	public SiteConfig getSiteConfig() throws ApiRemoteCallFailedException {
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_BASE_SITE_CONFIG);
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API读取站点配置失败");
			throw new ApiRemoteCallFailedException("API读取站点配置失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API读取站点配置请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return null;
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("调用API读取站点配置为空, message={}", response.getMessage());
			return null;
		}
		List<SiteConfig> result = SiteConfig.convertFromJSONArray(response.getData());
		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

}
