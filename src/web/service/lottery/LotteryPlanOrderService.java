package web.service.lottery;

import java.util.Date;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.lottery.PlanOrder;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PlanOrderStatus;
import com.lehecai.core.lottery.PlanOrderType;
import com.lehecai.core.lottery.PrizeStatus;

public interface LotteryPlanOrderService {

	public Map<String, Object> getResult(String userName,
                                         String orderId, String planId, PlanOrderStatus orderStatus, PrizeStatus prizeStatus, PlanOrderType orderType, Date rbeginDate, Date rendDate,
                                         Date lbeginDate, Date lendDate, String orderStr, String orderView, LotteryType lotteryType, String phase, PageBean pageBean) throws ApiRemoteCallFailedException;
	public PlanOrder get(String id) throws ApiRemoteCallFailedException;
	
	public Map<String,Object> lotteryPlanOrderStatistics(String userName,
                                                         String orderId, String planId, PlanOrderStatus orderStatus, PrizeStatus prizeStatus, PlanOrderType orderType, Date rbeginDate,
                                                         Date rendDate, Date lbeginDate, Date lendDate, LotteryType lotteryType, String phase) throws ApiRemoteCallFailedException;
	/**
	 * 重置未支付订单计数
	 * @param uid
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean counterResetOrderNotPaid(String uid) throws ApiRemoteCallFailedException;
	/**
	 * 更新订单状态
	 * @param oid
	 * @param status
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean updatePlanOrderStatus(String oid, PlanOrderStatus status) throws ApiRemoteCallFailedException;
}