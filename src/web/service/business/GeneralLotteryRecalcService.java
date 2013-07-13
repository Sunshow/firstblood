package web.service.business;

import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 普通彩种过关统计重算业务层接口
 * @author jinsheng
 *
 */
public interface GeneralLotteryRecalcService {
	
	/**
	 * 按彩期重算普通彩种过关统计
	 * @param lotteryType 彩种
	 * @param phase 彩期
	 * @param eventType 事件
	 */
	boolean recalcByGeneralLotteryPhase(Integer lotteryType, String phase, Integer eventType) throws ApiRemoteCallFailedException;
}

