package web.service.impl.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.user.SourceRebateConfigService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.SourceRebateConfig;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class SourceRebateConfigServiceImpl implements SourceRebateConfigService {
	private final Logger logger = LoggerFactory.getLogger(SourceRebateConfigServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;

	
	/**
	 * 查询分成配置
	 * @param sourceRebateConfig
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	@SuppressWarnings("static-access")
	public Map<String, Object> findSourceRebateConfigList(SourceRebateConfig sourceRebateConfig, PageBean pageBean) throws ApiRemoteCallFailedException{
		logger.info("进入调用API查询分成数据列表");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CONFIG_SEARCH);
		
		if (sourceRebateConfig != null) {
			if(sourceRebateConfig.getPartnerId() != null && sourceRebateConfig.getPartnerId() != 0){
				request.setParameter(sourceRebateConfig.QUERY_PARTNER_ID, sourceRebateConfig.getPartnerId() + "");//添加渠道合作商编码查询条件
			}
			if(sourceRebateConfig.getSource() != null && sourceRebateConfig.getSource() != 0){
				request.setParameter(sourceRebateConfig.QUERY_SOURCE, sourceRebateConfig.getSource() + "");//添加渠道来源编码查询条件
			}
		}
		if(pageBean != null){		
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取分成数据失败");
			throw new ApiRemoteCallFailedException("API获取分成数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取分成数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取分成数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取分成数据为空, message={}", response.getMessage());
			return null;
		}
		List<SourceRebateConfig> list = SourceRebateConfig.convertFromJSONArray(response.getData());
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
	
	/**
	 * 查询分成配置
	 * @param sourceRebateConfig
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	@SuppressWarnings("static-access")
	public SourceRebateConfig getSourceRebateConfig(SourceRebateConfig sourceRebateConfig) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询分成数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CONFIG_SEARCH);
		
		if (sourceRebateConfig != null) {
			if(sourceRebateConfig.getPartnerId() != null && sourceRebateConfig.getPartnerId() != 0){
				request.setParameter(sourceRebateConfig.QUERY_PARTNER_ID, sourceRebateConfig.getPartnerId() + "");//添加渠道合作商编码查询条件
			}
			if(sourceRebateConfig.getSource() != null && sourceRebateConfig.getSource() != 0){
				request.setParameter(sourceRebateConfig.QUERY_SOURCE, sourceRebateConfig.getSource() + "");//添加渠道来源编码查询条件
			}
			if (sourceRebateConfig.getLotteryType() != null) {
				request.setParameter(sourceRebateConfig.QUERY_LOTTERY_TYPE, sourceRebateConfig.getLotteryType().getValue() + "");//添加彩种编码查询条件
			} else {
				request.setParameter(sourceRebateConfig.QUERY_LOTTERY_TYPE, 0 + "");//添加彩种编码查询条件
			}
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取分成数据失败");
			throw new ApiRemoteCallFailedException("API获取分成数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取分成数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取分成数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取分成数据为空, message={}", response.getMessage());
			return null;
		}
		List<SourceRebateConfig> list = SourceRebateConfig.convertFromJSONArray(response.getData());
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 添加分成配置
	 * @param sourceRebateConfig
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean add(SourceRebateConfig sourceRebateConfig) throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加分成配置");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CONFIG_ADD);
		if (sourceRebateConfig != null) {
			request.setParameterForUpdate(SourceRebateConfig.SET_PARTNER_ID, sourceRebateConfig.getPartnerId() + "");//添加渠道合作商编码设置
			if (sourceRebateConfig.getLotteryType() != null) {
				request.setParameterForUpdate(SourceRebateConfig.SET_LOTTERY_TYPE, sourceRebateConfig.getLotteryType().getValue() + "");//添加彩种编码设置
			}
			request.setParameterForUpdate(SourceRebateConfig.SET_SOURCE, sourceRebateConfig.getSource() + "");//添加渠道来源编码设置
			request.setParameterForUpdate(SourceRebateConfig.SET_REBATE, sourceRebateConfig.getRebate() + "");//添加分成比例编码设置
		}
		
		logger.info("api request String: {}", request.toQueryString());
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.info("调用API添加分成配置异常!{}", e.getMessage());
			return false;
		}
		
		if(response.getCode() != ApiConstant.RC_SUCCESS){
			logger.error("API添加分成配置失败!");
			return false;
		}
		logger.info("API添加分成配置成功!");
		return true;
	}
	
	/**
	 * 修改分成配置
	 * @param sourceRebateConfig
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean update(SourceRebateConfig sourceRebateConfig) throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新分成配置");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CONFIG_UPDATE);
		if (sourceRebateConfig != null) {
			request.setParameterForUpdate(SourceRebateConfig.SET_REBATE, sourceRebateConfig.getRebate() + "");//添加分成比例设置
			if (sourceRebateConfig.getLotteryType() != null) {
				request.setParameter(SourceRebateConfig.SET_LOTTERY_TYPE, sourceRebateConfig.getLotteryType().getValue() + "");//添加彩种查询条件
			}
			request.setParameter(SourceRebateConfig.QUERY_PARTNER_ID, sourceRebateConfig.getPartnerId() + "");//添加渠道合作商编码查询条件
			request.setParameter(SourceRebateConfig.QUERY_SOURCE, sourceRebateConfig.getSource() + "");//添加渠道来源编码查询条件
		}
		
		logger.info("更新分成配置,api request String: {}", request.toQueryString());
		ApiResponse response = null;
		try {
			response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		} catch (ApiRemoteCallFailedException e) {
			logger.info("调用API更新分成配置异常!{}", e.getMessage());
			return false;
		}
		
		if(response.getCode() != ApiConstant.RC_SUCCESS){
			logger.error("API更新分成配置失败!");
			return false;
		}
		logger.info("API更新分成配置成功!");
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
