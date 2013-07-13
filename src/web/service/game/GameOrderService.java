/**
 * 
 */
package web.service.game;

import java.util.Date;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.game.GameOrder;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author chirowong
 *
 */
public interface GameOrderService {
	/**
	 * 游戏日志查询
	 * @return
	 */
	public Map<String, Object> queryGameOrderList(GameOrder gameOrder, Date beginTime, Date endTime, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 游戏金额汇总
	 * @param gameOrder
	 * @param beginTime
	 * @param endTime
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> queryGameOrderTotal(GameOrder gameOrder, Date beginTime, Date endTime) throws ApiRemoteCallFailedException;
	

	/**
	 * 更改订单状态
	 * @param gameOrder
	 * @throws ApiRemoteCallFailedException
	 */
	public void updateGameOrder(GameOrder gameOrder) throws ApiRemoteCallFailedException;
}
