package web.service.business;

import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 钱包兑换业务逻辑层接口
 * @author yanweijie
 *
 */
public interface WalletExchangeService {

	/**
	 * 钱包兑换
	 * @param uid 会员编号
	 * @param srcWallet 来源
	 * @param desWallet 目标
	 * @param amount 目标
	 */
	boolean exchangeWallet(Long uid, int srcWallet, int desWallet, double amount)
												throws ApiRemoteCallFailedException ;
}
