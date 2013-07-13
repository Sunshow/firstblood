package web.dao.impl.business;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.business.ChuangShiManDaoSmsDao;
import com.lehecai.admin.web.domain.business.ChuangShiManDaoSms;

public class ChuangShiManDaoSmsDaoImpl extends HibernateDaoSupport implements ChuangShiManDaoSmsDao {
	@Override
	public ChuangShiManDaoSms merge(ChuangShiManDaoSms sms) {
		return getHibernateTemplate().merge(sms);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChuangShiManDaoSms> list(final String sender, final Date beginDate, final Date endDate, final PageBean pageBean) {
		return (List<ChuangShiManDaoSms>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from ChuangShiManDaoSms u where 1 = 1");
						if(sender != null && !"".equals(sender)){
							hql.append(" and u.sender = :sender");
						}
						if(beginDate != null){
							hql.append(" and u.recdate >= :beginDate");
						}
						if(endDate != null){
							hql.append(" and u.recdate < :endDate");
						}
						hql.append(" order by u.recdate desc, u.id desc");
						Query query = session.createQuery(hql.toString());
						
						if(sender != null && !"".equals(sender)){
							query.setParameter("sender", sender);
						}
						if(beginDate != null){
							query.setParameter("beginDate", beginDate);
						}
						if(endDate != null){
							query.setParameter("endDate", endDate);
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
	public ChuangShiManDaoSms get(Long ID) {
		return getHibernateTemplate().get(ChuangShiManDaoSms.class, ID);
	}

	@Override
	public void del(ChuangShiManDaoSms sms) {
		getHibernateTemplate().delete(sms);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final String sender, final Date beginDate, final Date endDate, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from ChuangShiManDaoSms u where 1 = 1");
						
						if(sender != null && !"".equals(sender)){
							hql.append(" and u.sender = :sender");
						}
						if(beginDate != null){
							hql.append(" and u.recdate >= :beginDate");
						}
						if(endDate != null){
							hql.append(" and u.recdate < :endDate");
						}
						Query query = session.createQuery(hql.toString());
						
						if(sender != null && !"".equals(sender)){
							query.setParameter("sender", sender);
						}
						if(beginDate != null){
							query.setParameter("beginDate", beginDate);
						}
						if(endDate != null){
							query.setParameter("endDate", endDate);
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
