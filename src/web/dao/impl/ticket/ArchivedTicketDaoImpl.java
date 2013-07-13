package web.dao.impl.ticket;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.ticket.ArchivedTicketDao;
import com.lehecai.engine.entity.ticket.TicketArchive;

public class ArchivedTicketDaoImpl extends HibernateDaoSupport implements ArchivedTicketDao {
	@SuppressWarnings("unchecked")
	@Override
	public List<TicketArchive> query(final List<Long> idList, final PageBean pageBean) {
		return getHibernateTemplate().executeFind(new HibernateCallback<List<TicketArchive>>() {
			@Override
			public List<TicketArchive> doInHibernate(Session session)
					throws HibernateException, SQLException {
				StringBuffer sb = new StringBuffer("from TicketArchive t where 1=1");
				sb.append(" and t.id in (:idList)");

				Query query = session.createQuery(sb.toString());
				query.setParameterList("idList", idList);

				if (pageBean != null && pageBean.isPageFlag()) {
					if (pageBean.getPage() > 0) {
						query.setFirstResult((pageBean.getPage() - 1)
								* pageBean.getPageSize());
					}
					if (pageBean.getPageSize() > 0) {
						query.setMaxResults(pageBean.getPageSize());
					}
				}

				return query.list();
			}
		});
	}

}