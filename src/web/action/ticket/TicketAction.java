package web.action.ticket;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.ticket.TicketService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryConstant;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.core.service.setting.SettingService;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketStatus;

public class TicketAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private SettingService settingService;
	private String lotteryConfigData;

	private TicketService ticketService;
	private List<Ticket> tickets;
	private Ticket ticket = new Ticket();
	private String target;
	private String lotteryTypeValue;
	
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
		return "list";
	}
	
	@SuppressWarnings("unchecked")
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
		 
		 if(StringUtils.isEmpty(target) && !checkQueryCondition(ticket,beginSendTime,endSendTime)){
			 logger.error("票号、方案号、批次号 、彩种+彩期、出票编号+外部票号、终端号+票状态+送票时间至少满足一个条件");
			 super.setErrorMessage("票号、方案号、批次号 、彩种+彩期、出票编号+外部票号、终端号+票状态+送票时间至少满足一个条件");
			 return "failure";
		 }
		 
		if (beginCreateTime == null
				&& (ticket.getPlanNo() == null || ticket.getPlanNo().equals(""))
				&& (ticket.getBatchId() == null)) {						//默认拆票起始时间
			beginCreateTime = getDefaultQueryBeginDate();
		}
		
		Map<String, Object> map = ticketService.getResult(ticket, beginCreateTime, endCreateTime, beginPrintTime, endPrintTime, 
				beginSendTime, endSendTime, super.getPageBean(), orderStr, orderView);
		if (map != null) {
			tickets = (List<Ticket>) map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(ServletActionContext.getRequest(), pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		if (!StringUtils.isEmpty(target)) {
			return target;
		}
		return "list";
	}
	
	public String view() {
		logger.info("进入查看票明细");
		ticket = ticketService.get(ticket.getId());
		if (ticket != null && ticket.getExt() != null && !ticket.getExt().equals("")) {
			jcspStr = "";
			String ext = ticket.getExt();
			JSONObject jsonObject = JSONObject.fromObject(ext);
			for (Iterator<?> iterator = jsonObject.keys(); iterator.hasNext();) {
				String key = (String)iterator.next();
				String matchNum = jsonObject.getJSONObject(key).getString("match_num");
				String spMap = "";
				try {
					spMap = jsonObject.getJSONObject(key).getString("sp_map");
					if (spMap != null && !spMap.equals("")) {
						JSONObject j = JSONObject.fromObject(spMap);
						spMap = "";
						for (Iterator<?> it = j.keys(); it.hasNext();) {
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
		try {
			lotteryConfigData = settingService.get(SettingConstant.GROUP_LOTTERY_CONFIG, String.valueOf(lotteryTypeValue));
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取票明细，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
			return "failure";
		}
		if (target != null && !target.isEmpty()) {
			super.setForwardUrl("/ticket/ticket.do?ticket.planNo="+ticket.getPlanNo()+"&target="+target);
		} else {
			super.setForwardUrl("/ticket/ticket.do");
		}
		logger.info("查看票明细结束");
		return "view";
	}
	
	private boolean checkQueryCondition(Ticket ticket,Date beginSendTime,Date endSendTime){
		int count = 0;
		Long ticketId = ticket.getId();
		if(ticketId != null && ticketId > 0){
			count++;
		}
		String planNo = ticket.getPlanNo();
		if(!StringUtils.isEmpty(planNo)){
			count++;
		}
		Long batchId = ticket.getBatchId();
		if(batchId != null && batchId > 0){
			count++;
		}
		LotteryType lotteryType = ticket.getLotteryType();
		String phase = ticket.getPhase();
		if(lotteryType.getValue() != LotteryType.ALL.getValue() && !StringUtils.isEmpty(phase)){
			count++;
		}
		String serialId = ticket.getSerialId();
		String externalId = ticket.getExternalId();
		if(!StringUtils.isEmpty(serialId) && !StringUtils.isEmpty(externalId)){
			count++;
		}
		Long terminalId = ticket.getTerminalId();
		TicketStatus ticketStatus = ticket.getStatus();
		if(terminalId != null && terminalId.longValue() > 0 && 
				ticketStatus != null && ticketStatus.getValue() != TicketStatus.ALL.getValue() && 
				(beginSendTime != null || endSendTime != null)){
			count++;
		}
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}

	public TicketService getTicketService() {
		return ticketService;
	}
	public void setTicketService(TicketService ticketService) {
		this.ticketService = ticketService;
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
		orderStrMap.put("sendTime", "送票时间");
		orderStrMap.put("terminateTime", "截止时间");
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
	public SettingService getSettingService() {
		return settingService;
	}
	public void setSettingService(SettingService settingService) {
		this.settingService = settingService;
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
}
