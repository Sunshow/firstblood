package web.dao.impl.business;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.business.SmsMailMemberDao;
import com.lehecai.admin.web.domain.business.SmsMailMember;

/**
 * 短信邮件会员数据访问层实现类
 * @author yanweijie
 *
 */
public class SmsMailMemberDaoImpl extends HibernateDaoSupport implements SmsMailMemberDao{

	/**
	 * 分页并多条件查询所有短信邮件会员
	 * @param gruopId 短信邮件会员组Id
	 */
	@SuppressWarnings("unchecked")
	public List<SmsMailMember> findSmsMailMemberList(final PageBean pageBean, final Long gruopId) {
		return super.getHibernateTemplate().executeFind(new HibernateCallback<List<SmsMailMember>>() {

			@Override
			public List<SmsMailMember> doInHibernate(Session session)
					throws HibernateException, SQLException {
				List<SmsMailMember> smsMailMemberList = null;
				
				StringBuffer hql = new StringBuffer("from SmsMailMember sm where 1 = 1");
				hql.append(" and sm.groupId= :gruopId");
				
				Query query = session.createQuery(hql.toString());
				
				query.setParameter("gruopId", gruopId);
				
				if(pageBean != null && pageBean.isPageFlag()){
					if(pageBean.getPageSize() != 0){
						query.setFirstResult((pageBean.getPage() - 1) * pageBean.getPageSize());
						query.setMaxResults(pageBean.getPageSize());
					}
				}
				smsMailMemberList = query.list();
				
				return smsMailMemberList;
			}
		});
	}
	
	/**
	 * 根据短信邮件会员编号查询短信邮件会员
	 * @param userId 短信邮件会员编号
	 */
	@SuppressWarnings("unchecked")
	public List<SmsMailMember> findSmsMailMemberByUid(final Long uid) {
		return super.getHibernateTemplate().executeFind(new HibernateCallback<List<SmsMailMember>>() {

			@Override
			public List<SmsMailMember> doInHibernate(Session session)
					throws HibernateException, SQLException {
				
				StringBuffer hql = new StringBuffer("from SmsMailMember sm where 1 = 1");
				hql.append(" and sm.uid = :uid");
				
				Query query = session.createQuery(hql.toString());
				
				query.setParameter("uid", uid);
				
				return query.list();
			}
		});
	}
	
	/**
	 * 根据短信邮件会员用户名查询短信邮件会员
	 * @param userName 短信邮件会员用户名
	 */
	public SmsMailMember findSmsMailMemberByUserName(final String userName, final Long groupId) {
		return super.getHibernateTemplate().execute(new HibernateCallback<SmsMailMember>() {

			@Override
			public SmsMailMember doInHibernate(Session session)
					throws HibernateException, SQLException {
				
				StringBuffer hql = new StringBuffer("from SmsMailMember sm where 1 = 1");
				hql.append(" and sm.userName= :userName");
				hql.append(" and sm.groupId= :groupId");
				
				Query query = session.createQuery(hql.toString());
				
				query.setParameter("userName", userName);
				query.setParameter("groupId", groupId);
				
				return (SmsMailMember)query.uniqueResult();
			}
		});
	}
	
	/**
	 * 封装多条件查询分页信息
	 * @param gruopId 短信邮件会员组Id
	 * @return
	 */
	@Override
	public PageBean getPageBean(final PageBean pageBean,  final Long groupId) {
		return getHibernateTemplate().execute(
				new HibernateCallback<PageBean>() {
					public PageBean doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(sm) from SmsMailMember sm where 1 = 1");
						
						hql.append(" and sm.groupId=:groupId");
						
						Query query = session.createQuery(hql.toString());
								
						query.setParameter("groupId", groupId);
						
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
	
	/**
	 * 根据短信邮件会员组编号查询短信邮件会员组
	 * @param id 短信邮件会员组编号
	 */
	public SmsMailMember get(Long id) {
		return super.getHibernateTemplate().get(SmsMailMember.class, id);
	}
	
	/**
	 * 添加/修改短信邮件会员组
	 * @param smsMailMember 短信邮件会员组对象
	 */
	public void merge(SmsMailMember smsMailMember) {
		super.getHibernateTemplate().merge(smsMailMember);
	}
	
	/**
	 * 删除短信邮件会员组
	 */
	public void del(SmsMailMember smsMailMember) {
		super.getHibernateTemplate().delete(smsMailMember);
	}
	
}
