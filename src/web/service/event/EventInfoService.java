package web.service.event;

import com.lehecai.core.api.event.EventInfo;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 抽奖活动信息业务逻辑层接口，用于添加、修改抽奖活动信息
 * @author yanweijie
 *
 */
public interface EventInfoService {
	
	/**
	 * 添加抽奖活动信息
	 * @param eventInfo 抽奖活动信息
	 * @return
	 */
	public boolean addEventInfo(EventInfo eventInfo) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改抽奖活动信息
	 * @param eventInfo 抽奖活动信息
	 * @return
	 */
	public boolean updateEventInfo(EventInfo eventInfo) throws ApiRemoteCallFailedException;
}
