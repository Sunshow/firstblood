package web.service.impl.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.business.WalletExchangeService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.Wallet;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 钱包兑换业务逻辑层实现类
 * @author yanweijie
 *
 */
public class WalletExchangeServiceImpl implements WalletExchangeService {
	private Logger logger = LoggerFactory.getLogger(WalletExchangeServiceImpl.class);
	
	private ApiRequestService apiWriteRequestService ;
	
	/**
	 * 钱包兑换
	 * @param uid 会员编号
	 * @param srcWallet 来源
	 * @param desWallet 目标
	 * @param amount 金额
	 * @throws ApiRemoteCallFailedException 
	 */
	public boolean exchangeWallet(Long uid, int srcWallet, int desWallet, double amount) 
										throws ApiRemoteCallFailedException {
		logger.info("进入调用API兑换钱包");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MEMBER_WALLET_EXCHANGE);
		
		request.setParameter(Wallet.QUERY_UID, uid + "");
		request.setParameter(Wallet.QUERY_FROMWALLET, srcWallet + "");
		request.setParameter(Wallet.QUERY_TOWALLET, desWallet + "");
		
		request.setParameterForUpdate(Wallet.SET_AMOUNT, amount + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API兑换钱包失败");
			throw new ApiRemoteCallFailedException("API兑换钱包失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API兑换钱包请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}

		return true;
	}

	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}
}
