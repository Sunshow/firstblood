package web.dao.impl.multiconfirm;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.multiconfirm.MulticonfirmRecordDao;
import com.lehecai.admin.web.multiconfirm.MulticonfirmRecord;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTask;

public class MulticonfirmRecordDaoImpl extends HibernateDaoSupport implements MulticonfirmRecordDao {

	@SuppressWarnings("unchecked")
	@Override
	public MulticonfirmRecord get(final MulticonfirmTask task, final Long userId) {
		List<MulticonfirmRecord> list = (List<MulticonfirmRecord>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from MulticonfirmRecord u where 1 = 1");
						if(task != null && task.getId() != null && task.getId() != 0){
							hql.append(" and u.taskId = :taskId");
						}
						if(userId != null && userId != 0){
							hql.append(" and u.userId = :userId");
						}
						hql.append(" order by u.createTime desc");
						Query query = session.createQuery(hql.toString());
						if(task != null && task.getId()!= null && task.getId() != 0){
							query.setParameter("taskId", task.getId());
						}
						if(userId != null && userId != 0){
							query.setParameter("userId", userId);
						}
						return query.setMaxResults(1).list();
					}
				});
		if (list == null || list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public MulticonfirmRecord manage(MulticonfirmRecord record) {
		return (MulticonfirmRecord)getHibernateTemplate().merge(record);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MulticonfirmRecord> getRecordList(final MulticonfirmTask task, final PageBean pageBean) {
		return (List<MulticonfirmRecord>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from MulticonfirmRecord u where 1 = 1");
						if(task != null && task.getId() != null && task.getId() != 0){
							hql.append(" and u.taskId = :taskId");
						}
						hql.append(" order by u.createTime desc");
						Query query = session.createQuery(hql.toString());
						if(task != null && task.getId()!= null && task.getId() != 0){
							query.setParameter("taskId", task.getId());
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
	public PageBean getRecordPageBean(final MulticonfirmTask task, final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from MulticonfirmRecord u where 1 = 1");
						if(task != null && task.getId() != null && task.getId() != 0){
							hql.append(" and u.taskId = :taskId");
						}
						hql.append(" order by u.createTime desc");
						Query query = session.createQuery(hql.toString());
						if(task != null && task.getId()!= null && task.getId() != 0){
							query.setParameter("taskId", task.getId());
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
