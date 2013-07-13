package web.action.include.statics.ticket;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.service.ticket.TmpTicketService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequestDateRange;
import com.lehecai.core.api.ApiRequestOrder;
import com.lehecai.core.api.ApiRequestRange;
import com.lehecai.core.api.bean.query.IQueryProp;
import com.lehecai.core.api.bean.query.QueryOperator;
import com.lehecai.core.api.bean.query.impl.ListQueryProp;
import com.lehecai.core.api.bean.query.impl.SingleQueryProp;
import com.lehecai.core.api.bean.query.type.QueryDataSourceType;
import com.lehecai.core.entity.serializer.PropSourceType;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.service.archive.ArchiveIdService;
import com.lehecai.core.type.archive.ArchiveEntityKey;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.engine.entity.serializer.TicketPropConstant;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketQuery;
import com.opensymphony.xwork2.Action;

public class TmpTicketInfoAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private static final long serialVersionUID = 1L;
	
	private TmpTicketService tmpTicketService;
	private ArchiveIdService archiveIdService;
	
	private String plan_id;//方案编码
	private int page = 1;
	private int pagesize = 20;
	
	public String handle(){
		logger.info("开始执行查询票信息");
		
		Integer rc = 0;//0成功,1失败
		String message = "操作成功";
		
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject dataJson = new JSONObject();
		
		if (plan_id == null || "".equals(plan_id)) {
			rc = 1 ;
			message = "查看票信息:参数plan_id不能为空";
			logger.error("信息错误:查看票信息参数plan_id不能为空");
			return makeJson(response, rc, message, dataJson);
		}
		
		if (page < 1) {
			page = 1;
		}
		
		if (pagesize < 1) {
			pagesize = 20;
		}
		
		if (pagesize > 100) {
			pagesize = 100;
		}
		
		int cnt = page * pagesize;
		if (cnt - pagesize + 1 > 1000) {
			rc = 2 ;
			message = "查看票信息:只能查看最新的1000条记录";
			logger.error("信息错误:查看票信息只能查看最新的1000条记录");
			return makeJson(response, rc, message, dataJson);
		}
		
		PageBean pageBean = super.getPageBean();
		pageBean.setPage(page);
		if (cnt > 1000) {
			pageBean.setPageSize(1000 - (cnt - pagesize));
		} else {
			pageBean.setPageSize(pagesize);
		}
		JSONArray jsonArray = new JSONArray();
		
		TicketQuery ticketQueryProp = new TicketQuery();
		
		List<IQueryProp> queryPropItemList = new ArrayList<IQueryProp>();
		queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_PLAN_ID, QueryOperator.EQUAL, plan_id));
		ticketQueryProp.setQueryPropItemList(queryPropItemList);
		try {
			ticketQueryProp.setRange(TicketQuery.buildRange(queryPropItemList));
		} catch (Exception e) {
			rc = 1 ;
			message = "查看票信息:生成range错误";
			logger.error("信息错误:生成range错误");
			logger.error(e.getMessage(), e);
			return makeJson(response, rc, message, dataJson);
		}
		TicketPropConstant ticketPropConstant = TicketPropConstant.getInstance();
		
		List<ApiRequestOrder> orderList = new ArrayList<ApiRequestOrder>();
		ApiRequestOrder order = new ApiRequestOrder(ticketPropConstant.getProp(TicketPropConstant.PROP_ID, PropSourceType.JSON_PROPERTY), ApiConstant.API_REQUEST_ORDER_ASC);
		orderList.add(order);
		ticketQueryProp.setOrderList(orderList);
		
		String archiveIdString = null;
		try {
			archiveIdString = archiveIdService.getArchiveId(ArchiveEntityKey.TICKET);
		} catch (ApiRemoteCallFailedException e) {
			rc = 1 ;
			message = "查看票信息:查询最新归档数据id失败";
			logger.error("信息错误:查询最新归档数据id失败");
			logger.error(e.getMessage(), e);
			return makeJson(response, rc, message, dataJson);
		}
		Date archiveIdDate = CoreDateUtils.parseDate(archiveIdString.substring(0, 6), "yyMMdd");
		
		QueryDataSourceType queryDataSourceType = null;
		ApiRequestRange range = ticketQueryProp.getRange();
		Date rangeBeginDate = CoreDateUtils.parseDate(range.getBegin());
	
		if (archiveIdDate.before(rangeBeginDate)) {
			queryDataSourceType = QueryDataSourceType.ACTIVE;
		} else {
			queryDataSourceType = QueryDataSourceType.SEARCH;
			if (CoreDateUtils.parseDate(range.getEnd()).after(archiveIdDate)) {
				ApiRequestDateRange tmpRange = new ApiRequestDateRange(rangeBeginDate, archiveIdDate);
				ticketQueryProp.setRange(tmpRange);
			}
		}
		
		List<Ticket> tickets = null;
		try {
			tickets = tmpTicketService.query(ticketQueryProp, pageBean, queryDataSourceType);
		} catch (Exception e) {
			rc = 1 ;
			message = "查看票信息:查询" + queryDataSourceType.getName() + "数据失败";
			logger.error("信息错误:查询{}数据失败", queryDataSourceType.getName());
			logger.error(e.getMessage(), e);
			return makeJson(response, rc, message, dataJson);
		}
		if (tickets == null || tickets.size() == 0) {
			if (queryDataSourceType.getValue() == QueryDataSourceType.ACTIVE.getValue()) {
				queryDataSourceType = QueryDataSourceType.SEARCH;
			} else {
				queryDataSourceType = QueryDataSourceType.ACTIVE;
			}
			
			try {
				tickets = tmpTicketService.query(ticketQueryProp, pageBean, queryDataSourceType);
			} catch (Exception e) {
				rc = 1 ;
				message = "查看票信息:查询" + queryDataSourceType.getName() + "数据失败";
				logger.error("信息错误:查询{}数据失败", queryDataSourceType.getName());
				logger.error(e.getMessage(), e);
				return makeJson(response, rc, message, dataJson);
			}
		}
		
		if (tickets == null || tickets.size() == 0) {
			rc = 1 ;
			message = "查看票信息:查询" + queryDataSourceType.getName() + "数据列表返回值为空";
			logger.error("信息错误:查询{}数据列表返回值为空", queryDataSourceType.getName());
			return makeJson(response, rc, message, dataJson);
		}
		
		if (queryDataSourceType.getValue() == QueryDataSourceType.SEARCH.getValue()) {
			List<String> valueList = new ArrayList<String>();
			for (Ticket ticket : tickets) {
				valueList.add(String.valueOf(ticket.getId()));
			}
			
			ticketQueryProp = new TicketQuery();
			
			queryPropItemList = new ArrayList<IQueryProp>();
			queryPropItemList.add(new ListQueryProp(TicketPropConstant.PROP_ID, QueryOperator.IN, valueList));
			ticketQueryProp.setQueryPropItemList(queryPropItemList);
			try {
				tickets = tmpTicketService.query(ticketQueryProp, pageBean, QueryDataSourceType.ACHIVED);
			} catch (Exception e) {
				rc = 1 ;
				message = "查看票信息:查询" + queryDataSourceType.getName() + "数据失败";
				logger.error("信息错误:查询{}数据失败", queryDataSourceType.getName());
				logger.error(e.getMessage(), e);
				return makeJson(response, rc, message, dataJson);
			}
		}
		
		for (Ticket t : tickets) {
			JSONObject d = new JSONObject();
			d.put("id", t.getId());
			d.put("lottery_type", t.getLotteryType().getValue());
			d.put("status", t.getStatus().getValue());
			d.put("content", t.getContent());
			d.put("amount", t.getAmount());
			d.put("create_time", t.getCreateTime() == null ? null : CoreDateUtils.formatDate(t.getCreateTime(), CoreDateUtils.DATETIME));
			d.put("print_time", t.getPrintTime() == null ? null : CoreDateUtils.formatDate(t.getPrintTime(), CoreDateUtils.DATETIME));
			d.put("send_time", t.getSendTime() == null ? null : CoreDateUtils.formatDate(t.getSendTime(), CoreDateUtils.DATETIME));
			d.put("multiple", t.getMultiple());
			d.put("play_type", t.getPlayType().getValue());
			d.put("dlt_addition", t.getDltAddition().getValue());
			d.put("is_winning", t.getIsWinning().getValue());
			d.put("winning_amount", t.getWinningAmount());
			d.put("winning_detail", t.getWinningDetail());
			d.put("ext", t.getExt());
			jsonArray.add(d);
		}
		dataJson.put("data", jsonArray);
		
		int count = pageBean.getCount();
		dataJson.put("count", count);
		
		logger.info("结束执行查询票信息");
		return makeJson(response, rc, message, dataJson);
	}
	
	protected String makeJson(HttpServletResponse response, Integer rc, String message, JSONObject dataJson) {
		JSONObject json = new JSONObject();
		json.put("code", rc);
		json.put("message", message);
		json.put("data", dataJson);
		
		super.writeRs(response, json);
		return Action.NONE;
	}

	public TmpTicketService getTmpTicketService() {
		return tmpTicketService;
	}

	public void setTmpTicketService(TmpTicketService tmpTicketService) {
		this.tmpTicketService = tmpTicketService;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String planId) {
		plan_id = planId;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public ArchiveIdService getArchiveIdService() {
		return archiveIdService;
	}

	public void setArchiveIdService(ArchiveIdService archiveIdService) {
		this.archiveIdService = archiveIdService;
	}
}
