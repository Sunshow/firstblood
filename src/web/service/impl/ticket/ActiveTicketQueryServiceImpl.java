/**
 * 
 */
package web.service.impl.ticket;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.ticket.ActiveTicketDao;
import com.lehecai.admin.web.dao.ticket.TicketBatchDao;
import com.lehecai.admin.web.dao.ticket.TicketDao;
import com.lehecai.admin.web.service.ticket.ActiveTicketUpdateService;
import com.lehecai.admin.web.service.ticket.TicketQueryService;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketBatch;
import com.lehecai.engine.entity.ticket.TicketBatchStatus;
import com.lehecai.engine.entity.ticket.TicketQuery;
import com.lehecai.engine.entity.ticket.TicketStatus;

/**
 * @author qatang
 *
 */
public class ActiveTicketQueryServiceImpl implements TicketQueryService,ActiveTicketUpdateService {
	private ActiveTicketDao activeTicketDao;
	private TicketDao ticketDao;
	private TicketBatchDao ticketBatchDao;
	
	@Override
	public List<Ticket> query(TicketQuery ticketQueryProp, PageBean pageBean) {
		List<Ticket> tickets = activeTicketDao.query(ticketQueryProp, pageBean);
		
		if (pageBean != null) {
			int totalCount = activeTicketDao.getCounts(ticketQueryProp, pageBean);
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
		return tickets;
	}
	
	public ActiveTicketDao getActiveTicketDao() {
		return activeTicketDao;
	}
	
	public void setActiveTicketDao(ActiveTicketDao activeTicketDao) {
		this.activeTicketDao = activeTicketDao;
	}

	@Override
	public void updateStatus(Long ticketId, TicketStatus ticketStatus) {
		if(ticketStatus.getValue() == TicketStatus.UNSENT.getValue()){
			Ticket ticket = ticketDao.get(ticketId);
			if(ticket != null && ticket.getBatchId() != null){
				Long batchId = ticket.getBatchId();
				TicketBatch queryTb = new TicketBatch();
				queryTb.setId(batchId);
				TicketBatch tb = ticketBatchDao.findById(queryTb);
				if(tb != null && tb.getStatus() != null && tb.getStatus().getValue() != TicketBatchStatus.SEND_WAITING.getValue()){
					ticketBatchDao.updateTicketBatchStatus(batchId, TicketBatchStatus.SEND_WAITING);
				}
			}
		}
		this.activeTicketDao.updateStatus(ticketId, ticketStatus);
	}
	

	@Override
	public void updateStatus(Ticket ticket) {
		this.activeTicketDao.updateStatus(ticket);
	}

	public TicketDao getTicketDao() {
		return ticketDao;
	}

	public void setTicketDao(TicketDao ticketDao) {
		this.ticketDao = ticketDao;
	}

	public TicketBatchDao getTicketBatchDao() {
		return ticketBatchDao;
	}

	public void setTicketBatchDao(TicketBatchDao ticketBatchDao) {
		this.ticketBatchDao = ticketBatchDao;
	}
}
