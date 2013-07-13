package web.service.business;

import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;

public interface ExecuteComboService {
	
	/**
	 * 执行追号
	 * @return
	 */
	public String comboOrderExecute(List<String> comboOrderId, LotteryType lotteryType, String phase) throws ApiRemoteCallFailedException;
	/**
	 * 查询等待执行追好的套餐订单列表
	 * @return
	 */
	public Map<String, Object> queryWaitExcuteComboOrderList(Long uid, Long comboId, Long comboOrderId, LotteryType lotteryType, String phase, PageBean pageBean) throws ApiRemoteCallFailedException;
}

