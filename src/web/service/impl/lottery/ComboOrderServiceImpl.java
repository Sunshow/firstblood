package web.service.impl.lottery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.ComboOrderService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.ComboOrder;
import com.lehecai.core.api.lottery.ComboOrderDetail;
import com.lehecai.core.api.lottery.ComboOrderDetailRecord;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.FinishComboStatus;
import com.lehecai.core.lottery.LotteryType;

public class ComboOrderServiceImpl implements ComboOrderService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public Map<String, Object> queryComboOrderList(Long comboOrderId, Long comboId, Long uid, Long comborevId,
			FinishComboStatus finishComboStatus, PageBean pageBean)	throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询套餐订单信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_GET_COMBO_ORDER_LIST);
		if (comboOrderId != null && comboOrderId != 0) {
			request.setParameter(ComboOrder.QUERY_COMBO_ORDER_ID, comboOrderId + "");
		}
		if (comboId != null && comboId != 0) {
			request.setParameter(ComboOrder.QUERY_COMBO_ID, comboId + "");
		}
		if (uid != null && uid != 0) {
			request.setParameter(ComboOrder.QUERY_UID, uid + "");
		}
		if (comborevId != null && comborevId != 0) {
			request.setParameter(ComboOrder.QUERY_COMBOREV_ID, comborevId + "");
		}
		if (finishComboStatus != null && finishComboStatus.getValue() != FinishComboStatus.ALL.getValue()) {
			request.setParameter(ComboOrder.QUERY_STATUS, finishComboStatus.getValue() + "");
		}
		request.addOrder(ComboOrder.ORDER_COMBO_ORDER_ID, ApiConstant.API_REQUEST_ORDER_DESC);
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API查询套餐订单信息失败");
			throw new ApiRemoteCallFailedException("API查询套餐订单信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API查询套餐订单信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API查询套餐订单信息请求出错");
		}
		List<ComboOrder> list = ComboOrder.convertFromJSONArray(response.getData());
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
		logger.info("结束调用API查询会员跟单信息");
		return map;
	}
	
	@Override
	public Map<String, Object> queryComboOrderInfo(Long comboOrderId, Long comboId, Long uid, LotteryType lotteryType,
			FinishComboStatus finishComboStatus, PageBean pageBean)	throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询套餐订单详情");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_GET_COMBO_ORDER_DETAIL);
		if (comboId != null && comboId != 0) {
			request.setParameter(ComboOrderDetail.QUERY_COMBO_ID, comboId + "");
		}
		if (comboOrderId != null && comboOrderId != 0) {
			request.setParameter(ComboOrderDetail.QUERY_ID, comboOrderId + "");
		}
		if (uid != null && uid != 0) {
			request.setParameter(ComboOrderDetail.QUERY_UID, uid + "");
		}
		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(ComboOrderDetail.QUERY_LOTTERY_TYPE, lotteryType.getValue() + "");
		}
		if (finishComboStatus != null && finishComboStatus.getValue() != FinishComboStatus.ALL.getValue()) {
			request.setParameter(ComboOrderDetail.QUERY_STATUS, finishComboStatus.getValue() + "");
		}
		request.addOrder(ComboOrderDetail.ORDER_CREATED_TIME, ApiConstant.API_REQUEST_ORDER_DESC);
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API查询查询套餐订单详情失败");
			throw new ApiRemoteCallFailedException("API查询查询套餐订单详情失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API查询查询套餐订单详情请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API查询查询套餐订单详情请求出错");
		}
		List<ComboOrderDetail> list = ComboOrderDetail.convertFromJSONArray(response.getData());
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
		logger.info("结束调用API套餐订单详情信息");
		return map;
	}

	@Override
	public Map<String, Object> queryComboOrderRecord(Long comboOrderId, Long comboId, Long uid, 
			LotteryType lotteryType, String phase, Long planId, Long orderId, PageBean pageBean) 
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询套餐订单执行记录");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_GET_COMBO_ORDER_RECORD);
		if (comboId != null && comboId != 0) {
			request.setParameter(ComboOrderDetailRecord.QUERY_COMBO_ID, comboId + "");
		}
		if (comboOrderId != null && comboOrderId != 0) {
			request.setParameter(ComboOrderDetailRecord.QUERY_COMBO_ORDER_ID, comboOrderId + "");
		}
		if (planId != null && planId != 0) {
			request.setParameter(ComboOrderDetailRecord.QUERY_PLAN_ID, planId + "");
		}
		if (orderId != null && orderId != 0) {
			request.setParameter(ComboOrderDetailRecord.QUERY_ORDER_ID, orderId + "");
		}
		if (uid != null && uid != 0) {
			request.setParameter(ComboOrderDetailRecord.QUERY_UID, uid + "");
		}
		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(ComboOrderDetailRecord.QUERY_LOTTERY_TYPE, lotteryType.getValue() + "");
		}
		if (phase != null && !"".equals(phase) && !"-1".equals(phase)) {
			request.setParameter(ComboOrderDetailRecord.QUERY_PHASE, phase);
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API查询查询套餐订单执行记录失败");
			throw new ApiRemoteCallFailedException("API查询查询套餐订单执行记录失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API查询查询套餐订单执行记录请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API查询查询套餐订单执行记录请求出错");
		}
		List<ComboOrderDetailRecord> list = ComboOrderDetailRecord.convertFromJSONArray(response.getData());
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
		logger.info("结束调用API套餐订单详情信息");
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
