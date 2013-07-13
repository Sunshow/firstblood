package web.dao.impl.finance;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.finance.TerminalCompanyDao;
import com.lehecai.admin.web.domain.finance.TerminalCompany;

public class TerminalCompanyDaoImpl extends HibernateDaoSupport implements TerminalCompanyDao {
	@Override
	public TerminalCompany get(Long ID) {
		return getHibernateTemplate().get(TerminalCompany.class, ID);
	}

	@Override
	public void save(TerminalCompany terminalCompany) {
		getHibernateTemplate().save(terminalCompany);
	}
	
	@Override
	public void del(TerminalCompany terminalCompany) {
		getHibernateTemplate().delete(terminalCompany);
	}

	@Override
	public TerminalCompany merge(TerminalCompany terminalCompany) {
		return (TerminalCompany)getHibernateTemplate().merge(terminalCompany);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<TerminalCompany> list(final TerminalCompany terminalCompany,final PageBean pageBean){
		return (List<TerminalCompany>)getHibernateTemplate().execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from TerminalCompany t where 1 = 1");
						if(terminalCompany != null && terminalCompany.getName() != null && !terminalCompany.getName().equals("")){
							hql.append(" and t.name = :name");
						}
						hql.append(" order by t.createTime desc");
						Query query = session.createQuery(hql.toString());
						if(terminalCompany != null && terminalCompany.getName() != null && !terminalCompany.getName().equals("")){
							query.setParameter("name", terminalCompany.getName().trim());
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
	public PageBean getPageBean(final TerminalCompany terminalCompany, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(t) from TerminalCompany t where 1 = 1");
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
