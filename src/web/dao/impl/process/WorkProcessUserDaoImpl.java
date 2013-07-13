package web.dao.impl.process;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.process.WorkProcessUserDao;
import com.lehecai.admin.web.domain.process.WorkProcessUser;
import com.lehecai.admin.web.enums.ProcessUserType;

public class WorkProcessUserDaoImpl extends HibernateDaoSupport implements WorkProcessUserDao {

	@Override
	public void del(WorkProcessUser workProcessUser) {
		getHibernateTemplate().delete(workProcessUser);
	}
	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final String processId, final String taskId,
			final ProcessUserType processUserType, final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from WorkProcessUser u where 1 = 1");
						
						
						if(processId != null && !"".equals(processId)){
							hql.append(" and u.processId = :processId");
						}
						if(taskId != null && !"".equals(taskId)){
							hql.append(" and u.taskId = :taskId");
						}
						
						if(processUserType != null && processUserType.getValue() != ProcessUserType.ALL.getValue()){
							hql.append(" and u.processUserType = :processUserType");
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						
						
						if(processId != null && !"".equals(processId)){
							query.setParameter("processId", processId);
						}
						if(taskId != null && !"".equals(taskId)){
							query.setParameter("taskId", taskId);
						}
						
						if(processUserType != null && processUserType.getValue() != ProcessUserType.ALL.getValue()){
							query.setParameter("processUserType", processUserType.getValue());
						}
						if(pageBean != null && pageBean.isPageFlag()){
							int totalCount = ((Long)query.iterate().next()).intValue();
							pageBean.setCount(totalCount);
							int pageCount = 0;//页数
							if(pageBean != null && pageBean.getPageSize() != 0) {
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
	public List<WorkProcessUser> list(final String processId, final String taskId,
			final ProcessUserType processUserType, final PageBean pageBean) {
		return (List<WorkProcessUser>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from WorkProcessUser u where 1 = 1");
						
						if(processId != null && !"".equals(processId)){
							hql.append(" and u.processId = :processId");
						}
						if(taskId != null && !"".equals(taskId)){
							hql.append(" and u.taskId = :taskId");
						}
						
						if(processUserType != null && processUserType.getValue() != ProcessUserType.ALL.getValue()){
							hql.append(" and u.processUserType = :processUserType");
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						
						if(processId != null && !"".equals(processId)){
							query.setParameter("processId", processId);
						}
						if(taskId != null && !"".equals(taskId)){
							query.setParameter("taskId", taskId);
						}
						
						if(processUserType != null && processUserType.getValue() != ProcessUserType.ALL.getValue()){
							query.setParameter("processUserType", processUserType);
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
	public void manage(WorkProcessUser workProcessUser) {
		getHibernateTemplate().merge(workProcessUser);
	}
	@Override
	public WorkProcessUser get(Long id) {
		return getHibernateTemplate().get(WorkProcessUser.class, id);
	}
	@SuppressWarnings("unchecked")
	@Override
	public WorkProcessUser getByItem(final String processId, final String taskId,
			final ProcessUserType processUserType) {
		return (WorkProcessUser) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from WorkProcess u where 1 = 1");
						if(processId != null && !"".equals(processId)){
							hql.append(" and u.processId = :processId");
						}
						if(taskId != null && !"".equals(taskId)){
							hql.append(" and u.taskId = :taskId");
						}
						if(processUserType != null && processUserType.getValue() != ProcessUserType.ALL.getValue()){
							hql.append(" and u.processUserType = :processUserType");
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						if(processId != null && !"".equals(processId)){
							query.setParameter("processId", processId);
						}
						if(taskId != null && !"".equals(taskId)){
							query.setParameter("taskId", taskId);
						}
						if(processUserType != null && processUserType.getValue() != ProcessUserType.ALL.getValue()){
							query.setParameter("processUserType", processUserType.getValue());
						}
						List<WorkProcessUser> list = query.list();
						if (list != null && list.size() > 0) {
							return list.get(0);
						} else {
							return null;
						}
					}
				});
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<WorkProcessUser> list(final WorkProcessUser workProcessUser,
			final PageBean pageBean) {
		return (List<WorkProcessUser>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from WorkProcessUser u where 1 = 1");
						if (workProcessUser != null) {
							if(workProcessUser.getId() != null && workProcessUser.getId() != 0){
								hql.append(" and u.id = :id");
							}
							if(workProcessUser.getOperateId() != null && workProcessUser.getOperateId() != 0){
								hql.append(" and u.operateId = :operateId");
							}
							
							if(workProcessUser.getProcessUserType() != null && workProcessUser.getProcessUserType().getValue() != ProcessUserType.ALL.getValue()){
								hql.append(" and u.processUserType = :processUserType");
							}
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						
						if (workProcessUser != null) {
							if(workProcessUser.getId() != null && workProcessUser.getId() != 0){
								query.setParameter("id", workProcessUser.getId());
							}
							if(workProcessUser.getOperateId() != null && workProcessUser.getOperateId() != 0){
								query.setParameter("operateId", workProcessUser.getOperateId());
							}
							
							if(workProcessUser.getProcessUserType() != null && workProcessUser.getProcessUserType().getValue() != ProcessUserType.ALL.getValue()){
								query.setParameter("processUserType", workProcessUser.getProcessUserType());
							}
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
	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final WorkProcessUser workProcessUser,
			final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from WorkProcessUser u where 1 = 1");
						
						if (workProcessUser != null) {
							if(workProcessUser.getId() != null && workProcessUser.getId() != 0){
								hql.append(" and u.id = :id");
							}
							if(workProcessUser.getOperateId() != null && workProcessUser.getOperateId() != 0){
								hql.append(" and u.operateId = :operateId");
							}
							
							if(workProcessUser.getProcessUserType() != null && workProcessUser.getProcessUserType().getValue() != ProcessUserType.ALL.getValue()){
								hql.append(" and u.processUserType = :processUserType");
							}
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						
						if (workProcessUser != null) {
							if(workProcessUser.getId() != null && workProcessUser.getId() != 0){
								query.setParameter("id", workProcessUser.getId());
							}
							if(workProcessUser.getOperateId() != null && workProcessUser.getOperateId() != 0){
								query.setParameter("operateId", workProcessUser.getOperateId());
							}
							
							if(workProcessUser.getProcessUserType() != null && workProcessUser.getProcessUserType().getValue() != ProcessUserType.ALL.getValue()){
								query.setParameter("processUserType", workProcessUser.getProcessUserType());
							}
						}
						if(pageBean != null && pageBean.isPageFlag()){
							int totalCount = ((Long)query.iterate().next()).intValue();
							pageBean.setCount(totalCount);
							int pageCount = 0;//页数
							if(pageBean != null && pageBean.getPageSize() != 0) {
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
	public List<WorkProcessUser> list(final String processId, final String taskId,
			final ProcessUserType processUserType, final Long userId) {
		return (List<WorkProcessUser>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from WorkProcessUser u where 1 = 1");
						
						if(processId != null && !"".equals(processId)){
							hql.append(" and u.processId = :processId");
						}
						if(taskId != null && !"".equals(taskId)){
							hql.append(" and u.taskId = :taskId");
						}
						
						if(processUserType != null && processUserType.getValue() != ProcessUserType.ALL.getValue()){
							hql.append(" and u.processUserType = :processUserType");
						}
						
						if(userId != null && !"".equals(userId)){
							hql.append(" and u.operateId = :userId");
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						
						if(processId != null && !"".equals(processId)){
							query.setParameter("processId", processId);
						}
						if(taskId != null && !"".equals(taskId)){
							query.setParameter("taskId", taskId);
						}
						if(processUserType != null && processUserType.getValue() != ProcessUserType.ALL.getValue()){
							query.setParameter("processUserType", processUserType);
						}
						if(userId != null && !"".equals(userId)){
							query.setParameter("userId", userId);
						}
						return query.list();
						
					}
				});
	}
}