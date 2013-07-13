package web.dao.ticket;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketQuery;
import com.lehecai.engine.entity.ticket.TicketStatus;


public interface ActiveTicketDao {
	/**
	 * 获取活跃票数据
	 * @param id
	 * @return
	 */
	public List<Ticket> query(TicketQuery ticketQueryProp, PageBean pageBean);
	/**
	 * 获取活跃票数据总数
	 * @param id
	 * @return
	 */
	public int getCounts(TicketQuery ticketQueryProp, PageBean pageBean);
	
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
