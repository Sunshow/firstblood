package web.service.impl.openapi;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.openapi.OpenAPIAppRecommendService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.openapi.OpenAPIAppRecommend;
import com.lehecai.core.api.openapi.OpenAPIAppRecommendDownload;
import com.lehecai.core.api.openapi.OpenAPIAppRecommendType;
import com.lehecai.core.api.openapi.OpenAPIAppType;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.util.CoreDateUtils;

/**
 * 2013-05-09
 * @author He Wang
 *
 */
public class OpenAPIAppRecommendServiceImpl implements OpenAPIAppRecommendService {
	private Logger logger = LoggerFactory.getLogger(OpenAPIAppRecommendServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	/**
	 * 分页并多条件查询应用
	 */
	public Map<String, Object> findOpenAPIAppRecommendList(YesNoStatus appStatus,YesNoStatus appIsOpen, Long appId, OpenAPIAppRecommendType appTypeForRecommend,String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入API查询openAPI应用");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_APP_RECOMMEND_SEARCH);
		
		if (appTypeForRecommend != null && appTypeForRecommend.getValue() != OpenAPIAppType.ALL.getValue()) {	//应用类型
			request.setParameter(OpenAPIAppRecommend.QUERY_APP_TYPE_RECOMMEND, appTypeForRecommend.getValue() + "");
		}
		if (appStatus != null && appStatus.getValue() != YesNoStatus.ALL.getValue()) {	//应用状态
			request.setParameter(OpenAPIAppRecommend.QUERY_APP_STATUS, appStatus.getValue() + "");
		}
		if (appIsOpen != null && appIsOpen.getValue() != YesNoStatus.ALL.getValue()) {	//应用状态
			request.setParameter(OpenAPIAppRecommend.QUERY_APP_IS_OPEN, appIsOpen.getValue() + "");
		}
		if (appId != null && appId != 0L) {													//应用编码
			request.setParameter(OpenAPIAppRecommend.QUERY_APP_ID, appId + "");
		}
		if (orderStr != null && !orderStr.equals("")) {
			request.addOrder(orderStr, orderView);
		}
		if (pageBean != null && pageBean.isPageFlag()) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API查询openAPI应用失败");
			throw new ApiRemoteCallFailedException("API查询openAPI应用失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API查询openAPI应用请求异常");
			throw new ApiRemoteCallFailedException("API查询openAPI应用请求异常");
		}
		if (response.getData() == null) {
			logger.error("API查询openAPI应用响应数据为空");
			return null;
		}
		
		List<OpenAPIAppRecommend> list = OpenAPIAppRecommend.convertFromJSONArray(response.getData());
		
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
	
	/**
	 * 添加应用
	 */
	@Override
	public boolean addOpenAPIAppRecommend(OpenAPIAppRecommend openAPIAppRecommend) throws ApiRemoteCallFailedException{
		logger.info("进入API添加openAPI应用");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_APP_RECOMMEND_ADD);
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_NAME, openAPIAppRecommend.getAppName());
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_DOWNLOAD_URL, openAPIAppRecommend.getAppDownUrl());
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_IS_FREE, openAPIAppRecommend.getAppIsFree().getValue() + "");//下载地址			
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_DESCRIBE, openAPIAppRecommend.getAppDescribe());		//应用描述
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_VERSION, openAPIAppRecommend.getAppVersion());		//应用版本
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_STATUS, openAPIAppRecommend.getAppStatus().getValue() + "");
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_IS_OPEN, openAPIAppRecommend.getAppIsOpen().getValue() + "");
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_PRICE, openAPIAppRecommend.getAppPrice() + "");
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_TYPE_RECOMMEND, openAPIAppRecommend.getAppType().getValue() + "");
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_ICON, openAPIAppRecommend.getAppIcon());
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_SIZE, openAPIAppRecommend.getAppSize());
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_SORT, openAPIAppRecommend.getAppSort() + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API添加openAPI应用失败");
			throw new ApiRemoteCallFailedException("API添加openAPI应用失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API添加openAPI应用请求异常");
			return false;
		}
		
		return true;
	}
	
	/**
	 * 编辑应用
	 */
	@Override
	public boolean updateOpenAPIAppRecommend(OpenAPIAppRecommend openAPIAppRecommend) throws ApiRemoteCallFailedException {
		logger.info("进入API编辑openAPI应用推荐");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_APP_RECOMMEND_UPDATE);
		request.setParameterForUpdate(OpenAPIAppRecommend.QUERY_APP_ID, openAPIAppRecommend.getAppId() + "");
		request.setParameterForUpdate(OpenAPIAppRecommend.QUERY_SIGN, OpenAPIAppRecommend.QUERY_SIGN_UPDATE);//更新标识
		
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_DOWNLOAD_URL, openAPIAppRecommend.getAppDownUrl());
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_IS_FREE, openAPIAppRecommend.getAppIsFree().getValue() + "");//下载地址			
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_DESCRIBE, openAPIAppRecommend.getAppDescribe());		//应用描述
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_VERSION, openAPIAppRecommend.getAppVersion());		//应用版本
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_STATUS, YesNoStatus.YES.getValue() + "");
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_IS_OPEN, openAPIAppRecommend.getAppIsOpen().getValue() + "");
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_NAME, openAPIAppRecommend.getAppName() + "");
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_PRICE, openAPIAppRecommend.getAppPrice() + "");
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_TYPE_RECOMMEND, openAPIAppRecommend.getAppType().getValue() + "");
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_SORT, openAPIAppRecommend.getAppSort() + "");
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_ICON, openAPIAppRecommend.getAppIcon());
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_SIZE, openAPIAppRecommend.getAppSize());
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API编辑openAPI应用推荐失败");
			throw new ApiRemoteCallFailedException("API编辑openAPI应用推荐失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API编辑openAPI应用推荐请求异常");
			return false;
		}
		
		return true;
	}

	@Override
	public boolean auditOpenAPIAppRecommend(OpenAPIAppRecommend openAPIAppRecommend) throws ApiRemoteCallFailedException {
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_APP_RECOMMEND_UPDATE);
		request.setParameterForUpdate(OpenAPIAppRecommend.QUERY_APP_ID, openAPIAppRecommend.getAppId() + "");
		request.setParameterForUpdate(OpenAPIAppRecommend.QUERY_SIGN, OpenAPIAppRecommend.QUERY_SIGN_STATUS);//更新标识
		request.setParameterForUpdate(OpenAPIAppRecommend.SET_APP_STATUS, openAPIAppRecommend.getAppStatus().getValue() + "");
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API更改openAPI应用推荐状态失败");
			throw new ApiRemoteCallFailedException("API更改openAPI应用推荐状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更改openAPI应用推荐状态请求异常");
			return false;
		}
		return true;
	}
	
	@Override
	public Map<String, Object> findOpenAPIAppRecommendDownloadList(String sign, Long appId, Date beginDate, Date endDate, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入API查询openAPI应用");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_APP_RECOMMEND_LOG_SEARCH);
		
		if (endDate != null) {
			request.setParameter(OpenAPIAppRecommendDownload.QUERY_APP_BEGIN_DATE, CoreDateUtils.formatDate(beginDate, CoreDateUtils.DATETIME));
		}
		if (beginDate != null) {
			request.setParameter(OpenAPIAppRecommendDownload.QUERY_APP_END_DATE, CoreDateUtils.formatDate(endDate, CoreDateUtils.DATETIME));
		}
		if (appId != null && appId != 0L) {													//应用编码
			request.setParameter(OpenAPIAppRecommendDownload.QUERY_APP_ID, appId + "");
		}
		request.setParameter(OpenAPIAppRecommendDownload.QUERY_SIGN, sign);
		if (pageBean != null && pageBean.isPageFlag()) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API查询openAPI应用失败");
			throw new ApiRemoteCallFailedException("API查询openAPI应用失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API查询openAPI应用请求异常");
			throw new ApiRemoteCallFailedException("API查询openAPI应用请求异常");
		}
		if (response.getData() == null) {
			logger.error("API查询openAPI应用响应数据为空");
			return null;
		}
		
		List<OpenAPIAppRecommendDownload> list = OpenAPIAppRecommendDownload.convertFromJSONArray(response.getData());
		
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