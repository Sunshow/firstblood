package web.service.member;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.user.ConsumptionLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.TransType;
import com.lehecai.core.lottery.WalletType;

public interface ConsumptionService {

	/**
	 * 多条件分页查询钱包流水
	 * @param lotteryType	彩票种类
	 * @param transType		交易类型
	 * @param username		账户名
	 * @param beginDate		交易起始时间
	 * @param endDate		交易终止时间
	 * @param logId			钱包流水号
	 * @param orderId		订单编号
	 * @param planId		方案编号
	 * @param orderStr		排序字段
	 * @param orderView		排序方式
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> getResult(LotteryType lotteryType,
                                         TransType transType, List<WalletType> walletTypeList, String username, Date beginDate, Date endDate,
                                         String logId, String orderId, String planId, String orderStr,
                                         String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;
	/**
	 * 多条件分页统计钱包流水
	 * @param lotteryType	彩票种类
	 * @param transType		交易类型
	 * @param username		账户名
	 * @param beginDate		交易起始时间
	 * @param endDate		交易终止时间
	 * @param logId			钱包流水号
	 * @param orderId		订单编号
	 * @param planId		方案编号
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> getConsumptionStatistics(LotteryType lotteryType,
                                                        TransType transType, List<WalletType> walletTypeList, String username, Date beginDate, Date endDate,
                                                        String logId, String orderId, String planId) throws ApiRemoteCallFailedException;
			
	public ConsumptionLog get(String id) throws ApiRemoteCallFailedException;
}