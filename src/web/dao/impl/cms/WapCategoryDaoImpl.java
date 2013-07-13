package web.dao.impl.cms;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.cms.WapCategoryDao;
import com.lehecai.admin.web.domain.cms.WapCategory;

public class WapCategoryDaoImpl extends HibernateDaoSupport implements WapCategoryDao {

	@Override
	public void merge(WapCategory wapCategory) {
		getHibernateTemplate().merge(wapCategory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WapCategory> list(final WapCategory wapCategory) {
		return (List<WapCategory>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from WapCategory u where 1 = 1");
						if(wapCategory != null && wapCategory.getParentID() != null){
							hql.append(" and u.parentID = :parentID");
						}
						hql.append(" order by u.parentID,u.orderView desc");
						Query query = session.createQuery(hql.toString());
						if(wapCategory != null && wapCategory.getParentID() != null){
							query.setParameter("parentID", wapCategory.getParentID());
						}
						return query.list();
					}
				});
	}

	@Override
	public WapCategory get(Long ID) {
		return getHibernateTemplate().get(WapCategory.class, ID);
	}

	@Override
	public void del(WapCategory wapCategory) {
		getHibernateTemplate().delete(wapCategory);
	}
}
