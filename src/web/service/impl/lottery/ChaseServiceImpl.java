package web.service.impl.lottery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.ChaseService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.Chase;
import com.lehecai.core.api.lottery.ChaseDetail;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ChaseStatus;
import com.lehecai.core.lottery.ChaseType;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.StopChaseType;

/**
 * 追号业务逻辑层实现类
 * @author yanweijie
 *
 */
public class ChaseServiceImpl implements ChaseService {
	private final Logger logger = LoggerFactory.getLogger(ChaseServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	private MemberService memberService;
	
	@Override
	public Map<String, Object> getResult(String chaseId, String planId, String username,
			LotteryType lotteryType, ChaseStatus chaseStatus, StopChaseType stopChaseType, ChaseType chaseType, Date beginCreateTime, Date endCreateTime,
			String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询追号数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CHASE_QUERY);
		
		if (chaseId != null && !"".equals(chaseId)) {
			request.setParameter(Chase.QUERY_ID, chaseId);
		}
		if (planId != null && !"".equals(planId)) {
			request.setParameter(Chase.QUERY_PLAN_ID, planId);
		}
		if (username != null && !"".equals(username)) {
			Member member = memberService.get(username);
			if (member != null) {				
				request.setParameter(Plan.QUERY_UID, member.getUid() + "");
			}else{
				return null;
			}
		}
		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(Chase.QUERY_LOTTERY_TYPE, lotteryType.getValue()+"");
		}
		if (chaseStatus != null && chaseStatus.getValue() != ChaseStatus.ALL.getValue()) {
			request.setParameter(Chase.QUERY_CHASE_STATUS, chaseStatus.getValue()+"");
		}
		if (stopChaseType != null && stopChaseType.getValue() != StopChaseType.ALL.getValue()) {
			request.setParameter(Chase.QUERY_STOPCHASE_TYPE, stopChaseType.getValue()+"");
		}
		if (chaseType != null && chaseType.getValue() != ChaseType.ALL.getValue()) {
			request.setParameter(Chase.QUERY_CHASE_TYPE, chaseType.getValue()+"");
		}
		
		if (beginCreateTime != null) {
            request.setParameterBetween(Chase.ORDER_CREATED_TIME, DateUtil.formatDate(beginCreateTime, DateUtil.DATETIME), null);
        }
		 if (endCreateTime != null) {
            request.setParameterBetween(Chase.QUERY_CREATED_TIME, null, DateUtil.formatDate(endCreateTime, DateUtil.DATETIME));
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
			logger.error("API获取追号数据失败");
			throw new ApiRemoteCallFailedException("API获取追号数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取追号数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API获取追号数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取追号数据为空, message={}", response.getMessage());
			return null;
		}
		List<Chase> list = Chase.convertFromJSONArray(response.getData());
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
	
	private boolean recycle(List<String> chaseIdList) throws ApiRemoteCallFailedException {
		logger.info("进入调用API回收追号数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CHASE_RECYCLE);
		
		request.setParameterIn(Chase.QUERY_ID, chaseIdList);
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API回收追号数据失败");
			throw new ApiRemoteCallFailedException("API回收追号数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API回收追号数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	@Override
	public boolean cancel(String chaseId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API取消追号");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CHASE_STOP);
		request.setParameter(Chase.QUERY_ID, chaseId);
		request.setParameter(Chase.QUERY_CHASE_STATUS, ChaseStatus.CANCELLED.getValue() + "");
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API取消追号失败");
			return false;
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API取消追号请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		List<String> list = new ArrayList<String>();
		list.add(chaseId);
		this.recycle(list);
		return true;
	}
	
	@Override
	public ResultBean batchCancel(String refundStr) throws ApiRemoteCallFailedException {
		logger.info("进入调用API批量取消追号");
		ResultBean rb = new ResultBean();
		String successIds = "";
		String errorIds = "";
		if (!StringUtils.isEmpty(refundStr)) {
			String [] str = refundStr.split(",");		
			for (int i = 0; i < str.length; i++) {
				String idStr = str[i];			
				boolean flag = false;
				try {
					flag = cancel(idStr);
				} catch (ApiRemoteCallFailedException e) {
					logger.error(e.getMessage(), e);
				}	
				if (flag) {
					logger.info("编号[{}]取消追号成功", idStr);
					successIds += idStr + ",\n";
				} else {
					logger.info("编号[{}]取消追号失败", idStr);
					errorIds += idStr + ",\n";
				}
			}
		}
		if (StringUtils.isEmpty(errorIds)) {
			rb.setResult(true);
		} else {
			rb.setResult(false);
		}
		rb.setMessage("成功[" + successIds + "]" + "失败[" + errorIds + "]");
		return rb;
	}

	@Override
	public List<ChaseDetail> listDetail(String chaseId)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询追号详情数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CHASE_DETAIL_QUERY);
		
		if (chaseId != null && !"".equals(chaseId)) {
			request.setParameter(ChaseDetail.QUERY_CHASE_ID, chaseId);
		}		
		request.setPage(1);
		//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
		request.setPagesize(100);

		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取追号详情数据失败");
			throw new ApiRemoteCallFailedException("API获取追号详情数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取追号详情数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API获取追号详情数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取追号详情数据为空, message={}", response.getMessage());
			return null;
		}
		List<ChaseDetail> list = ChaseDetail.convertFromJSONArray(response.getData());
		
		return list;
	}
	
	@Override
	public Chase get(String chaseId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询追号数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CHASE_QUERY);

		request.setParameter(Chase.QUERY_ID, chaseId);

		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取追号数据失败");
			throw new ApiRemoteCallFailedException("API获取订单数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取追号数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API获取追号数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取追号数据为空, message={}", response.getMessage());
			return null;
		}
		return Chase.convertFromJSONObject((JSONObject)response.getData().get(0));
	}
	
	@Override
	public String parseContent(LotteryType lotteryType, String content)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API解析追号方案内容");
		String parseContent = "";
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CONVERT_CONTENT_TO_READ);
		request.setParameter(Chase.QUERY_LOTTERY_TYPE, lotteryType.getValue()+"");
		if (content != null && !content.isEmpty()) {
			request.setParameter(Chase.QUERY_CONTENT, content);
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API解析追号方案内容失败");
			throw new ApiRemoteCallFailedException("API解析追号方案内容失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API解析追号方案内容请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API解析追号方案内容请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API解析追号方案内容为空, message={}", response.getMessage());
			return parseContent;
		}
		
		JSONArray data = response.getData();
		
		if (data != null) {
			parseContent = (String) data.get(0);
		}
		
		return parseContent;
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

	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}
}
