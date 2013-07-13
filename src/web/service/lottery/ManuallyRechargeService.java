package web.service.lottery;

import com.lehecai.admin.web.domain.user.User;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ManuallyRechargeType;
import com.lehecai.core.lottery.WalletType;


public interface ManuallyRechargeService {
	public void recharge(String account, Double amount,
                         String orderId, String payNo, User opUser, WalletType walletType,
                         ManuallyRechargeType manuallyRechargeType, String bankTypeId, String remark, String eventId) throws ApiRemoteCallFailedException;
	/**
	 * 业务逻辑与上面的方法相同，只是增加一个planId参数
	 * 目前仅用于补充派奖功能
	 * @author chirowong
	 * @param account
	 * @param amount
	 * @param orderId
	 * @param payNo
	 * @param opUser
	 * @param walletType
	 * @param manuallyRechargeType
	 * @param bankTypeId
	 * @param remark
	 * @param eventId
	 * @param planId
	 * @throws ApiRemoteCallFailedException
	 */
	public void rechargeAddPlanId(String account, Double amount,
                                  String orderId, String payNo, User opUser, WalletType walletType,
                                  ManuallyRechargeType manuallyRechargeType, String bankTypeId, String remark, String eventId, String planId) throws ApiRemoteCallFailedException;
}