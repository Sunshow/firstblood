package web.service.impl.partner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.partner.PartnerDataApiService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.partner.PartnerDataApi;
import com.lehecai.core.api.partner.PartnerDataApiLottery;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class PartnerDataApiServiceImpl implements PartnerDataApiService {
	private final Logger logger = LoggerFactory.getLogger(PartnerDataApiServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;

	@Override
	public Map<String, Object> getPartnerDataApiResult(PartnerDataApi partnerDataApi, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询合作商数据项");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_DATA_ITEM_SEARCH);
		if (partnerDataApi == null) {
			logger.error("查询条件为空。");
			return null;
		}
		if (partnerDataApi.getDataApiId() != null) {
			request.setParameter(PartnerDataApi.QUERY_DATA_API_ID, partnerDataApi.getDataApiId().toString());
		}
		if (partnerDataApi.getContentFlag() != null) {
			request.setParameter(PartnerDataApi.QUERY_CONTENT_FLAG, partnerDataApi.getContentFlag() + "");
		} else {
			request.setParameter(PartnerDataApi.QUERY_CONTENT_FLAG, Boolean.TRUE + "");
		}
		if (partnerDataApi.getAgentId() != null && partnerDataApi.getAgentId() > 0) {
			request.setParameter(PartnerDataApi.QUERY_AGENT_ID, partnerDataApi.getAgentId().toString());
		}
		if (partnerDataApi.getDataApiName() != null && !partnerDataApi.getDataApiName().equals("")) {
			request.setParameter(PartnerDataApi.QUERY_DATA_API_NAME, partnerDataApi.getDataApiName());
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取合作商数据失败");
			throw new ApiRemoteCallFailedException("API获取合作商数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取合作商数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取合作商数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取合作商数据为空, message={}", response.getMessage());
			return null;
		}
		List<PartnerDataApi> list = PartnerDataApi.convertFromJSONArray(response.getData());
		if (pageBean != null) {		
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;//页数
			if (pageBean.getPageSize() != 0) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
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
	public boolean updatePartnerDataApi(PartnerDataApi partnerDataApi)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加合作商");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_DATA_ITEM_UPDATE);
		if (partnerDataApi != null) {
			request.setParameterForUpdate(PartnerDataApi.SET_DATA_API_NAME, partnerDataApi.getDataApiName());
			request.setParameterForUpdate(PartnerDataApi.SET_AGENT_ID, partnerDataApi.getAgentId()+"");
			request.setParameterForUpdate(PartnerDataApi.SET_SHOW_RESULT, partnerDataApi.getShowResult().getValue() + "");
			request.setParameterForUpdate(PartnerDataApi.SET_CATEGORY, partnerDataApi.getCategory().getValue() + "");
			request.setParameterForUpdate(PartnerDataApi.SET_RESULT_FORMAT, partnerDataApi.getResultFormat());
			request.setParameterForUpdate(PartnerDataApi.SET_FREQUENCY, partnerDataApi.getFrequency() + "");
			request.setParameterForUpdate(PartnerDataApi.SET_PHASE, partnerDataApi.getPhase() + "");
			request.setParameterForUpdate(PartnerDataApi.SET_CHARSET_TYPE, partnerDataApi.getCharsetType());
			request.setParameter(PartnerDataApi.SET_DATA_API_ID, partnerDataApi.getDataApiId() + "");
		}
		
		logger.info("添加合作商,api request String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API添加合作商失败");
			throw new ApiRemoteCallFailedException("API添加合作商失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API添加合作商请求异常");
			return false;
		}
		return true;
	}

	@Override
	public boolean createPartnerDataApi(PartnerDataApi partnerDataApi)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加合作商");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_DATA_ITEM_ADD);
		if (partnerDataApi != null) {
			request.setParameterForUpdate(PartnerDataApi.SET_DATA_API_NAME, partnerDataApi.getDataApiName());
			request.setParameterForUpdate(PartnerDataApi.SET_AGENT_ID, partnerDataApi.getAgentId()+"");
			request.setParameterForUpdate(PartnerDataApi.SET_SHOW_RESULT, partnerDataApi.getShowResult().getValue() + "");
			request.setParameterForUpdate(PartnerDataApi.SET_CATEGORY, partnerDataApi.getCategory().getValue() + "");
			request.setParameterForUpdate(PartnerDataApi.SET_RESULT_FORMAT, partnerDataApi.getResultFormat());
			request.setParameterForUpdate(PartnerDataApi.SET_FREQUENCY, partnerDataApi.getFrequency() + "");
			request.setParameterForUpdate(PartnerDataApi.SET_PHASE, partnerDataApi.getPhase() + "");
			request.setParameterForUpdate(PartnerDataApi.SET_CHARSET_TYPE, partnerDataApi.getCharsetType());
			request.setParameterForUpdate(PartnerDataApi.SET_FILE_NAME, partnerDataApi.getFileName());
		}
		
		logger.info("添加合作商,api request String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API添加合作商失败");
			throw new ApiRemoteCallFailedException("API添加合作商失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API添加合作商请求异常");
			throw new ApiRemoteCallFailedException("API添加合作商请求异常");
		}
		if (response.getTotal() == 0) {
			logger.error("API添加合作商失败");
			throw new ApiRemoteCallFailedException(response.getMessage());
		}
		return true;
	}

	@Override
	public boolean updatePartnerDataApiTemplate(PartnerDataApi partnerDataApi)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新合作商数据项模板");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_DATA_ITEM_TEMPLATE_UPDATE);
		if (partnerDataApi != null) {
			request.setParameterForUpdate(PartnerDataApi.SET_TEMPLATE_CONTENT, partnerDataApi.getTemplateContent());
		}
		request.setParameter(PartnerDataApi.SET_DATA_API_ID, partnerDataApi.getDataApiId()+"");
		logger.info("更新合作商数据项模板,api request String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API更新合作商数据项模板失败");
			throw new ApiRemoteCallFailedException("API添加合作商数据项模板失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更新合作商数据项模板请求异常");
			throw new ApiRemoteCallFailedException("API更新合作商数据项模板请求异常");
		}
		return true;
	}

	@Override
	public boolean updatePartnerDataApiStatus(PartnerDataApi partnerDataApi)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新合作商数据项状态信息");
		ApiRequest request = new ApiRequest();
		if (partnerDataApi != null && partnerDataApi.getStatus() != null) {
			if (partnerDataApi.getStatus().getValue() == YesNoStatus.YES.getValue()) {
				request.setUrl(ApiConstant.API_URL_PARTNER_DATA_ITEM_STATUS_ON);
			} else {
				request.setUrl(ApiConstant.API_URL_PARTNER_DATA_ITEM_STATUS_OFF);
			}
			//request.setParameterForUpdate(PartnerDataApi.SET_STATUS, partnerDataApi.getStatus().getValue() + "");
			request.setParameter(PartnerDataApi.QUERY_DATA_API_ID, partnerDataApi.getDataApiId() + "");
		}
		logger.info("更新合作商信息,api request String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API更新合作商数据项状态信息失败");
			throw new ApiRemoteCallFailedException("API更新合作商数据项状态信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更新合作商数据项状态信息失败");
			return false;
		}
		return true;
	}
	

	@Override
	public Map<String, Object> getPartnerDataApiLotteryResult(PartnerDataApiLottery partnerDataApiLottery, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询合作商数据项");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_DATA_ITEM_LOTTERY_SEARCH);
		if (partnerDataApiLottery == null) {
			logger.error("查询条件为空。");
			return null;
		}
		if (partnerDataApiLottery.getDataApiId() != null) {
			request.setParameter(PartnerDataApiLottery.QUERY_DATA_API_ID, partnerDataApiLottery.getDataApiId().toString());
		}
		if (partnerDataApiLottery.getLotteryType() != null) {
			request.setParameter(PartnerDataApiLottery.QUERY_LOTTERY_TYPE, partnerDataApiLottery.getLotteryType().getValue() + "");
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取合作商数据失败");
			throw new ApiRemoteCallFailedException("API获取合作商数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取合作商数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取合作商数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取合作商数据为空, message={}", response.getMessage());
			return null;
		}
		List<PartnerDataApiLottery> list = PartnerDataApiLottery.convertFromJSONArray(response.getData());
		if (pageBean != null) {		
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;//页数
			if (pageBean.getPageSize() != 0) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
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
	public boolean updatePartnerDataApiLottery(
			PartnerDataApiLottery partnerDataApiLottery)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新合作商数据项彩种信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_DATA_ITEM_LOTTERY_UPDATE);
		if (partnerDataApiLottery == null ||partnerDataApiLottery.getDataApiId() == null || partnerDataApiLottery.getLotteryType() == null) {
			logger.error("参数错误");
			throw new ApiRemoteCallFailedException("参数错误");
		}
		request.setParameter(PartnerDataApiLottery.QUERY_DATA_API_ID, partnerDataApiLottery.getDataApiId() + "");
		request.setParameter(PartnerDataApiLottery.QUERY_LOTTERY_TYPE, partnerDataApiLottery.getLotteryType().getValue() + "");
		
		request.setParameterForUpdate(PartnerDataApiLottery.SET_CATEGORY, partnerDataApiLottery.getCategory());
		request.setParameterForUpdate(PartnerDataApiLottery.SET_DESCRIPTION, partnerDataApiLottery.getDescription());
		request.setParameterForUpdate(PartnerDataApiLottery.SET_PHASET_TYPE, partnerDataApiLottery.getPhaseType());
		request.setParameterForUpdate(PartnerDataApiLottery.SET_CUSTOM_SHOWNAME, partnerDataApiLottery.getCustomShowName());
		
		logger.info("更新合作商信息,api request String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API更新合作商数据项彩种信息失败");
			throw new ApiRemoteCallFailedException("API更新合作商数据项彩种信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更新合作商数据项彩种信息失败");
			return false;
		}
		return true;
	}

	@Override
	public boolean createPartnerDataApiLottery(
			PartnerDataApiLottery partnerDataApiLottery)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新合作商数据项状态信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_DATA_ITEM_LOTTERY_ADD);
		request.setParameterForUpdate(PartnerDataApiLottery.QUERY_DATA_API_ID, partnerDataApiLottery.getDataApiId() + "");
		request.setParameterForUpdate(PartnerDataApiLottery.SET_CATEGORY, partnerDataApiLottery.getCategory());
		request.setParameterForUpdate(PartnerDataApiLottery.SET_DESCRIPTION, partnerDataApiLottery.getDescription());
		request.setParameterForUpdate(PartnerDataApiLottery.SET_LOTTERY_TYPE, partnerDataApiLottery.getLotteryType().getValue() + "");
		request.setParameterForUpdate(PartnerDataApiLottery.SET_PHASET_TYPE, partnerDataApiLottery.getPhaseType());
		request.setParameterForUpdate(PartnerDataApiLottery.SET_CUSTOM_SHOWNAME, partnerDataApiLottery.getCustomShowName());
		logger.info("更新合作商信息,api request String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API更新合作商数据项状态信息失败");
			throw new ApiRemoteCallFailedException("API更新合作商数据项状态信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更新合作商数据项状态信息失败");
			return false;
		}
		return true;
	}
	
	@Override
	public PartnerDataApi preview(PartnerDataApi partnerDataApi, Long userId)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API预览合作商模板");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_DATA_ITEM_PREVIEW);
		request.setParameter(PartnerDataApi.QUERY_DATA_API_ID, partnerDataApi.getDataApiId() + "");
		request.setParameter(PartnerDataApi.QUERY_USER_ID, userId+"");
		request.setParameter(PartnerDataApi.QUERY_CONTENT, partnerDataApi.getTemplateContent());
		logger.info("更新合作商信息,api request String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API调用API预览合作商模板失败");
			throw new ApiRemoteCallFailedException("API调用API预览合作商模板失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			if (response.getMessage() != null && !response.getMessage().equals("")) {
				logger.error("API调用API预览合作商模板失败,原因:{}", response.getMessage());
				throw new ApiRemoteCallFailedException("API调用API预览合作商模板失败,原因：" + response.getMessage());
			} else {
				logger.error("API调用API预览合作商模板失败");
				throw new ApiRemoteCallFailedException("API调用API预览合作商模板失败");
			}
		}
		
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.error("API调用API预览模板失败，原因{}", response.getMessage());
			throw new ApiRemoteCallFailedException("API调用API预览模板失败，原因" + response.getMessage());
		}
		List<PartnerDataApi> list = PartnerDataApi.convertFromJSONArray(response.getData());
		if (list != null && list.size() > 0  && list.get(0).getPreviewUrl() != null) {
			partnerDataApi = list.get(0);
		}
		return partnerDataApi;
	}

	@Override
	public boolean updateManual(PartnerDataApi partnerDataApi)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API合作商数据项手动更新");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_DATA_ITEM_UPDATE_MANUAL);
		request.setParameter(PartnerDataApiLottery.QUERY_DATA_API_ID, partnerDataApi.getDataApiId() + "");
		logger.info("合作商手动更新,api request String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API合作商数据项手动更新失败");
			throw new ApiRemoteCallFailedException("API合作商数据项手动更新失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API合作商数据项手动更新失败");
			return false;
		}
		List<PartnerDataApi> list = PartnerDataApi.convertFromJSONArray(response.getData());
		if (list != null && list.size() > 0 && list.get(0).getStatus() != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean delLottery(PartnerDataApiLottery partnerDataApiLottery)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除合作商数据项彩种信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_DATA_ITEM_LOTTERY_DELETE);
		if (partnerDataApiLottery == null ||partnerDataApiLottery.getDataApiId() == null || partnerDataApiLottery.getLotteryType() == null) {
			logger.error("参数错误");
			throw new ApiRemoteCallFailedException("参数错误");
		}
		request.setParameter(PartnerDataApiLottery.QUERY_DATA_API_ID, partnerDataApiLottery.getDataApiId() + "");
		request.setParameter(PartnerDataApiLottery.QUERY_LOTTERY_TYPE, partnerDataApiLottery.getLotteryType().getValue() + "");
		
		logger.info("更新合作商信息,api request String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API更新合作商数据项彩种信息失败");
			throw new ApiRemoteCallFailedException("API更新合作商数据项彩种信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更新合作商数据项彩种信息失败");
			throw new ApiRemoteCallFailedException("API更新合作商数据项彩种信息失败");
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
