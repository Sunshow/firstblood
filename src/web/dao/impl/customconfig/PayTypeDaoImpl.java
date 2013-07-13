package web.dao.impl.customconfig;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.customconfig.PayTypeDao;
import com.lehecai.admin.web.domain.customconfig.PayType;

public class PayTypeDaoImpl extends HibernateDaoSupport implements PayTypeDao {
	@Override
	public PayType get(Integer ID) {
		return getHibernateTemplate().get(PayType.class, ID);
	}

	@Override
	public void save(PayType payType) {
		getHibernateTemplate().save(payType);
	}
	
	@Override
	public void del(PayType payType) {
		getHibernateTemplate().delete(payType);
	}

	@Override
	public PayType merge(PayType payType) {
		return (PayType)getHibernateTemplate().merge(payType);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<PayType> list(final PayType payType,final PageBean pageBean){
		return (List<PayType>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer hql = new StringBuffer("from PayType t where 1 = 1");
				hql.append(" order by t.id desc");
				Query query = session.createQuery(hql.toString());
				
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
	public PageBean getPageBean(final PayType payType, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(t) from PayType t where 1 = 1");
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
