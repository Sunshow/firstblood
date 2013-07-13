package web.action.event;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.event.EventService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.event.EventLog;
import com.lehecai.core.event.EventLogStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.PlatformType;

/**
 * 用户中奖日志Action
 * @author yanweijie
 *
 */
public class EventLogAction extends BaseAction {
	private static final long serialVersionUID = -6376216780104515623L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private EventService eventService;
	
	private String userName;									//用户名查询
	private Date beginTimeline; 								//起始参与时间
	private Date endTimeline;									//终止参与时间
	private Integer status = EventLogStatus.ALL.getValue();		//是否派奖
	private Integer platformType = PlatformType.ALL.getValue(); //平台
	private int eventId;
	private List<EventLog> eventLogs;
	
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	
	private String orderStrValue;
	private String orderViewValue;
	
	/**
	 * 条件分页查询用户中奖日志
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String handle() {
		logger.info("进入查询中奖日志！");
		
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map = null;
		
		if (orderStrValue == null || orderStrValue.equals("")) {
			orderStrValue = EventLog.ORDER_EVENT_TIMELINE;
		}
		if (orderViewValue == null || orderViewValue.equals("")) {
			orderViewValue = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		
		try {
			map = eventService.findEventLogListByCondition(eventId,userName,
					beginTimeline,endTimeline,EventLogStatus.getItem(status),
					PlatformType.getItem(platformType), orderStrValue, orderViewValue, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询中奖日志，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		if (map != null) {
			eventLogs = (List<EventLog>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		
		logger.info("查询中奖日志结束！");
		
		return "list";
	}
	
	public EventService getEventService() {
		return eventService;
	}
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getBeginTimeline() {
		return beginTimeline;
	}
	public void setBeginTimeline(Date beginTimeline) {
		this.beginTimeline = beginTimeline;
	}
	public Date getEndTimeline() {
		return endTimeline;
	}
	public void setEndTimeline(Date endTimeline) {
		this.endTimeline = endTimeline;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getPlatformType() {
		return platformType;
	}
	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public List<EventLog> getEventLogs() {
		return eventLogs;
	}
	public void setEventLogs(List<EventLog> eventLogs) {
		this.eventLogs = eventLogs;
	}
	public List<PlatformType> getPlatformTypes(){
		return PlatformType.getItems();
	}
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(EventLog.ORDER_EVENT_TIMELINE, "参与时间");
		orderStrMap.put(EventLog.ORDER_EVENT_SOURCE, "来源");
		orderStrMap.put(EventLog.ORDER_EVENT_STATUS, "派奖状态");
		orderStrMap.put(EventLog.ORDER_EVENT_PRIZEID, "奖项等级");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}

	public String getOrderStrValue() {
		return orderStrValue;
	}

	public void setOrderStrValue(String orderStrValue) {
		this.orderStrValue = orderStrValue;
	}

	public String getOrderViewValue() {
		return orderViewValue;
	}

	public void setOrderViewValue(String orderViewValue) {
		this.orderViewValue = orderViewValue;
	}
}
