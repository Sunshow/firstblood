package web.service.impl.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.BankCardService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.BankCard;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.BankType;

public class BankCardServiceImpl implements BankCardService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public boolean delBankCard(Long bankCardId)	throws ApiRemoteCallFailedException {
		logger.info("进入调用API取消会员银行卡信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_UPDATE_USER_BANK_INFO);

		if (bankCardId != null && bankCardId != 0L) {
			request.setParameter(BankCard.QUERY_ID, bankCardId + "");
			request.setParameterForUpdate(BankCard.SET_STATUS, YesNoStatus.NO.getValue() + "");
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
		request.setUrl(ApiConstant.API_URL_GET_USER_BANK_BY_USER);
		if (member == null || member.getUid() == 0L) {
			logger.error("用户信息传递错误");
			return null;
		}
		request.setParameter(BankCard.QUERY_UID, member.getUid() + "");
		request.addOrder(BankCard.ORDER_UPDATE_TIME, ApiConstant.API_REQUEST_ORDER_DESC);
		
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
		List<BankCard> list = BankCard.convertFromJSONArray(response.getData());
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
		request.setUrl(ApiConstant.API_URL_GET_USER_BANK_BY_USER);
		if (bankCardId == null || bankCardId == 0L) {
			logger.error("ID传递错误");
			return null;
		}
		request.setParameter(BankCard.QUERY_ID, bankCardId + "");
		
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
		List<BankCard> list = BankCard.convertFromJSONArray(response.getData());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_LIST, list);
		logger.info("结束调用API查询会员跟单信息");
		return map;
	}
	
	@Override
	public boolean manageBankCard(Long bankCardId, String bankCardno,
			Integer provinceId, Integer cityId, BankType bankType,
			String bankBranch) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改会员银行卡信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_UPDATE_USER_BANK_INFO);

		if (bankCardId != null && bankCardId != 0L) {
			request.setParameter(BankCard.QUERY_ID, bankCardId + "");
		} else {
			logger.error("ID传输错误");
			return false;
		}
		if (bankBranch != null && !"".equals(bankBranch)) {
			request.setParameterForUpdate(BankCard.SET_BANK_BRANCH, bankBranch);
		}
		if (bankCardno != null && !"".equals(bankCardno)) {
			request.setParameterForUpdate(BankCard.SET_BANK_CARD_NO, bankCardno);
		}
		if (provinceId != null && provinceId != 0) {
			request.setParameterForUpdate(BankCard.SET_PROVINCE, provinceId + "");
		}
		if (cityId != null) {
			request.setParameterForUpdate(BankCard.SET_CITY, cityId + "");
		}
		if (bankType != null && bankType.getValue() != BankType.ALL.getValue()) {
			request.setParameterForUpdate(BankCard.SET_BANK_ID, bankType.getValue() + "");
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
