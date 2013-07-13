package web.service.impl.ticket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.service.ticket.TicketQueryService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.SearchApiUrlConstant;
import com.lehecai.core.api.bean.query.IQueryProp;
import com.lehecai.core.api.bean.query.QueryOperator;
import com.lehecai.core.entity.serializer.PropSourceType;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.engine.entity.serializer.TicketPropConstant;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketQuery;

public class SearchTicketQueryServiceImpl implements TicketQueryService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ApiRequestService searchApiRequestService;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Ticket> query(TicketQuery ticketQueryProp, PageBean pageBean) throws Exception {
		logger.info("归档票数据条件查询");
		ApiRequest request = new ApiRequest();
		request.setUrl(SearchApiUrlConstant.TICKET_QUERY);
		
		TicketPropConstant ticketPropConstant = TicketPropConstant.getInstance();
		
		for (IQueryProp queryPropItem : ticketQueryProp.getQueryPropItemList()) {
			if (queryPropItem.getOperator().getValue() == QueryOperator.BETWEEN.getValue()) {
				request.setParameterBetween(ticketPropConstant.getProp(queryPropItem.getName(), PropSourceType.JSON_PROPERTY), queryPropItem.getValues()[0], queryPropItem.getValues()[1]);
			} else if (queryPropItem.getOperator().getValue() == QueryOperator.IN.getValue()) {
				List<String> valueList = new ArrayList<String>();
				for (String value : queryPropItem.getValues()) {
					valueList.add(value);
				}
				request.setParameterIn(ticketPropConstant.getProp(queryPropItem.getName(), PropSourceType.JSON_PROPERTY), valueList);
			} else {
				request.setParameter(ticketPropConstant.getProp(queryPropItem.getName(), PropSourceType.JSON_PROPERTY), queryPropItem.getValues()[0], queryPropItem.getOperator().getName());
			}
		}
		
		request.setRange(ticketQueryProp.getRange());
		
		if (ticketQueryProp.getOrderList() != null && ticketQueryProp.getOrderList().size() != 0) {
			request.addOrder(ticketQueryProp.getOrderList());
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = searchApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("归档票数据条件查询api调用失败：返回值ApiResponse为空");
			throw new ApiRemoteCallFailedException("归档票数据复杂条件查询api调用失败：返回值ApiResponse为空");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("归档票数据条件查询出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("归档票数据条件查询出错, rc=" + response.getCode() + ", message=" + response.getMessage() + "");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("归档票数据条件查询数据为空");
			return null;
		}
		
		List<Ticket> ticketList = new ArrayList<Ticket>();
		for (Iterator<JSONObject> iterator = response.getData().iterator(); iterator
				.hasNext();) {
			JSONObject jsonObject = iterator.next();
			
			Ticket ticket = ticketPropConstant.convertFromJSONObject(jsonObject);
			
			if (ticket != null) {
				ticketList.add(ticket);
			}
		}
		
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
		return ticketList;
	}

	public ApiRequestService getSearchApiRequestService() {
		return searchApiRequestService;
	}

	public void setSearchApiRequestService(ApiRequestService searchApiRequestService) {
		this.searchApiRequestService = searchApiRequestService;
	}

}
