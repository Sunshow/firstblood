package web.dao.impl.leheq;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.leheq.SmsLogDao;
import com.lehecai.admin.web.domain.leheq.SmsLog;
import com.lehecai.core.YesNoStatus;

/**
 * 短信日志数据访问层实现类
 * @author yanweijie
 *
 */
public class SmsLogDaoImpl extends HibernateDaoSupport implements SmsLogDao {
	
	/**
	 * 条件并分页查询短信日志
	 */
	@SuppressWarnings("unchecked")
	public List<SmsLog> findSmsLogList(final String smsto, final Date beginSendTime, final Date endSendTime, 
			final YesNoStatus result, final PageBean pageBean) {
		return super.getHibernateTemplate().executeFind(new HibernateCallback<List<SmsLog>>() {

			@Override
			public List<SmsLog> doInHibernate(Session session) throws HibernateException,
					SQLException {
				StringBuffer hql = new StringBuffer("from SmsLog sl where 1 = 1");
				
				if (smsto != null && !smsto.equals("")) {
					hql.append(" and sl.smsto = :smsto");				//根据接收人查询
				}
				if (beginSendTime != null) {
					hql.append(" and sl.sendTime >= :beginSendTime");	//根据接收起始时间查询
				}
				if (endSendTime != null) {
					hql.append(" and sl.sendTime <= :endSendTime");		//根据接收截止时间查询
				}
				if (result != null && result.getValue() != YesNoStatus.ALL.getValue()) {
					hql.append(" and sl.result = :result");				//根据发送结果查询
				}
				hql.append(" order by sl.id desc");
				
				Query query = session.createQuery(hql.toString());
				
				if (smsto != null && !smsto.equals("")) {
					query.setParameter("smsto", smsto);
				}
				if (beginSendTime != null) {
					query.setParameter("beginSendTime", beginSendTime);
				}
				if (endSendTime != null) {
					query.setParameter("endSendTime", endSendTime);
				}
				if (result != null && result.getValue() != YesNoStatus.ALL.getValue()) {
					query.setParameter("result", result.getValue());
				}
				if (pageBean != null && pageBean.isPageFlag()) {
					query.setFirstResult((pageBean.getPage()-1)*pageBean.getPageSize());
					query.setMaxResults(pageBean.getPageSize());
				}
				
				return query.list();
			}
		});
	}
	
	/**
	 * 查询短信日志详细信息
	 */
	public SmsLog get(final Integer id) {
		return super.getHibernateTemplate().execute(new HibernateCallback<SmsLog>() {

			@Override
			public SmsLog doInHibernate(Session session) throws HibernateException,
					SQLException {
				StringBuffer hql = new StringBuffer("from SmsLog sl where 1 = 1");
				hql.append(" and sl.id = :id");
				
				Query query = session.createQuery(hql.toString());
				query.setParameter("id", id);
				
				return (SmsLog)query.setMaxResults(1).uniqueResult();
			}
		});
	}
	
	/**
	 * 条件并分页查询短信日志分页
	 */
	public PageBean getPageBean(final String smsto, final Date beginSendTime, final Date endSendTime, 
			final YesNoStatus result, final PageBean pageBean) {
		return super.getHibernateTemplate().execute(new HibernateCallback<PageBean>() {

			@Override
			public PageBean doInHibernate(Session session) throws HibernateException,
					SQLException {
				StringBuffer hql = new StringBuffer("select count(sl.id) from SmsLog sl where 1 = 1");
				
				if (smsto != null && !smsto.equals("")) {
					hql.append(" and sl.smsto = :smsto");				//根据接收人查询
				}
				if (beginSendTime != null) {
					hql.append(" and sl.sendTime >= :beginSendTime");	//根据接收起始时间查询
				}
				if (endSendTime != null) {
					hql.append(" and sl.sendTime <= :endSendTime");		//根据接收截止时间查询
				}
				if (result != null && result.getValue() != YesNoStatus.ALL.getValue()) {
					hql.append(" and sl.result = :result");				//根据发送结果查询
				}
				
				Query query = session.createQuery(hql.toString());
				
				if (smsto != null && !smsto.equals("")) {
					query.setParameter("smsto", smsto);
				}
				if (beginSendTime != null) {
					query.setParameter("beginSendTime", beginSendTime);
				}
				if (endSendTime != null) {
					query.setParameter("endSendTime", endSendTime);
				}
				if (result != null && result.getValue() != YesNoStatus.ALL.getValue()) {
					query.setParameter("result", result.getValue());
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
