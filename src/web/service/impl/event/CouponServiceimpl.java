package web.service.impl.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.event.CouponService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.event.Coupon;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.CouponStatus;
import com.lehecai.core.lottery.CouponType;

public class CouponServiceimpl implements CouponService {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;

	/**
	 * 分页查询所有充值券信息
	 * @param pageBean 分页信息
	 */
	@Override
	public Map<String, Object> findCouponList(PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询充值券数据");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_COUPON_SEARCH);//设置api地址
	//	request.addOrder(EventInfo.ORDER_EVENT_ID, ApiConstant.API_REQUEST_ORDER_DESC);//按照活动编码(event_id)降序排列
		if (pageBean != null && pageBean.isPageFlag()) {	
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取充值券数据失败");
			throw new ApiRemoteCallFailedException("API获取充值券数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取充值券数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取充值券数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取充值券数据为空, message={}", response.getMessage());
			return null;
		}

		List<Coupon> couponList = Coupon.convertFromJSONArray(response.getData());
		
		if (pageBean != null && pageBean.isPageFlag()) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			
			int pageCount = 0;//页数
			if ( pageBean.getPageSize() != 0 ) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, couponList);
		
		
		return map;
	}

	/**
	 * 多条件分页查询充值券信息
	 * @param cpId 充值券id
	 * @param type 充值券类型
	 * @param uid 用户id
	 * @param eventId 活动id
	 * @param pageBean 分页信息
	 */
	@Override
	public Map<String, Object> findCouponListByCondition(Long cpId,
			CouponType type, CouponStatus status, Long uid, Integer eventId, String orderStr,
			String orderView, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API多条件查询充值券数据");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_COUPON_SEARCH);//设置api地址

		if (cpId != null) {
			request.setParameter(Coupon.QUERY_COUPON_ID, cpId + "");
		}
		
		if (type != null && type.getValue() != CouponType.ALL.getValue()) {
			request.setParameter(Coupon.QUERY_TYPE, type.getValue() + "");
		}

		if (status != null && status.getValue() != CouponStatus.ALL.getValue()) {
			request.setParameter(Coupon.QUERY_STATUS, status.getValue() + "");
		}
		
		if (uid != null) {
			request.setParameter(Coupon.QUERY_UID, uid + "");
		}
		
		if (eventId != null) {
			request.setParameter(Coupon.QUERY_EVENT_ID, eventId + "");
		}
		
		if (orderStr != null && !"".equals(orderStr) && orderView != null && !"".equals(orderView)) {		
			request.addOrder(orderStr,orderView);
		}
		
		if (pageBean != null && pageBean.isPageFlag()) {	
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取充值券数据失败");
			throw new ApiRemoteCallFailedException("API获取充值券数据数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取充值券数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API获取充值券数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取充值券数据为空, message={}", response.getMessage());
			return null;
		}

		List<Coupon> couponList = Coupon.convertFromJSONArray(response.getData());
		
		if (pageBean != null && pageBean.isPageFlag()) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			
			int pageCount = 0;//页数
			if ( pageBean.getPageSize() != 0 ) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, couponList);
		
		return map;
	}
	
	/**
	 * 删除指定cpId的充值券
	 * @param cpId 充值券Id
	 */
	@Override
	public void delCoupon(Long cpId, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除充值券");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_COUPON_DELETE);//设置api地址

		if (cpId != null && cpId != 0) {
			request.setParameter(Coupon.QUERY_COUPON_ID, cpId + "");
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API删除充值券失败");
			throw new ApiRemoteCallFailedException("API删除充值券失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API删除充值券请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API删除充值券请求出错");
		}
	}

	/**
	 * 启用指定cpId的充值券
	 * @param cpId 充值券Id
	 */
	@Override
	public void enable(Long cpId, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API启用充值券");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_COUPON_ENABLE);//设置api地址

		if (cpId != null && cpId != 0) {
			request.setParameter(Coupon.QUERY_COUPON_ID, cpId + "");
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API启用充值券失败");
			throw new ApiRemoteCallFailedException("API启用充值券失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API启用充值券请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API启用充值券请求异常");
		}
	}
	
	/**
	 * 禁用指定cpId的充值券
	 * @param cpId 充值券Id
	 */
	@Override
	public void disable(Long cpId, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API禁用充值券");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_COUPON_DISABLE);//设置api地址

		if (cpId != null && cpId != 0) {
			request.setParameter(Coupon.QUERY_COUPON_ID, cpId + "");
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API禁用充值券失败");
			throw new ApiRemoteCallFailedException("API禁用充值券失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API禁用充值券请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API禁用充值券请求异常");
		}
	}
	
	@Override
	public Map<String, Object> getAmount(Long cpId, CouponType type, CouponStatus status,
			Long uid, Integer eventId, String orderStr, String orderView) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询充值券总金额");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_COUPON_STATS_SUM);//设置api地址

		if (cpId != null) {
			request.setParameter(Coupon.QUERY_COUPON_ID, cpId + "");
		}
		if (type != null && type.getValue() != CouponType.ALL.getValue()) {
			request.setParameter(Coupon.QUERY_TYPE, type.getValue() + "");
		}
		if (status != null && status.getValue() != CouponStatus.ALL.getValue()) {
			request.setParameter(Coupon.QUERY_STATUS, status.getValue() + "");
		}
		if (uid != null) {
			request.setParameter(Coupon.QUERY_UID, uid + "");
		}
		if (eventId != null) {
			request.setParameter(Coupon.QUERY_EVENT_ID, eventId + "");
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取充值券总金额失败");
			throw new ApiRemoteCallFailedException("API获取充值券总金额失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取充值券总金额请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取充值券总金额请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取充值券总金额为空, message={}", response.getMessage());
			return null;
		}

		//List<Coupon> couponList = Coupon.convertFromJSONArray(response.getData());
		
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject jsonObj = response.getData().getJSONObject(0);
		map.put(Global.API_MAP_KEY_AMOUNT, jsonObj.get("amount"));
		
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
