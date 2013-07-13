/**
 * 
 */
package web.dao.impl.statics;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.statics.DataConsolidationDao;
import com.lehecai.admin.web.domain.statics.DataConsolidation;
import com.lehecai.admin.web.domain.statics.DataConsolidationItem;

/**
 * @author qatang
 *
 */
public class DataConsolidationDaoImpl extends HibernateDaoSupport implements
		DataConsolidationDao {

	@Override
	public void add(DataConsolidationItem dataConsolidationItem) {
		getHibernateTemplate().merge(dataConsolidationItem);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void del(final Long dataId) {
		getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						String hql = "delete from DataConsolidationItem u where u.dataId = :dataId";
						Query query = session.createQuery(hql);
						query.setParameter("dataId", dataId);
						query.executeUpdate();
						return null;
					}
				});
	}

	@Override
	public DataConsolidation get(Long id) {
		return getHibernateTemplate().get(DataConsolidation.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DataConsolidation> list() {
		return (List<DataConsolidation>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from DataConsolidation u where 1=1");
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						return query.list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DataConsolidationItem> list(final Long dataId) {
		return (List<DataConsolidationItem>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from DataConsolidationItem u where 1=1");
						if(dataId != null){
							hql.append(" and u.dataId = :dataId");
						}
						hql.append(" order by u.id");
						Query query = session.createQuery(hql.toString());
						if(dataId != null){
							query.setParameter("dataId", dataId);
						}
						return query.list();
					}
				});
	}

	@Override
	public DataConsolidation merge(DataConsolidation dataConsolidation) {
		return getHibernateTemplate().merge(dataConsolidation);
	}

}
