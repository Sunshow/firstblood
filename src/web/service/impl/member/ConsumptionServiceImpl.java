package web.service.impl.member;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.ConsumptionService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.ConsumptionLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.TransType;
import com.lehecai.core.lottery.WalletType;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class ConsumptionServiceImpl implements ConsumptionService {
	private final Logger logger = LoggerFactory.getLogger(ConsumptionServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private MemberService memberService;
	
	/**
	 * 多条件分页查询钱包流水
	 * @param lotteryType	彩票种类
	 * @param transType		交易类型
	 * @param username		账户名
	 * @param beginDate		交易起始时间
	 * @param endDate		交易终止时间
	 * @param logId			钱包流水号
	 * @param orderId		订单编号
	 * @param planId		方案编号
	 * @param orderStr		排序字段
	 * @param orderView		排序方式
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	@Override
	public Map<String, Object> getResult(LotteryType lotteryType,
			TransType transType, List<WalletType> walletTypeList, String username, Date beginDate, Date endDate,
			String logId, String orderId, String planId, String orderStr,
			String orderView, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员钱包流水数据列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CONSUMPTION_LOG_QUERY);
		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(ConsumptionLog.QUERY_LOTTERY_TYPE, lotteryType.getValue()+"");
		}
		if (transType != null && transType.getValue() != TransType.ALL.getValue()) {
			request.setParameter(ConsumptionLog.QUERY_TRANS_TYPE, transType.getValue()+"");
		}
		if (walletTypeList != null && !walletTypeList.isEmpty()) {
			List<String> walletTypeStrList = new ArrayList<String>();
			for (WalletType walletType : walletTypeList) {
				walletTypeStrList.add(String.valueOf(walletType.getValue()));
			}
			request.setParameterIn(ConsumptionLog.QUERY_WALLET_TYPE, walletTypeStrList);
		}
		if (username != null && !"".equals(username)) {
			Long uid = null;
			try {
				uid = memberService.getIdByUserName(username);
			} catch (Exception e) {
				logger.error("API根据用户名获取用户ID异常!{}", e.getMessage());
			}
			if (uid != null && uid.longValue() != 0) {
				request.setParameter(ConsumptionLog.QUERY_UID, String.valueOf(uid.longValue()));
			} else {
				logger.info("用户名不存在!返回空记录!");
				return null;
			}
		}
		if (beginDate != null) {
			request.setParameterBetween(ConsumptionLog.QUERY_CREATED_TIME, DateUtil.formatDate(beginDate,DateUtil.DATETIME),null);
		}
		if (endDate != null) {
			request.setParameterBetween(ConsumptionLog.QUERY_CREATED_TIME, null,DateUtil.formatDate(endDate,DateUtil.DATETIME));
		}
		if (StringUtils.isNotBlank(logId)) {
			request.setParameter(ConsumptionLog.QUERY_LOG_ID, logId);
		} else {
            request.setParameterIdRange(ConsumptionLog.QUERY_LOG_ID, beginDate, endDate);
        }
		if (orderId != null && !"".equals(orderId)) {
			request.setParameter(ConsumptionLog.QUERY_ORDER_ID, orderId);
		}
		if (planId != null && !"".equals(planId)) {
			request.setParameter(ConsumptionLog.QUERY_PLAN_ID, planId);
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
			logger.error("API获取钱包流水数据失败");
			throw new ApiRemoteCallFailedException("API获取钱包流水数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取钱包流水数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取钱包流水数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取钱包流水数据为空, message={}", response.getMessage());
			return null;
		}
		List<ConsumptionLog> list = ConsumptionLog.convertFromJSONArray(response.getData());
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
	
	/**
	 * 多条件分页统计钱包流水
	 * @param lotteryType	彩票种类
	 * @param transType		交易类型
	 * @param username		账户名
	 * @param beginDate		交易起始时间
	 * @param endDate		交易终止时间
	 * @param logId			钱包流水号
	 * @param orderId		订单编号
	 * @param planId		方案编号
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	@Override
	public Map<String, Object> getConsumptionStatistics(LotteryType lotteryType,
			TransType transType, List<WalletType> walletTypeList, String username, Date beginDate, Date endDate,
			String logId, String orderId, String planId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员钱包流水统计");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CONSUMPTION_STATS_SUM);

		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(ConsumptionLog.QUERY_LOTTERY_TYPE, lotteryType.getValue() + "");//添加彩票种类查询条件
		}
		if (transType != null && transType.getValue() != TransType.ALL.getValue()) {
			request.setParameter(ConsumptionLog.QUERY_TRANS_TYPE, transType.getValue() + "");//添加交易类型查询条件
		}
		if (walletTypeList != null && !walletTypeList.isEmpty()) {
			List<String> walletTypeStrList = new ArrayList<String>();
			for (WalletType walletType : walletTypeList) {
				walletTypeStrList.add(String.valueOf(walletType.getValue()));
			}
			request.setParameterIn(ConsumptionLog.QUERY_WALLET_TYPE, walletTypeStrList);
		}
		if (username != null && !"".equals(username)) {
			Long uid = null;
			try {
				uid = memberService.getIdByUserName(username);
			} catch (Exception e) {
				logger.error("API根据用户名获取用户ID异常!{}", e.getMessage());
			}
			if (uid != null && uid.longValue() != 0) {
				logger.info(username+"对应的用户编号"+uid);
				request.setParameter(ConsumptionLog.QUERY_UID, String.valueOf(uid.longValue()));//添加用户编号查询条件
			} else {
				logger.info("用户名不存在!返回空记录!");
				return null;
			}
		}
		if (beginDate != null) {
			request.setParameterBetween(ConsumptionLog.QUERY_CREATED_TIME, 
					DateUtil.formatDate(beginDate,DateUtil.DATETIME),null);//添加交易起始时间查询条件
		}
		if (endDate != null) {
			request.setParameterBetween(ConsumptionLog.QUERY_CREATED_TIME, null,
					DateUtil.formatDate(endDate,DateUtil.DATETIME));//添加交易终止时间查询条件
		}
        if (StringUtils.isNotBlank(logId)) {
            request.setParameter(ConsumptionLog.QUERY_LOG_ID, logId);
        } else {
            request.setParameterIdRange(ConsumptionLog.QUERY_LOG_ID, beginDate, endDate);
        }
		if (orderId != null && !"".equals(orderId)) {
			request.setParameter(ConsumptionLog.QUERY_ORDER_ID, orderId);//添加订单编号查询条件
		}
		if (planId != null && !"".equals(planId)) {
			request.setParameter(ConsumptionLog.QUERY_PLAN_ID, planId);//添加方案编号查询条件
		}

		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取钱包流水统计金额失败");
			throw new ApiRemoteCallFailedException("API获取钱包流水统计金额失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取钱包流水统计金额请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取钱包流水统计金额请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取钱包流水统计金额数据为空, message={}", response.getMessage());
			return null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject jsonObj = response.getData().getJSONObject(0);
		
		if (jsonObj != null && !jsonObj.isNullObject() && jsonObj.get("amount") != null) {
			map.put(Global.API_MAP_KEY_AMOUNT, jsonObj.get("amount"));
		} else {
			map.put(Global.API_MAP_KEY_AMOUNT, "0");
		}
		return map;
	}
	
	@Override
	public ConsumptionLog get(String id) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员钱包流水数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CONSUMPTION_LOG_QUERY);
		request.setParameter(ConsumptionLog.QUERY_LOG_ID, id);
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取钱包流水数据失败");
			throw new ApiRemoteCallFailedException("API获取钱包流水数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取钱包流水数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取钱包流水数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取钱包流水数据为空, message={}", response.getMessage());
			return null;
		}
		List<ConsumptionLog> consumptionLogs = ConsumptionLog.convertFromJSONArray(response.getData());
		if (consumptionLogs != null && consumptionLogs.size() > 0) {			
			return consumptionLogs.get(0);
		}
		return null;
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}
	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
}
