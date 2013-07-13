package web.service.impl.ticket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.ticket.BettingLimitNumberService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LimitNumberType;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PlayType;
import com.lehecai.core.lottery.ticket.BettingLimitNumber;

/**
 * 2013-05-09
 * @author He Wang
 *
 */
public class BettingLimitNumberServiceImpl implements BettingLimitNumberService {
	
	private final Logger logger = LoggerFactory.getLogger(BettingLimitNumberServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	
	@Override
	public Map<String, Object> getResult(BettingLimitNumber bettingLimitNumber,
			PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API获取投注限号数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_BETTING_LIMIT_QUERY);
		
		if (bettingLimitNumber == null) {
			logger.error("API根据用户名获取投注限号数据异常!原因：查询条件为空");
			throw new ApiRemoteCallFailedException("API根据用户名获取投注限号数据异常!原因：查询条件为空");
		}
		if (bettingLimitNumber.getId() != null && bettingLimitNumber.getId() > 0) {
			request.setParameter(BettingLimitNumber.QUERY_ID, bettingLimitNumber.getId() + "");
		}
		if (bettingLimitNumber.getLotteryType() != null && bettingLimitNumber.getLotteryType().getValue() != LotteryType.ALL.getValue()) {
			request.setParameter(BettingLimitNumber.QUERY_LOTTERY_TYPE, bettingLimitNumber.getLotteryType().getValue() + "");
		}
		if (bettingLimitNumber.getPlayType() != null && bettingLimitNumber.getPlayType().getValue() != PlayType.ALL.getValue()) {
			request.setParameter(BettingLimitNumber.QUERY_PLAY_TYPE, bettingLimitNumber.getPlayType().getValue() + "");
		}
		if (bettingLimitNumber.getLimitNumberType() != null && bettingLimitNumber.getLimitNumberType().getValue() != LimitNumberType.ALL.getValue()) {
			request.setParameter(BettingLimitNumber.SET_LIMIT_TYPE, bettingLimitNumber.getLimitNumberType().getValue() + "");
		}
		if (bettingLimitNumber.getStatus() != null && bettingLimitNumber.getStatus().getValue() != YesNoStatus.ALL.getValue()) {
			request.setParameter(BettingLimitNumber.QUERY_STATUS, bettingLimitNumber.getStatus().getValue() + "");
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		request.addOrder(BettingLimitNumber.QUERY_ID, ApiConstant.API_REQUEST_ORDER_DESC);
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取投注限号数据失败");
			throw new ApiRemoteCallFailedException("API获取投注限号数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取投注限号数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取投注限号数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取投注限号数据为空, message={}", response.getMessage());
			return null;
		}
		List<BettingLimitNumber> list = BettingLimitNumber.convertFromJSONArray(response.getData());
		if (pageBean != null) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;// 页数
			if (pageBean.getPageSize() != 0) {
				pageCount = totalCount / pageBean.getPageSize();
				if (totalCount % pageBean.getPageSize() != 0) {
					pageCount++;
				}
			}
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		return map;
	}

	@Override
	public BettingLimitNumber get(Integer id) throws ApiRemoteCallFailedException {
		logger.info("进入调用API获取投注限号数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_BETTING_LIMIT_QUERY);
		
		if (id == null || id <= 0) {
			logger.error("API根据用户名获取投注限号数据异常!原因：查询id为空或小于0");
			throw new ApiRemoteCallFailedException("API根据用户名获取投注限号数据异常!原因：查询id为空或小于0");
		}
		
		request.setParameter(BettingLimitNumber.QUERY_ID, id + "");
		

		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取投注限号数据失败");
			throw new ApiRemoteCallFailedException("API获取投注限号数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取投注限号数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取投注限号数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取投注限号数据为空, message={}", response.getMessage());
			return null;
		}
		List<BettingLimitNumber> list = BettingLimitNumber.convertFromJSONArray(response.getData());
		if (list == null) {
			logger.warn("调用API获取投注限号数据请求出错, 未能获取数据");
			throw new ApiRemoteCallFailedException("调用API获取投注限号数据请求出错, 未能获取数据");
		}
		if (list.size() != 1) {
			logger.error("调用API获取投注限号数据请求出错, 通过id获取到多条数据");
			throw new ApiRemoteCallFailedException("调用API获取投注限号数据请求出错, 通过id获取到多条数据");
		}
		return list.get(0);
	}

	@Override
	public boolean add(BettingLimitNumber bettingLimitNumber) throws ApiRemoteCallFailedException {

		logger.info("进入调用API添加投注限号");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_BETTING_LIMIT_ADD);
		if (bettingLimitNumber != null) {
			request.setParameterForUpdate(BettingLimitNumber.QUERY_LOTTERY_TYPE, bettingLimitNumber.getLotteryType().getValue() + "");
			request.setParameterForUpdate(BettingLimitNumber.QUERY_PLAY_TYPE, bettingLimitNumber.getPlayType().getValue() + "");
			request.setParameterForUpdate(BettingLimitNumber.SET_LIMIT_TYPE, bettingLimitNumber.getLimitNumberType().getValue() + "");
			request.setParameterForUpdate(BettingLimitNumber.SET_LIMIT_VALUE, bettingLimitNumber.getLimitValue());
			request.setParameterForUpdate(BettingLimitNumber.SET_VALUE, bettingLimitNumber.getValue() + "");
			request.setParameterForUpdate(BettingLimitNumber.QUERY_STATUS, bettingLimitNumber.getStatus().getValue() + "");
		}
		
		logger.info("添加投注限号,api request String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API添加投注限号失败");
			throw new ApiRemoteCallFailedException("API添加投注限号失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API添加投注限号请求异常");
			return false;
		}
		return true;
	
	}

	@Override
	public boolean update(BettingLimitNumber bettingLimitNumber) throws ApiRemoteCallFailedException  {

		logger.info("进入调用API更新投注限号信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_BETTING_LIMIT_UPDATE);
		if (bettingLimitNumber != null) {
			request.setParameterForUpdate(BettingLimitNumber.SET_LIMIT_TYPE, bettingLimitNumber.getLimitNumberType().getValue() + "");
			request.setParameterForUpdate(BettingLimitNumber.SET_LIMIT_VALUE, bettingLimitNumber.getLimitValue());
			request.setParameterForUpdate(BettingLimitNumber.SET_VALUE, bettingLimitNumber.getValue() + "");
			request.setParameterForUpdate(BettingLimitNumber.QUERY_STATUS, bettingLimitNumber.getStatus().getValue() + "");
			request.setParameter(BettingLimitNumber.QUERY_ID, bettingLimitNumber.getId() + "");
		}
		
		logger.info("更新投注限号信息,api request String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API更新投注限号信息失败");
			throw new ApiRemoteCallFailedException("API更新投注限号信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API更新投注限号信息失败");
			return false;
		}
		return true;
	}

	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}

	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}


}
