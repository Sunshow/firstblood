package web.service.lottery;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.FinishComboStatus;
import com.lehecai.core.lottery.LotteryType;

public interface ComboOrderService {
	
	/**
	 * 查询套餐订单信息
	 * @return
	 */
	public Map<String, Object> queryComboOrderList(Long comboOrderId, Long comboId, Long uid, Long comborevId, FinishComboStatus finishComboStatus, PageBean pageBean) throws ApiRemoteCallFailedException;

	/**
	 * 查询单一套餐详情
	 * @return
	 */
	public Map<String, Object> queryComboOrderInfo(Long comboOrderId, Long comboId, Long uid, LotteryType lotteryType, FinishComboStatus finishComboStatus, PageBean pageBean) throws ApiRemoteCallFailedException;

	/**
	 * 查询套餐执行记录
	 * @return
	 */
	public Map<String, Object> queryComboOrderRecord(Long comboOrderId, Long comboId, Long uid, LotteryType lotteryType, String phase, Long planId, Long orderId, PageBean pageBean) throws ApiRemoteCallFailedException;

}
