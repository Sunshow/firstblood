package web.dao.impl.multiconfirm;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.multiconfirm.MulticonfirmTaskDao;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTaskStatus;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTask;

public class MulticonfirmTaskDaoImpl extends HibernateDaoSupport implements MulticonfirmTaskDao {

	@Override
	public MulticonfirmTask getTask(Long id) {
		return getHibernateTemplate().get(MulticonfirmTask.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MulticonfirmTask> getTaskList(final Long id, final Long configId, final String taskKey,
			final MulticonfirmTaskStatus taskStatus, final Date createTimeFrom, final Date createTimeTo, final Date timeoutTimeFrom, final Date timeoutTimeTo, final PageBean pageBean) {
		return (List<MulticonfirmTask>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from MulticonfirmTask u where 1 = 1");
						if(id != null && id != 0){
							hql.append(" and u.id = :id");
						}
						if(configId != null && configId != 0){
							hql.append(" and u.configId = :configId");
						}
						if(taskKey != null && !taskKey.equals("")){
							hql.append(" and u.taskKey like '%").append(taskKey).append("%'");
						}
						if(taskStatus != null && taskStatus.getValue() != MulticonfirmTaskStatus.ALL.getValue()){
							hql.append(" and u.taskStatus = :taskStatus");
						}
						if(createTimeFrom != null) {
							hql.append(" and u.createTime >= :createTimeFrom");
						}
						if(createTimeTo != null) {
							hql.append(" and u.createTime < :createTimeTo");
						}
						if(timeoutTimeFrom != null) {
							hql.append(" and u.timeoutTime >= :timeoutTimeFrom");
						}
						if(timeoutTimeTo != null) {
							hql.append(" and u.timeoutTime < :timeoutTimeTo");
						}
						hql.append(" order by u.createTime desc");
						Query query = session.createQuery(hql.toString());
						if(id != null && id != 0){
							query.setParameter("id", id);
						}
						if(configId != null && configId != 0){
							query.setParameter("configId", configId);
						}
						if(taskStatus != null && taskStatus.getValue() != MulticonfirmTaskStatus.ALL.getValue()){
							query.setParameter("taskStatus", taskStatus);
						}
						if(createTimeFrom != null) {
							query.setParameter("createTimeFrom", createTimeFrom);
						}
						if(createTimeTo != null) {
							query.setParameter("createTimeTo", createTimeTo);
						}
						if(timeoutTimeFrom != null) {
							query.setParameter("timeoutTimeFrom", timeoutTimeFrom);
						}
						if(timeoutTimeTo != null) {
							query.setParameter("timeoutTimeTo", timeoutTimeTo);
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
	public PageBean getTaskPageBean(final Long id, final Long configId, final String taskKey,
			final MulticonfirmTaskStatus taskStatus, final Date createTimeFrom, final Date createTimeTo, final Date timeoutTimeFrom, final Date timeoutTimeTo, final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from MulticonfirmTask u where 1 = 1");
						if(id != null && id != 0){
							hql.append(" and u.id = :id");
						}
						if(configId != null && configId != 0){
							hql.append(" and u.configId = :configId");
						}
						if(taskKey != null && !taskKey.equals("")){
							hql.append(" and u.taskKey like '%").append(taskKey).append("%'");
						}
						if(taskStatus != null && taskStatus.getValue() != MulticonfirmTaskStatus.ALL.getValue()){
							hql.append(" and u.taskStatus = :taskStatus");
						}
						if(createTimeFrom != null) {
							hql.append(" and u.createTime >= :createTimeFrom");
						}
						if(createTimeTo != null) {
							hql.append(" and u.createTime < :createTimeTo");
						}
						if(timeoutTimeFrom != null) {
							hql.append(" and u.timeoutTime >= :timeoutTimeFrom");
						}
						if(timeoutTimeTo != null) {
							hql.append(" and u.timeoutTime < :timeoutTimeTo");
						}
						hql.append(" order by u.createTime desc");
						Query query = session.createQuery(hql.toString());
						if(id != null && id != 0){
							query.setParameter("id", id);
						}
						if(configId != null && configId != 0){
							query.setParameter("configId", configId);
						}
						if(taskStatus != null && taskStatus.getValue() != MulticonfirmTaskStatus.ALL.getValue()){
							query.setParameter("taskStatus", taskStatus);
						}
						if(createTimeFrom != null) {
							query.setParameter("createTimeFrom", createTimeFrom);
						}
						if(createTimeTo != null) {
							query.setParameter("createTimeTo", createTimeTo);
						}
						if(timeoutTimeFrom != null) {
							query.setParameter("timeoutTimeFrom", timeoutTimeFrom);
						}
						if(timeoutTimeTo != null) {
							query.setParameter("timeoutTimeTo", timeoutTimeTo);
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

	@SuppressWarnings("unchecked")
	@Override
	public MulticonfirmTask getTask(final String taskKey, final MulticonfirmTaskStatus taskStatus) {
		List<MulticonfirmTask> list = (List<MulticonfirmTask>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from MulticonfirmTask u where 1 = 1");
						if(taskKey != null && !taskKey.equals("")){
							hql.append(" and u.taskKey = :taskKey");
						}
						if(taskStatus != null && taskStatus.getValue() != MulticonfirmTaskStatus.ALL.getValue()){
							hql.append(" and u.taskStatus = :taskStatus");
						}
						hql.append(" order by u.createTime desc");
						Query query = session.createQuery(hql.toString());
								
						if(taskKey != null && !taskKey.equals("")){
							query.setParameter("taskKey", taskKey);
						}
						if(taskStatus != null && taskStatus.getValue() != MulticonfirmTaskStatus.ALL.getValue()){
							query.setParameter("taskStatus", taskStatus);
						}
						return query.list();
					}
				});
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public MulticonfirmTask manage(MulticonfirmTask task) {
		return (MulticonfirmTask)getHibernateTemplate().merge(task);
	}

}
