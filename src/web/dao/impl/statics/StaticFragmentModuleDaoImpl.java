package web.dao.impl.statics;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.statics.StaticFragmentModuleDao;
import com.lehecai.admin.web.domain.statics.StaticFragmentModule;
import com.lehecai.admin.web.domain.statics.StaticFragmentModuleItem;

/**
 * 静态碎片模板数据访问层实现类
 * @author yanweijie
 *
 */
public class StaticFragmentModuleDaoImpl extends HibernateDaoSupport implements StaticFragmentModuleDao {

	/**
	 * 查询静态碎片模板列表
	 */
	@SuppressWarnings("unchecked")
	public List<StaticFragmentModule> findModuleList() {
		return super.getHibernateTemplate().executeFind(new HibernateCallback<List<StaticFragmentModule>>() {

			@Override
			public List<StaticFragmentModule> doInHibernate(Session session)
					throws HibernateException, SQLException {
				StringBuffer hql = new StringBuffer("from StaticFragmentModule scm");
				hql.append(" order by scm.id desc");
				
				Query query = session.createQuery(hql.toString());
				
				return query.list();
			}
		});
	}
	
	/**
	 * 查询静态碎片模板
	 */
	public StaticFragmentModule getModule(Long id) {
		return super.getHibernateTemplate().get(StaticFragmentModule.class, id);
	}
	
	/**
	 * 根据静态碎片模板名称查询静态碎片模板
	 */
	public StaticFragmentModule getModuleByName (final String moduleName) {
		return super.getHibernateTemplate().execute(new HibernateCallback<StaticFragmentModule>() {

			@Override
			public StaticFragmentModule doInHibernate(Session session) throws HibernateException,
					SQLException {
				StringBuffer hql = new StringBuffer("from StaticFragmentModule scm where 1 = 1");
				hql.append(" and scm.moduleName = :moduleName");
				
				Query query = session.createQuery(hql.toString());
				query.setParameter("moduleName", moduleName);
				
				if (query.list().size() > 0) {
					return (StaticFragmentModule)query.list().get(0);
				}
				
				return null;
			}
		});
	}
	
	/**
	 * 更新静态碎片模板
	 */
	public void mergeModule(StaticFragmentModule staticFragmentModule) {
		super.getHibernateTemplate().merge(staticFragmentModule);
	}
	
	/**
	 * 查询静态碎片模板自定义属性列表
	 */
	@SuppressWarnings("unchecked")
	public List<StaticFragmentModuleItem> findItemList(final Long moduleId) {
		return super.getHibernateTemplate().executeFind(new HibernateCallback<List<StaticFragmentModuleItem>>() {

			@Override
			public List<StaticFragmentModuleItem> doInHibernate(Session session)
					throws HibernateException, SQLException {
				StringBuffer hql = new StringBuffer("from StaticFragmentModuleItem scmi where 1 = 1");
				hql.append(" and scmi.moduleId = :moduleId");
				
				Query query = session.createQuery(hql.toString());
				query.setParameter("moduleId", moduleId);
				
				return query.list();
			}
		});
	}
	
	/**
	 * 查询静态碎片模板自定义属性
	 */
	public StaticFragmentModuleItem getItem(Long id) {
		return super.getHibernateTemplate().get(StaticFragmentModuleItem.class, id);
	}
	
	/**
	 * 更新静态碎片模板自定义属性
	 */
	public void mergeItem(StaticFragmentModuleItem staticFragmentModuleItem) {
		super.getHibernateTemplate().merge(staticFragmentModuleItem);
	}
	
	/**
	 * 删除静态碎片模板属性
	 */
	public void deleteItem(StaticFragmentModuleItem staticFragmentModuleItem) {
		super.getHibernateTemplate().delete(staticFragmentModuleItem);
	}
}
