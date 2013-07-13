package web.service.impl.event;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.event.EventService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.event.EventInfo;
import com.lehecai.core.api.event.EventLog;
import com.lehecai.core.api.event.EventPrize;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.event.EventInfoStatus;
import com.lehecai.core.event.EventLogStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.PlatformType;

/**
 * 抽奖活动业务逻辑层实现类
 * @author yanweijie
 *
 */
public class EventServiceImpl implements EventService {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private ApiRequestService apiRequestService;
	private MemberService memberService;
	
	/**
	 * 分页查询所有活动信息
	 * @param pageBean 分页信息
	 */
	public Map<String, Object> findEventInfoList (EventInfoStatus eventInfoStatus, PageBean pageBean) 
		throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询活动数据");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EVENT_INFO_SEARCH);//设置api地址
		request.addOrder(EventInfo.ORDER_EVENT_ID, ApiConstant.API_REQUEST_ORDER_DESC);//按照活动编码(event_id)降序排列
		if (pageBean != null && pageBean.isPageFlag()) {	
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		if (eventInfoStatus != null && eventInfoStatus.getValue() != EventInfoStatus.ALL.getValue()) {
			request.setParameter(EventInfo.QUERY_EVENT_STATUS, eventInfoStatus.getValue() + "");
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取活动数据失败");
			throw new ApiRemoteCallFailedException("API获取活动数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取活动数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取活动数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取活动数据为空, message={}", response.getMessage());
			return null;
		}

		List<EventInfo> eventInfoList = EventInfo.convertFromJSONArray(response.getData());
		
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
		map.put(Global.API_MAP_KEY_LIST, eventInfoList);
		
		return map;
	}
	
	@Override
	public Map<String, Object> findEventInfoListByCondition(
			Date createDateFrom, Date createDateTo, Date beginDateFrom,
			Date beginDateTo, Date endDateFrom, Date endDateTo,
			EventInfoStatus eventInfoStatus, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API条件查询活动数据");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EVENT_INFO_SEARCH);//设置api地址
		request.addOrder(EventInfo.ORDER_EVENT_ID, ApiConstant.API_REQUEST_ORDER_DESC);//按照活动编码(event_id)降序排列
		if (pageBean != null && pageBean.isPageFlag()) {	
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		
		if (createDateFrom != null) {
			request.setParameterBetween(EventInfo.QUERY_EVENT_TIMELINE, DateUtil.formatDate(createDateFrom, DateUtil.DATETIME), null);
		}
		if (createDateTo != null) {
			request.setParameterBetween(EventInfo.QUERY_EVENT_TIMELINE, null, DateUtil.formatDate(createDateTo, DateUtil.DATETIME));
		}
		if (beginDateFrom != null) {
			request.setParameterBetween(EventInfo.QUERY_EVENT_START_TIME, DateUtil.formatDate(beginDateFrom, DateUtil.DATETIME), null);
		}
		if (beginDateTo != null) {
			request.setParameterBetween(EventInfo.QUERY_EVENT_START_TIME, null, DateUtil.formatDate(beginDateTo, DateUtil.DATETIME));
		}
		if (endDateFrom != null) {
			request.setParameterBetween(EventInfo.QUERY_EVENT_END_TIME, DateUtil.formatDate(endDateFrom, DateUtil.DATETIME), null);
		}
		if (endDateTo != null) {
			request.setParameterBetween(EventInfo.QUERY_EVENT_END_TIME, null, DateUtil.formatDate(endDateTo, DateUtil.DATETIME));
		}
		if (eventInfoStatus != null && eventInfoStatus.getValue() != EventInfoStatus.ALL.getValue()) {
			request.setParameter(EventInfo.QUERY_EVENT_STATUS, eventInfoStatus.getValue() + "");
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取活动数据失败");
			throw new ApiRemoteCallFailedException("API获取活动数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取活动数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取活动数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取活动数据为空, message={}", response.getMessage());
			return null;
		}

		List<EventInfo> eventInfoList = EventInfo.convertFromJSONArray(response.getData());
		
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
		map.put(Global.API_MAP_KEY_LIST, eventInfoList);
		
		return map;
	}
	
	@Override
	public Map<String, Object> findEventInfoListByCondition(Integer eventId,
			Date createDateFrom, Date createDateTo, Date beginDateFrom,
			Date beginDateTo, Date endDateFrom, Date endDateTo,
			EventInfoStatus eventInfoStatus, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API条件查询活动数据");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EVENT_INFO_SEARCH);//设置api地址
		request.addOrder(EventInfo.ORDER_EVENT_ID, ApiConstant.API_REQUEST_ORDER_DESC);//按照活动编码(event_id)降序排列
		if (pageBean != null && pageBean.isPageFlag()) {	
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		
		if (eventId != null){
			request.setParameter(EventInfo.QUERY_EVENT_ID, eventId+"");
		}
		if (createDateFrom != null) {
			request.setParameterBetween(EventInfo.QUERY_EVENT_TIMELINE, DateUtil.formatDate(createDateFrom, DateUtil.DATETIME), null);
		}
		if (createDateTo != null) {
			request.setParameterBetween(EventInfo.QUERY_EVENT_TIMELINE, null, DateUtil.formatDate(createDateTo, DateUtil.DATETIME));
		}
		if (beginDateFrom != null) {
			request.setParameterBetween(EventInfo.QUERY_EVENT_START_TIME, DateUtil.formatDate(beginDateFrom, DateUtil.DATETIME), null);
		}
		if (beginDateTo != null) {
			request.setParameterBetween(EventInfo.QUERY_EVENT_START_TIME, null, DateUtil.formatDate(beginDateTo, DateUtil.DATETIME));
		}
		if (endDateFrom != null) {
			request.setParameterBetween(EventInfo.QUERY_EVENT_END_TIME, DateUtil.formatDate(endDateFrom, DateUtil.DATETIME), null);
		}
		if (endDateTo != null) {
			request.setParameterBetween(EventInfo.QUERY_EVENT_END_TIME, null, DateUtil.formatDate(endDateTo, DateUtil.DATETIME));
		}
		if (eventInfoStatus != null && eventInfoStatus.getValue() != EventInfoStatus.ALL.getValue()) {
			request.setParameter(EventInfo.QUERY_EVENT_STATUS, eventInfoStatus.getValue() + "");
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取活动数据失败");
			throw new ApiRemoteCallFailedException("API获取活动数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取活动数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取活动数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取活动数据为空, message={}", response.getMessage());
			return null;
		}

		List<EventInfo> eventInfoList = EventInfo.convertFromJSONArray(response.getData());
		
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
		map.put(Global.API_MAP_KEY_LIST, eventInfoList);
		
		return map;
	}
	
	/**
	 * 多条件分页查询活动日志
	 * @param eventId 活动编码
	 * @param userName 用户名
	 * @param beginTimeline 起始参与时间
	 * @param endTimeline 终止参与时间
	 * @param status 送彩金状态
	 * @param pageBean 分页信息
	 */
	public Map<String, Object> findEventLogListByCondition (Integer eventId, String userName, 
			Date beginTimeline, Date endTimeline, EventLogStatus logStatus, PlatformType platformType, 
			String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询对应活动的中奖日志");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EVENT_LOG_SEARCH); //设置api请求地址
		if (eventId != null && eventId != 0) {
			request.setParameter(EventLog.QUERY_EVENT_ID, eventId + "");
		} else {
			logger.error("必须的查询条件-活动编码为空");
			return null;
		}
		if (userName != null && !"".equals(userName)) {//根据用户名查询
			Member member = memberService.get(userName); //根据用户名查询用户
			if (member != null) {
				request.setParameter(EventLog.QUERY_UID, member.getUid() + "");//添加用户编号查询条件
			} else {
				request.setParameter(EventLog.QUERY_UID, "");
			}
		}
		if (beginTimeline != null) {//根据参与起始时间查询
			request.setParameterBetween(EventLog.QUERY_TIMELINE,
					DateUtil.formatDate(beginTimeline,DateUtil.DATETIME),null);//添加参与时间查询条件
		}
		if (endTimeline != null) {//根据参与终止时间查询
			request.setParameterBetween(EventLog.QUERY_TIMELINE,null,
					DateUtil.formatDate(endTimeline,DateUtil.DATETIME));//添加参与时间查询条件
		}
		if (logStatus != null && logStatus != EventLogStatus.ALL) {//根据是否派奖查询
			request.setParameter(EventLog.QUERY_EVENT_STATUS, logStatus.getValue()+"");//添加送彩金是否成功查询条件
		}
		if (platformType != null && platformType != PlatformType.ALL) {//根据平台查询
			request.setParameter(EventLog.QUERY_PLATFORM, platformType.getValue()+"");//添加平台查询条件
		}
		if (pageBean != null) {		
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		if (orderStr != null && !orderStr.equals("") && orderView != null && !orderView.equals("")) {
			request.addOrder(orderStr, orderView);//以活动日志编码(id)降序排列
		} else {
			request.addOrder(EventLog.ORDER_EVENT_TIMELINE, ApiConstant.API_REQUEST_ORDER_DESC);//以活动参与时间(id)降序排列
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取中奖日志数据失败");
			throw new ApiRemoteCallFailedException("API获取中奖日志数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取中奖日志数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取中奖日志数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取中奖日志数据为空, message={}", response.getMessage());
			return null;
		}

		List<EventLog> eventLogList = EventLog.convertFromJSONArray(response.getData());
		
		if (pageBean != null && pageBean.isPageFlag()) {
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
		map.put(Global.API_MAP_KEY_LIST, eventLogList);
		
		return map;
	}
	
	/**
	 * 根据活动编号查询活动信息
	 * @param id 活动编号
	 */
	@Override
	public EventInfo getEventInfo(Integer id) 
		throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询活动信息");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EVENT_INFO_SEARCH);
		if (id != null && id != 0) {
			request.setParameter(EventInfo.QUERY_EVENT_ID,id+"");
		} else {
			logger.error("必要的查询条件-活动编码为空");
			return null;
		}
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取活动数据失败");
			throw new ApiRemoteCallFailedException("API获取活动数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取活动数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取活动数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取活动数据为空, message={}", response.getMessage());
			return null;
		}
		List<EventInfo> events = EventInfo.convertFromJSONArray(response.getData());
		
		if (events != null && events.size() > 0) {
			return events.get(0);
		}
		
		return null;
	}
	
	/**
	 * 根据活动编号查询奖项
	 * @param eventId 活动编号
	 */
	public List<EventPrize> findEventPrizeList (Integer eventId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询奖项");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EVENT_PRIZE_SEARCH);//设置api地址
		if (eventId != null && eventId != 0) {
			request.setParameter(EventPrize.QUERY_EVENT_ID,eventId+"");//设置活动编码
		} else {
			logger.error("必要的查询条件-活动编码为空");
			return null;
		}
		request.addOrder(EventPrize.ORDER_PRIZE_ID,ApiConstant.API_REQUEST_ORDER_ASC);
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取奖项数据失败");
			throw new ApiRemoteCallFailedException("API获取奖项数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取奖项数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取奖项数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取奖项数据为空, message={}", response.getMessage());
			return null;
		}
		
		List<EventPrize> eventPrizes = EventPrize.convertFromJSONArray(response.getData());
		
		if (eventPrizes == null || eventPrizes.size() == 0) {
			logger.info("当前活动没有对应的奖项");
			return null;
		}
		
		return eventPrizes;
	}
	
	/**
	 * 根据活动编号和奖项编号查询奖项
	 * @param eventId 活动编号
	 * @param prizeId 奖项编号
	 */
	@Override
	public EventPrize getEventPrize (Integer eventId, Integer prizeId) 
				throws ApiRemoteCallFailedException {
		logger.info("进入调用API根据活动编号和奖项编号查询奖项");
		
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_EVENT_PRIZE_SEARCH);//设置api地址
		if (eventId != null && eventId != 0) {
			request.setParameter(EventPrize.QUERY_EVENT_ID,eventId+"");//设置活动编号
		} else {
			logger.error("必要的查询条件-活动编码为空");
			return null;
		}
		if (prizeId != null && prizeId != 0) {
			request.setParameter(EventPrize.QUERY_PRIZE_ID, prizeId+"");//设置奖项编号
		} else {
			logger.error("必要的查询条件-奖项编码为空");
			return null;
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取奖项数据失败");
			throw new ApiRemoteCallFailedException("API获取奖项数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取奖项数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取奖项数据请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取奖项数据为空, message={}", response.getMessage());
			return null;
		}
		
		List<EventPrize> eventPrizes = EventPrize.convertFromJSONArray(response.getData());
		
		if (eventPrizes != null && eventPrizes.size() > 0) {
			return eventPrizes.get(0);
		}
		
		return null;
	}
	
	public ApiRequestService getApiRequestService () {
		return apiRequestService;
	}
	public void setApiRequestService (ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}
	public MemberService getMemberService () {
		return memberService;
	}
	public void setMemberService (MemberService memberService) {
		this.memberService = memberService;
	}

}
