package web.service.impl.openapi;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.openapi.OpenAPIAdsService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.openapi.OpenAPIAds;
import com.lehecai.core.api.openapi.OpenAPIAdsType;
import com.lehecai.core.api.openapi.OpenAPIAppType;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.util.CoreDateUtils;

public class OpenAPIAdsServiceImpl implements OpenAPIAdsService {

	private Logger logger = LoggerFactory.getLogger(OpenAPIAdsServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public Map<String, Object> findOpenAPIAdsList(Long adsId, String adsTitle,
			YesNoStatus status, OpenAPIAppType openAPIAppType, OpenAPIAdsType openAPIAdsType, Date addTimeFrom, Date addTimeEnd, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入API查询openAPI广告");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_ADS_SEARCH);
		
		if (adsId != null && adsId != 0L) {
			request.setParameter(OpenAPIAds.QUERY_ADS_ID, adsId + "");
		}
		if (adsTitle != null && !adsTitle.equals("")) {						//应用名称
			request.setParameterLike(OpenAPIAds.QUERY_ADS_TITLE, adsTitle);
		}
		if (status != null && status.getValue() != YesNoStatus.ALL.getValue()) {	//应用类型
			request.setParameter(OpenAPIAds.QUERY_STATUS, status.getValue() + "");
		}
		if (openAPIAppType != null && openAPIAppType.getValue() != OpenAPIAppType.ALL.getValue()) {	//平台类型
			request.setParameter(OpenAPIAds.QUERY_APP_TYPE, openAPIAppType.getValue() + "");
		}
		if (openAPIAdsType != null && openAPIAdsType.getValue() != OpenAPIAdsType.ALL.getValue()) {	//类型
			request.setParameter(OpenAPIAds.QUERY_TYPE, openAPIAdsType.getValue() + "");
		}
		if (addTimeFrom != null) {	
			request.setParameterBetween(OpenAPIAds.QUERY_TIME_ADD, CoreDateUtils.formatDate(addTimeFrom, CoreDateUtils.DATETIME), null);
		}
		if (addTimeEnd != null) {	
			request.setParameterBetween(OpenAPIAds.QUERY_TIME_ADD, null, CoreDateUtils.formatDate(addTimeEnd, CoreDateUtils.DATETIME));
		}
		request.addOrder(OpenAPIAds.ORDER_ADS_TIME_ADD, ApiConstant.API_REQUEST_ORDER_DESC);
		
		if (pageBean != null && pageBean.isPageFlag()) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API查询openAPI广告失败");
			throw new ApiRemoteCallFailedException("API查询openAPI广告失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API查询openAPI广告请求异常");
			throw new ApiRemoteCallFailedException("API查询openAPI广告请求异常");
		}
		if (response.getData() == null) {
			logger.error("API查询openAPI广告响应数据为空");
			return null;
		}		
		List<OpenAPIAds> list = OpenAPIAds.convertFromJSONArray(response.getData());
		
		if (pageBean != null) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;// 页数
			
			if (pageBean.getPageSize() != 0) {
				pageCount = totalCount / pageBean.getPageSize();
		
				if (totalCount % pageBean.getPageSize() != 0) {
					pageCount++;
				}
			}
			pageBean.setPageCount(pageCount);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		return map;
	}

	@Override
	public boolean updateOpenAPIAds(OpenAPIAds openAPIAds) throws ApiRemoteCallFailedException {
		logger.info("进入API编辑openAPI广告");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_ADS_UPDATE);
		request.setParameter(OpenAPIAds.QUERY_ADS_ID, openAPIAds.getAdsId() + "");		
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_TITLE, openAPIAds.getAdsTitle());			//广告标题
		
		if (openAPIAds.getAppType() != null && openAPIAds.getAppType().getValue() != OpenAPIAppType.ALL.getValue()) {
			request.setParameterForUpdate(OpenAPIAds.SET_APP_TYPE, openAPIAds.getAppType().getValue() + "");			//平台类型
		}
		request.setParameterForUpdate(OpenAPIAds.SET_APP_VERSION, openAPIAds.getAppVersion() + "");		//版本
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_DESC, openAPIAds.getAdsDesc());			//广告描述
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_CONTENT, openAPIAds.getAdsContent());		//广告内容			
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_IMG1_URL, openAPIAds.getImg1Url());		//广告图片1
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_IMG2_URL, openAPIAds.getImg2Url());		//广告图片2
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_IMG_DISABLE_DESC, openAPIAds.getImgDisableDesc());		//图片描述
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_STATUS, openAPIAds.getStatus().getValue() + "");		//状态
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_NEWS_ID, openAPIAds.getNewsId() + "");		//对应新闻活动ID
		request.setParameterForUpdate(OpenAPIAds.SET_TYPE, openAPIAds.getType().getValue() + "");		//类型
		request.setParameterForUpdate(OpenAPIAds.SET_TOP, openAPIAds.getTop() + "");		//排序值
		request.setParameterForUpdate(OpenAPIAds.SET_ENABLED_TIME, CoreDateUtils.formatDate(openAPIAds.getEnabledTime(), CoreDateUtils.DATETIME));		//启用时间
		request.setParameterForUpdate(OpenAPIAds.SET_SOURCE, openAPIAds.getSource());		//渠道
		request.setParameterForUpdate(OpenAPIAds.SET_NEWS_URL, openAPIAds.getNewsUrl());//广告所对应活动的链接
		
		logger.info("Request Query String: {}", request.toQueryString());		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API编辑openAPI广告失败");
			throw new ApiRemoteCallFailedException("API编辑openAPI广告失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API编辑openAPI广告请求异常");
			return false;
		}
		return true;
	}

	@Override
	public boolean addOpenAPIAds(OpenAPIAds openAPIAds) throws ApiRemoteCallFailedException {
		
		logger.info("进入API添加openAPI广告");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_ADS_ADD);
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_TITLE, openAPIAds.getAdsTitle());			//广告标题
		
		if (openAPIAds.getAppType() != null && openAPIAds.getAppType().getValue() != OpenAPIAppType.ALL.getValue()) {
			request.setParameterForUpdate(OpenAPIAds.SET_APP_TYPE, openAPIAds.getAppType().getValue() + "");			//平台类型
		}
		request.setParameterForUpdate(OpenAPIAds.SET_APP_VERSION, openAPIAds.getAppVersion() + "");		//版本
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_DESC, openAPIAds.getAdsDesc());			//广告描述
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_CONTENT, openAPIAds.getAdsContent());		//广告内容			
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_IMG1_URL, openAPIAds.getImg1Url());		//广告图片1
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_IMG2_URL, openAPIAds.getImg2Url());		//广告图片2
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_IMG_DISABLE_DESC, openAPIAds.getImgDisableDesc());		//图片描述
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_STATUS, openAPIAds.getStatus().getValue() + "");		//状态
		request.setParameterForUpdate(OpenAPIAds.SET_ADS_NEWS_ID, openAPIAds.getNewsId() + "");		//对应新闻活动ID
		request.setParameterForUpdate(OpenAPIAds.SET_TYPE, openAPIAds.getType().getValue() + "");		//类型
		request.setParameterForUpdate(OpenAPIAds.SET_TOP, openAPIAds.getTop() + "");		//排序值
		request.setParameterForUpdate(OpenAPIAds.SET_ENABLED_TIME, CoreDateUtils.formatDate(openAPIAds.getEnabledTime(), CoreDateUtils.DATETIME));		//启用时间
		request.setParameterForUpdate(OpenAPIAds.SET_SOURCE, openAPIAds.getSource());		//渠道
		request.setParameterForUpdate(OpenAPIAds.SET_NEWS_URL, openAPIAds.getNewsUrl());	//广告所对应活动的链接
		
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API添加openAPI广告失败");
			throw new ApiRemoteCallFailedException("API添加openAPI广告失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API添加openAPI广告请求异常");
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