package web.dao.impl.cms;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.cms.ResourceDao;
import com.lehecai.admin.web.domain.cms.Resource;

public class ResourceDaoImpl extends HibernateDaoSupport implements ResourceDao {

	@Override
	public void merge(Resource resource) {
		// TODO Auto-generated method stub
		getHibernateTemplate().merge(resource);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Resource> list(final Resource resource, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (List<Resource>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from Resource u where 1 = 1");
						if(resource != null && resource.getCateID() != null){
							hql.append(" and u.cateID = :cateID");
						}
						hql.append(" order by u.createTime desc,u.id");
						Query query = session.createQuery(hql.toString());
								
						if(resource != null && resource.getCateID() != null){
							query.setParameter("cateID", resource.getCateID());
						}
						if(pageBean.isPageFlag()){
							if(pageBean.getPageSize() != 0){
								query.setFirstResult((pageBean.getPage() - 1) * pageBean.getPageSize());
								query.setMaxResults(pageBean.getPageSize());
							}
						}
						return query.list();
					}
				});
	}

	@Override
	public Resource get(Long ID) {
		// TODO Auto-generated method stub
		return getHibernateTemplate().get(Resource.class, ID);
	}

	@Override
	public void del(Resource resource) {
		// TODO Auto-generated method stub
		getHibernateTemplate().delete(resource);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final Resource resource, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from Resource u where 1 = 1");
						
						if(resource != null && resource.getCateID() != null){
							hql.append(" and u.cateID = :cateID");
						}
						Query query = session.createQuery(hql.toString());
								
						if(resource != null && resource.getCateID() != null){
							query.setParameter("cateID", resource.getCateID());
						}
						if(pageBean.isPageFlag()){
							int totalCount = ((Long)query.iterate().next()).intValue();
							pageBean.setCount(totalCount);
							int pageCount = 0;//页数
							if(pageBean.getPageSize() != 0) {
					            pageCount = totalCount / pageBean.getPageSize();
					            if(totalCount % pageBean.getPageSize() != 0) {
					                pageCount ++;
					            }
					        }
							pageBean.setPageCount(pageCount);
						}
						return pageBean;
					}
				});
	}
}
