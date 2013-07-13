package web.service.impl.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.AutoFollowService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.AutoFollow;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.AutoFollowType;
import com.lehecai.core.lottery.LotteryType;

public class AutoFollowServiceImpl implements AutoFollowService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public Map<String, Object> queryAutoFollowList(Member member, PageBean pageBean)
		throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员跟单信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_AUTOFOLLOW_MEMBER_QUERY);
		if (member == null || member.getUid() == 0L) {
			logger.error("用户信息传递错误");
			return null;
		}
		request.setParameter(AutoFollow.QUERY_FUID, member.getUid() + "");
		request.addOrder(AutoFollow.ORDER_TIMELINE, ApiConstant.API_REQUEST_ORDER_DESC);
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取会员自动跟单信息失败");
			throw new ApiRemoteCallFailedException("API获取会员自动跟单信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员自动跟单信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员自动跟单信息请求出错");
		}
		List<AutoFollow> list = AutoFollow.convertFromJSONArray(response.getData());
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
	public Map<String, Object> queryAutoFollowInfoList(Long fuid, Long tuid,
			PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询会员跟具体跟单人详细信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_AUTOFOLLOW_INFO_QUERY);
		if (fuid == null || fuid == 0L) {
			logger.error("跟单人ID为空");
			return null;
		}
		if (tuid == null || tuid == 0L) {
			logger.error("被跟单人ID为空");
			return null;
		}
		request.setParameter(AutoFollow.QUERY_FUID, fuid + "");
		request.setParameter(AutoFollow.QUERY_TUID, tuid + "");
		request.addOrder(AutoFollow.ORDER_TIMELINE, ApiConstant.API_REQUEST_ORDER_DESC);
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取会员自动跟单跟具体跟单人详细信息失败");
			throw new ApiRemoteCallFailedException("API获取会员自动跟单跟具体跟单人详细信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取会员自动跟单详细信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取会员自动跟单详细信息请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn(", message={}", response.getMessage());
			return null;
		}
		List<AutoFollow> list = AutoFollow.convertFromJSONArray(response.getData());
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
		logger.info("结束调用API查询会员跟具体跟单人详细信息");
		return map;
	}
	

	@Override
	public void addAutoFollow(Long fuid, Long tuid, LotteryType lotteryType,
			AutoFollowType autoFollowType, Integer numPerphase,
			Double unitAmount, Double cancelBelowAmount)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API添加会员跟单信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_AUTOFOLLOW_INFO_SET);

		if (fuid != null && fuid != 0L) {
			request.setParameterForUpdate(AutoFollow.SET_FUID, fuid + "");
		}
		if (tuid != null && tuid != 0L) {
			request.setParameterForUpdate(AutoFollow.SET_TUID, tuid + "");
		}
		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameterForUpdate(AutoFollow.SET_LOTTERYTYPE, lotteryType.getValue() + "");
		}
		if (autoFollowType != null && autoFollowType.getValue() != AutoFollowType.ALL.getValue()) {
			request.setParameterForUpdate(AutoFollow.SET_AUTOFOLLOWTYPE, autoFollowType.getValue() + "");
		}
		if (numPerphase != null) {
			request.setParameterForUpdate(AutoFollow.SET_NUMPERPHASE, numPerphase + "");
		}
		if (cancelBelowAmount != null) {
			request.setParameterForUpdate(AutoFollow.SET_CANCELBELOWAMOUNT, cancelBelowAmount + "");
		}
		if (unitAmount != null) {
			request.setParameterForUpdate(AutoFollow.SET_UNITAMOUNT, unitAmount + "");
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API添加会员自动跟单信息失败");
			throw new ApiRemoteCallFailedException("API添加会员自动跟单信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API添加会员自动跟单信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API添加会员自动跟单信息请求出错");
		}
		logger.info("结束调用API添加会员跟单信息");
	}
	
	@Override
	public void delAutoFollow(Long fuid, Long tuid, LotteryType lotteryType)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API取消会员自动跟单");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_AUTOFOLLOW_INFO_CANCEL);

		if (fuid != null && fuid != 0L) {
			request.setParameterForUpdate(AutoFollow.SET_FUID, fuid + "");
		}
		if (tuid != null && tuid != 0L) {
			request.setParameterForUpdate(AutoFollow.SET_TUID, tuid + "");
		}
		if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
			request.setParameterForUpdate(AutoFollow.SET_LOTTERYTYPE, lotteryType.getValue() + "");
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API取消会员自动跟单信息失败");
			throw new ApiRemoteCallFailedException("API取消会员自动跟单信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API取消会员自动跟单信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API取消会员自动跟单信息请求出错");
		}
		logger.info("结束调用API取消会员自动跟单");
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
