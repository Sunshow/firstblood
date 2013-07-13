package web.dao.impl.business;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.business.MailDao;
import com.lehecai.admin.web.domain.business.Mail;
import com.lehecai.admin.web.enums.StatusType;

public class MailDaoImpl extends HibernateDaoSupport implements MailDao {

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.MailDao#merge(com.lehecai.admin.web.domain.business.Mail)
	 */
	@Override
	public Mail merge(Mail mail) {
		// TODO Auto-generated method stub
		return getHibernateTemplate().merge(mail);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.MailDao#list(com.lehecai.admin.web.domain.business.Mail, com.lehecai.admin.web.bean.PageBean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Mail> list(final String mailTo, final String subject, final Integer status, final Date beginDate, final Date endDate, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (List<Mail>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from Mail u where 1 = 1");
						if(mailTo != null && !"".equals(mailTo)){
							hql.append(" and u.mailTo like :mailTo");
						}
						if(subject != null && !"".equals(subject)){
							hql.append(" and u.subject like :subject");
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
						
						if(mailTo != null && !"".equals(mailTo)){
							query.setParameter("mailTo", "%" + mailTo + "%");
						}
						if(subject != null && !"".equals(subject)){
							query.setParameter("subject", "%" + subject + "%");
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
	 * @see com.lehecai.admin.web.dao.impl.business.MailDao#get(java.lang.Long)
	 */
	@Override
	public Mail get(Long ID) {
		// TODO Auto-generated method stub
		return getHibernateTemplate().get(Mail.class, ID);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.MailDao#del(com.lehecai.admin.web.domain.business.Mail)
	 */
	@Override
	public void del(Mail mail) {
		// TODO Auto-generated method stub
		getHibernateTemplate().delete(mail);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.MailDao#getPageBean(com.lehecai.admin.web.domain.business.Mail, com.lehecai.admin.web.bean.PageBean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final String mailTo, final String subject, final Integer status, final Date beginDate, final Date endDate, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from Mail u where 1 = 1");
						
						if(mailTo != null && !"".equals(mailTo)){
							hql.append(" and u.mailTo like :mailTo");
						}
						if(subject != null && !"".equals(subject)){
							hql.append(" and u.subject like :subject");
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
						
						if(mailTo != null && !"".equals(mailTo)){
							query.setParameter("mailTo", "%" + mailTo + "%");
						}
						if(subject != null && !"".equals(subject)){
							query.setParameter("subject", "%" + subject + "%");
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
