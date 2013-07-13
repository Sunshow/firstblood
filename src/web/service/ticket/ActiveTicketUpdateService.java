package web.service.ticket;

import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketStatus;

public interface ActiveTicketUpdateService {
	/**
	 * 更新票状态
	 * @param ticketId
	 * @param ticketStatus
	 */
	public void updateStatus(Long ticketId, TicketStatus ticketStatus);
	
	/**
	 * 更新票状态
	 * 更新为出票成功状态时需要更新出票时间
	 * 2013-06-09
	 * @param ticket
	 * @author chirowong
	 */
	public void updateStatus(Ticket ticket);
}
