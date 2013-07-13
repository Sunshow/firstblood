package web.dao.impl.cms;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.cms.CategoryDao;
import com.lehecai.admin.web.domain.cms.Category;

public class CategoryDaoImpl extends HibernateDaoSupport implements CategoryDao {

	@Override
	public void merge(Category category) {
		getHibernateTemplate().merge(category);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Category> list(final Category category) {
		return (List<Category>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from Category u where 1 = 1");
						if(category != null && category.getParentID() != null){
							hql.append(" and u.parentID = :parentID");
						}
						if(category != null && category.getRoleId() != null){
							hql.append(" and u.roleId = :roleId");
						}
						hql.append(" order by u.parentID,u.orderView desc");
						Query query = session.createQuery(hql.toString());
						if(category != null && category.getParentID() != null){
							query.setParameter("parentID", category.getParentID());
						}
						if(category != null && category.getRoleId() != null){
							query.setParameter("roleId", category.getRoleId());
						}
						return query.list();
					}
				});
	}

	@Override
	public Category get(Long ID) {
		return getHibernateTemplate().get(Category.class, ID);
	}

	@Override
	public void del(Category category) {
		getHibernateTemplate().delete(category);
	}
}
