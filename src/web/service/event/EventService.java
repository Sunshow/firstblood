package web.service.event;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.event.EventInfo;
import com.lehecai.core.api.event.EventPrize;
import com.lehecai.core.event.EventInfoStatus;
import com.lehecai.core.event.EventLogStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.PlatformType;

/**
 * 抽奖活动业务逻辑层接口
 * @author yanweijie
 *
 */
public interface EventService {
	
	/**
	 * 分页查询所有活动信息
	 * @param eventInfoStatusValue 活动状态
	 * @param pageBean 分页信息
	 */
	Map<String, Object> findEventInfoList(EventInfoStatus eventInfoStatus, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 多条件分页查询所有活动信息
	 * @param createDateFrom 创建时间起始
	 * @param createDateTo 创建时间结束
	 * @param beginDateFrom 活动开始时间起始
	 * @param beginDateTo 活动开始时间结束
	 * @param endDateFrom 结束时间起始
	 * @param endDateTo 结束时间结束
	 * @param eventInfoStatusValue 活动状态
	 * @param pageBean 分页信息
	 */
	Map<String, Object> findEventInfoListByCondition(Date createDateFrom, Date createDateTo,
                                                     Date beginDateFrom, Date beginDateTo, Date endDateFrom, Date endDateTo,
                                                     EventInfoStatus eventInfoStatus, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 多条件分页查询所有活动信息
	 * @param createDateFrom 创建时间起始
	 * @param createDateTo 创建时间结束
	 * @param beginDateFrom 活动开始时间起始
	 * @param beginDateTo 活动开始时间结束
	 * @param endDateFrom 结束时间起始
	 * @param endDateTo 结束时间结束
	 * @param eventInfoStatusValue 活动状态
	 * @param pageBean 分页信息
	 */
	Map<String, Object> findEventInfoListByCondition(Integer enventId, Date createDateFrom, Date createDateTo,
                                                     Date beginDateFrom, Date beginDateTo, Date endDateFrom, Date endDateTo,
                                                     EventInfoStatus eventInfoStatus, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 多条件分页查询抽奖活动日志
	 * @param eventId 活动编码
	 * @param userName 用户名
	 * @param beginTimeline 起始参与时间
	 * @param endTimeline 终止参与时间
	 * @param status 送彩金状态
	 * @param pageBean 分页信息
	 */
	Map<String, Object> findEventLogListByCondition(Integer eventId, String userName, Date beginTimeline,
                                                    Date endTimeline, EventLogStatus logStatus, PlatformType platformType, String orderStr, String orderView, PageBean pageBean)
			 throws ApiRemoteCallFailedException;

	/**
	 * 根据活动编号查询活动信息
	 * @param id 活动编号
	 */
	public EventInfo getEventInfo(Integer id) throws ApiRemoteCallFailedException;
	
	/**
	 * 根据活动编号查询奖项
	 * @param eventId 活动编号
	 */
	public List<EventPrize> findEventPrizeList(Integer eventId) throws ApiRemoteCallFailedException;
	
	/**
	 * 根据活动编号和奖项编号查询奖项
	 * @param eventId 活动编号
	 * @param prizeId 奖项编号
	 */
	public EventPrize getEventPrize(Integer eventId, Integer prizeId) throws ApiRemoteCallFailedException;
	
}
