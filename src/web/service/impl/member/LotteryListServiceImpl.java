package web.service.impl.member;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.LotteryListService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.PrizeRank;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;

public class LotteryListServiceImpl implements LotteryListService {

	private ApiRequestService apiRequestService;
	private final Logger logger = LoggerFactory.getLogger(LotteryListServiceImpl.class);
	private final int PERIOD_VALUE = 5;
	
	@Override
	public Map<String, Object> fuzzyQueryResult(String lotteryType,Date ticketPrintTimeStart, Date ticketPrintTimeEnd, PageBean pageBean)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API模糊查询中奖排行信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_LOTTERY_LIST);
		if (lotteryType != null && Integer.parseInt(lotteryType) != LotteryType.ALL.getValue()) {
			request.setParameter(PrizeRank.LOTTERY_TYPE, lotteryType);
		}
		request.setParameter(PrizeRank.QUERY_PERIOD, PERIOD_VALUE + "");
		if(ticketPrintTimeStart != null){
			request.setParameterBetween(PrizeRank.TICKET_PRINT_TIME, DateUtil.formatDate(ticketPrintTimeStart,DateUtil.DATETIME),null);
		}
		if(ticketPrintTimeEnd != null){
			request.setParameterBetween(PrizeRank.TICKET_PRINT_TIME, null,DateUtil.formatDate(ticketPrintTimeEnd,DateUtil.DATETIME));
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取中奖排行数据失败");
			throw new ApiRemoteCallFailedException("API获取中奖排行数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取中奖排行数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取中奖排行数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取中奖排行数据为空, message={}", response.getMessage());
			return null;
		}
		
		List<PrizeRank> list = PrizeRank.convertFromJSONArray(response.getData());
		if (pageBean != null) {	
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;//页数
			if (pageBean.getPageSize() != 0) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		return map;
	}

	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

}
