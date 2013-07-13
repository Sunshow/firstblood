package web.dao.impl.finance;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.finance.TerminalFundAdjustDao;
import com.lehecai.admin.web.domain.finance.TerminalFundAdjust;

public class TerminalFundAdjustDaoImpl extends HibernateDaoSupport implements TerminalFundAdjustDao {
	@Override
	public TerminalFundAdjust get(Long ID) {
		return getHibernateTemplate().get(TerminalFundAdjust.class, ID);
	}

	@Override
	public void save(TerminalFundAdjust terminalFundAdjust) {
		getHibernateTemplate().save(terminalFundAdjust);
	}
	
	@Override
	public void del(TerminalFundAdjust terminalFundAdjust) {
		getHibernateTemplate().delete(terminalFundAdjust);
	}

	@Override
	public TerminalFundAdjust merge(TerminalFundAdjust terminalFundAdjust) {
		return (TerminalFundAdjust)getHibernateTemplate().merge(terminalFundAdjust);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<TerminalFundAdjust> list(final TerminalFundAdjust terminalFundAdjust,final PageBean pageBean){
		return (List<TerminalFundAdjust>)getHibernateTemplate().execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from TerminalFundAdjust t where 1 = 1");
						if(terminalFundAdjust != null && terminalFundAdjust.getTerminalAccountCheckItemId() != null){
							hql.append(" and t.terminalAccountCheckItemId = :terminalAccountCheckItemId");
						}
						hql.append(" order by t.createTime desc");
						Query query = session.createQuery(hql.toString());
						if(terminalFundAdjust != null && terminalFundAdjust.getTerminalAccountCheckItemId() != null){
							query.setParameter("terminalAccountCheckItemId", terminalFundAdjust.getTerminalAccountCheckItemId());
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
	public PageBean getPageBean(final TerminalFundAdjust terminalFundAdjust, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(t) from TerminalFundAdjust t where 1 = 1");

						hql.append(" order by t.createTime desc");
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
