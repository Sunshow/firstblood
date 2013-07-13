package web.service.impl.ticket;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.dao.ticket.TicketDao;
import com.lehecai.admin.web.domain.finance.TerminalAccountCheckItem;
import com.lehecai.admin.web.service.lottery.LotteryPlanService;
import com.lehecai.admin.web.service.ticket.TicketService;
import com.lehecai.core.api.lottery.Plan;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketStatus;

public class TicketServiceImpl implements TicketService {

	private TicketDao ticketDao;
	private LotteryPlanService lotteryPlanService;
	
	@Override
	public Map<String, Object> getResult(Ticket ticket, Date beginCreateTime, Date endCreateTime, Date beginPrintTime, Date endPrintTime, 
			Date beginSendTime, Date endSendTime, PageBean pageBean, String orderStr, String OrderView){
		List<Ticket> tickets = ticketDao.getResult(ticket, beginCreateTime, endCreateTime, beginPrintTime, endPrintTime, 
				beginSendTime, endSendTime, pageBean, orderStr, OrderView);
		if (pageBean != null) {
			int totalCount = ticketDao.getCounts(ticket, beginCreateTime, endCreateTime, beginPrintTime, endPrintTime, beginSendTime, endSendTime);
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
		map.put(Global.API_MAP_KEY_LIST, tickets);
		return map;
	}
	
	@Override
	public TerminalAccountCheckItem getTerminalAccountCheckItem(String terminalIds,Integer lotteryTypeId,Integer accountCheckType,String accountCheckDate){
		return ticketDao.getTerminalAccountCheckItem(terminalIds, lotteryTypeId, accountCheckType, accountCheckDate);
	}
	
	@Override
	public Ticket get(Long id) {
		return ticketDao.get(id);
	}
	
	@Override
	public List<Ticket> getListByPlanId(String planId, PageBean pageBean) {
		return ticketDao.getResultByPlanId(planId, pageBean);
	}

	@Override
	public int getCountByPlanId(String planId) {
		return ticketDao.getCountsByPlanId(planId);
	}
	
	@Override
	public void updateTerminateTimeByPlanIds(List<String> planIds, List<String> changedList) throws ApiRemoteCallFailedException {
		Map<String, String> map = new HashMap<String, String>();
		for (String planId : changedList) {
			map.put(planId, planId);
		}
		List<TicketStatus> ticketStatusList = new ArrayList<TicketStatus>();
		ticketStatusList.add(TicketStatus.UNALLOTTED);
		ticketStatusList.add(TicketStatus.UNSENT);
		ticketStatusList.add(TicketStatus.PRINTING);
		for (String planIdTemp : planIds) {
			if (map.get(planIdTemp) != null) {
				Plan tempPlan = lotteryPlanService.get(planIdTemp);
				Date terminateTime = tempPlan.getDeadline();
				ticketDao.updateTerminateTimeByPlanId(planIdTemp, terminateTime, ticketStatusList);
			}
		}
	}
	
	public TicketDao getTicketDao() {
		return ticketDao;
	}

	public void setTicketDao(TicketDao ticketDao) {
		this.ticketDao = ticketDao;
	}

	public void setLotteryPlanService(LotteryPlanService lotteryPlanService) {
		this.lotteryPlanService = lotteryPlanService;
	}

	public LotteryPlanService getLotteryPlanService() {
		return lotteryPlanService;
	}

}
