/**
 * 
 */
package web.service.impl.ticket;

import java.util.HashMap;
import java.util.Map;

import com.lehecai.admin.web.service.ticket.TicketQueryService;
import com.lehecai.core.api.bean.query.type.QueryDataSourceType;

/**
 * @author qatang
 *
 */
public class TicketQueryFactory {
	private TicketQueryService activeTicketQueryService;
	private TicketQueryService archivedTicketQueryService;
	private TicketQueryService searchTicketQueryService;
	
	private Map<QueryDataSourceType, TicketQueryService> map = new HashMap<QueryDataSourceType, TicketQueryService>();
	
	public void init() {
		map.put(QueryDataSourceType.ACTIVE, activeTicketQueryService);
		map.put(QueryDataSourceType.ACHIVED, archivedTicketQueryService);
		map.put(QueryDataSourceType.SEARCH, searchTicketQueryService);
	}
	
	public TicketQueryService getInstance(QueryDataSourceType queryDataSourceType) {
		return map.get(queryDataSourceType);
	}

	public TicketQueryService getActiveTicketQueryService() {
		return activeTicketQueryService;
	}

	public void setActiveTicketQueryService(
			TicketQueryService activeTicketQueryService) {
		this.activeTicketQueryService = activeTicketQueryService;
	}

	public TicketQueryService getArchivedTicketQueryService() {
		return archivedTicketQueryService;
	}

	public void setArchivedTicketQueryService(
			TicketQueryService archivedTicketQueryService) {
		this.archivedTicketQueryService = archivedTicketQueryService;
	}

	public TicketQueryService getSearchTicketQueryService() {
		return searchTicketQueryService;
	}

	public void setSearchTicketQueryService(
			TicketQueryService searchTicketQueryService) {
		this.searchTicketQueryService = searchTicketQueryService;
	}
	
}
