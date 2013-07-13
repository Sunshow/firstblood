package web.service.impl.openapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.openapi.RechargeCopywriterService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.openapi.DocPosition;
import com.lehecai.core.api.openapi.RechargeCopywriter;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 2013-7-2
 * @author likunpeng
 *
 */
public class RechargeCopywriterServiceImpl implements RechargeCopywriterService {

	private final Logger logger = LoggerFactory.getLogger(RechargeCopywriterServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public RechargeCopywriter get(Long id) throws ApiRemoteCallFailedException {
		logger.info("进入调用API根据id查询充值文案信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_RECHARGEDESC_SEARCH);
		if (id != null) {
			request.setParameter(RechargeCopywriter.ORDER_ID, String.valueOf(id));
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API根据id获取充值文案信息失败");
			throw new ApiRemoteCallFailedException("API根据id获取充值文案信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API根据id获取充值文案信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API根据id获取充值文案信息请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API根据id获取充值文案信息为空, message={}", response.getMessage());
			return null;
		}
		List<RechargeCopywriter> rechargeCopywriters = RechargeCopywriter.convertFromJSONArray(response.getData());
		if(rechargeCopywriters != null && rechargeCopywriters.size() > 0){
			return rechargeCopywriters.get(0);
		}
		return null;
	}
	
	@Override
	public Map<String, Object> getList(RechargeCopywriter rechargeCopywriter,PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API根据充值文案信息查询");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_RECHARGEDESC_SEARCH);
		if (rechargeCopywriter.getDocName() != null && StringUtils.isNotEmpty(rechargeCopywriter.getDocName())) {
			request.setParameter(RechargeCopywriter.QUERY_DOCNAME, rechargeCopywriter.getDocName());
		}
		if (rechargeCopywriter.getRechargeTypeName() != null && StringUtils.isNotEmpty(rechargeCopywriter.getRechargeTypeName())) {
			request.setParameter(RechargeCopywriter.QUERY_RECHARGE_TYPE_NAME, rechargeCopywriter.getRechargeTypeName());
		}
		if (rechargeCopywriter.getIsValid() != null && rechargeCopywriter.getIsValid() != YesNoStatus.ALL.getValue()) {
			request.setParameter(RechargeCopywriter.QUERY_STATUS, rechargeCopywriter.getIsValid()+"");
		}
		if (rechargeCopywriter.getDocPlace() != null && rechargeCopywriter.getDocPlace() != DocPosition.ALL.getValue()) {
			request.setParameter(RechargeCopywriter.QUERY_DOC_POSITION, rechargeCopywriter.getDocPlace()+"");
		}
		if (rechargeCopywriter.getQueryStr() != null && !"".equals(rechargeCopywriter.getQueryStr()) && rechargeCopywriter.getQueryOrder() != null && !"".equals(rechargeCopywriter.getQueryOrder())) {
			request.addOrder(rechargeCopywriter.getQueryStr(),rechargeCopywriter.getQueryOrder());
		}
		if (pageBean != null) {		
			request.setPage(pageBean.getPage());
			//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取充值文案信息数据失败");
			throw new ApiRemoteCallFailedException("API获取充值文案信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取充值文案信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("message=" + response.getMessage());
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取充值文案信息为空, message={}", response.getMessage());
			return null;
		}
		List<RechargeCopywriter> rechargeCopywriterList = RechargeCopywriter.convertFromJSONArray(response.getData());
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
		map.put(Global.API_MAP_KEY_LIST, rechargeCopywriterList);
		return map;
	}
	
	/**
	 * 添加充值文案信息
	 */
	@Override
	public Long addRechargeCopywriterInfo(RechargeCopywriter rechargeCopywriter) throws ApiRemoteCallFailedException {
		logger.info("进入API添加充值文案信息");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_RECHARGEDESC_ADD);
		request.setParameterForUpdate(RechargeCopywriter.QUERY_DOC_POSITION, rechargeCopywriter.getDocPlace() + "");
		request.setParameterForUpdate(RechargeCopywriter.ORDER_RECHARGE_TYPE, rechargeCopywriter.getNum() + "");
		request.setParameterForUpdate(RechargeCopywriter.QUERY_RECHARGE_TYPE_NAME, rechargeCopywriter.getRechargeTypeName());
		request.setParameterForUpdate(RechargeCopywriter.QUERY_RECHARGE_CONTENT, rechargeCopywriter.getRechargeContent());
		request.setParameterForUpdate(RechargeCopywriter.QUERY_STATUS, rechargeCopywriter.getIsValid() + "");
		request.setParameterForUpdate(RechargeCopywriter.QUERY_DOCNAME, rechargeCopywriter.getDocName());
		request.setParameterForUpdate(RechargeCopywriter.MAX_AMOUNT, rechargeCopywriter.getMaxAmount() + "");
		request.setParameterForUpdate(RechargeCopywriter.MIN_AMOUNT, rechargeCopywriter.getMinAmount() + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API添加充值文案信息失败");
			throw new ApiRemoteCallFailedException("API添加充值文案信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API添加充值文案信息请求异常");
		}
		if (response.getData() == null) {
			logger.error("API添加充值文案信息响应数据为空");
			return null;
		}
		JSONArray dataArray = response.getData();
		Integer newId = (Integer)dataArray.get(0);
		return Long.valueOf(newId);
	}

	/**
	 * 编辑充值文案信息
	 */
	@Override
	public boolean updateRechargeCopywriterInfo(RechargeCopywriter rechargeCopywriter) throws ApiRemoteCallFailedException {
		logger.info("进入API编辑充值文案信息");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_RECHARGEDESC_UPDATE);
		request.setParameter(RechargeCopywriter.ORDER_ID, rechargeCopywriter.getId() + "");
		request.setParameterForUpdate(RechargeCopywriter.QUERY_DOC_POSITION, rechargeCopywriter.getDocPlace() + "");
		request.setParameterForUpdate(RechargeCopywriter.QUERY_DOCNAME, rechargeCopywriter.getDocName());
		request.setParameterForUpdate(RechargeCopywriter.ORDER_RECHARGE_TYPE, rechargeCopywriter.getNum() + "");
		request.setParameterForUpdate(RechargeCopywriter.QUERY_RECHARGE_TYPE_NAME, rechargeCopywriter.getRechargeTypeName());
		request.setParameterForUpdate(RechargeCopywriter.QUERY_RECHARGE_CONTENT, rechargeCopywriter.getRechargeContent());
		request.setParameterForUpdate(RechargeCopywriter.QUERY_STATUS, rechargeCopywriter.getIsValid() + "");
		request.setParameterForUpdate(RechargeCopywriter.MAX_AMOUNT, rechargeCopywriter.getMaxAmount() + "");
		request.setParameterForUpdate(RechargeCopywriter.MIN_AMOUNT, rechargeCopywriter.getMinAmount() + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API编辑充值文案信息失败");
			throw new ApiRemoteCallFailedException("API编辑充值文案信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API编辑充值文案信息请求异常");
			return false;
		}
		return true;
	}
	
	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}
}
