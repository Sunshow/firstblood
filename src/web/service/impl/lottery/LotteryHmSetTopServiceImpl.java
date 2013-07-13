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
import com.lehecai.admin.web.enums.LotteryAmountType;
import com.lehecai.admin.web.service.lottery.LotteryHmSetTopService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PlanStatus;
import com.lehecai.core.lottery.PlanType;


public class LotteryHmSetTopServiceImpl implements LotteryHmSetTopService {
	private final Logger logger = LoggerFactory.getLogger(LotteryHmSetTopServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	private MemberService memberService;
	
	public Map<String, Object> getResult(String userName,
			String planId, LotteryType lotteryType, String phase, 
			Integer amount, Date rbeginDate, Date rendDate,
			Date lbeginDate, Date lendDate, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API根据置顶值和进度查询合买方案");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_SYNDICATE_LIST_QUERY);
		request.setParameter(Plan.QUERY_PLAN_TYPE, PlanType.HM.getValue()+"");
		request.setParameter(Plan.QUERY_PLAN_STATUS, PlanStatus.RECRUITING.getValue()+"");
		if (userName != null && !"".equals(userName)) {
			Member member = memberService.get(userName);
			if (member != null) {				
				request.setParameter(Plan.QUERY_UID, member.getUid() + "");
			}else{
				request.setParameter(Plan.QUERY_UID, "");
			}
		}
		if (planId != null && !"".equals(planId)) {
			request.setParameter(Plan.QUERY_ID, planId);
		}
		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_LOTTERY_TYPE, lotteryType.getValue()+"");
		}
		if (phase != null && !"-1".equals(phase)) {
			request.setParameter(Plan.QUERY_PHASE, phase);
		}
		if (amount != null && amount != -1) {
			if (amount == LotteryAmountType.F0T50.getValue()) {
				request.setParameterBetween(Plan.ORDER_AMOUNT, "0", "50");
			}
			if (amount == LotteryAmountType.F50T100.getValue()) {
				request.setParameterBetween(Plan.ORDER_AMOUNT, "50", "100");
			}
			if (amount == LotteryAmountType.F100T200.getValue()) {
				request.setParameterBetween(Plan.ORDER_AMOUNT, "100", "200");
			}
			if (amount == LotteryAmountType.F200T500.getValue()) {
				request.setParameterBetween(Plan.ORDER_AMOUNT, "200", "500");
			}
			if (amount == LotteryAmountType.F500T1000.getValue()) {
				request.setParameterBetween(Plan.ORDER_AMOUNT, "500", "1000");
			}
			if (amount == LotteryAmountType.F1000T10000.getValue()) {
				request.setParameterBetween(Plan.ORDER_AMOUNT, "1000", "10000");
			}
			if (amount == LotteryAmountType.F10000T100000.getValue()) {
				request.setParameterBetween(Plan.ORDER_AMOUNT, "10000", "100000");
			}
			if (amount == LotteryAmountType.F100000.getValue()) {
				request.setParameter(Plan.ORDER_AMOUNT, "100000", ApiConstant.API_OP_BETWEEN_START);
			}
		}
		if (rbeginDate != null) {
			request.setParameterBetween(Plan.QUERY_CREATED_TIME, DateUtil.formatDate(rbeginDate,DateUtil.DATETIME),null);
		}
		if (rendDate != null) {
			request.setParameterBetween(Plan.QUERY_CREATED_TIME, null,DateUtil.formatDate(rendDate,DateUtil.DATETIME));
		}
		if (lbeginDate != null) {
			request.setParameterBetween(Plan.QUERY_DEAD_LINE, DateUtil.formatDate(lbeginDate,DateUtil.DATETIME),null);
		}
		if (lendDate != null) {
			request.setParameterBetween(Plan.QUERY_DEAD_LINE, null,DateUtil.formatDate(lendDate,DateUtil.DATETIME));
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
			logger.error("API获取方案数据失败");
			throw new ApiRemoteCallFailedException("API获取方案数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取方案数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取方案数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取方案数据为空, message={}", response.getMessage());
			return null;
		}
		List<Plan> list = Plan.convertFromJSONArray(response.getData());
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
	
	
	public Plan get(String id) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询方案");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_DETAIL_QUERY);
		request.setParameter(Plan.QUERY_ID, id);
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取方案数据失败");
			throw new ApiRemoteCallFailedException("API获取方案数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取方案数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取方案数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取方案数据为空, message={}", response.getMessage());
			return null;
		}
		List<Plan> plans = Plan.convertFromJSONArray(response.getData());
		if (plans != null && plans.size() > 0) {			
			return plans.get(0);
		}
		return null;
	}
	
	public boolean updateTopStatus(Plan plan, int top) throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新置顶值");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_EXT_UPDATE);
		request.setParameter(Plan.QUERY_ID, plan.getId());
		
		request.setParameterForUpdate(Plan.SET_TOP, top + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API更新置顶值失败");
			throw new ApiRemoteCallFailedException("API更新置顶值失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更新置顶值请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}

	public Map<String, Object> lotteryPlanStatistics(String userName,
			String planId, LotteryType lotteryTypeId, String phase,
			Integer amount,Date rbeginDate,
			Date rendDate, Date lbeginDate, Date lendDate) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询方案统计");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_PLAN_STATISTICS);
		request.setParameter(Plan.QUERY_PLAN_TYPE, PlanType.HM.getValue()+"");
		request.setParameter(Plan.QUERY_PLAN_STATUS, PlanStatus.RECRUITING.getValue()+"");
		
		if (amount != null && amount != -1) {
			if (amount == LotteryAmountType.F0T50.getValue()) {
				request.setParameterBetween(Plan.ORDER_AMOUNT, "0", "50");
			}
			if (amount == LotteryAmountType.F50T100.getValue()) {
				request.setParameterBetween(Plan.ORDER_AMOUNT, "50", "100");
			}
			if (amount == LotteryAmountType.F100T200.getValue()) {
				request.setParameterBetween(Plan.ORDER_AMOUNT, "100", "200");
			}
			if (amount == LotteryAmountType.F200T500.getValue()) {
				request.setParameterBetween(Plan.ORDER_AMOUNT, "200", "500");
			}
			if (amount == LotteryAmountType.F500T1000.getValue()) {
				request.setParameterBetween(Plan.ORDER_AMOUNT, "500", "1000");
			}
			if (amount == LotteryAmountType.F1000T10000.getValue()) {
				request.setParameterBetween(Plan.ORDER_AMOUNT, "1000", "10000");
			}
			if (amount == LotteryAmountType.F10000T100000.getValue()) {
				request.setParameterBetween(Plan.ORDER_AMOUNT, "10000", "100000");
			}
			if (amount == LotteryAmountType.F100000.getValue()) {
				request.setParameter(Plan.ORDER_AMOUNT, "100000", ApiConstant.API_OP_BETWEEN_START);
			}
		}
		if (userName != null && !"".equals(userName)) {
			Member member = null;
			try {
				member = memberService.get(userName);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage());
			}
			if (member != null) {				
				request.setParameter(Plan.QUERY_UID, member.getUid() + "");
			}else{
				request.setParameter(Plan.QUERY_UID, "");
			}
		}
		if (planId != null && !"".equals(planId)) {
			request.setParameter(Plan.QUERY_ID, planId);
		}
		if (lotteryTypeId != null && lotteryTypeId.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(Plan.QUERY_LOTTERY_TYPE, lotteryTypeId.getValue() + "");
		}
		if (phase != null && !"-1".equals(phase)) {
			request.setParameter(Plan.QUERY_PHASE, phase);
		}
		if (rbeginDate != null) {
			request.setParameterBetween(Plan.QUERY_CREATED_TIME, DateUtil.formatDate(rbeginDate,DateUtil.DATETIME),null);
		}
		if (rendDate != null) {
			request.setParameterBetween(Plan.QUERY_CREATED_TIME, null,DateUtil.formatDate(rendDate,DateUtil.DATETIME));
		}
		if (lbeginDate != null) {
			request.setParameterBetween(Plan.QUERY_DEAD_LINE, DateUtil.formatDate(lbeginDate,DateUtil.DATETIME),null);
		}
		if (lendDate != null) {
			request.setParameterBetween(Plan.QUERY_DEAD_LINE, null,DateUtil.formatDate(lendDate,DateUtil.DATETIME));
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取方案统计数据失败");
			throw new ApiRemoteCallFailedException("API获取方案统计数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取方案统计数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取方案统计数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取方案统计数据为空, message={}", response.getMessage());
			return null;
		}
		Map<String, Object> map = new HashMap<String,Object>();
		JSONObject jsonObj = response.getData().getJSONObject(0);
		
		if (jsonObj != null && !jsonObj.isNullObject() && jsonObj.get("amount") != null) {
			map.put(Global.API_MAP_KEY_AMOUNT, jsonObj.get("amount"));
		} else {
			map.put(Global.API_MAP_KEY_AMOUNT, "0");
		}
	
		if (jsonObj != null && !jsonObj.isNullObject() && jsonObj.get("prize_posttax") != null) {
			map.put(Global.API_MAP_KEY_POSTTAXPRIZE, jsonObj.get("prize_posttax"));
		} else {
			map.put(Global.API_MAP_KEY_POSTTAXPRIZE, "0");
		}	
		
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

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
}
