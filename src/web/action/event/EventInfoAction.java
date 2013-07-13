package web.action.event;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.event.EventInfoService;
import com.lehecai.admin.web.service.event.EventService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.event.EventInfo;
import com.lehecai.core.event.EventInfoStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.util.CoreDateUtils;

/**
 * 抽奖活动Action
 * @author yanweijie
 *
 */
public class EventInfoAction extends BaseAction {
	private static final long serialVersionUID = -8239556348809720038L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private EventService eventService;
	private EventInfoService eventInfoService ;
	
	private List<EventInfo> events;			//抽奖活动列表
	
	private EventInfo eventInfo;			//抽奖活动信息
	
	private Integer eventId;				//活动编码
	private String eventName;       		//活动名称
	private String eventDescription;		//活动描述
	private String eventStartTime;    		//活动起始时间
	private String eventEndTime;      		//活动结束时间
	private Integer status;     			//0,活动关闭  1,活动开启
	private Integer presetHits;				//预估的参与人数
	
	//条件查询变量 start
	private Date createDateFrom;
	private Date createDateTo;
	private Date beginDateFrom;
	private Date beginDateTo;
	private Date endDateFrom;
	private Date endDateTo;
	private Integer eventInfoStatusValue;
	//条件查询变量 end
	
	/**
	 * 条件查询活动列表
	 */
	public String handle() {
		logger.info("进入条件查询活动信息");
		return "list";
	}
	
	/**
	 * 条件查询活动列表
	 */
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入条件查询活动信息");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map = null;
		if (createDateFrom != null && createDateTo != null) {
			if (createDateFrom.compareTo(createDateTo) > 0) {
				logger.error("查询条件（创建时间）输入有误");
				super.setErrorMessage("查询条件（创建时间）输入有误");
				return "failure";
			}
			if (!DateUtil.isSameMonth(createDateFrom, createDateTo)) {
				logger.error("查询条件（创建时间）开始时间和结束时间不在同一年同一月");
				super.setErrorMessage("查询条件（创建时间）开始时间和结束时间必须为同一年同一月，不支持跨年月查询");
				return "failure";
			}
		}
		if (beginDateFrom != null && beginDateTo != null) {
			if (beginDateFrom.compareTo(beginDateTo) > 0) {
				logger.error("查询条件（开始时间）输入有误");
				super.setErrorMessage("查询条件（开始时间）输入有误");
				return "failure";
			}
			if (!DateUtil.isSameMonth(beginDateFrom, beginDateTo)) {
				logger.error("查询条件（开始时间）开始时间和结束时间不在同一年同一月");
				super.setErrorMessage("查询条件（开始时间）开始时间和结束时间必须为同一年同一月，不支持跨年月查询");
				return "failure";
			}
		}
		if (endDateFrom != null && endDateTo != null) {
			if (endDateFrom.compareTo(endDateTo) > 0) {
				logger.error("查询条件（结束时间）输入有误");
				super.setErrorMessage("查询条件（结束时间）输入有误");
				return "failure";
			}
			if (!DateUtil.isSameMonth(endDateFrom, endDateTo)) {
				logger.error("查询条件（结束时间）开始时间和结束时间不在同一年同一月");
				super.setErrorMessage("查询条件（结束时间）开始时间和结束时间必须为同一年同一月，不支持跨年月查询");
				return "failure";
			}
		}
		if (eventInfoStatusValue == null) {
			this.eventInfoStatusValue = EventInfoStatus.OPEN.getValue();
		}
		EventInfoStatus eventInfoStatus = EventInfoStatus.getItem(this.getEventInfoStatusValue());
		try {
			map = eventService.findEventInfoListByCondition(eventId,createDateFrom, createDateTo, beginDateFrom, 
					beginDateTo, endDateFrom, endDateTo, eventInfoStatus, super.getPageBean());//条件查询所有活动
		} catch (ApiRemoteCallFailedException e) {
			logger.error("条件查询抽奖活动，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			events = (List<EventInfo>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		
		logger.info("条件查询活动信息结束");
		return "list";
	}
	
	/**
	 * 转向添加/修改抽奖活动信息
	 * @return
	 */
	public String input() {
		logger.info("进入输入抽奖活动信息");
		if (eventId != null && eventId != 0) {//如果抽奖活动编号存在，则为修改抽奖活动信息
			try {
				eventInfo = eventService.getEventInfo(eventId);//根据抽奖活动编号查询抽奖活动信息
				status = eventInfo.getStatus().getValue();
				eventStartTime = CoreDateUtils.formatDate(eventInfo.getEventStartTime(), CoreDateUtils.DATETIME);
				eventEndTime = CoreDateUtils.formatDate(eventInfo.getEventEndTime(), CoreDateUtils.DATETIME);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询抽奖活动，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
		} else {
			status = EventInfoStatus.OPEN.getValue();
			presetHits = 0;
		}
		
		return "inputForm";
	}
	
	/**
	 * 添加/修改抽奖活动信息
	 * @return
	 */
	public String manage() {
		logger.info("进入更新抽奖活动信息");
		
		if (eventName == null || eventName.trim().equals("")) {
			logger.error("抽奖活动名称为空");
			super.setErrorMessage("抽奖活动名称不能为空");
			return "failure";
		}
		Date eventStartDate = null;
		Date eventEndDate = null;
		if (eventStartTime != null && !"".equals(eventStartTime)) {
			eventStartDate = CoreDateUtils.parseDate(eventStartTime, CoreDateUtils.DATETIME);//得到抽奖活动起始时间的Date类型
			if (eventStartDate == null) {
				logger.error("格式化抽奖活动起始时间失败");
				super.setErrorMessage("格式化抽奖活动起始时间失败");
				return "failure";
			}
		}
		if (eventEndTime != null && !"".equals(eventEndTime)) {
			eventEndDate = CoreDateUtils.parseDate(eventEndTime, CoreDateUtils.DATETIME);//得到抽奖活动结束时间的Date类型
			if (eventEndDate == null) {
				logger.error("格式化抽奖活动结束时间失败");
				super.setErrorMessage("格式化抽奖活动结束时间失败");
				return "failure";
			}
		}
		if (eventStartDate != null && eventEndDate != null) {
			if (eventStartDate.getTime() > eventEndDate.getTime()) {
				logger.error("抽奖活动起始时间大于抽奖活动结束时间");
				super.setErrorMessage("抽奖活动起始时间不能大于抽奖活动结束时间");
				return "failure";
			}
		}
		
		eventInfo = new EventInfo();
		if (eventId != null && eventId != 0) {
			eventInfo.setEventId(eventId);
		}
		eventInfo.setEventName(eventName);
		eventInfo.setEventStartTime(eventStartDate);
		eventInfo.setEventEndTime(eventEndDate);
		eventInfo.setEventDescription(eventDescription);
		eventInfo.setStatus(EventInfoStatus.getItem(status));
		eventInfo.setPresetHits(presetHits);
		
		if (eventId != null && eventId != 0) {
			logger.info("修改抽奖活动信息");
			boolean updateResult = false;
			try {
				updateResult = eventInfoService.updateEventInfo(eventInfo);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("修改抽奖活动信息，api调用失败，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
				return "failure";
			}
			if (updateResult) {
				logger.info("修改抽奖活动成功");
			} else {
				logger.error("修改抽奖活动信息失败");
				super.setErrorMessage("修改抽奖活动信息失败");
				return "failure";
			}
		} else {
			logger.info("添加抽奖活动信息");
			boolean addResult = false;
			try {
				addResult = eventInfoService.addEventInfo(eventInfo);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("添加抽奖活动信息，api调用失败，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
				return "failure";
			}
			if (addResult) {
				logger.info("添加抽奖活动信息成功");
			} else {
				logger.error("添加抽奖活动信息失败");
				super.setErrorMessage("添加抽奖活动信息失败");
				return "failure";
			}
		}
		
		logger.info("更新抽奖活动信息结束");
		
		super.setForwardUrl("/event/eventInfo.do");
		return "forward";
	}
	
	
	public EventService getEventService() {
		return eventService;
	}
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}
	public EventInfoService getEventInfoService() {
		return eventInfoService;
	}
	public void setEventInfoService(EventInfoService eventInfoService) {
		this.eventInfoService = eventInfoService;
	}
	public List<EventInfo> getEvents() {
		return events;
	}
	public void setEvents(List<EventInfo> events) {
		this.events = events;
	}
	public EventInfo getEventInfo() {
		return eventInfo;
	}
	public void setEventInfo(EventInfo eventInfo) {
		this.eventInfo = eventInfo;
	}
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getEventDescription() {
		return eventDescription;
	}
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}
	public String getEventStartTime() {
		return eventStartTime;
	}
	public void setEventStartTime(String eventStartTime) {
		this.eventStartTime = eventStartTime;
	}
	public String getEventEndTime() {
		return eventEndTime;
	}
	public void setEventEndTime(String eventEndTime) {
		this.eventEndTime = eventEndTime;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getPresetHits() {
		return presetHits;
	}
	public void setPresetHits(Integer presetHits) {
		this.presetHits = presetHits;
	}
	
	public Date getCreateDateFrom() {
		return createDateFrom;
	}

	public void setCreateDateFrom(Date createDateFrom) {
		this.createDateFrom = createDateFrom;
	}

	public Date getCreateDateTo() {
		return createDateTo;
	}

	public void setCreateDateTo(Date createDateTo) {
		this.createDateTo = createDateTo;
	}

	public Date getBeginDateFrom() {
		return beginDateFrom;
	}

	public void setBeginDateFrom(Date beginDateFrom) {
		this.beginDateFrom = beginDateFrom;
	}

	public Date getBeginDateTo() {
		return beginDateTo;
	}

	public void setBeginDateTo(Date beginDateTo) {
		this.beginDateTo = beginDateTo;
	}

	public Date getEndDateFrom() {
		return endDateFrom;
	}

	public void setEndDateFrom(Date endDateFrom) {
		this.endDateFrom = endDateFrom;
	}

	public Date getEndDateTo() {
		return endDateTo;
	}

	public void setEndDateTo(Date endDateTo) {
		this.endDateTo = endDateTo;
	}

	public List<EventInfoStatus> getEventInfoStatusList() {
		return EventInfoStatus.getItems();
	}

	public Integer getEventInfoStatusValue() {
		return this.eventInfoStatusValue;
	}

	public void setEventInfoStatusValue(Integer eventInfoStatusValue) {
		this.eventInfoStatusValue = eventInfoStatusValue;
	}
}
