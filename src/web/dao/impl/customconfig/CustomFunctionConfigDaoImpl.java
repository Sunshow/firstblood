package web.dao.impl.customconfig;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.customconfig.CustomFunctionConfigDao;
import com.lehecai.admin.web.domain.customconfig.CustomFunctionConfig;
import com.lehecai.admin.web.domain.customconfig.FunctionType;

public class CustomFunctionConfigDaoImpl extends HibernateDaoSupport implements CustomFunctionConfigDao {
	@Override
	public CustomFunctionConfig get(Long ID) {
		return getHibernateTemplate().get(CustomFunctionConfig.class, ID);
	}

	@Override
	public void save(CustomFunctionConfig customFunctionConfig) {
		getHibernateTemplate().save(customFunctionConfig);
	}
	
	@Override
	public void del(CustomFunctionConfig customFunctionConfig) {
		getHibernateTemplate().delete(customFunctionConfig);
	}

	@Override
	public CustomFunctionConfig merge(CustomFunctionConfig customFunctionConfig) {
		return (CustomFunctionConfig)getHibernateTemplate().merge(customFunctionConfig);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<CustomFunctionConfig> list(final CustomFunctionConfig customFunctionConfig,final PageBean pageBean){
		return (List<CustomFunctionConfig>)getHibernateTemplate().execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from CustomFunctionConfig t where 1 = 1");
						FunctionType functionType = customFunctionConfig.getFunctionType();
						if(functionType != null && functionType.getValue() != 0){
							hql.append(" and t.functionType = :functionType");
						}
						hql.append(" order by t.id desc");
						Query query = session.createQuery(hql.toString());
						if(functionType != null && functionType.getValue() != 0){
							query.setParameter("functionType", functionType);
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
	public PageBean getPageBean(final CustomFunctionConfig customFunctionConfig, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(t) from CustomFunctionConfig t where 1 = 1");
						hql.append(" order by t.id desc");
						Query query = session.createQuery(hql.toString());
						if(pageBean != null && pageBean.isPageFlag()){
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
