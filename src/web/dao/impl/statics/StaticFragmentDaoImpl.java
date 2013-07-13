package web.dao.impl.statics;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.statics.StaticFragmentDao;
import com.lehecai.admin.web.domain.statics.StaticFragment;

public class StaticFragmentDaoImpl extends HibernateDaoSupport implements StaticFragmentDao {

	/**
	 * 查询所有的碎片
	 */
	@SuppressWarnings("unchecked")
	public List<StaticFragment> findList() {
		return super.getHibernateTemplate().executeFind(new HibernateCallback<List<StaticFragment>>() {

			@Override
			public List<StaticFragment> doInHibernate(Session session)
					throws HibernateException, SQLException {
				StringBuffer hql = new StringBuffer("from StaticFragment sf where 1 = 1");
				hql.append(" order by sf.id desc");
				
				Query query = session.createQuery(hql.toString());
				return query.list();
				
			}
		});
	}
	
	/**
	 * 根据碎片编码查询碎片
	 */
	public StaticFragment get(Long id) {
		return super.getHibernateTemplate().get(StaticFragment.class, id);
	}
	
	/**
	 * 根据碎片名称查询碎片
	 */
	public StaticFragment getByName(final String fragmentName) {
		return super.getHibernateTemplate().execute(new HibernateCallback<StaticFragment>() {

			@Override
			public StaticFragment doInHibernate(Session session)
					throws HibernateException, SQLException {
				StringBuffer hql = new StringBuffer("from StaticFragment sf where 1 = 1");
				hql.append(" and sf.fragmentName = :fragmentName");
				
				Query query = session.createQuery(hql.toString());
				query.setParameter("fragmentName", fragmentName);
				
				return (StaticFragment)query.uniqueResult();
				
			}
		});
	}
	
	/**
	 * 更新静态碎片
	 */
	public void merge(StaticFragment staticFragment) {
		super.getHibernateTemplate().merge(staticFragment);
	}
}
