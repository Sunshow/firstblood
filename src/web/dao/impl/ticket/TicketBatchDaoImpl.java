package web.dao.impl.ticket;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.ticket.TicketBatchDao;
import com.lehecai.engine.entity.ticket.TicketBatch;
import com.lehecai.engine.entity.ticket.TicketBatchStatus;

public class TicketBatchDaoImpl extends HibernateDaoSupport implements
		TicketBatchDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<TicketBatch> findByStatusAndTerminalId(
			final TicketBatchStatus status, final Long terminalId, final int max) {
		return getHibernateTemplate().executeFind(
				new HibernateCallback<List<TicketBatch>>() {

					@Override
					public List<TicketBatch> doInHibernate(Session session)
							throws HibernateException, SQLException {
						StringBuffer sb = new StringBuffer(
								"from TicketBatch m where m.terminalId=:terminalId and m.status=:status");

						Query query = session.createQuery(sb.toString());
						query.setParameter("terminalId", terminalId);
						query.setParameter("status", status);

						if (max > 0) {
							query.setMaxResults(max);
						}

						return query.list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateTerminalId(final Long id, final Long terminalId) {
		 getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("update TicketBatch m set m.terminalId = :terminalId");
						hql.append(" where m.id = :id");
						Query query = session.createQuery(hql.toString());
						query.setParameter("terminalId", terminalId);
						query.setParameter("id", id);
						query.executeUpdate();
						return null;
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TicketBatch findById(final TicketBatch ticketBatch) {
		return (TicketBatch) getHibernateTemplate().execute(
				new HibernateCallback() {
					@Override
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						StringBuffer sb = new StringBuffer("from TicketBatch m where m.id=:ticketBatchId");
						Query query = session.createQuery(sb.toString());
						query.setParameter("ticketBatchId", ticketBatch.getId());
						List<TicketBatch> ticketBatchList = query.list();
						if (ticketBatchList != null && ticketBatchList.size() != 0) {
							return ticketBatchList.get(0);
						}
						return null;
					}
				});
	}

	@Override
	public void updateTicketBatchStatus(final Long id, final TicketBatchStatus ticketBatchStatus) {
		 getHibernateTemplate().execute(
					new HibernateCallback<Object>() {
						public Object doInHibernate(Session session)
								throws HibernateException {
							StringBuffer hql = new StringBuffer("update TicketBatch m set m.status = :status");
							hql.append(" where m.id = :id");
							Query query = session.createQuery(hql.toString());
							query.setParameter("status", ticketBatchStatus);
							query.setParameter("id", id);
							query.executeUpdate();
							return null;
						}
					});
	}

}
