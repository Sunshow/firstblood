package web.service.business;

import java.util.List;

import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 缓存业务逻辑层接口
 * @author yanweijie
 *
 */
public interface CacheManageService {

	/**
	 * 删除钱包缓存
	 * @param uid 
	 */
	boolean deleteWalletCache(Long uid) throws ApiRemoteCallFailedException;
	
	/**
	 * 删除单场缓存
	 * @param id
	 */
	boolean deleteDCCache(Long id) throws ApiRemoteCallFailedException;
	/**
	 * 删除胜负过关缓存
	 * @param id
	 */
	boolean deleteSFGGCache(Long id) throws ApiRemoteCallFailedException;
	
	/**
	 * 删除竞彩篮球缓存
	 * @param match_num
	 */
	boolean deleteJCLQCache(Long match_num) throws ApiRemoteCallFailedException;
	/**
	 * 删除竞彩足球缓存
	 * @param match_num
	 */
	boolean deleteJCZQCache(Long match_num) throws ApiRemoteCallFailedException;
	/**
	 * 删除方案缓存
	 */
	boolean deletePlanCache(Long planId) throws ApiRemoteCallFailedException;
	/**
	 * 删除订单缓存
	 */
	boolean deleteOrderCache(Long orderId) throws ApiRemoteCallFailedException;
	/**
	 * 删除计数器缓存
	 */
	boolean deleteCounterCache(List<Long> uids) throws ApiRemoteCallFailedException;
	/**
	 * 删除用户登录失败计数缓存
	 */
	boolean deleteUserLoginFailureCountCache() throws ApiRemoteCallFailedException;
}
