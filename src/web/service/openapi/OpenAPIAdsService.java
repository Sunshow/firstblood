package web.service.openapi;

import java.util.Date;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.openapi.OpenAPIAds;
import com.lehecai.core.api.openapi.OpenAPIAdsType;
import com.lehecai.core.api.openapi.OpenAPIAppType;
import com.lehecai.core.exception.ApiRemoteCallFailedException;


public interface OpenAPIAdsService {

	Map<String, Object> findOpenAPIAdsList(Long adsId, String adsTitle,
                                           YesNoStatus status, OpenAPIAppType openAPIAppType, OpenAPIAdsType openAPIAdsType, Date addTimeFrom, Date addTimeEnd, PageBean pageBean)
			throws ApiRemoteCallFailedException;

	boolean updateOpenAPIAds(OpenAPIAds openAPIAds) throws ApiRemoteCallFailedException;

	boolean addOpenAPIAds(OpenAPIAds openAPIAds) throws ApiRemoteCallFailedException;
	
}