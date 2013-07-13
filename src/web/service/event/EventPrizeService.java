package web.service.event;

import com.lehecai.core.api.event.EventPrize;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 奖项业务逻辑层接口，用于添加、修改、删除奖项
 * @author yanweijie
 *
 */
public interface EventPrizeService {

	/**
	 * 添加奖项
	 * @param eventPrize
	 */
	public boolean addEventPrize(EventPrize eventPrize) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改奖项
	 * @param eventPrize
	 */
	public boolean updateEventPrize(EventPrize eventPrize) throws ApiRemoteCallFailedException;
	
	/**
	 * 删除奖项
	 * @param eventId 活动编码
	 * @param prizeId 奖项编码
	 */
	public boolean delEventPrize(int eventId, int prizeId) throws ApiRemoteCallFailedException;
}
