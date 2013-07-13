package web.action.ticket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.action.lottery.LotteryDrawAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.service.ticket.TmpTicketService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequestDateRange;
import com.lehecai.core.api.ApiRequestOrder;
import com.lehecai.core.api.ApiRequestRange;
import com.lehecai.core.api.bean.query.IQueryProp;
import com.lehecai.core.api.bean.query.QueryOperator;
import com.lehecai.core.api.bean.query.impl.RegionQueryProp;
import com.lehecai.core.api.bean.query.impl.SingleQueryProp;
import com.lehecai.core.api.bean.query.type.QueryDataSourceType;
import com.lehecai.core.api.lottery.LotteryConfig;
import com.lehecai.core.entity.serializer.PropSourceType;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryConstant;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.core.service.archive.ArchiveIdService;
import com.lehecai.core.service.lottery.LotteryCommonService;
import com.lehecai.core.type.archive.ArchiveEntityKey;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.engine.entity.serializer.TicketPropConstant;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketQuery;
import com.lehecai.engine.entity.ticket.TicketStatus;

public class TmpTicketAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private static final Logger logger = LoggerFactory.getLogger(LotteryDrawAction.class);

	private LotteryCommonService lotteryCommonService;
	
	private String lotteryConfigData;

	private TmpTicketService tmpTicketService;
	private ArchiveIdService archiveIdService;
	
	private List<Ticket> tickets;
	private Ticket ticket = new Ticket();
	private String target;
	private String lotteryTypeValue;
	private Integer queryDataSourceTypeValue;
	
	private Date beginCreateTime;				//拆票起始时间
	private Date endCreateTime;					//拆票截止时间
	private Date beginPrintTime;				//出票起始时间
	private Date endPrintTime;					//出票截止时间
	private Date beginSendTime;					//送票起始时间
	private Date endSendTime;					//送票截止时间
	private String orderStr = "id";	//排序字段
	private String orderView = ApiConstant.API_REQUEST_ORDER_DESC;	//排序方式
	
	private Map<String, String> orderStrMap;	//排序字段列表
	private Map<String, String> orderViewMap;	//排序方式
	
	private String jcspStr;
	
	public String handle() {
		logger.info("进入查询票列表");
		if (beginCreateTime == null
				&& (ticket.getPlanNo() == null || ticket.getPlanNo().equals(""))
				&& (ticket.getBatchId() == null)) {						//默认拆票起始时间
			beginCreateTime = getDefaultQueryBeginDate();
		}
		return "list";
	}
	
	public String query() {
		logger.info("进入查询票列表");
		
		if (beginCreateTime != null && endCreateTime != null) {
			 if (beginCreateTime.after(endCreateTime)) {
				 logger.error("拆票起始时间应小于拆票截止时间");
				 super.setErrorMessage("拆票起始时间应小于拆票截止时间");
				 return "failure";
			 }
		 }
		 if (beginPrintTime != null && endPrintTime != null) {
			 if (beginPrintTime.after(endPrintTime)) {
				 logger.error("出票起始时间应小于出票截止时间");
				 super.setErrorMessage("出票起始时间应小于出票截止时间");
				 return "failure";
			 }
		 }
		 if (beginSendTime != null && endSendTime != null) {
			 if (beginSendTime.after(endSendTime)) {
				 logger.error("送票起始时间应小于送票截止时间");
				 super.setErrorMessage("送票起始时间应小于送票截止时间");
				 return "failure";
			 }
		 }
		 
		if (beginCreateTime != null && endCreateTime == null) {
			Calendar cd = Calendar.getInstance();
			cd.setTime(beginCreateTime);
			cd.add(Calendar.DATE, 7);
			cd.add(Calendar.HOUR_OF_DAY, 23);
			cd.add(Calendar.MINUTE, 59);
			cd.add(Calendar.SECOND, 59);
			endCreateTime = cd.getTime();
		}
		if (beginPrintTime != null && endPrintTime == null) {
			Calendar cd = Calendar.getInstance();
			cd.setTime(beginPrintTime);
			cd.add(Calendar.DATE, 7);
			cd.add(Calendar.HOUR_OF_DAY, 23);
			cd.add(Calendar.MINUTE, 59);
			cd.add(Calendar.SECOND, 59);
			endPrintTime = cd.getTime();
		}
		if (beginSendTime != null && endSendTime == null) {
			Calendar cd = Calendar.getInstance();
			cd.setTime(beginSendTime);
			cd.add(Calendar.DATE, 7);
			cd.add(Calendar.HOUR_OF_DAY, 23);
			cd.add(Calendar.MINUTE, 59);
			cd.add(Calendar.SECOND, 59);
			endSendTime = cd.getTime();
		}
		
		PageBean pageBean = super.getPageBean();
		QueryDataSourceType queryDataSourceType = queryDataSourceTypeValue == null ? QueryDataSourceType.AUTO : QueryDataSourceType.getItem(queryDataSourceTypeValue);
		
		TicketQuery ticketQueryProp = new TicketQuery();
		List<IQueryProp> queryPropItemList = new ArrayList<IQueryProp>();
		if (!StringUtils.isEmpty(lotteryTypeValue) && Integer.valueOf(lotteryTypeValue) != LotteryType.ALL.getValue()) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_LOTTERY_TYPE, QueryOperator.EQUAL, lotteryTypeValue));
		}
		if (!StringUtils.isEmpty(ticket.getPhase())) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_PHASE, QueryOperator.EQUAL, ticket.getPhase()));
		}
		if (!StringUtils.isEmpty(ticket.getPlanNo())) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_PLAN_ID, QueryOperator.EQUAL, ticket.getPlanNo()));
		}
		if (ticket.getBatchId() != null && ticket.getBatchId() != 0L) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_BATCH_ID, QueryOperator.EQUAL, ticket.getBatchId() + ""));
		}
		if (ticket.getId() != null && ticket.getId() != 0L) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_ID, QueryOperator.EQUAL, ticket.getId() + ""));
		}
		if (!StringUtils.isEmpty(ticket.getUsername())) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_USERNAME, QueryOperator.EQUAL, ticket.getUsername()));
		}
		if (ticket.getTerminalId() != null && ticket.getTerminalId() != 0L) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_TERMINAL_ID, QueryOperator.EQUAL, ticket.getTerminalId() + ""));
		}
		if (ticket.getStatus() != null && ticket.getStatus().getValue() != TicketStatus.ALL.getValue()) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_STATUS, QueryOperator.EQUAL, ticket.getStatus().getValue() + ""));
		}
		if (ticket.getIsWinning() != null && ticket.getIsWinning().getValue() != YesNoStatus.ALL.getValue()) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_IS_WINNING, QueryOperator.EQUAL, ticket.getIsWinning().getValue() + ""));
		}
		if (beginCreateTime != null || endCreateTime != null) {
			String beginCreateTimeStr = beginCreateTime == null ? null : CoreDateUtils.formatDateTime(beginCreateTime);
			String endCreateTimeStr = endCreateTime == null ? null : CoreDateUtils.formatDateTime(endCreateTime);
			queryPropItemList.add(new RegionQueryProp(TicketPropConstant.PROP_CREATE_TIME, QueryOperator.BETWEEN, beginCreateTimeStr, endCreateTimeStr));
		}
		if (beginPrintTime != null || endPrintTime != null) {
			String beginPrintTimeStr = beginPrintTime == null ? null : CoreDateUtils.formatDateTime(beginPrintTime);
			String endPrintTimeStr = endPrintTime == null ? null : CoreDateUtils.formatDateTime(endPrintTime);
			queryPropItemList.add(new RegionQueryProp(TicketPropConstant.PROP_PRINT_TIME, QueryOperator.BETWEEN, beginPrintTimeStr, endPrintTimeStr));
		}
		if (beginSendTime != null || endSendTime != null) {
			String beginSendTimeStr = beginSendTime == null ? null : CoreDateUtils.formatDateTime(beginSendTime);
			String endSendTimeStr = endSendTime == null ? null : CoreDateUtils.formatDateTime(endSendTime);
			queryPropItemList.add(new RegionQueryProp(TicketPropConstant.PROP_SEND_TIME, QueryOperator.BETWEEN, beginSendTimeStr, endSendTimeStr));
		}
		
		ticketQueryProp.setQueryPropItemList(queryPropItemList);
		try {
			ticketQueryProp.setRange(TicketQuery.buildRange(queryPropItemList));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage(e.getMessage());
			return "failure";
		}
		
		if (queryDataSourceType.getValue() == QueryDataSourceType.AUTO.getValue()) {
			String archiveIdString = null;
			try {
				archiveIdString = archiveIdService.getArchiveId(ArchiveEntityKey.TICKET);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("查询最新归档数据id失败");
				return "failure";
			}
			Date archiveIdDate = CoreDateUtils.parseDate(archiveIdString.substring(0, 6), "yyMMdd");
			
			ApiRequestRange range = ticketQueryProp.getRange();
			if (range == null) {
				logger.error("至少设置一个能确定查询时间范围的条件");
				super.setErrorMessage("至少设置一个能确定查询时间范围的条件");
				return "failure";
			}
			Date rangeEndDate = CoreDateUtils.parseDate(range.getEnd());
		
			if (rangeEndDate.before(archiveIdDate)) {
				queryDataSourceType = QueryDataSourceType.SEARCH;
			} else {
				queryDataSourceType = QueryDataSourceType.ACTIVE;
			}
		} else if (queryDataSourceType.getValue() == QueryDataSourceType.SEARCH.getValue()) {
			String archiveIdString = null;
			try {
				archiveIdString = archiveIdService.getArchiveId(ArchiveEntityKey.TICKET);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("查询最新归档数据id失败");
				return "failure";
			}
			Date archiveIdDate = CoreDateUtils.parseDate(archiveIdString.substring(0, 6), "yyMMdd");
			
			ApiRequestRange range = ticketQueryProp.getRange();
			if (range == null) {
				logger.error("至少设置一个能确定查询时间范围的条件");
				super.setErrorMessage("至少设置一个能确定查询时间范围的条件");
				return "failure";
			}
			
			Date rangeEndDate = CoreDateUtils.parseDate(range.getEnd());
			if (rangeEndDate.after(archiveIdDate)) {
				ApiRequestDateRange tmpRange = new ApiRequestDateRange(CoreDateUtils.parseDate(range.getBegin()), archiveIdDate);
				ticketQueryProp.setRange(tmpRange);
			}
		}
		TicketPropConstant ticketPropConstant = TicketPropConstant.getInstance();
		List<ApiRequestOrder> orderList = new ArrayList<ApiRequestOrder>();
		ApiRequestOrder order = new ApiRequestOrder(ticketPropConstant.getProp(orderStr, queryDataSourceType.getValue() == QueryDataSourceType.SEARCH.getValue() ? PropSourceType.JSON_PROPERTY : PropSourceType.JAVA_PROPERTY), orderView);
		orderList.add(order);
		ticketQueryProp.setOrderList(orderList);
		
		try {
			tickets = tmpTicketService.query(ticketQueryProp, pageBean, queryDataSourceType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage(e.getMessage());
			return "failure";
		}
		if ((queryDataSourceTypeValue == null || QueryDataSourceType.getItem(queryDataSourceTypeValue).getValue() == QueryDataSourceType.AUTO.getValue()) && (tickets == null || tickets.isEmpty())) {
			if (queryDataSourceType.getValue() == QueryDataSourceType.ACTIVE.getValue()) {
				queryDataSourceType = QueryDataSourceType.SEARCH;
				
				String archiveIdString = null;
				try {
					archiveIdString = archiveIdService.getArchiveId(ArchiveEntityKey.TICKET);
				} catch (ApiRemoteCallFailedException e) {
					logger.error(e.getMessage(), e);
					super.setErrorMessage("查询最新归档数据id失败");
					return "failure";
				}
				Date archiveIdDate = CoreDateUtils.parseDate(archiveIdString.substring(0, 6), "yyMMdd");
				
				ApiRequestRange range = ticketQueryProp.getRange();
				if (range == null) {
					logger.error("至少设置一个能确定查询时间范围的条件");
					super.setErrorMessage("至少设置一个能确定查询时间范围的条件");
					return "failure";
				}
				
				Date rangeEndDate = CoreDateUtils.parseDate(range.getEnd());
				if (rangeEndDate.after(archiveIdDate)) {
					ApiRequestDateRange tmpRange = new ApiRequestDateRange(CoreDateUtils.parseDate(range.getBegin()), archiveIdDate);
					ticketQueryProp.setRange(tmpRange);
				}
			} else {
				queryDataSourceType = QueryDataSourceType.ACTIVE;
			}
			try {
				tickets = tmpTicketService.query(ticketQueryProp, pageBean, queryDataSourceType);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage(e.getMessage());
				return "failure";
			}
			
		}
		super.setPageString(PageUtil.getPageString(ServletActionContext.getRequest(), pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		
		if (target != null && !target.isEmpty()) {
			return target;
		}
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String view() {
		logger.info("进入查看票明细");
		PageBean pageBean = super.getPageBean();
		
		TicketQuery ticketQueryProp = new TicketQuery();
		
		List<IQueryProp> queryPropItemList = new ArrayList<IQueryProp>();
		queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_ID, QueryOperator.EQUAL, ticket.getId() + ""));
		ticketQueryProp.setQueryPropItemList(queryPropItemList);
		try {
			ticketQueryProp.setRange(TicketQuery.buildRange(queryPropItemList));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage(e.getMessage());
			return "failure";
		}
		
		String ticketIdString = ticket.getId() + "";
		Date ticketIdDate = CoreDateUtils.parseDate(ticketIdString.substring(0, 6), "yyMMdd");
		
		String archiveIdString = null;
		try {
			archiveIdString = archiveIdService.getArchiveId(ArchiveEntityKey.TICKET);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("查询最新归档数据id失败");
			return "failure";
		}
		Date archiveIdDate = CoreDateUtils.parseDate(archiveIdString.substring(0, 6), "yyMMdd");
		
		QueryDataSourceType queryDataSourceType = QueryDataSourceType.ACTIVE;
		if (ticketIdDate.before(archiveIdDate)) {
			queryDataSourceType = QueryDataSourceType.ACHIVED;
		}
		
		try {
			tickets = tmpTicketService.query(ticketQueryProp, pageBean, queryDataSourceType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage(e.getMessage());
			return "failure";
		}
		if (tickets == null || tickets.size() == 0) {
			if (queryDataSourceType.getValue() == QueryDataSourceType.ACTIVE.getValue()) {
				queryDataSourceType = QueryDataSourceType.ACHIVED;
			} else {
				queryDataSourceType = QueryDataSourceType.ACTIVE;
			}
			
			try {
				tickets = tmpTicketService.query(ticketQueryProp, pageBean, queryDataSourceType);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage(e.getMessage());
				return "failure";
			}
		}
		
		if (tickets == null || tickets.size() == 0) {
			logger.error("查询票列表返回值为空");
			super.setErrorMessage("查询票列表返回值为空");
			return "failure";
		}
		ticket = tickets.get(0);
		if (ticket != null && ticket.getExt() != null && !ticket.getExt().equals("")) {
			jcspStr = "";
			String ext = ticket.getExt();
			JSONObject jsonObject = JSONObject.fromObject(ext);
			for (Iterator iterator = jsonObject.keys(); iterator.hasNext();) {
				String key = (String)iterator.next();
				String matchNum = jsonObject.getJSONObject(key).getString("match_num");
				String spMap = "";
				try {
					spMap = jsonObject.getJSONObject(key).getString("sp_map");
					if (spMap != null && !spMap.equals("")) {
						JSONObject j = JSONObject.fromObject(spMap);
						spMap = "";
						for (Iterator it = j.keys(); it.hasNext();) {
							String k = (String)it.next();
							String v = j.getString(k);
							spMap = spMap + LotteryConstant.getLotteryConstantName(ticket.getLotteryType(), k) + ",SP:" + v + ";";
						}
					}
				} catch (Exception e) {
					logger.info("SP信息解析出错");
				}
			
				String handicap = "";
				try {
					handicap = jsonObject.getJSONObject(key).getString(LotteryConstant.JCLQ_RFSF_HANDICAP);
				} catch (Exception e) {
					logger.info("无让分信息");
				}
				String presetScore = "";
				try{
					presetScore = jsonObject.getJSONObject(key).getString(LotteryConstant.JCLQ_DXF_PRESETSCORE);
				} catch (Exception e) {
					logger.info("无大小分信息");
				}
				jcspStr += matchNum + (presetScore == null || presetScore.equals("") ? "" : "(预设总分:" + presetScore + ")")
							+ (handicap == null || handicap.equals("") ? "" : "(让分:" + handicap + ")") 
							+ "(" + (spMap == null || spMap.equals("") ? "" : spMap) + "),\n";
			}
		}
		LotteryConfig lotteryConfig = lotteryCommonService.getLotteryConfigFromCache(ticket.getLotteryType());
		if (lotteryConfig != null) {
			lotteryConfigData = JSONArray.fromObject(lotteryConfig.getResultDetailTemplateItemList()).toString();
		}
		if (target != null && !target.isEmpty()) {
			super.setForwardUrl("/ticket/tmpTicket.do?ticket.planNo="+ticket.getPlanNo()+"&target="+target);
		} else {
			super.setForwardUrl("/ticket/tmpTicket.do");
		}
		logger.info("查看票明细结束");
		return "view";
	}

	public TmpTicketService getTmpTicketService() {
		return tmpTicketService;
	}

	public void setTmpTicketService(TmpTicketService tmpTicketService) {
		this.tmpTicketService = tmpTicketService;
	}

	public List<Ticket> getTickets() {
		return tickets;
	}
	public void setTickets(List<Ticket> tickets) {
		this.tickets = tickets;
	}
	public Ticket getTicket() {
		return ticket;
	}
	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}
	
	public List<YesNoStatus> getYesNoStatus() {
		return YesNoStatus.getItemsForQuery();
	}
	
	public List<LotteryType> getLotteryTypes() {
		return OnSaleLotteryList.getForQuery();
	}
	public void setPhaseType(int type) {
		ticket.setLotteryType(LotteryType.getItem(type));
	}
	public List<TicketStatus> getTicketStatus() {
		return TicketStatus.getItems();
	}
	public void setStatus(int status) {
		ticket.setStatus(TicketStatus.getItem(status));
	}
	public void setIsWinning (int isWinning) {
		ticket.setIsWinning(YesNoStatus.getItem(isWinning));
	}
	public Date getBeginCreateTime() {
		return beginCreateTime;
	}
	public void setBeginCreateTime(Date beginCreateTime) {
		this.beginCreateTime = beginCreateTime;
	}
	public Date getEndCreateTime() {
		return endCreateTime;
	}
	public void setEndCreateTime(Date endCreateTime) {
		this.endCreateTime = endCreateTime;
	}
	public Date getBeginPrintTime() {
		return beginPrintTime;
	}
	public void setBeginPrintTime(Date beginPrintTime) {
		this.beginPrintTime = beginPrintTime;
	}
	public Date getEndPrintTime() {
		return endPrintTime;
	}
	public void setEndPrintTime(Date endPrintTime) {
		this.endPrintTime = endPrintTime;
	}
	public Date getBeginSendTime() {
		return beginSendTime;
	}
	public void setBeginSendTime(Date beginSendTime) {
		this.beginSendTime = beginSendTime;
	}
	public Date getEndSendTime() {
		return endSendTime;
	}
	public void setEndSendTime(Date endSendTime) {
		this.endSendTime = endSendTime;
	}
	public void setOrderStrMap(Map<String, String> orderStrMap) {
		this.orderStrMap = orderStrMap;
	}
	public void setOrderViewMap(Map<String, String> orderViewMap) {
		this.orderViewMap = orderViewMap;
	}
	public String getOrderStr() {
		return orderStr;
	}
	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}
	public String getOrderView() {
		return orderView;
	}
	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put("createTime", "拆票生成时间");
		orderStrMap.put("amount", "票金额");
		orderStrMap.put("planNo", "方案编号");
		orderStrMap.put("phase", "期数");
		orderStrMap.put("id", "票ID");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}
	public String getLotteryTypeValue() {
		return lotteryTypeValue;
	}
	public void setLotteryTypeValue(String lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}
	public String getLotteryConfigData() {
		return lotteryConfigData;
	}
	public void setLotteryConfigData(String lotteryConfigData) {
		this.lotteryConfigData = lotteryConfigData;
	}

	public String getJcspStr() {
		return jcspStr;
	}

	public void setJcspStr(String jcspStr) {
		this.jcspStr = jcspStr;
	}

	public Integer getQueryDataSourceTypeValue() {
		return queryDataSourceTypeValue;
	}

	public void setQueryDataSourceTypeValue(Integer queryDataSourceTypeValue) {
		this.queryDataSourceTypeValue = queryDataSourceTypeValue;
	}
	
	public List<QueryDataSourceType> getQueryDataSourceTypeList() {
		return QueryDataSourceType.getItems();
	}

	public ArchiveIdService getArchiveIdService() {
		return archiveIdService;
	}

	public void setArchiveIdService(ArchiveIdService archiveIdService) {
		this.archiveIdService = archiveIdService;
	}

	public LotteryCommonService getLotteryCommonService() {
		return lotteryCommonService;
	}

	public void setLotteryCommonService(LotteryCommonService lotteryCommonService) {
		this.lotteryCommonService = lotteryCommonService;
	}
	
}
