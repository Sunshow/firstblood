package web.service.impl.openapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.openapi.OpenAPIAppService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.openapi.OpenAPIApp;
import com.lehecai.core.api.openapi.OpenAPIAppStatus;
import com.lehecai.core.api.openapi.OpenAPIAppType;
import com.lehecai.core.api.openapi.OpenAPIAppUpdatePolicyStatus;
import com.lehecai.core.api.openapi.OpenAPIAppVersionLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class OpenAPIAppServiceImpl implements OpenAPIAppService {
	private Logger logger = LoggerFactory.getLogger(OpenAPIAppServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	@SuppressWarnings("unchecked")
	public List<OpenAPIApp> findAllOpenAPIAppList(String appName, long appOwner, long source, OpenAPIAppStatus status, long level, 
			long appKey, long appId, OpenAPIAppType appType, String orderStr, String orderView) throws ApiRemoteCallFailedException{
		List<OpenAPIApp> openAPIAppList = new ArrayList<OpenAPIApp>();
		PageBean pageBean = new PageBean();
		int page = pageBean.getPage();
		Map<String, Object> map = null;
		List<OpenAPIApp> tmpOpenAPIAppList = null;
		do{
			try{
				map = findOpenAPIAppList(appName, appOwner, source, status, level, 
						appKey, appId, appType, orderStr, orderView, pageBean);
			}catch(ApiRemoteCallFailedException e){
				throw new ApiRemoteCallFailedException(e.getMessage());
			}
			if(map != null){
				tmpOpenAPIAppList = (List<OpenAPIApp>)map.get(Global.API_MAP_KEY_LIST);
				if(tmpOpenAPIAppList != null && tmpOpenAPIAppList.size() > 0) {
					openAPIAppList.addAll((List<OpenAPIApp>)map.get(Global.API_MAP_KEY_LIST));
				}
				page++;
				pageBean.setPage(page);
			}
		}while(tmpOpenAPIAppList != null && tmpOpenAPIAppList.size() > 0);
		return openAPIAppList;
	}
	
	/**
	 * 分页并多条件查询应用
	 */
	public Map<String, Object> findOpenAPIAppList(String appName, long appOwner, long source, OpenAPIAppStatus status, long level, 
			long appKey, long appId, OpenAPIAppType appType, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入API查询openAPI应用");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_APP_SEARCH);
		
		if (appName != null && !appName.equals("")) {						//应用名称
			request.setParameterLike(OpenAPIApp.QUERY_APP_NAME, appName);
		}
		if (appType != null && appType.getValue() != OpenAPIAppType.ALL.getValue()) {	//应用类型
			request.setParameter(OpenAPIApp.QUERY_APP_TYPE, appType.getValue() + "");
		}
		if (appOwner != 0L) {												//应用拥有者
			request.setParameter(OpenAPIApp.QUERY_APP_OWNER, appOwner + "");
		}
		if (source != 0L) {													//应用来源
			request.setParameter(OpenAPIApp.QUERY_SOURCE, source + "");
		}
		if (status != null && status.getValue() != OpenAPIAppStatus.ALL.getValue()) {	//应用状态
			request.setParameter(OpenAPIApp.QUERY_STATUS, status.getValue() + "");
		}
		if (level != 0L) {
			request.setParameter(OpenAPIApp.QUERY_LEVEL, level + "");		//应用级别
		}
		if (appKey != 0L) {													//应用key
			request.setParameter(OpenAPIApp.QUERY_APP_KEY, appKey + "");
		}
		if (appId != 0L) {													//应用编码
			request.setParameter(OpenAPIApp.QUERY_APP_ID, appId + "");
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
		
		List<OpenAPIApp> list = OpenAPIApp.convertFromJSONArray(response.getData());
		
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
	 * 查询版本列表
	 */
	@Override
	public Map<String, Object> findOpenAPIAppVersionList(Long id, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入API查询openAPI应用");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_APP_VERSION_LOG_SEARCH);
		
		if (id != null && !id.equals("")) {						//应用Id
			request.setParameter(OpenAPIAppVersionLog.QUERY_APP_ID, id + "");
		}
		
		if (pageBean != null && pageBean.isPageFlag()) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API查询openAPI应用版本信息失败");
			throw new ApiRemoteCallFailedException("API查询openAPI应用版本信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API查询openAPI应用版本信息请求异常");
			throw new ApiRemoteCallFailedException("API查询openAPI应用版本信息请求异常");
		}
		if (response.getData() == null) {
			logger.error("API查询openAPI应用版本信息响应数据为空");
			return null;
		}
		
		List<OpenAPIAppVersionLog> list = OpenAPIAppVersionLog.convertFromJSONArray(response.getData());
		
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
	public boolean addOpenAPIApp(OpenAPIApp openAPIApp) throws ApiRemoteCallFailedException{
		logger.info("进入API添加openAPI应用");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_APP_ADD);
		
		request.setParameterForUpdate(OpenAPIApp.SET_APP_NAME, openAPIApp.getAppName());			//应用名称
		request.setParameterForUpdate(OpenAPIApp.SET_APP_TYPE, openAPIApp.getOpenAPIAppType().getValue() + "");//应用类型
		request.setParameterForUpdate(OpenAPIApp.SET_APP_DOWNLOAD_URL, openAPIApp.getDownloadUrl());//下载地址			
		request.setParameterForUpdate(OpenAPIApp.SET_NEWS_IS_WAP, openAPIApp.getNewsIsWap().getValue() + "");//设置新闻是否用wap浏览
		request.setParameterForUpdate(OpenAPIApp.SET_APP_DESC, openAPIApp.getAppDesc());			//应用描述
		request.setParameterForUpdate(OpenAPIApp.SET_APP_VERSION, openAPIApp.getAppVersion());		//应用版本
		request.setParameterForUpdate(OpenAPIApp.SET_APP_OWNER, openAPIApp.getAppOwner() + "");		//应用拥有者
		request.setParameterForUpdate(OpenAPIApp.SET_SOURCE, openAPIApp.getSource() + "");			//应用来源
		request.setParameterForUpdate(OpenAPIApp.SET_STATUS, openAPIApp.getStatus().getValue() + "");//设置应用状态
		request.setParameterForUpdate(OpenAPIApp.SET_LEVEL, openAPIApp.getLevel() + "");			 //设置应用级别
		if (openAPIApp.getUpdatePolicy() != null && openAPIApp.getUpdatePolicy().getValue() != OpenAPIAppUpdatePolicyStatus.ALL.getValue()) {
			request.setParameterForUpdate(OpenAPIApp.SET_UPDATE_POLICY, openAPIApp.getUpdatePolicy().getValue() + "");	//更新策略
		}
		if (openAPIApp.getUpdateLog() != null && !openAPIApp.getUpdateLog().equals("")) {
			request.setParameterForUpdate(OpenAPIApp.SET_UPDATE_LOG, openAPIApp.getUpdateLog());	//更新策略
		}
		
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
	@SuppressWarnings("static-access")
	public boolean updateOpenAPIApp(OpenAPIApp openAPIApp) throws ApiRemoteCallFailedException {
		logger.info("进入API编辑openAPI应用");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPENAPI_APP_UPDATE);
		request.setParameter(openAPIApp.QUERY_APP_ID, openAPIApp.getAppId() + "");
		
		request.setParameterForUpdate(OpenAPIApp.SET_APP_NAME, openAPIApp.getAppName());			//设置应用名称
		request.setParameterForUpdate(OpenAPIApp.SET_APP_TYPE, openAPIApp.getOpenAPIAppType().getValue() + "");//应用类型
		request.setParameterForUpdate(OpenAPIApp.SET_APP_DOWNLOAD_URL, openAPIApp.getDownloadUrl());//下载地址			
		request.setParameterForUpdate(OpenAPIApp.SET_NEWS_IS_WAP, openAPIApp.getNewsIsWap().getValue() + "");//设置新闻是否用wap浏览
		request.setParameterForUpdate(OpenAPIApp.SET_APP_DESC, openAPIApp.getAppDesc());			//设置应用描述
		request.setParameterForUpdate(OpenAPIApp.SET_APP_VERSION, openAPIApp.getAppVersion());		//设置应用版本
		request.setParameterForUpdate(OpenAPIApp.SET_APP_OWNER, openAPIApp.getAppOwner() + "");		//设置应用拥有者
		request.setParameterForUpdate(OpenAPIApp.SET_SOURCE, openAPIApp.getSource() + "");			//设置应用来源
		request.setParameterForUpdate(OpenAPIApp.SET_STATUS, openAPIApp.getStatus().getValue() + "");//设置应用状态
		request.setParameterForUpdate(OpenAPIApp.SET_LEVEL, openAPIApp.getLevel() + "");			 //设置应用级别
		if (openAPIApp.getUpdatePolicy() != null && openAPIApp.getUpdatePolicy().getValue() != OpenAPIAppUpdatePolicyStatus.ALL.getValue()) {
			request.setParameterForUpdate(openAPIApp.SET_UPDATE_POLICY, openAPIApp.getUpdatePolicy().getValue() + "");	//更新策略
		}
		if (openAPIApp.getUpdateLog() != null && !openAPIApp.getUpdateLog().equals("")) {
			request.setParameterForUpdate(openAPIApp.SET_UPDATE_LOG, openAPIApp.getUpdateLog());	//更新策略
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API编辑openAPI应用失败");
			throw new ApiRemoteCallFailedException("API编辑openAPI应用失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API编辑openAPI应用请求异常");
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