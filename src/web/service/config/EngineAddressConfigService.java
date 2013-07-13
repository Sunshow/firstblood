/**
 * 
 */
package web.service.config;

import com.lehecai.core.lottery.LotteryType;

/**
 * @author Sunshow
 *
 */
public interface EngineAddressConfigService {

	/**
	 * 获取默认配置
	 * @return
	 */
	public String getDefaultAddress() throws Exception;
	
	/**
	 * 获取分彩种配置
	 * @param lotteryType
	 * @return
	 */
	public String getLotteryAddress(LotteryType lotteryType) throws Exception;
}
