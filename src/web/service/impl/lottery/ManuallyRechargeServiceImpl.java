package web.service.impl.lottery;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.service.lottery.ManuallyRechargeService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.ManuallyRecharge;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ManuallyRechargeType;
import com.lehecai.core.lottery.WalletType;


public class ManuallyRechargeServiceImpl implements ManuallyRechargeService {
	private final Logger logger = LoggerFactory.getLogger(ManuallyRechargeServiceImpl.class);
	
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public void recharge(String account, Double amount,
			String orderId, String payNo, User opUser, WalletType walletType, 
			ManuallyRechargeType manuallyRechargeType, String bankTypeId, String remark, String eventId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API充值和补单");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MANUAL_PAY);
		
		if(account != null && !"".equals(account)){
			request.setParameter(ManuallyRecharge.QUERY_USERNAME, account);
		}
		if(amount != null && amount != 0.00D){
			request.setParameter(ManuallyRecharge.QUERY_AMOUNT, amount+"");
		}
		if(orderId != null && !"".equals(orderId)){
			request.setParameter(ManuallyRecharge.QUERY_ORDER_ID, orderId+"");
		}
		if(payNo != null && !"".equals(payNo)){
			request.setParameter(ManuallyRecharge.QUERY_PAY_NO, payNo);
		}
		if(opUser != null){
			if(opUser.getId() != null && opUser.getId() != 0L && opUser.getUserName() != null && !"".equals(opUser.getUserName())){
				request.setParameter(ManuallyRecharge.QUERY_ADMIN_UID, opUser.getId()+"");
				request.setParameter(ManuallyRecharge.QUERY_ADMIN_USER, opUser.getUserName()+"");
			}
		}
		request.setParameter(ManuallyRecharge.QUERY_ADD_TYPE, manuallyRechargeType.getValue()+"");
		
		if (walletType != null ) {
			request.setParameter(ManuallyRecharge.QUERY_WALLET_TYPE, walletType.getValue() + "");
		}
		
		if(bankTypeId != null && !"".equals(bankTypeId)){
			request.setParameter(ManuallyRecharge.QUERY_BANK_ID, bankTypeId);
		}
		
		if(!StringUtils.isEmpty(remark)){
			request.setParameter(ManuallyRecharge.QUERY_REMARK, remark);
		}
		if(!StringUtils.isEmpty(eventId)){
			request.setParameter(ManuallyRecharge.QUERY_BANK_ID, eventId);
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API充值补单失败:返回response为空");
			throw new ApiRemoteCallFailedException("API获取方案数据失败:返回response为空");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取方案数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("充值补单失败请求出错:code=" + response.getCode() + ", message:" + response.getMessage());
		}
	}
	
	@Override
	public void rechargeAddPlanId(String account, Double amount,
			String orderId, String payNo, User opUser, WalletType walletType, 
			ManuallyRechargeType manuallyRechargeType, String bankTypeId, String remark, String eventId,String planId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API充值和补单");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_MANUAL_PAY);
		
		if(account != null && !"".equals(account)){
			request.setParameter(ManuallyRecharge.QUERY_USERNAME, account);
		}
		if(amount != null && amount != 0.00D){
			request.setParameter(ManuallyRecharge.QUERY_AMOUNT, amount+"");
		}
		if(orderId != null && !"".equals(orderId)){
			request.setParameter(ManuallyRecharge.QUERY_ORDER_ID, orderId+"");
		}
		if(payNo != null && !"".equals(payNo)){
			request.setParameter(ManuallyRecharge.QUERY_PAY_NO, payNo);
		}
		if(opUser != null){
			if(opUser.getId() != null && opUser.getId() != 0L && opUser.getUserName() != null && !"".equals(opUser.getUserName())){
				request.setParameter(ManuallyRecharge.QUERY_ADMIN_UID, opUser.getId()+"");
				request.setParameter(ManuallyRecharge.QUERY_ADMIN_USER, opUser.getUserName()+"");
			}
		}
		request.setParameter(ManuallyRecharge.QUERY_ADD_TYPE, manuallyRechargeType.getValue()+"");
		
		if (walletType != null ) {
			request.setParameter(ManuallyRecharge.QUERY_WALLET_TYPE, walletType.getValue() + "");
		}
		
		if(bankTypeId != null && !"".equals(bankTypeId)){
			request.setParameter(ManuallyRecharge.QUERY_BANK_ID, bankTypeId);
		}
		
		if(!StringUtils.isEmpty(remark)){
			request.setParameter(ManuallyRecharge.QUERY_REMARK, remark);
		}
		if(!StringUtils.isEmpty(eventId)){
			request.setParameter(ManuallyRecharge.QUERY_BANK_ID, eventId);
		}
		if(!StringUtils.isEmpty(planId)){
			request.setParameter(ManuallyRecharge.QUERY_PLAN_ID, planId+"");
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API充值补单失败:返回response为空");
			throw new ApiRemoteCallFailedException("API获取方案数据失败:返回response为空");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取方案数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("充值补单失败请求出错:code=" + response.getCode() + ", message:" + response.getMessage());
		}
	}


	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}
	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}
}
