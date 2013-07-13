package web.service.business;

import java.util.List;

import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 足彩过关统计重算业务层接口
 * @author yanweijie
 *
 */
public interface ZcRecalcService {
	
	/**
	 * 按彩期重算足彩过关统计
	 * @param lotteryType 彩种
	 * @param phase 彩期
	 */
	boolean reCalcByPhase(Integer lotteryType, String phase) throws ApiRemoteCallFailedException;
	/**
	 * 按方案编号重算足彩过关统计
	 */
	boolean reCalcByPlanId(Integer lotteryType, String planId) throws ApiRemoteCallFailedException;
	/**
	 * 按彩期同步足彩过关统计
	 * @param lotteryType 彩种
	 * @param phase 彩期
	 */
	boolean syncByPhase(Integer lotteryType, String phase) throws ApiRemoteCallFailedException;
	/**
	 * 按彩期结束足彩过关统计
	 * @param lotteryType 彩种
	 * @param phase 彩期
	 */
	boolean terminateByPhase(Integer lotteryType, String phase) throws ApiRemoteCallFailedException;
	/**
	 * 按方案编号同步足彩过关统计
	 */
	boolean syncByPlanId(Integer lotteryType, String planId) throws ApiRemoteCallFailedException;
	/**
	 * 获取结果更新状态
	 */
	Integer getUpdateStatus() throws ApiRemoteCallFailedException;
	/**
	 * 暂停结果更新 
	 */
	boolean pauseResultUpdate(long expire) throws ApiRemoteCallFailedException;
	/**
	 * 恢复结果更新 
	 */
	boolean resumeResultUpdate() throws ApiRemoteCallFailedException;
	/**
	 * 赛事结果删除  
	 */
	boolean removeResult(Integer lotteryType, List<String> items) throws ApiRemoteCallFailedException;
}

