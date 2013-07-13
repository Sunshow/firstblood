package web.dao.lottery;

import com.lehecai.core.lottery.LotteryType;
import com.lehecai.engine.entity.lottery.LotteryTicketConfig;

public interface LotteryTicketConfigDAO {
	
	/**
	 * 按彩种类型获取彩种出票配置
	 * @param lotteryType
	 * @return
	 */
	public LotteryTicketConfig get(LotteryType lotteryType);
	
	/**
	 * 添加或者更新彩种出票配置
	 * @param lotteryTicketConfig
	 */
	public void update(LotteryTicketConfig lotteryTicketConfig);
	
	/**
	 * 删除彩种出票配置
	 * @param lotteryTicketConfig
	 */
	public void delete(LotteryTicketConfig lotteryTicketConfig);
	
	/**
	 * 按彩种删除出票配置
	 * @param lotteryType
	 */
	public void delete(LotteryType lotteryType);
	
}
