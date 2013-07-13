package web.service.ticket;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketQuery;

public interface TicketQueryService {
	/**
	 * 获取归档票数据
	 * @param ticketQueryProp
	 * @param pageBean
	 * @return
	 */
	public List<Ticket> query(TicketQuery ticketQueryProp, PageBean pageBean) throws Exception;
}
