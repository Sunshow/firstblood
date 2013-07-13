package web.dao.impl.cms;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.cms.ResourceCategoryDao;
import com.lehecai.admin.web.domain.cms.ResourceCategory;

public class ResourceCategoryDaoImpl extends HibernateDaoSupport implements ResourceCategoryDao {

	@Override
	public void merge(ResourceCategory resourceCategory) {
		// TODO Auto-generated method stub
		getHibernateTemplate().merge(resourceCategory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceCategory> list(final ResourceCategory resourceCategory) {
		// TODO Auto-generated method stub
		return (List<ResourceCategory>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from ResourceCategory u where 1 = 1 order by u.reLevel,u.parentID,u.orderView desc");
						Query query = session.createQuery(hql.toString());
						return query.list();
					}
				});
	}

	@Override
	public ResourceCategory get(Long ID) {
		// TODO Auto-generated method stub
		return getHibernateTemplate().get(ResourceCategory.class, ID);
	}

	@Override
	public void del(ResourceCategory resourceCategory) {
		// TODO Auto-generated method stub
		getHibernateTemplate().delete(resourceCategory);
	}
}
