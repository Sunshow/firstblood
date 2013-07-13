package web.dao.ticket;

import java.util.List;

import com.lehecai.engine.entity.ticket.TicketBatch;
import com.lehecai.engine.entity.ticket.TicketBatchStatus;

public interface TicketBatchDao {
	
	/**
	 * 按状态和终端Id查找批次
	 * @param status
	 * @param terminalId
	 * @param max
	 * @return
	 */
	List<TicketBatch> findByStatusAndTerminalId(TicketBatchStatus status, Long terminalId, int max);

	/**
	 * 根据批次id修改终端
	 * @param id
	 * @param terminalId
	 */
	void updateTerminalId(Long id, Long terminalId);
	
	/**
	 * 根据批次号查询批次
	 * @param ticketBatch
	 * @return
	 * @author chirowong
	 */
	TicketBatch findById(TicketBatch ticketBatch);
	
	/**
	 * 修改批次
	 * @param id
	 * @param ticketBatch
	 * @author chirowong
	 */
	void updateTicketBatchStatus(Long id, TicketBatchStatus ticketBatchStatus);
}
