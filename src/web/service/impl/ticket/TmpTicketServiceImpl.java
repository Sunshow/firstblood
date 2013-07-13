/**
 * 
 */
package web.service.impl.ticket;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.service.ticket.TicketQueryService;
import com.lehecai.admin.web.service.ticket.TmpTicketService;
import com.lehecai.core.api.bean.query.type.QueryDataSourceType;
import com.lehecai.core.service.archive.ArchiveIdService;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketQuery;

/**
 * @author qatang
 *
 */
public class TmpTicketServiceImpl implements TmpTicketService {
	private ArchiveIdService archiveIdService;
	private TicketQueryFactory ticketQueryFactory;
	
	@Override
	public List<Ticket> query(TicketQuery ticketQueryProp, PageBean pageBean, QueryDataSourceType queryDataSourceType)
			throws Exception {
		TicketQueryService ticketQueryService = ticketQueryFactory.getInstance(queryDataSourceType);
		return ticketQueryService.query(ticketQueryProp, pageBean);
	}

	public ArchiveIdService getArchiveIdService() {
		return archiveIdService;
	}

	public void setArchiveIdService(ArchiveIdService archiveIdService) {
		this.archiveIdService = archiveIdService;
	}

	public TicketQueryFactory getTicketQueryFactory() {
		return ticketQueryFactory;
	}

	public void setTicketQueryFactory(TicketQueryFactory ticketQueryFactory) {
		this.ticketQueryFactory = ticketQueryFactory;
	}

}
