package web.service.impl.ticket;

import java.util.List;

import com.lehecai.admin.web.dao.ticket.TicketBatchDao;
import com.lehecai.admin.web.service.ticket.TicketBatchService;
import com.lehecai.engine.entity.ticket.TicketBatch;
import com.lehecai.engine.entity.ticket.TicketBatchStatus;

public class TicketBatchServiceImpl implements TicketBatchService {
	
	private TicketBatchDao ticketBatchDao;

	@Override
	public List<TicketBatch> findByStatusAndTerminalId(
			TicketBatchStatus status, Long terminalId, int max) {
		return ticketBatchDao.findByStatusAndTerminalId(status, terminalId, max);
	}
	
	@Override
	public void updateTerminalId(Long id, Long terminalId) {
		ticketBatchDao.updateTerminalId(id, terminalId);
	}
	
	@Override
	public TicketBatch findById(TicketBatch ticketBatch) {
		return ticketBatchDao.findById(ticketBatch);
	}

	@Override
	public void updateTicketBatchStatus(Long id,
			TicketBatchStatus ticketBatchStatus) {
		ticketBatchDao.updateTicketBatchStatus(id, ticketBatchStatus);
	}

	public TicketBatchDao getTicketBatchDao() {
		return ticketBatchDao;
	}

	public void setTicketBatchDao(TicketBatchDao ticketBatchDao) {
		this.ticketBatchDao = ticketBatchDao;
	}
}
