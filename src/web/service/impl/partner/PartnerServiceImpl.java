package web.service.impl.partner;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.partner.PartnerService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.partner.Partner;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class PartnerServiceImpl implements PartnerService {
	private final Logger logger = LoggerFactory.getLogger(PartnerServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public Map<String, Object> getResult(Integer partnerId, String partnerName, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询渠道合作商");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_SEARCH);
		
		if (partnerId != null) {
			request.setParameter(Partner.QUERY_PARTNER_ID, partnerId.toString());
		}
		if (partnerName != null && !"".equals(partnerName)) {
			request.setParameter(Partner.QUERY_PARTNER_NAME, partnerName);
		}
		if (pageBean != null) {		
			request.setPage(pageBean.getPage());
			//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取渠道合作商数据失败");
			throw new ApiRemoteCallFailedException("API获取渠道合作商数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取渠道合作商数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取渠道合作商数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取渠道合作商数据为空, message={}", response.getMessage());
			return null;
		}
		List<Partner> list = Partner.convertFromJSONArray(response.getData());
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
	public boolean update(Partner partner) throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新渠道合作商信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_UPDATE);
		if (partner != null) {
			request.setParameterForUpdate(Partner.SET_PARTNER_NAME, partner.getPartnerName());
			request.setParameterForUpdate(Partner.SET_DEFAULT_REBATE, partner.getDefaultRebate().toString());
			//修改密码
			if (partner.getPassword() != null && !"".equals(partner.getPassword())) {
				request.setParameterForUpdate(Partner.SET_PASSWORD, partner.getPassword());
			}
			request.setParameter(Partner.SET_PARTNER_ID, partner.getPartnerId().toString());
		}
		
		logger.info("更新渠道合作商信息,api request String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API更新渠道合作商信息失败");
			throw new ApiRemoteCallFailedException("API更新渠道合作商信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更新渠道合作商信息失败");
			return false;
		}
		return true;
	}
	
	@Override
	public boolean create(Partner partner) throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加渠道合作商");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_PARTNER_ADD);
		if (partner != null) {
			request.setParameterForUpdate(Partner.SET_PARTNER_NAME, partner.getPartnerName());
			request.setParameterForUpdate(Partner.SET_PASSWORD, partner.getPassword());
			request.setParameterForUpdate(Partner.SET_DEFAULT_REBATE, partner.getDefaultRebate().toString());
		}
		
		logger.info("添加渠道合作商,api request String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API添加渠道合作商失败");
			throw new ApiRemoteCallFailedException("API添加渠道合作商失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API添加渠道合作商请求异常");
			return false;
		}
		return true;
	}
	
	@Override
	public Double getTotalAmount(List<String> sourceIds, Date beginDate, Date endDate)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询渠道费用");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_SUM_REBATE_AMOUNT);
		if (sourceIds != null && sourceIds.size() > 0) {
			request.setParameterIn("source", sourceIds);
		}
		if (beginDate != null) {
			request.setParameterBetween("timeline", DateUtil.formatDate(beginDate), null);
		}
		if (endDate != null){
			request.setParameterBetween("timeline", null, DateUtil.formatDate(endDate));
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取渠道合作商数据失败");
			throw new ApiRemoteCallFailedException("API获取渠道合作商数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取渠道合作商数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取渠道合作商数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取渠道合作商数据为空, message={}", response.getMessage());
			return null;
		}
		
		Double total = 0D;
		JSONArray array = response.getData();
		for (Iterator<?> iterator = array.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			total += object.getDouble("rebate_amount");
		}
		return total;
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
