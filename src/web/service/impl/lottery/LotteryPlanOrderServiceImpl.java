package web.service.impl.lottery;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.LotteryPlanOrderService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.PlanOrder;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PlanOrderStatus;
import com.lehecai.core.lottery.PlanOrderType;
import com.lehecai.core.lottery.PrizeStatus;


public class LotteryPlanOrderServiceImpl implements LotteryPlanOrderService {
	private final Logger logger = LoggerFactory.getLogger(LotteryPlanOrderServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	private MemberService memberService;
	
	@Override
	public Map<String, Object> getResult(String userName,
			String orderId, String planId, PlanOrderStatus orderStatus, PrizeStatus prizeStatus, PlanOrderType orderType, Date rbeginDate, Date rendDate,
			Date lbeginDate, Date lendDate, String orderStr, String orderView, LotteryType lotteryType, String phase, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询订单数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_ORDER_LIST);
		
		if (userName != null && !"".equals(userName)) {
			Member member = memberService.get(userName);
			if (member != null) {				
				request.setParameter(PlanOrder.QUERY_UID, member.getUid() + "");
			} else {
				request.setParameter(PlanOrder.QUERY_UID, "");
			}
		}
		if (orderId != null && !"".equals(orderId)) {
			request.setParameter(PlanOrder.QUERY_ID, orderId);
		}
		if (planId != null && !"".equals(planId)) {
			request.setParameter(PlanOrder.QUERY_PLAN_ID, planId);
		}
		if (orderStatus != null && orderStatus.getValue() != PlanOrderStatus.ALL.getValue()) {
			request.setParameter(PlanOrder.QUERY_ORDER_STATUS, orderStatus.getValue() +"");
		}
		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(PlanOrder.QUERY_LOTTERY_TYPE, lotteryType.getValue() +"");
		}
		if (phase != null && !"".equals(phase) && !"-1".equals(phase)) {
			request.setParameter(PlanOrder.QUERY_PHASE, phase);
		}
		if (prizeStatus != null && prizeStatus.getValue() != PrizeStatus.ALL.getValue()) {
			request.setParameter(PlanOrder.QUERY_PRIZE_STATUS, prizeStatus.getValue()+"");
		}
		if (orderType != null && orderType.getValue() != PlanOrderType.ALL.getValue()) {
			request.setParameter(PlanOrder.QUERY_ORDER_TYPE, orderType.getValue()+"");
		}
		if (rbeginDate != null) {
			request.setParameterBetween(PlanOrder.QUERY_CREATED_TIME, DateUtil.formatDate(rbeginDate,DateUtil.DATETIME),null);
		}
		if (rendDate != null) {
			request.setParameterBetween(PlanOrder.QUERY_CREATED_TIME, null,DateUtil.formatDate(rendDate,DateUtil.DATETIME));
		}
		if (lbeginDate != null) {
			request.setParameterBetween(PlanOrder.QUERY_PRIZE_TIME, DateUtil.formatDate(lbeginDate,DateUtil.DATETIME),null);
		}
		if (lendDate != null) {
			request.setParameterBetween(PlanOrder.QUERY_PRIZE_TIME, null,DateUtil.formatDate(lendDate,DateUtil.DATETIME));
		}
		if (orderStr != null && !"".equals(orderStr) && orderView != null && !"".equals(orderView)) {		
			request.addOrder(orderStr,orderView);
		}
		if (pageBean != null) {		
			request.setPage(pageBean.getPage());
			//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取订单数据失败");
			throw new ApiRemoteCallFailedException("API获取订单数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取订单数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取订单数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取订单数据为空, message={}", response.getMessage());
			return null;
		}
		List<PlanOrder> list = PlanOrder.convertFromJSONArray(response.getData());
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
	public PlanOrder get(String id) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询订单详情");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_ORDER_DETAIL);
		request.setParameter(PlanOrder.QUERY_ID, id);
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取订单详情失败");
			throw new ApiRemoteCallFailedException("API获取订单详情失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取订单详情请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取订单详情请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取订单详情为空, message={}", response.getMessage());
			return null;
		}
		List<PlanOrder> planOrders = PlanOrder.convertFromJSONArray(response.getData());
		if (planOrders != null && planOrders.size() > 0) {			
			return planOrders.get(0);
		}
		return null;
	}

	@Override
	public Map<String, Object> lotteryPlanOrderStatistics(String userName,
			String orderId, String planId, PlanOrderStatus orderStatus,
			PrizeStatus prizeStatus, PlanOrderType orderType, Date rbeginDate, Date rendDate,
			Date lbeginDate, Date lendDate, LotteryType lotteryType, String phase) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询订单统计");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_ORDER_STATISTICS);
		
		if (userName != null && !"".equals(userName)) {
			Member member = null;
			try {
				member = memberService.get(userName);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage());
			}
			if (member != null) {				
				request.setParameter(PlanOrder.QUERY_UID, member.getUid() + "");
			} else {
				request.setParameter(PlanOrder.QUERY_UID, "");
			}
		}
		if (orderId != null && !"".equals(orderId)) {
			request.setParameter(PlanOrder.QUERY_ID, orderId);
		}
		if (planId != null && !"".equals(planId)) {
			request.setParameter(PlanOrder.QUERY_PLAN_ID, planId);
		}
		if (orderStatus != null && orderStatus.getValue() != PlanOrderStatus.ALL.getValue()) {
			request.setParameter(PlanOrder.QUERY_ORDER_STATUS, orderStatus.getValue()+"");
		}
		if (prizeStatus != null && prizeStatus.getValue() != PrizeStatus.ALL.getValue()) {
			request.setParameter(PlanOrder.QUERY_PRIZE_STATUS, prizeStatus.getValue()+"");
		}
		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(PlanOrder.QUERY_LOTTERY_TYPE, lotteryType.getValue() +"");
		}
		if (phase != null && !"".equals(phase) && !"-1".equals(phase)) {
			request.setParameter(PlanOrder.QUERY_PHASE, phase);
		}
		if (orderType != null && orderType.getValue() != PlanOrderType.ALL.getValue()) {
			request.setParameter(PlanOrder.QUERY_ORDER_TYPE, orderType.getValue()+"");
		}
		if (rbeginDate != null) {
			request.setParameterBetween(PlanOrder.QUERY_CREATED_TIME, DateUtil.formatDate(rbeginDate,DateUtil.DATETIME),null);
		}
		if (rendDate != null) {
			request.setParameterBetween(PlanOrder.QUERY_CREATED_TIME, null,DateUtil.formatDate(rendDate,DateUtil.DATETIME));
		}
		if (lbeginDate != null) {
			request.setParameterBetween(PlanOrder.QUERY_PRIZE_TIME, DateUtil.formatDate(lbeginDate,DateUtil.DATETIME),null);
		}
		if (lendDate != null) {
			request.setParameterBetween(PlanOrder.QUERY_PRIZE_TIME, null,DateUtil.formatDate(lendDate,DateUtil.DATETIME));
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取订单统计数据失败");
			throw new ApiRemoteCallFailedException("API获取订单统计数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取订单统计数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取订单统计数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.error("API获取订单统计数据为空, message={}", response.getMessage());
			return null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject jsonObj = response.getData().getJSONObject(0);
		
		if (jsonObj != null && !jsonObj.isNullObject() && jsonObj.getString("amount") != null) {
			map.put(Global.API_MAP_KEY_AMOUNT, jsonObj.getString("amount"));
		} else {
			map.put(Global.API_MAP_KEY_AMOUNT, "0");
		}
		if (jsonObj != null && !jsonObj.isNullObject() && jsonObj.getString("prize_posttax") != null) {
			map.put(Global.API_MAP_KEY_POSTTAXPRIZE, jsonObj.getString("prize_posttax"));
		} else {
			map.put(Global.API_MAP_KEY_POSTTAXPRIZE, "0");
		}
		
		return map;
	}
	
	@Override
	public boolean counterResetOrderNotPaid(String uid)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API重置未支付订单计数");
		if (uid == null || uid.isEmpty()) {
			return true;
		}
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_COUNTER_RESET_ORDER_NOTPAID);
		
		request.setParameter(PlanOrder.QUERY_UID, uid);

		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用API重置未支付订单计数失败");
			throw new ApiRemoteCallFailedException("调用API重置未支付订单计数失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API重置未支付订单计数请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		
		return true;
	}

	@Override
	public boolean updatePlanOrderStatus(String oid, PlanOrderStatus status)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API重置订单状态");
		if (oid == null || oid.isEmpty()) {
			return true;
		}
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_ORDER_UPDATE);
		
		request.setParameter(PlanOrder.QUERY_ID, oid);
		if (status != null) {
			request.setParameterForUpdate(PlanOrder.SET_ORDER_STATUS, status.getValue() + "");
		}

		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API重置订单状态失败");
			throw new ApiRemoteCallFailedException("API重置订单状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API重置订单状态请求出错, rc={}, message={}", response.getCode(), response.getMessage());
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

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
}
