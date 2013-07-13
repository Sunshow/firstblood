package web.dao.impl.process;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.process.AmountSettingDao;
import com.lehecai.admin.web.domain.process.AmountSetting;
import com.lehecai.admin.web.domain.process.WorkProcessUser;
import com.lehecai.admin.web.enums.ProcessUserType;

public class AmountSettingDaoImpl extends HibernateDaoSupport implements AmountSettingDao {

	@Override
	public void del(AmountSetting amountSetting) {
		getHibernateTemplate().delete(amountSetting);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AmountSetting get(final Long id) {
		AmountSetting amountSetting = (AmountSetting) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from AmountSetting u where 1 = 1");
						if(id != null && id != 0){
							hql.append(" and u.id = :id");
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						if(id != null && id != 0){
							query.setParameter("id", id);
						}
						return query.uniqueResult();
					}
				});
		return amountSetting;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final String processId, final String taskId, final Long operateId, final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from AmountSetting u where 1 = 1");
						
						if(processId != null && !processId.equals("")){
							hql.append(" and u.processId = :processId");
						}
						if(taskId != null && !taskId.equals("")){
							hql.append(" and u.taskId = :taskId");
						}
						if(operateId != null && operateId != 0){
							hql.append(" and u.operateId = :operateId");
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						if(processId != null && !processId.equals("")){
							query.setParameter("processId", processId);
						}
						if(taskId != null && !taskId.equals("")){
							query.setParameter("taskId", taskId);
						}
						if(operateId != null && operateId != 0){
							query.setParameter("operateId", operateId);
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

	@Override
	public void merge(AmountSetting amountSetting) {
		getHibernateTemplate().merge(amountSetting);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AmountSetting> list(final String processId, final String taskId, final Long operateId, PageBean pageBean) {
		List<AmountSetting> amountSettingList = (List<AmountSetting>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from AmountSetting u where 1 = 1");
						if(processId != null && !processId.equals("")){
							hql.append(" and u.processId = :processId");
						}
						if(taskId != null && !taskId.equals("")){
							hql.append(" and u.taskId = :taskId");
						}
						if(operateId != null && operateId != 0){
							hql.append(" and u.operateId = :operateId");
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						if(processId != null && !processId.equals("")){
							query.setParameter("processId", processId);
						}
						if(taskId != null && !taskId.equals("")){
							query.setParameter("taskId", taskId);
						}
						if(operateId != null && operateId != 0){
							query.setParameter("operateId", operateId);
						}
						return query.list();
						
					}
				});
		return amountSettingList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Double getOneTimeAmount(final String processId, final String taskId, final Long operateId,final ProcessUserType userType) {
		WorkProcessUser workProcessUser = (WorkProcessUser) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from WorkProcessUser u where 1 = 1");
						if(processId != null && !processId.equals("")){
							hql.append(" and u.processId = :processId");
						}
						if(taskId != null && !taskId.equals("")){
							hql.append(" and u.taskId = :taskId");
						}
						if(operateId != null && operateId != 0){
							hql.append(" and u.operateId = :operateId");
						}
						if(userType != null && userType.getValue() != ProcessUserType.ALL.getValue()){
							hql.append(" and u.processUserType = :userType");
						}
						hql.append(" order by u.id desc");
						Query query = session.createQuery(hql.toString());
						if(processId != null && !processId.equals("")){
							query.setParameter("processId", processId);
						}
						if(taskId != null && !taskId.equals("")){
							query.setParameter("taskId", taskId);
						}
						if(operateId != null && operateId != 0){
							query.setParameter("operateId", operateId);
						}
						if(userType != null && userType.getValue() != ProcessUserType.ALL.getValue()){
							query.setParameter("userType", userType);
						}
						return query.uniqueResult();
					}
				});
		if (workProcessUser != null) {
			return workProcessUser.getAmount();
		} else {
			return null;
		}
	}
}