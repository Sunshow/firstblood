package web.service.lottery;

import java.util.List;

import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.engine.entity.lottery.LotteryTicketConfig;

public interface LotteryTicketConfigService {
	/**
	 * 按彩种查询对应彩种的出票配置
	 * @param lotteryType
	 * @return
	 */
	public LotteryTicketConfig get(LotteryType lotteryType);
	
	/**
	 * 查询彩种列表内所有彩种的出票配置
	 * @param lotteryTypeList
	 * @return
	 */
	public List<LotteryTicketConfig> get(List<LotteryType> lotteryTypeList);
	/**
	 * 更新彩种出票配置
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
	
	/**
	 * 彩种出票配置相关属性同步到API Setting服务
	 * @param lotteryTicketConfig
	 */
	public void updateSetting(LotteryTicketConfig lotteryTicketConfig) throws ApiRemoteCallFailedException;
}
