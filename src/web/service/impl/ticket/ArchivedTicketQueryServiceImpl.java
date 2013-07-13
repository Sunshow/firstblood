package web.service.impl.ticket;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.ticket.ArchivedTicketDao;
import com.lehecai.admin.web.service.ticket.TicketQueryService;
import com.lehecai.core.api.bean.query.IQueryProp;
import com.lehecai.core.entity.serializer.PropSourceType;
import com.lehecai.engine.entity.serializer.TicketPropConstant;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketArchive;
import com.lehecai.engine.entity.ticket.TicketQuery;

public class ArchivedTicketQueryServiceImpl implements TicketQueryService {
	private ArchivedTicketDao archivedTicketDao;
	
	@Override
	public List<Ticket> query(TicketQuery ticketQueryProp, PageBean pageBean) throws Exception {
		List<IQueryProp> queryPropItemList = ticketQueryProp.getQueryPropItemList();
		String[] values = null;
		for (IQueryProp queryPropItem : queryPropItemList) {
			if (queryPropItem.getName().equals(TicketPropConstant.PROP_ID)) {
				values = queryPropItem.getValues();
				break;
			}
		}
		if (values == null || values.length == 0) {
			throw new Exception("票归档数据查询只支持主键查询，但参数未找到主键值列表");
		}
		List<Long> idList = new ArrayList<Long>();
		for (String value : values) {
			Long id = Long.valueOf(value);
			idList.add(id);
		}
		List<TicketArchive> ticketArchiveList = archivedTicketDao.query(idList, pageBean);
		return this.convert(ticketArchiveList);
	}
	
	private List<Ticket> convert(List<TicketArchive> ticketArchiveList) throws Exception {
		List<Ticket> ticketList = new ArrayList<Ticket>();
		for (TicketArchive ticketArchive : ticketArchiveList) {
			Ticket ticket = TicketPropConstant.getInstance().convertFromObject(ticketArchive, PropSourceType.JAVA_PROPERTY, PropSourceType.JAVA_PROPERTY);
			ticketList.add(ticket);
		}
		return ticketList;
	}
	
	public ArchivedTicketDao getArchivedTicketDao() {
		return archivedTicketDao;
	}

	public void setArchivedTicketDao(ArchivedTicketDao archivedTicketDao) {
		this.archivedTicketDao = archivedTicketDao;
	}

}
