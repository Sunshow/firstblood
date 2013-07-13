package web.service.impl.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.WaitChaseService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.lottery.ChaseWait;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ChaseStatus;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.StopChaseType;

/**
 * 等待追号业务逻辑层实现类
 * @author yanweijie
 *
 */
public class WaitChaseServiceImpl implements WaitChaseService {
	private final Logger logger = LoggerFactory.getLogger(WaitChaseServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	
	private ApiRequestService apiWriteRequestService;
	
	/**
	 * 多条件分页查询正在追号的
	 * @param chaseId		追号ID
	 * @param lotteryType	彩种
	 * @param phase			彩期
	 * @param stopChaseType 追号停止类型
	 * @param pageBean		分页类
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> getWaitResult(String chaseId, LotteryType lotteryType, String phase, 
			StopChaseType stopChaseType, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询等待追号数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CHASE_WAIT_QUERY);
		request.setParameter(ChaseWait.QUERY_CHASE_STATUS, 
				ChaseStatus.CHASING.getValue()+"");//添加追号状态(默认为正在追号)查询条件
		
		if(chaseId != null && !"".equals(chaseId)){
			request.setParameter(ChaseWait.QUERY_CHASE_ID, chaseId);//添加追号ID查询条件
		}
		if(lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()){
			request.setParameter(ChaseWait.QUERY_LOTTERY_TYPE, lotteryType.getValue()+"");//添加彩种查询条件
		}
		if (phase != null && !"".equals(phase) && !"-1".equals(phase)) {
			request.setParameter(ChaseWait.QUERY_PHASE, phase);//添加彩期查询条件
		}
		if(stopChaseType != null && stopChaseType.getValue() != StopChaseType.ALL.getValue()){
			request.setParameter(ChaseWait.QUERY_STOPCHASE_TYPE, stopChaseType.getValue()+"");//添加停止追号类型查询条件
		}
		request.addOrder(ChaseWait.ORDER_ID,ApiConstant.API_REQUEST_ORDER_DESC);//添加排序方式(默认以等待追号的编号降序)
		
		if(pageBean != null){
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API获取等待追号数据失败");
			throw new ApiRemoteCallFailedException("API获取等待追号数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取等待追号数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取等待追号数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取等待追号数据为空, message={}", response.getMessage());
			return null;
		}
		logger.info("相应数据---------"+response.getData());
		List<ChaseWait> list = ChaseWait.convertFromJSONArray(response.getData());
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
		return map;
	}
	
	/**
	 * 执行追号
	 * @param id 等待追号编号
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean executeWaitChase(String id) throws ApiRemoteCallFailedException {
		logger.info("进入调用API执行追号");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_CHASE_EXECUTE);
		
		if(id != null && !"".equals(id)){
			request.setParameter(ChaseWait.QUERY_ID, id);//添加追号ID查询条件
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API执行追号失败");
			throw new ApiRemoteCallFailedException("API执行追号失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API执行追号请求出错, rc={}, message={}", response.getCode(), response.getMessage());
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
