package web.dao.ticket;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.engine.entity.ticket.TicketArchive;


public interface ArchivedTicketDao {
	/**
	 * 根据ID列表获取归档票数据
	 * @param id
	 * @return
	 */
	public List<TicketArchive> query(List<Long> idList, PageBean pageBean);
}
