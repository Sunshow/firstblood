package web.dao.impl.statics;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.statics.StaticCacheLayoutDao;
import com.lehecai.admin.web.domain.statics.StaticCacheLayout;
import com.lehecai.admin.web.domain.statics.StaticCacheLayoutItem;

public class StaticCacheLayoutDaoImpl extends HibernateDaoSupport implements StaticCacheLayoutDao {
	@Override
	public StaticCacheLayout get(Long id) {
		return getHibernateTemplate().get(StaticCacheLayout.class, id);
	}

	@Override
	public void del(StaticCacheLayout staticCacheLayout) {
		getHibernateTemplate().delete(staticCacheLayout);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StaticCacheLayout> list(final StaticCacheLayout staticCacheLayout) {
		return (List<StaticCacheLayout>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from StaticCacheLayout u where 1=1");
						if(staticCacheLayout != null && staticCacheLayout.getId() != null && staticCacheLayout.getTheLevel() == 1){
							hql.append(" and u.parentId = :parentId");
						}
						hql.append(" order by u.theLevel,u.parentId,u.orderView desc,u.id");
						Query query = session.createQuery(hql.toString());
						if(staticCacheLayout != null && staticCacheLayout.getId() != null && staticCacheLayout.getTheLevel() == 1){
							query.setParameter("parentId", staticCacheLayout.getId());
						}
						return query.list();
					}
				});
	}

	@Override
	public void save(StaticCacheLayout staticCacheLayout) {
		getHibernateTemplate().save(staticCacheLayout);
	}

	@Override
	public void update(StaticCacheLayout staticCacheLayout) {
		getHibernateTemplate().update(staticCacheLayout);
	}

	@Override
	public void delItem(StaticCacheLayoutItem staticCacheLayoutItem) {
		getHibernateTemplate().delete(staticCacheLayoutItem);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StaticCacheLayoutItem> getStaticCachesByLayoutId(final Long layoutId) {
		return (List<StaticCacheLayoutItem>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from StaticCacheLayoutItem u where 1=1");
						hql.append(" and u.layoutId = :layoutId");
						Query query = session.createQuery(hql.toString());
						query.setParameter("layoutId", layoutId);
						return query.list();
					}
				});
	}

	@Override
	public void insertItem(StaticCacheLayoutItem staticCacheLayoutItem) {
		getHibernateTemplate().save(staticCacheLayoutItem);
	}

	@SuppressWarnings("unchecked")
	@Override
	public StaticCacheLayoutItem getItem(final Long staticCacheId, final Long layoutId) {
		return (StaticCacheLayoutItem) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from StaticCacheLayoutItem u where 1=1");
						hql.append(" and u.layoutId = :layoutId");
						hql.append(" and u.staticCacheId = :staticCacheId");
						Query query = session.createQuery(hql.toString());
						query.setParameter("layoutId", layoutId);
						query.setParameter("staticCacheId", staticCacheId);
						return query.uniqueResult();
					}
				});
	}
}
