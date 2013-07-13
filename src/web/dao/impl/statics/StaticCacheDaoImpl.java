package web.dao.impl.statics;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.statics.StaticCacheDao;
import com.lehecai.admin.web.domain.statics.StaticCache;

public class StaticCacheDaoImpl extends HibernateDaoSupport implements StaticCacheDao {

	@Override
	public void merge(StaticCache staticCache) {
		getHibernateTemplate().merge(staticCache);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StaticCache> list(final StaticCache staticCache) {
		return (List<StaticCache>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from StaticCache u where 1=1");
						if(staticCache != null && staticCache.getId() != null && staticCache.getStLevel() == 1){
							hql.append(" and u.parentID = :parentID");
						}
						hql.append(" order by u.stLevel,u.parentID,u.orderView desc,u.id");
						Query query = session.createQuery(hql.toString());
						
						if(staticCache != null && staticCache.getId() != null && staticCache.getStLevel() == 1){
							query.setParameter("parentID", staticCache.getId());
						}
						return query.list();
					}
				});
	}

	@Override
	public StaticCache get(Long ID) {
		return getHibernateTemplate().get(StaticCache.class, ID);
	}

	@Override
	public void del(StaticCache staticCache) {
		getHibernateTemplate().delete(staticCache);
	}

	@SuppressWarnings("unchecked")
	@Override
	public StaticCache getBySlug(final String slug) {
		return (StaticCache) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						String hql = "from StaticCache u where u.slug = :slug";
						Query query = session.createQuery(hql);
						query.setParameter("slug", slug);
						List list = query.list();
						if (list != null && list.size() != 0) {
							return list.get(0);
						}
						return null;
					}
				});
	}

	@SuppressWarnings("unchecked")
	@Override
	public StaticCache getByTargetUrl(final String targetUrl) {
		return (StaticCache) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						String hql = "from StaticCache u where u.targetUrl = :targetUrl";
						Query query = session.createQuery(hql);
						query.setParameter("targetUrl", targetUrl);
						List list = query.list();
						if (list != null && list.size() != 0) {
							return list.get(0);
						}
						return null;
					}
				});
	}
}
