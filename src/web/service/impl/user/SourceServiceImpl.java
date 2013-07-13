package web.service.impl.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.user.SourceService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.Source;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class SourceServiceImpl implements SourceService {
	private final Logger logger = LoggerFactory.getLogger(SourceServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public Map<String, Object> getResult(Long id, Long partnerId,
			String name, Integer status, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员来源");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_SOURCE_SEARCH);
		
		if(id != null){
			request.setParameter(Source.QUERY_ID, id.toString());
		}
		if(partnerId != null){
			request.setParameter(Source.QUERY_PARTNER_ID, partnerId.toString());
		}
		if(name != null && !"".equals(name)){
			request.setParameter(Source.QUERY_NAME, name);
		}
		if (status != null) {
			request.setParameter(Source.QUERY_STATUS, status.toString());
		}
		if(pageBean != null){		
			request.setPage(pageBean.getPage());
			//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员来源数据失败");
			throw new ApiRemoteCallFailedException("API获取会员来源数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员来源数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员来源数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取会员来源数据为空, message={}", response.getMessage());
			return null;
		}
		List<Source> list = Source.convertFromJSONArray(response.getData());
		if(pageBean != null){		
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;//页数
			if(pageBean.getPageSize() != 0) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if(totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
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
	public boolean create(Long id, Long partnerId, String name, Integer status) throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加会员来源信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_SOURCE_ADD);
		if(id != null){
			request.setParameterForUpdate(Source.SET_ID, id.toString());
		}
		if(partnerId != null){
			request.setParameterForUpdate(Source.SET_PARTNER_ID, partnerId.toString());
		}
		if(name != null && !"".equals(name)){
			request.setParameterForUpdate(Source.SET_NAME, name);
		}
		if (status != null) {
			request.setParameterForUpdate(Source.SET_STATUS, status.toString());
		}
		
		logger.info("添加会员来源信息,api request String: {}", request.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API添加会员来源信息异常!{}", e.getMessage());
			return false;
		}
		logger.info("添加会员来源信息,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode() != ApiConstant.RC_SUCCESS || response.getTotal() == 0){
			logger.error("会员来源信息添加失败!");
			return false;
		}
		logger.info("会员来源信息添加成功!");
		//刷新来源列表
		try {
			refreshSource(partnerId);
		} catch (Exception e) {
			logger.error("刷新来源列表API异常!");
		}
		return true;
	}
	@Override
	public boolean createExistSource(Long id, Long partnerId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加会员来源信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_SOURCE_ADD);
		if(id != null){
			request.setParameterForUpdate(Source.SET_SOURCE, id.toString());
		}
		if(partnerId != null){
			request.setParameterForUpdate(Source.SET_PARTNER_ID, partnerId.toString());
		}
		
		logger.info("添加会员来源信息,api request String: {}", request.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API添加会员来源信息异常!{}", e.getMessage());
			return false;
		}
		logger.info("添加会员来源信息,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode() != ApiConstant.RC_SUCCESS){
			logger.error("会员来源信息添加失败!");
			return false;
		}
		logger.info("会员来源信息添加成功!");
		//刷新来源列表
		try {
			refreshSource(partnerId);
		} catch (Exception e) {
			logger.error("刷新来源列表API异常!");
		}
		return true;
	}

	@Override
	public boolean update(Long id, Long partnerId, String name, Integer status) throws ApiRemoteCallFailedException {
		logger.info("进入调用API编辑会员来源信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_SOURCE_UPDATE);
		if(id != null){
			request.setParameter(Source.SET_ID, id.toString());
		}
		if(partnerId != null){
			request.setParameterForUpdate(Source.SET_PARTNER_ID, partnerId.toString());
		}
		if(name != null && !"".equals(name)){
			request.setParameterForUpdate(Source.SET_NAME, name);
		}
		if (status != null) {
			request.setParameterForUpdate(Source.SET_STATUS, status.toString());
		}
		
		logger.info("编辑会员来源信息,api request String: {}", request.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API编辑会员来源信息异常!{}", e.getMessage());
			return false;
		}
		logger.info("编辑会员来源信息,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode() != ApiConstant.RC_SUCCESS){
			logger.error("会员来源信息编辑失败!");
			return false;
		}
		logger.info("会员来源信息编辑成功!");
		return true;
	}
	
	@Override
	public boolean refreshSource(Long partnerId) {
		logger.info("进入调用API刷新来源列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_REFRESH_SOURCE);
		request.setParameter(Source.SET_PARTNER_ID, partnerId.toString());
		
		logger.info("刷新来源列表信息,api request String: {}", request.toQueryString());
		ApiResponse response;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API刷新来源列表信息异常!{}", e.getMessage());
			return false;
		}
		logger.info("刷新来源列表信息,api response code = {}, message = {}", response.getCode(), response.getMessage());
		
		if(response.getCode() != ApiConstant.RC_SUCCESS){
			logger.error("刷新来源列表失败!");
			return false;
		}
		logger.info("刷新来源列表成功!");
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
