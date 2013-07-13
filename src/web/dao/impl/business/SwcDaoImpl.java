package web.dao.impl.business;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.business.SwcDao;
import com.lehecai.admin.web.domain.business.Swc;
import com.lehecai.core.EnabledStatus;

/**
 * 后台敏感词数据访问层实现类
 * @author yanweijie
 *
 */
public class SwcDaoImpl extends HibernateDaoSupport implements SwcDao {

	/**
	 * 多条件并分页查询所有敏感词
	 * @param name 敏感词
	 * @param status 状态
	 * @param pageBean 分页
	 */
	@SuppressWarnings("unchecked")
	public List<Swc> findSwcList(final String name, final int status, final PageBean pageBean) {
		return super.getHibernateTemplate().executeFind(new HibernateCallback<List<Swc>>() {
			@Override
			public List<Swc> doInHibernate(Session session)
					throws HibernateException, SQLException {
				
				StringBuffer hql = new StringBuffer("from Swc swc where 1 = 1");
				if (name != null && !name.equals("")) {		//按照敏感词模糊查询
					hql.append(" and swc.name like :name");
				}
				if (status != EnabledStatus.ALL.getValue()) {//按照状态查询
					hql.append(" and swc.status = :status");
				}
				
				Query query = session.createQuery(hql.toString());
				
				if (name != null && !name.equals("")) {
					query.setParameter("name", "%" + name + "%");
				}
				if (status != EnabledStatus.ALL.getValue()) {
					query.setParameter("status", EnabledStatus.getItem(status));
				}
				
				if(pageBean.isPageFlag()){
					query.setFirstResult((pageBean.getPage() - 1) * pageBean.getPageSize());
					query.setMaxResults(pageBean.getPageSize());
				}
				return query.list();
			}
		});
	}
	
	/**
	 * 封装多条件查询分页对象
	 * @param pageBean	分页对象
	 * @param name	敏感词
	 * @param status 状态
	 * @return
	 */
	@Override
	public PageBean getPageBean(final PageBean pageBean, final String name, final int status) {
		return getHibernateTemplate().execute(new HibernateCallback<PageBean>() {
			@Override
			public PageBean doInHibernate(Session session)
					throws HibernateException {
				StringBuffer hql = new StringBuffer("select count(swc) from Swc swc where 1 = 1");
				if (name != null && !name.equals("")) {	//根据敏感词模糊查询
					hql.append(" and swc.name like :name");
				}
				if (status != EnabledStatus.ALL.getValue()) {	//根据状态查询
					hql.append(" and swc.status = :status");
				}
				Query query = session.createQuery(hql.toString());
						
				if (name != null && !name.equals("")) {
					query.setParameter("name", "%" + name + "%");
				}
				if (status != EnabledStatus.ALL.getValue()) {
					query.setParameter("status", EnabledStatus.getItem(status));
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
	 * 根据敏感词编号查询敏感词对象
	 * @param id 敏感词编号
	 */
	public Swc getById(Long id) {
		return super.getHibernateTemplate().get(Swc.class, id);
	}
	
	/**
	 * 根据敏感词查询敏感词对象
	 * @param name 敏感词
	 */
	public Swc getByName(final String name) {
		return super.getHibernateTemplate().execute(new HibernateCallback<Swc>() {
			@Override
			public Swc doInHibernate(Session session)
					throws HibernateException, SQLException {
				String hql = "from Swc swc where swc.name = :name";
				Query query = session.createQuery(hql);
				query.setParameter("name",name);
				
				return (Swc)query.setMaxResults(1).uniqueResult();
			}
		});
	}
	
	/**
	 * 添加/修改敏感词
	 * @param adminSwc 敏感词对象
	 */
	public void merge(Swc adminSwc) {
		super.getHibernateTemplate().merge(adminSwc);
	}
}
