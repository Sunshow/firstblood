package web.service.impl.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.search.OrderSearchService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.lottery.PlanOrder;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.search.api.ISearchApiResponse;
import com.lehecai.core.search.api.SearchApiConstant;
import com.lehecai.core.search.api.SearchApiRequestAggregate;
import com.lehecai.core.search.api.SearchApiRequestFilter;
import com.lehecai.core.search.api.SearchApiRequestFilterItem;
import com.lehecai.core.search.api.SearchApiRequestGet;
import com.lehecai.core.search.api.SearchApiRequestService;
import com.lehecai.core.search.api.impl.AggregateSearchApiRequest;
import com.lehecai.core.search.api.impl.AggregateSearchApiRequestBody;
import com.lehecai.core.search.api.impl.SelectSearchApiRequest;
import com.lehecai.core.search.api.impl.SelectSearchApiRequestBody;
import com.lehecai.core.search.api.impl.SelectSearchApiResponseBody;
import com.lehecai.core.search.entity.SearchEntityDefine;
import com.lehecai.core.search.entity.lottery.PlanOrderSearch;
import com.lehecai.core.search.entity.lottery.PlanOrderSearchDefine;
import com.lehecai.core.search.type.SearchEntityKey;

public class OrderSearchServiceImpl implements OrderSearchService {
	private final Logger logger = LoggerFactory
			.getLogger(OrderSearchServiceImpl.class);

	private SearchApiRequestService psearchApiRequestService;

	@Override
	public Map<String, Object> getLotteryPlanOrderResult(
			PlanOrderSearch searchEntity, Map<String, Object> param,
			PageBean pageBean) throws Exception {
		logger.info("进入调用搜索服务API查询订单数据");

		SelectSearchApiRequest request = new SelectSearchApiRequest();
		request.setService(SearchEntityKey.LOTTERY_ORDER);

		SelectSearchApiRequestBody requestBody = new SelectSearchApiRequestBody();

		SearchApiRequestGet requestGet = new SearchApiRequestGet();
		requestGet.setField(SearchEntityDefine.getField(searchEntity
				.getEntityKey()));
		if (param.get("orderStr") != null
				&& !StringUtils.isEmpty(param.get("orderStr").toString())) {
			if (param.get("orderView") != null) {
				if (param.get("orderView").toString()
						.equals(ApiConstant.API_REQUEST_ORDER_ASC)) {
					requestGet.addOrderASC(param.get("orderStr").toString());
				} else if (param.get("orderView").toString()
						.equals(ApiConstant.API_REQUEST_ORDER_DESC)) {
					requestGet.addOrderDESC(param.get("orderStr").toString());
				}
			}
		}

		requestGet.setLimit(pageBean.getPageSize());
		requestGet.setOffset((pageBean.getPage() - 1) * requestGet.getLimit());
		requestBody.setGet(requestGet);

		SearchApiRequestFilter requestFilter = new SearchApiRequestFilter();
		// 根据字段批量添加EQUAL FILTER的方法
		requestFilter = SearchEntityDefine.createFilter(searchEntity, false);
		requestFilter = this.addDateFilter(requestFilter, param);

		// 进行filter转换，适应搜索服务的数据类型
		// 注意：如果没有其他条件，可以在上一行调用传递true合并convert操作，此处为了演示分离步骤
		SearchEntityDefine.convertFilter(searchEntity.getEntityKey(),
				requestFilter);
		requestBody.setFilter(requestFilter);
		request.setBody(requestBody);
		ISearchApiResponse response = psearchApiRequestService.request(request,
				SearchApiConstant.API_REQUEST_TIME_OUT_LONG);

		if (response == null) {
			logger.error("查询订单数据失败");
			throw new ApiRemoteCallFailedException("查询订单数据失败");
		}

		if (response.getStatus() != SearchApiConstant.RC_SUCCESS) {
			logger.error("查询订单数据请求出错, rc={}, message={}", response.getStatus(),
					SearchApiConstant.getMessage(response.getStatus()));
			throw new ApiRemoteCallFailedException("查询订单数据请求出错");
		}

		SelectSearchApiResponseBody responseBody = (SelectSearchApiResponseBody) response
				.getBody();
		if (responseBody == null) {
			return null;
		}

		List<JSONArray> valueList = responseBody.getValueList();
		if (valueList == null || valueList.isEmpty()) {
			return null;
		}

		List<PlanOrder> planOrderList = new ArrayList<PlanOrder>();
		for (JSONArray jsonArray : valueList) {
			PlanOrderSearch planOrderSearch = new PlanOrderSearch();
			SearchEntityDefine.copyProperties(planOrderSearch, jsonArray,
					requestGet.getField());
			planOrderList.add(planOrderSearch.convert());
		}
		if (pageBean != null) {
			int totalCount = responseBody.getTotal();
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
		map.put(Global.API_MAP_KEY_LIST, planOrderList);
		return map;
	}

	@Override
	public Map<String, Object> lotteryPlanOrderStatistics(
			PlanOrderSearch searchEntity, Map<String, Object> param,
			String[] groupArray) throws Exception {
		logger.info("进入调用搜索服务API查询订单汇总数据");
		AggregateSearchApiRequest request = new AggregateSearchApiRequest();
		request.setService(SearchEntityKey.LOTTERY_ORDER);

		SelectSearchApiRequestBody requestBody = new SelectSearchApiRequestBody();
		AggregateSearchApiRequestBody aggregateRequestBody = new AggregateSearchApiRequestBody();

		SearchApiRequestFilter requestFilter = new SearchApiRequestFilter();
		// 根据字段批量添加EQUAL FILTER的方法
		requestFilter = SearchEntityDefine.createFilter(searchEntity, false);
		// 处理时间区间
		requestFilter = this.addDateFilter(requestFilter, param);
		// 进行filter转换，适应搜索服务的数据类型
		// 注意：如果没有其他条件，可以在上一行调用传递true合并convert操作，此处为了演示分离步骤
		SearchEntityDefine.convertFilter(searchEntity.getEntityKey(),
				requestFilter);

		requestBody.setFilter(requestFilter);
		SearchApiRequestGet requestBodyGet = new SearchApiRequestGet();
		// 先通过search中查询进行第一次筛选，所以需要取出所有满足条件的记录进行后续分组
		requestBodyGet.setLimit(Integer.MAX_VALUE);
		requestBodyGet.setField(groupArray);

		SearchApiRequestGet requestAggregateGet = new SearchApiRequestGet();
		String[] aggregateGet = new String[groupArray.length];
		for (int i = 0; i < groupArray.length; i++) {
			aggregateGet[i] = groupArray[i] + "Aggregate";
		}
		requestAggregateGet.setField(aggregateGet);
		requestBody.setGet(requestBodyGet);
		aggregateRequestBody.setSelect(requestBody);

		// 汇总
		SearchApiRequestAggregate aggregateSearch = new SearchApiRequestAggregate();
		for (int i = 0; i < groupArray.length; i++) {
			aggregateSearch.addAggreate(SearchApiConstant.API_AGGREGATE_SUM,
					groupArray[i], aggregateGet[i]);
		}
		aggregateSearch.setGet(requestAggregateGet);
		aggregateRequestBody.addAggregate(aggregateSearch);
		request.setBody(aggregateRequestBody);

		requestBodyGet.addOrderDESC(param.get("orderStr").toString());
		ISearchApiResponse response = psearchApiRequestService.request(request,
				SearchApiConstant.API_REQUEST_TIME_OUT_LONG);

		if (response == null) {
			logger.error("查询订单数据失败");
			throw new ApiRemoteCallFailedException("查询订单数据失败");
		}

		if (response.getStatus() != SearchApiConstant.RC_SUCCESS) {
			logger.error("查询订单数据请求出错, rc={}, message={}", response.getStatus(),
					SearchApiConstant.getMessage(response.getStatus()));
			throw new ApiRemoteCallFailedException("查询订单数据请求出错");
		}

		SelectSearchApiResponseBody responseBody = (SelectSearchApiResponseBody) response
				.getBody();
		if (responseBody == null) {
			return null;
		}

		List<JSONArray> valueList = responseBody.getValueList();
		if (valueList == null || valueList.isEmpty()) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		for (JSONArray jsonArray : valueList) {
			if (!jsonArray.isEmpty()) {
				map.put(Global.API_MAP_KEY_AMOUNT, jsonArray);
				break;
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> getMemberConsumptionResult(
			PlanOrderSearch searchEntity, Map<String, Object> param, PageBean pageBean) throws Exception {
		logger.info("进入调用搜索服务API查询订单汇总数据");
		String[] fieldArray = {PlanOrder.QUERY_UID,PlanOrder.ORDER_AMOUNT, PlanOrder.ORDER_POSTTAX_PRIZE};
		String[] aggregateArray = {PlanOrder.ORDER_AMOUNT, PlanOrder.ORDER_POSTTAX_PRIZE};
		String[] groupArray = {PlanOrder.QUERY_UID};
		AggregateSearchApiRequest request = new AggregateSearchApiRequest();
		request.setService(SearchEntityKey.LOTTERY_ORDER);

		AggregateSearchApiRequestBody aggregateRequestBody = new AggregateSearchApiRequestBody();
		SelectSearchApiRequestBody requestBody = new SelectSearchApiRequestBody();

		SearchApiRequestFilter requestFilter = new SearchApiRequestFilter();
		// 根据字段批量添加EQUAL FILTER的方法
		requestFilter = SearchEntityDefine.createFilter(searchEntity, false);
		requestFilter = this.addDateFilter(requestFilter, param);
		SearchEntityDefine.convertFilter(searchEntity.getEntityKey(),
				requestFilter);
		requestBody.setFilter(requestFilter);

		SearchApiRequestGet requestBodyGet = new SearchApiRequestGet();
		requestBodyGet.setLimit(Integer.MAX_VALUE);
		requestBodyGet.setField(fieldArray);
		requestBody.setGet(requestBodyGet);
		
		aggregateRequestBody.setSelect(requestBody);
		
		SearchApiRequestGet requestAggregateGet = new SearchApiRequestGet();
		requestAggregateGet.setLimit(pageBean.getPageSize());
		requestAggregateGet.setOffset((pageBean.getPage() - 1) * requestAggregateGet.getLimit());
		String[] aggregateGet = new String[fieldArray.length]; 
		for (int i=0; i< fieldArray.length; i++) {
			for(int j = 0; j < groupArray.length; j++){
				if(fieldArray[i].equals(groupArray[j])){
					aggregateGet[i] = fieldArray[i];
				}else{
					aggregateGet[i] = fieldArray[i] + "Aggregate"; 
				}
			}
		}
		requestAggregateGet.setField(aggregateGet);
		SearchApiRequestAggregate aggregateSearch = new SearchApiRequestAggregate();
		for (int i=0; i<aggregateArray.length; i++){ 
			aggregateSearch.addAggreate(SearchApiConstant.API_AGGREGATE_SUM, aggregateArray[i], aggregateArray[i] + "Aggregate"); 
		}
		
		if (param.get("orderStr") != null
				&& !StringUtils.isEmpty(param.get("orderStr").toString())) {
			if (param.get("orderView") != null) {
				if (param.get("orderView").toString()
						.equals(ApiConstant.API_REQUEST_ORDER_ASC)) {
					requestAggregateGet.addOrderASC(param.get("orderStr").toString()+"Aggregate");
				} else if (param.get("orderView").toString()
						.equals(ApiConstant.API_REQUEST_ORDER_DESC)) {
					requestAggregateGet.addOrderDESC(param.get("orderStr").toString()+"Aggregate");
				}
			}
		}
		
		aggregateSearch.setGroup(groupArray);
		aggregateSearch.setGet(requestAggregateGet);
		aggregateRequestBody.addAggregate(aggregateSearch);
		request.setBody(aggregateRequestBody);
		ISearchApiResponse response =  psearchApiRequestService.request(request, SearchApiConstant.API_REQUEST_TIME_OUT_LONG);
		if (response == null) {
			logger.error("查询订单数据失败");
			throw new ApiRemoteCallFailedException("查询订单数据失败");
		}

		if (response.getStatus() != SearchApiConstant.RC_SUCCESS) {
			logger.error("查询订单数据请求出错, rc={}, message={}", response.getStatus(),
					SearchApiConstant.getMessage(response.getStatus()));
			throw new ApiRemoteCallFailedException("查询订单数据请求出错");
		}

		SelectSearchApiResponseBody responseBody = (SelectSearchApiResponseBody) response
				.getBody();
		if (responseBody == null) {
			return null;
		}

		List<JSONArray> valueList = responseBody.getValueList();
		if (valueList == null || valueList.isEmpty()) {
			return null;
		}
		List<PlanOrder> planOrderList = new ArrayList<PlanOrder>();
		for (JSONArray jsonArray : valueList) {
			PlanOrderSearch planOrderSearch = new PlanOrderSearch();
			SearchEntityDefine.copyProperties(planOrderSearch, jsonArray,
					requestBodyGet.getField());
			planOrderList.add(planOrderSearch.convert());
		}
		if (pageBean != null) {
			int totalCount = responseBody.getTotal();
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
		map.put(Global.API_MAP_KEY_LIST, planOrderList);
		return map;
	}


	private SearchApiRequestFilter addDateFilter(
			SearchApiRequestFilter requestFilter, Map<String, Object> param) {
		List<SearchApiRequestFilterItem> filterItemList = requestFilter
				.getFilterItemList();
		boolean sortFlag = false;
		// filter为空或者filter中不包括order_id,plan_id,uid时,时间区间放在前面,规则就是“把区分度大的字段的条件放前面”,而order_id,plan_id,uid的区分度最大
		if (filterItemList != null && filterItemList.size() > 0) {
			for (SearchApiRequestFilterItem item : filterItemList) {
				if (!StringUtils.isEmpty(item.getField())
						&& (item.getField().equals(
								PlanOrderSearchDefine.ORDER_ID)
								|| item.getField().equals(
										PlanOrderSearchDefine.PLAN_ID) || item
								.getField().equals(PlanOrderSearchDefine.UID))) {
					sortFlag = true;
					break;
				}
			}
		} else {
			sortFlag = true;
		}
		Date createTimeStart = (Date) param
				.get(PlanOrderSearchDefine.CREATE_AT_START);
		Date createTimeEnd = (Date) param
				.get(PlanOrderSearchDefine.CREATE_AT_END);
		Date prizeTimeStart = (Date) param
				.get(PlanOrderSearchDefine.PRIZE_TIME_START);
		Date prizeTimeEnd = (Date) param
				.get(PlanOrderSearchDefine.PRIZE_TIME_END);

		if (sortFlag) {
			if (createTimeStart != null && createTimeEnd != null) {
				requestFilter.addBetween(PlanOrderSearchDefine.CREATE_AT,
						createTimeStart, createTimeEnd);
			} else if (createTimeStart != null) {
				requestFilter.addGreaterEqual(PlanOrderSearchDefine.CREATE_AT,
						createTimeStart);
			} else if (createTimeEnd != null) {
				requestFilter.addLessEqual(PlanOrderSearchDefine.CREATE_AT,
						createTimeEnd);
			}
			if (prizeTimeStart != null && prizeTimeEnd != null) {
				requestFilter.addBetween(PlanOrderSearchDefine.PRIZE_TIME,
						prizeTimeStart, prizeTimeEnd);
			} else if (prizeTimeStart != null) {
				requestFilter.addGreaterEqual(PlanOrderSearchDefine.PRIZE_TIME,
						prizeTimeStart);
			} else if (prizeTimeEnd != null) {
				requestFilter.addLessEqual(PlanOrderSearchDefine.PRIZE_TIME,
						prizeTimeEnd);
			}
			return requestFilter;
		} else {
			SearchApiRequestFilter requestFilterNew = new SearchApiRequestFilter();
			if (createTimeStart != null && createTimeEnd != null) {
				requestFilterNew.addBetween(PlanOrderSearchDefine.CREATE_AT,
						createTimeStart, createTimeEnd);
			} else if (createTimeStart != null) {
				requestFilterNew.addGreaterEqual(
						PlanOrderSearchDefine.CREATE_AT, createTimeStart);
			} else if (createTimeEnd != null) {
				requestFilterNew.addLessEqual(PlanOrderSearchDefine.CREATE_AT,
						createTimeEnd);
			}
			if (prizeTimeStart != null && prizeTimeEnd != null) {
				requestFilterNew.addBetween(PlanOrderSearchDefine.PRIZE_TIME,
						prizeTimeStart, prizeTimeEnd);
			} else if (prizeTimeStart != null) {
				requestFilterNew.addGreaterEqual(
						PlanOrderSearchDefine.PRIZE_TIME, prizeTimeStart);
			} else if (prizeTimeEnd != null) {
				requestFilterNew.addLessEqual(PlanOrderSearchDefine.PRIZE_TIME,
						prizeTimeEnd);
			}
			if (filterItemList != null && filterItemList.size() > 0) {
				for (SearchApiRequestFilterItem item : filterItemList) {
					requestFilterNew.addEqual(item.getField(), item.getValue());
				}
			}
			return requestFilterNew;
		}
	}

	public void setPsearchApiRequestService(
			SearchApiRequestService psearchApiRequestService) {
		this.psearchApiRequestService = psearchApiRequestService;
	}

	public SearchApiRequestService getPsearchApiRequestService() {
		return psearchApiRequestService;
	}
}
