package web.service.impl.member;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.member.OperationService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.OperationLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.OperationStatus;
import com.lehecai.core.lottery.OperationType;


public class OperationServiceImpl implements OperationService {
	private final Logger logger = LoggerFactory.getLogger(OperationServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private MemberService memberService;
	@Override
	public Map<String, Object> getResult(OperationType operationType,
			OperationStatus operationStatus, String username, Long uid,
			Date beginDate, Date endDate, Long sourceId,
			boolean distinctMember, String orderStr, String orderView,
			PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询操作日志数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_OPERATION_LOG_QUERY);
		
		if(operationType != null && operationType.getValue() != OperationType.ALL.getValue()){
			request.setParameter(OperationLog.QUERY_OPERATION_TYPE, operationType.getValue()+"");
		}
		if(operationStatus != null && operationStatus.getValue() != OperationStatus.ALL.getValue()){
			request.setParameter(OperationLog.QUERY_STATUS, operationStatus.getValue()+"");
		}
		//通过用户名获取用户ID异常!
		if(username != null && !"".equals(username)){
			Long uid2 = null;
			try {
				uid2 = memberService.getIdByUserName(username);
			} catch (Exception e) {
				logger.error("API根据用户名获取用户ID异常!{}", e.getMessage());
			}
			if(uid2 != null && uid2.longValue() != 0){
				request.setParameter(OperationLog.QUERY_UID, String.valueOf(uid2.longValue()));
			} else {
				logger.info("用户名不存在！返回空记录！");
				return null;
			}
		}
		if(uid != null && uid != 0){
			request.setParameter(OperationLog.QUERY_UID, uid.toString());
		}
		if(beginDate != null){
			request.setParameterBetween(OperationLog.QUERY_TIMELINE, DateUtil.formatDate(beginDate,DateUtil.DATETIME),null);
		}
		if(endDate != null){
			request.setParameterBetween(OperationLog.QUERY_TIMELINE, null,DateUtil.formatDate(endDate,DateUtil.DATETIME));
		}
		if(sourceId != null){
			request.setParameter(OperationLog.QUERY_SOURCE_ID, sourceId.toString());
		}
		if(distinctMember){
			request.setParameter(OperationLog.QUERY_DISTINCT_USER, "1");
		}
		if(orderStr != null && !"".equals(orderStr) && orderView != null && !"".equals(orderView)){		
			request.addOrder(orderStr,orderView);
		}
		if(pageBean != null){		
			request.setPage(pageBean.getPage());
			//request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取操作日志数据失败");
			throw new ApiRemoteCallFailedException("API获取操作日志数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取操作日志数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取操作日志数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取操作日志数据为空, message={}", response.getMessage());
			return null;
		}
		List<OperationLog> list = OperationLog.convertFromJSONArray(response.getData());
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
