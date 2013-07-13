package web.dao.impl.process;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.process.WorkProcessDao;
import com.lehecai.admin.web.domain.process.WorkProcess;

public class WorkProcessDaoImpl extends HibernateDaoSupport implements WorkProcessDao {

	@Override
	public void del(WorkProcess workProcess) {
		getHibernateTemplate().delete(workProcess);
	}
	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final String processId, final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from WorkProcess u where 1 = 1");
						
						if(processId != null && !"".equals(processId)){
							hql.append(" and u.processId = :processId");
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						
						if(processId != null && !"".equals(processId)){
							query.setParameter("processId", processId);
						}
						if(pageBean != null && pageBean.isPageFlag()){
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

	@SuppressWarnings("unchecked")
	@Override
	public List<WorkProcess> list(final String processId, final PageBean pageBean) {
		return (List<WorkProcess>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from WorkProcess u where 1 = 1");
						
						if(processId != null && !"".equals(processId)){
							hql.append(" and u.processId = :processId");
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						
						if(processId != null && !"".equals(processId)){
							query.setParameter("processId", processId);
						}
						if(pageBean != null && pageBean.isPageFlag()){
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
	public void manage(WorkProcess workProcess) {
		getHibernateTemplate().merge(workProcess);
	}
	@Override
	public WorkProcess get(Long id) {
		return getHibernateTemplate().get(WorkProcess.class, id);
	}
	@SuppressWarnings("unchecked")
	@Override
	public WorkProcess getByProcessId(final String processId) {
		return (WorkProcess) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from WorkProcess u where 1 = 1");
						if(processId != null && !"".equals(processId)){
							hql.append(" and u.processId = :processId");
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						if(processId != null && !"".equals(processId)){
							query.setParameter("processId", processId);
						}
						List<WorkProcess> list = query.list();
						if (list != null && list.size() > 0) {
							return list.get(0);
						} else {
							return null;
						}
					}
				});
	}
}