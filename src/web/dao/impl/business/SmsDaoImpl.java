package web.dao.impl.business;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.business.SmsDao;
import com.lehecai.admin.web.domain.business.Sms;
import com.lehecai.admin.web.enums.StatusType;

public class SmsDaoImpl extends HibernateDaoSupport implements SmsDao {

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.SMSDao#merge(com.lehecai.admin.web.domain.business.SMS)
	 */
	@Override
	public Sms merge(Sms sms) {
		// TODO Auto-generated method stub
		return getHibernateTemplate().merge(sms);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.SMSDao#list(com.lehecai.admin.web.domain.business.SMS, com.lehecai.admin.web.bean.PageBean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Sms> list(final String smsTo, final Integer status, final Date beginDate, final Date endDate, final PageBean pageBean) {
		return (List<Sms>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from Sms u where 1 = 1");
						if(smsTo != null && !"".equals(smsTo)){
							hql.append(" and u.smsTo like :smsTo");
						}
						if(beginDate != null){
							hql.append(" and u.sendTime >= :beginDate");
						}
						if(endDate != null){
							hql.append(" and u.sendTime < :endDate");
						}
						if(status != null && status != 0){
							hql.append(" and u.status = :status");
						}
						hql.append(" order by u.sendTime desc, u.id desc");
						Query query = session.createQuery(hql.toString());
						
						if(smsTo != null && !"".equals(smsTo)){
							query.setParameter("smsTo", "%" + smsTo + "%");
						}
						if(beginDate != null){
							query.setParameter("beginDate", beginDate);
						}
						if(endDate != null){
							query.setParameter("endDate", endDate);
						}
						if(status != null && status != 0){
							query.setParameter("status", StatusType.getItem(status));
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

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.SMSDao#get(java.lang.Long)
	 */
	@Override
	public Sms get(Long ID) {
		// TODO Auto-generated method stub
		return getHibernateTemplate().get(Sms.class, ID);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.SMSDao#del(com.lehecai.admin.web.domain.business.SMS)
	 */
	@Override
	public void del(Sms sms) {
		// TODO Auto-generated method stub
		getHibernateTemplate().delete(sms);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.SMSDao#getPageBean(com.lehecai.admin.web.domain.business.SMS, com.lehecai.admin.web.bean.PageBean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final String smsTo, final Integer status, final Date beginDate, final Date endDate, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from Sms u where 1 = 1");
						
						if(smsTo != null && !"".equals(smsTo)){
							hql.append(" and u.smsTo like :smsTo");
						}
						if(beginDate != null){
							hql.append(" and u.sendTime >= :beginDate");
						}
						if(endDate != null){
							hql.append(" and u.sendTime < :endDate");
						}
						if(status != null && status != 0){
							hql.append(" and u.status = :status");
						}
						Query query = session.createQuery(hql.toString());
						
						if(smsTo != null && !"".equals(smsTo)){
							query.setParameter("smsTo", "%" + smsTo + "%");
						}
						if(beginDate != null){
							query.setParameter("beginDate", beginDate);
						}
						if(endDate != null){
							query.setParameter("endDate", endDate);
						}
						if(status != null && status != 0){
							query.setParameter("status", StatusType.getItem(status));
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
