package web.dao.impl.business;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.business.SmsMailMemberGroupDao;
import com.lehecai.admin.web.domain.business.SmsMailMemberGroup;

/**
 * 短信邮件会员组数据访问层实现类
 * @author yanweijie
 *
 */
public class SmsMailMemberGroupDaoImpl extends HibernateDaoSupport implements SmsMailMemberGroupDao{

	/**
	 * 分页并多条件查询所有短信邮件会员组
	 * @param pageBean 分页对象
	 * @param userGroup 短信邮件会员组对象
	 */
	@SuppressWarnings("unchecked")
	public List<SmsMailMemberGroup> findSmsMailMemberGroupList(final PageBean pageBean, final String name, final String valid) {
		return super.getHibernateTemplate().executeFind(new HibernateCallback<List<SmsMailMemberGroup>>() {

			@Override
			public List<SmsMailMemberGroup> doInHibernate(Session session)
					throws HibernateException, SQLException {
				List<SmsMailMemberGroup> smsMailMemberGroupList = null;
				
				StringBuffer hql = new StringBuffer("from SmsMailMemberGroup smg where 1 = 1");
				if (name != null && !"".equals(name)) {
					hql.append(" and smg.name like :name");
				}
				if(valid != null && !"".equals(valid)){
					hql.append(" and smg.valid= :valid");
				}
				
				Query query = session.createQuery(hql.toString());
				
				if(name != null && !"".equals(name)){
					query.setParameter("name", "%" + name + "%");
				}
				if(valid != null && !"".equals(valid)){
					if("true".equals(valid)){
						query.setParameter("valid", true);
					}else{
						query.setParameter("valid", false);
					}
				}
				
				if(pageBean != null && pageBean.isPageFlag()){
					if(pageBean.getPageSize() != 0){
						query.setFirstResult((pageBean.getPage() - 1) * pageBean.getPageSize());
						query.setMaxResults(pageBean.getPageSize());
					}
				}
				smsMailMemberGroupList = query.list();
				
				return smsMailMemberGroupList;
			}
		});
	}
	
	/**
	 * 根据短信邮件会员组名称查询短信邮件会员组
	 * @param name 短信邮件会员组名称
	 */
	public SmsMailMemberGroup findSmsMailMemberGroupByName(final String name) {
		return super.getHibernateTemplate().execute(new HibernateCallback<SmsMailMemberGroup>() {

			@Override
			public SmsMailMemberGroup doInHibernate(Session session)
					throws HibernateException, SQLException {
				StringBuffer hql = new StringBuffer("from SmsMailMemberGroup smg where 1 = 1");
				if (name != null && !"".equals(name)) {
					hql.append(" and smg.name = :name");
				}
				
				Query query = session.createQuery(hql.toString());
				
				if(name != null && !"".equals(name)){
					query.setParameter("name", name);
				}
				
				return (SmsMailMemberGroup)query.setMaxResults(1).uniqueResult();
			}
		});
	}
	
	/**
	 * 封装多条件查询分页信息
	 * @param pageBean	分页对象
	 * @param name	短信邮件会员组名称
	 * @param valid	是否有效
	 * @return
	 */
	@Override
	public PageBean getPageBean(final PageBean pageBean, final String name,final String valid) {
		return getHibernateTemplate().execute(
				new HibernateCallback<PageBean>() {
					public PageBean doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(smg) from SmsMailMemberGroup smg where 1 = 1");
						
						if(name != null && !"".equals(name)){
							hql.append(" and smg.name like :name");
						}
						if(valid != null && !"".equals(valid)){
							hql.append(" and smg.valid=:valid");
						}
						Query query = session.createQuery(hql.toString());
								
						if(name != null && !"".equals(name)){
							query.setParameter("name", "%" + name + "%");
						}
						if(valid != null && !"".equals(valid)){
							if("true".equals(valid)){
								query.setParameter("valid", true);
							}else{
								query.setParameter("valid", false);
							}
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
	
	/**
	 * 根据短信邮件会员组编号查询短信邮件会员组
	 * @param id 短信邮件会员组编号
	 */
	public SmsMailMemberGroup get(Long id) {
		return super.getHibernateTemplate().get(SmsMailMemberGroup.class, id);
	}
	
	/**
	 * 添加/修改短信邮件会员组
	 * @param smsMailMemberGroup 短信邮件会员组对象
	 */
	public void merge(SmsMailMemberGroup smsMailMemberGroup) {
		super.getHibernateTemplate().merge(smsMailMemberGroup);
	}
	
	/**
	 * 删除短信邮件会员组
	 */
	public void del(SmsMailMemberGroup smsMailMemberGroup) {
		super.getHibernateTemplate().delete(smsMailMemberGroup);
	}
	
}
