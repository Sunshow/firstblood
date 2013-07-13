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
import com.lehecai.admin.web.service.event.EventPrizeService;
import com.lehecai.admin.web.service.event.EventService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.event.EventInfo;
import com.lehecai.core.api.event.EventPrize;
import com.lehecai.core.api.event.EventPrizeType;
import com.lehecai.core.event.EventInfoStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 奖项Action
 * @author yanweijie
 *
 */
public class EventPrizeAction extends BaseAction {
	private static final long serialVersionUID = -6376216780104515623L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private EventService eventService;
	private EventPrizeService eventPrizeService;

	private List<EventPrize> eventPrizes;	//对应活动的奖项列表
	private List<EventInfo> events;			//抽奖活动列表
	
	private EventPrize eventPrize;
	private Integer eventId;				//活动编码
	private Integer prizeId;				//奖项编码
	private String prizeName;				//奖项名称
	private int prizeQuantity;				//奖项总数
	private double prizeMoney;   			//奖金
	private int prizeLevel;	    			//奖级
	private int prizeTypeValue;				//奖项类型
	private String imgSrc;					//奖项图片
	
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
	 * 查询活动对应的奖项
	 * @return
	 */
	public String list() {
		logger.info("进入查询活动对应的奖项");
		if (eventId == null || eventId == 0) {
			logger.error("活动编码为空");
			super.setErrorMessage("活动编码不能为空");
			return "failure";
		}
		try {
			eventPrizes = eventService.findEventPrizeList(eventId);//查询活动对应的奖项
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询活动对应的奖项，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		if (eventPrizes == null || eventPrizes.size() == 0) {
			logger.info("此活动没有对应的奖项");
		}
		
		logger.info("查询活动对应的奖项结束");
		return "prize_list";
	}
	
	/**
	 * 查询中奖活动对应的奖项
	 * @return
	 */
	public String listYesstatus() {
		logger.info("进入查询中奖活动对应的奖项");
		try {
			eventPrize = eventService.getEventPrize(eventId,eventPrize.getPrizeId());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询活动奖项，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		logger.info("action查询中奖活动对应的奖项结束");
		
		return "yesstatus_eventPrize_list";
	}
	
	/**
	 * 查询所有抽奖活动用于奖项管理活动列表
	 * @return
	 */
	public String handle() {
		logger.info("进入查询所有抽奖活动");
		return "prize_event_list";
	}
	
	/**
	 * 查询所有抽奖活动用于奖项管理活动列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询所有抽奖活动");
		
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
		
		logger.info("查询所有抽奖活动结束");
		
		return "prize_event_list";
	}
	
	/**
	 * 转向添加/修改奖项
	 * @return
	 */
	public String input() {
		logger.info("进入输入奖项信息");
		if (eventId == null || eventId == 0) {
			logger.error("活动编码为空");
			super.setErrorMessage("活动编码不能为空");
			return "failure";
		}
		if (prizeId != null && prizeId != 0) {
			try {
				eventPrize = eventService.getEventPrize(eventId, prizeId);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询奖项，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
				return "failure";
			}
		} else {
			prizeQuantity = 0;
			prizeMoney = 0.00D;
			prizeLevel = 0;
		}
		return "inputForm";
	}
	
	/**
	 * 添加/修改奖项
	 * @return
	 */
	public String manage() {
		logger.info("进入更新奖项");
		
		if (eventId == null || eventId == 0) {
			logger.error("活动编码为空");
			super.setErrorMessage("活动编码不能为空");
			return "failure";
		}
		if (prizeName == null || prizeName.equals("")) {
			logger.error("奖项名称为空");
			super.setErrorMessage("奖项名称不能为空");
			return "failure";
		}
		if (prizeQuantity < 0) {
			logger.error("奖项总数为空");
			super.setErrorMessage("奖项总数不能为空");
			return "failure";
		}
		if (prizeMoney < 0) {
			logger.error("奖金为空");
			super.setErrorMessage("奖金不能为空");
			return "failure";
		}
		if (prizeLevel < 0) {
			logger.error("奖级为空");
			super.setErrorMessage("奖级不能为空");
			return "failure";
		}
		if (prizeTypeValue == 0) {
			logger.error("奖项类型为空");
			super.setErrorMessage("奖项类型不能为空");
			return "failure";
		}
		eventPrize = new EventPrize();
		if (prizeId != null && prizeId != 0) {
			eventPrize.setPrizeId(prizeId);
		}
		eventPrize.setEventId(eventId);
		eventPrize.setPrizeName(prizeName);
		eventPrize.setPrizeQuantity(prizeQuantity);
		eventPrize.setPrizeMoney(prizeMoney);
		eventPrize.setPrizeLevel(prizeLevel);
		eventPrize.setPrizeType(EventPrizeType.getItem(prizeTypeValue));
		eventPrize.setImgSrc(imgSrc);
		
		if (prizeId != null && prizeId != 0) {
			logger.info("修改奖项信息");
			boolean updateResult = false;
			try {
				updateResult = eventPrizeService.updateEventPrize(eventPrize);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("修改奖项信息，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
				return "failure";
			}
			if (updateResult) {
				logger.info("修改奖项信息成功");
			} else {
				logger.error("修改奖项信息失败");
				super.setErrorMessage("修改奖项信息失败");
				return "failure";
			}
		} else {
			logger.info("添加奖项信息");
			boolean addResult = false;
			try {
				addResult = eventPrizeService.addEventPrize(eventPrize);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("添加奖项信息，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
				return "failure";
			}
			if (addResult) {
				logger.info("添加奖项信息成功");
			} else {
				logger.error("添加奖项信息失败");
				super.setErrorMessage("添加奖项信息失败");
				return "failure";
			}
		}
		logger.info("更新奖项结束");
		super.setForwardUrl("/event/eventPrize.do?action=list&eventId=" + eventId);
		return "forward";
	}
	
	/**
	 * 查看奖项详细信息
	 */
	public String view () {
		logger.info("进入查询奖项详情");
		
		if (eventId == null || eventId == 0) {
			logger.error("活动编码为空");
			super.setErrorMessage("活动编码为空");
			return "failure";
		}
		if (prizeId == null || prizeId == 0) {
			logger.error("奖项编码为空");
			super.setErrorMessage("奖项编码为空");
			return "failure";
		}
		
		try {
			eventPrize = eventService.getEventPrize(eventId, prizeId);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询奖项详情，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
			return "failure";
		}
		
		logger.info("查询奖项详情结束");
		
		return "view";
	}
	
	/**
	 * 删除奖项
	 * @return
	 */
	public String del() {
		logger.info("进入删除奖项");
		if (eventId == null || eventId == 0) {
			logger.error("活动编码为空");
			super.setErrorMessage("活动编码不能为空");
			return "failure";
		}
		if (prizeId == null || prizeId == 0) {
			logger.error("奖项编码为空");
			super.setErrorMessage("奖项编码不能为空");
			return "failure";
		}
		boolean deleteResult = false;
		try {
			deleteResult = eventPrizeService.delEventPrize(eventId,prizeId);	//删除奖项
		} catch (ApiRemoteCallFailedException e) {
			logger.error("删除奖项，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
			return "failure";
		}
		
		if (deleteResult) {
			logger.info("删除奖项成功");
		} else {
			logger.error("删除奖项失败");
			super.setErrorMessage("删除奖项失败");
			return "failure";
		}
		
		logger.info("删除奖项结束");
		
		super.setForwardUrl("/event/eventPrize.do?action=list&eventId="+eventId);
		return "forward";
	}
	
	public List<EventPrizeType> getPrizeTypeList() {
		return EventPrizeType.getItems();
	}
	public EventService getEventService() {
		return eventService;
	}
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}
	public EventPrizeService getEventPrizeService() {
		return eventPrizeService;
	}
	public void setEventPrizeService(EventPrizeService eventPrizeService) {
		this.eventPrizeService = eventPrizeService;
	}

	public List<EventPrize> getEventPrizes() {
		return eventPrizes;
	}
	public void setEventPrizes(List<EventPrize> eventPrizes) {
		this.eventPrizes = eventPrizes;
	}
	public List<EventInfo> getEvents() {
		return events;
	}
	public void setEvents(List<EventInfo> events) {
		this.events = events;
	}
	public EventPrize getEventPrize() {
		return eventPrize;
	}
	public void setEventPrize(EventPrize eventPrize) {
		this.eventPrize = eventPrize;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public Integer getPrizeId() {
		return prizeId;
	}
	public void setPrizeId(Integer prizeId) {
		this.prizeId = prizeId;
	}
	public String getPrizeName() {
		return prizeName;
	}
	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}
	public int getPrizeQuantity() {
		return prizeQuantity;
	}
	public void setPrizeQuantity(int prizeQuantity) {
		this.prizeQuantity = prizeQuantity;
	}
	public double getPrizeMoney() {
		return prizeMoney;
	}
	public void setPrizeMoney(double prizeMoney) {
		this.prizeMoney = prizeMoney;
	}
	public int getPrizeLevel() {
		return prizeLevel;
	}
	public void setPrizeLevel(int prizeLevel) {
		this.prizeLevel = prizeLevel;
	}
	public String getImgSrc() {
		return imgSrc;
	}
	public int getPrizeTypeValue() {
		return prizeTypeValue;
	}
	public void setPrizeTypeValue(int prizeTypeValue) {
		this.prizeTypeValue = prizeTypeValue;
	}
	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
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

	public Integer getEventInfoStatusValue() {
		if (eventInfoStatusValue == null) {
			eventInfoStatusValue = EventInfoStatus.OPEN.getValue();
		}
		return eventInfoStatusValue;
	}

	public void setEventInfoStatusValue(Integer eventInfoStatusValue) {
		if (eventInfoStatusValue == null) {
			this.eventInfoStatusValue = EventInfoStatus.OPEN.getValue();
		} else {
			this.eventInfoStatusValue = eventInfoStatusValue;
		}
	}
	
	public List<EventInfoStatus> getEventInfoStatusList() {
		return EventInfoStatus.getItems();
	}
}
