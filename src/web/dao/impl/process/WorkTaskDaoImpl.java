package web.dao.impl.process;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.process.WorkTaskDao;
import com.lehecai.admin.web.domain.process.WorkProcess;
import com.lehecai.admin.web.domain.process.WorkTask;

public class WorkTaskDaoImpl extends HibernateDaoSupport implements WorkTaskDao {

	@Override
	public void del(WorkTask workTask) {
		getHibernateTemplate().delete(workTask);
	}
	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final String processId, final String taskId,
			final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from WorkTask u where 1 = 1");
						
						if(processId != null && !"".equals(processId)){
							hql.append(" and u.processId = :processId");
						}
						if(taskId != null && !"".equals(taskId)){
							hql.append(" and u.taskId = :taskId");
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						
						if(processId != null && !"".equals(processId)){
							query.setParameter("processId", processId);
						}
						if(taskId != null && !"".equals(taskId)){
							query.setParameter("taskId", taskId);
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
	public List<WorkTask> list(final String processId, final String taskId,
			final PageBean pageBean) {
		return (List<WorkTask>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from WorkTask u where 1 = 1");
						
						if(processId != null && !"".equals(processId)){
							hql.append(" and u.processId = :processId");
						}
						if(taskId != null && !"".equals(taskId)){
							hql.append(" and u.taskId = :taskId");
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						
						if(processId != null && !"".equals(processId)){
							query.setParameter("processId", processId);
						}
						if(taskId != null && !"".equals(taskId)){
							query.setParameter("taskId", taskId);
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
	public void manage(WorkTask workTask) {
		getHibernateTemplate().merge(workTask);
	}
	@Override
	public WorkTask get(Long id) {
		return getHibernateTemplate().get(WorkTask.class, id);
	}
	@SuppressWarnings("unchecked")
	@Override
	public WorkTask getByTaskId(final String taskId, final String processId) {
		return (WorkTask) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from WorkTask u where 1 = 1");
						if(taskId != null && !"".equals(taskId)){
							hql.append(" and u.taskId = :taskId");
						}
						if(processId != null && !"".equals(processId)){
							hql.append(" and u.processId = :processId");
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						if(taskId != null && !"".equals(taskId)){
							query.setParameter("taskId", taskId);
						}
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