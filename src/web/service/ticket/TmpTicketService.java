/**
 * 
 */
package web.service.ticket;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.bean.query.type.QueryDataSourceType;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketQuery;

/**
 * @author qatang
 *
 */
public interface TmpTicketService {
	public List<Ticket> query(TicketQuery ticketQueryProp, PageBean pageBean, QueryDataSourceType queryDataSourceType) throws Exception;
}
