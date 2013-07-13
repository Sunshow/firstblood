package web.service.impl.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.UserBankCardService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.api.user.UserBankCard;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.BankType;

public class UserBankCardServiceImpl implements UserBankCardService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public boolean delBankCard(Long bankCardId)	throws ApiRemoteCallFailedException {
		logger.info("进入调用API取消会员银行卡信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_UPDATE_USER_BANK_CARD);

		if (bankCardId != null && bankCardId != 0L) {
			request.setParameter(UserBankCard.QUERY_ID, bankCardId + "");
			request.setParameterForUpdate(UserBankCard.SET_STATUS, YesNoStatus.NO.getValue() + "");
		} else {
			logger.error("ID传输错误");
			return false;
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API取消会员银行卡信息失败");
			throw new ApiRemoteCallFailedException("API取消会员银行卡信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API取消会员银行卡信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API取消会员银行卡信息请求出错");
		}
		logger.info("结束调用API取消会员自动跟单");
		return true;
	}

	@Override
	public Map<String, Object> queryBankCardList(Member member,
			PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员银行卡信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_GET_USER_BANK_CARD);
		if (member == null || member.getUid() == 0L) {
			logger.error("用户信息传递错误");
			return null;
		}
		request.setParameter(UserBankCard.QUERY_UID, member.getUid() + "");
		request.addOrder(UserBankCard.ORDER_UPDATE_TIME, ApiConstant.API_REQUEST_ORDER_DESC);
		
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员银行卡信息失败");
			throw new ApiRemoteCallFailedException("API获取会员银行卡信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员银行卡信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员银行卡信息请求出错");
		}
		List<UserBankCard> list = UserBankCard.convertFromJSONArray(response.getData());
		if(pageBean != null){
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;//页数
			if(pageBean.getPageSize() != 0) {
				pageCount = totalCount / pageBean.getPageSize();
				if(totalCount % pageBean.getPageSize() != 0) {
					pageCount ++;
				}
			}
			pageBean.setPageCount(pageCount);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		logger.info("结束调用API查询会员跟单信息");
		return map;
	}

	@Override
	public Map<String, Object> queryBankCardListById(Long bankCardId)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员银行卡信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_GET_USER_BANK_CARD_BY_ID);
		if (bankCardId == null || bankCardId == 0L) {
			logger.error("ID传递错误");
			return null;
		}
		request.setParameter(UserBankCard.QUERY_ID, bankCardId + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员银行卡信息失败");
			throw new ApiRemoteCallFailedException("API获取会员银行卡信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员银行卡信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员银行卡信息请求出错");
		}
		List<UserBankCard> list = UserBankCard.convertFromJSONArray(response.getData());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_LIST, list);
		logger.info("结束调用API查询会员跟单信息");
		return map;
	}
	
	@Override
	public boolean manageBankCard(UserBankCard userBankCard) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改会员银行卡信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_UPDATE_USER_BANK_CARD);

		if (userBankCard != null && userBankCard.getId() != 0L) {
			request.setParameter(UserBankCard.QUERY_ID, userBankCard.getId() + "");
		} else {
			logger.error("ID传输错误");
			return false;
		}
		if (userBankCard.getBankBranch() != null && !"".equals(userBankCard.getBankBranch())) {
			request.setParameterForUpdate(UserBankCard.SET_BANK_BRANCH, userBankCard.getBankBranch());
		}
		if (userBankCard.getProvince() != 0) {
			request.setParameterForUpdate(UserBankCard.SET_PROVINCE, userBankCard.getProvince() + "");
		}
		if (userBankCard.getCity() != 0) {
			request.setParameterForUpdate(UserBankCard.SET_CITY, userBankCard.getCity() + "");
		}
		if (userBankCard.getBankType() != null && userBankCard.getBankType().getValue() != BankType.ALL.getValue()) {
			request.setParameterForUpdate(UserBankCard.SET_BANK_TYPE, userBankCard.getBankType().getValue() + "");
		}
		if (userBankCard.getBankId() != 0) {
			request.setParameterForUpdate(UserBankCard.SET_BANK_ID, userBankCard.getBankId() + "");
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API修改会员银行卡信息失败");
			throw new ApiRemoteCallFailedException("API修改会员银行卡信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API修改会员银行卡信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API修改会员银行卡信息请求出错");
		}
		logger.info("结束调用API修改会员自动跟单");
		return true;
	}
	
	/**
	 * 解绑用户银行卡
	 */
	public boolean unlock (UserBankCard userBankCard) throws ApiRemoteCallFailedException {
		logger.info("进入调用API解绑用户银行卡");
		
		ApiRequest request = new ApiRequest();
		
		request.setUrl(ApiConstant.API_URL_USER_BANK_CARD_IVR_UNLOCK);
		request.setParameter(UserBankCard.QUERY_ID, userBankCard.getId() + "");
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API解绑用户银行卡异常");
			throw new ApiRemoteCallFailedException("API解绑用户银行卡异常");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API解绑用户银行卡失败");
			return false;
		}
		return true;
		
	}
	
	@Override
	public boolean lock(UserBankCard userBankCard) throws ApiRemoteCallFailedException {
		logger.info("进入调用API绑定用户银行卡");
		
		ApiRequest request = new ApiRequest();
		
		request.setUrl(ApiConstant.API_URL_USER_BANK_CARD_IVR_LOCK);
		request.setParameter(UserBankCard.QUERY_ID, userBankCard.getId() + "");
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API解绑用户银行卡异常");
			throw new ApiRemoteCallFailedException("API解绑用户银行卡异常");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API解绑用户银行卡失败");
			return false;
		}
		return true;
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}

}
