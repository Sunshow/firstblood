package web.action.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.service.ticket.ActiveTicketUpdateService;
import com.lehecai.admin.web.service.ticket.TmpTicketService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequestOrder;
import com.lehecai.core.api.bean.query.IQueryProp;
import com.lehecai.core.api.bean.query.QueryOperator;
import com.lehecai.core.api.bean.query.impl.ListQueryProp;
import com.lehecai.core.api.bean.query.impl.SingleQueryProp;
import com.lehecai.core.api.bean.query.type.QueryDataSourceType;
import com.lehecai.core.entity.serializer.PropSourceType;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.engine.entity.serializer.TicketPropConstant;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketQuery;
import com.lehecai.engine.entity.ticket.TicketStatus;

public class UpdateTicketStatusAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private TmpTicketService tmpTicketService;
	private ActiveTicketUpdateService activeTicketUpdateService;
	
	private List<Ticket> tickets;
	private String planId;
	private String batchId;
	private String ticketId;
	private int ticketStatusValue;
	private Integer lotteryTypeId;
	private String phase;
	private Long terminalId;
	private Date printTime;
	
	
	public String handle() {
		logger.info("进入查询票列表");
		if(printTime == null){
			printTime = new Date();
		}
		return "list";
	}
	
	public String query() {
		logger.info("进入查询票列表");
		String queryMessage = "查询票列表错误，原因：方案号、票号、批次号必需至少输入一个条件；或者输入彩种+彩期两个条件。";
		if (!StringUtils.isEmpty(phase) && !phase.equals("-1") && lotteryTypeId != null && LotteryType.getItem(lotteryTypeId) != null && LotteryType.getItem(lotteryTypeId).getValue() != LotteryType.ALL.getValue()){
			queryMessage = null;
		}
		if (!StringUtils.isEmpty(planId) || !StringUtils.isEmpty(batchId) || !StringUtils.isEmpty(ticketId)) {
			queryMessage = null;
		}
		if (!StringUtils.isEmpty(queryMessage)) {
			super.setErrorMessage(queryMessage);
			super.setForwardUrl("/business/updateTicketStatus.do");
			return "failure";
		}
		
		PageBean pageBean = super.getPageBean();
		pageBean.setPageSize(100);
		QueryDataSourceType queryDataSourceType = QueryDataSourceType.ACTIVE;
		
		TicketQuery ticketQueryProp = new TicketQuery();
		List<IQueryProp> queryPropItemList = new ArrayList<IQueryProp>();
		if (!StringUtils.isEmpty(planId)) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_PLAN_ID, QueryOperator.EQUAL, planId));
		}
		if (!StringUtils.isEmpty(batchId)) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_BATCH_ID, QueryOperator.EQUAL, batchId));
		}
		if (!StringUtils.isEmpty(ticketId)) {
			List<String> ticketIdList = new ArrayList<String>();
			String[] ticketIdArray = StringUtils.split(ticketId, ",");
			for (String t : ticketIdArray) {
				ticketIdList.add(t);
			}
			queryPropItemList.add(new ListQueryProp(TicketPropConstant.PROP_ID, QueryOperator.IN, ticketIdList));
		}
		
		if (TicketStatus.getItem(ticketStatusValue) != null) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_STATUS, QueryOperator.EQUAL, ticketStatusValue + ""));
		}
		
		if (lotteryTypeId != null && LotteryType.getItem(lotteryTypeId) != null && LotteryType.getItem(lotteryTypeId).getValue() != LotteryType.ALL.getValue()) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_LOTTERY_TYPE, QueryOperator.EQUAL, lotteryTypeId + ""));
		}
		if (!StringUtils.isEmpty(phase) && !phase.equals("-1")) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_PHASE, QueryOperator.EQUAL, phase));
		}
		if (terminalId != null && terminalId > 0) {
			queryPropItemList.add(new SingleQueryProp(TicketPropConstant.PROP_TERMINAL_ID, QueryOperator.EQUAL, terminalId + ""));
		}
		
		ticketQueryProp.setQueryPropItemList(queryPropItemList);
		try {
			ticketQueryProp.setRange(TicketQuery.buildRange(queryPropItemList));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage(e.getMessage());
			return "failure";
		}
		
		TicketPropConstant ticketPropConstant = TicketPropConstant.getInstance();
		List<ApiRequestOrder> orderList = new ArrayList<ApiRequestOrder>();
		ApiRequestOrder order = new ApiRequestOrder(ticketPropConstant.getProp(TicketPropConstant.PROP_ID, PropSourceType.JSON_PROPERTY ), ApiConstant.API_REQUEST_ORDER_ASC);
		orderList.add(order);
		ticketQueryProp.setOrderList(orderList);
		
		try {
			tickets = tmpTicketService.query(ticketQueryProp, pageBean, queryDataSourceType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage(e.getMessage());
			return "failure";
		}
		
		super.setPageString(PageUtil.getPageString(ServletActionContext.getRequest(), pageBean));
		super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		
		if(printTime == null){
			printTime = new Date();
		}
		
		return "list";
	}
	
	public String unallotted(){
		if (tickets == null || tickets.size() == 0) {
			logger.error("批量更新票状态失败，原因：tickets为空");
			super.setErrorMessage("批量更新票状态失败，原因：tickets为空");
			return "failure";
		}
		
		HttpServletResponse response = ServletActionContext.getResponse();
		for (Ticket ticket : tickets) {
			activeTicketUpdateService.updateStatus(ticket.getId(), TicketStatus.UNALLOTTED);
		}
	    
		JSONObject obj = new JSONObject();
		obj.put("message", "更新票状态为" + TicketStatus.UNALLOTTED.getName() + "成功！");
		super.writeRs(response, obj);
		return null;
	}
	
	public String unsent(){
		if (tickets == null || tickets.size() == 0) {
			logger.error("批量更新票状态失败，原因：tickets为空");
			super.setErrorMessage("批量更新票状态失败，原因：tickets为空");
			return "failure";
		}
		
		HttpServletResponse response = ServletActionContext.getResponse();
		for (Ticket ticket : tickets) {
			activeTicketUpdateService.updateStatus(ticket.getId(), TicketStatus.UNSENT);
		}
	    
		JSONObject obj = new JSONObject();
		obj.put("message", "更新票状态为" + TicketStatus.UNSENT.getName() + "成功！");
		super.writeRs(response, obj);
		return null;
	}

	public String printSuccess(){
		if (tickets == null || tickets.size() == 0) {
			logger.error("批量更新票状态失败，原因：tickets为空");
			super.setErrorMessage("批量更新票状态失败，原因：tickets为空");
			return "failure";
		}
		
		HttpServletResponse response = ServletActionContext.getResponse();
		for (Ticket ticket : tickets) {
			if(printTime == null){
				printTime = new Date();
			}
			ticket.setStatus(TicketStatus.PRINT_SUCCESS);
			ticket.setPrintTime(printTime);
			activeTicketUpdateService.updateStatus(ticket);
		}
	    
		JSONObject obj = new JSONObject();
		obj.put("message", "更新票状态为" + TicketStatus.PRINT_SUCCESS.getName() + "成功！");
		super.writeRs(response, obj);
		return null;
	}
	
	public List<TicketStatus> getTicketStatusList() {
		List<TicketStatus> list = new ArrayList<TicketStatus>();
		list.add(TicketStatus.PRINTING);
		list.add(TicketStatus.UNSENT);
		return list;
	}
	
	public List<LotteryType> getLotteryTypes() {
		return LotteryType.getItems();
	}
	
	public TicketStatus getUnallottedStatus() {
		return TicketStatus.UNALLOTTED;
	}
	
	public TicketStatus getUnsentStatus() {
		return TicketStatus.UNSENT;
	}
	
	public TicketStatus getPrintSuccessStatus() {
		return TicketStatus.PRINT_SUCCESS;
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

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public int getTicketStatusValue() {
		return ticketStatusValue;
	}

	public void setTicketStatusValue(int ticketStatusValue) {
		this.ticketStatusValue = ticketStatusValue;
	}

	public ActiveTicketUpdateService getActiveTicketUpdateService() {
		return activeTicketUpdateService;
	}

	public void setActiveTicketUpdateService(
			ActiveTicketUpdateService activeTicketUpdateService) {
		this.activeTicketUpdateService = activeTicketUpdateService;
	}

	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}

	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public String getPhase() {
		return phase;
	}

	public void setTerminalId(Long terminalId) {
		this.terminalId = terminalId;
	}

	public Long getTerminalId() {
		return terminalId;
	}

	public Date getPrintTime() {
		return printTime;
	}

	public void setPrintTime(Date printTime) {
		this.printTime = printTime;
	}
	
}
