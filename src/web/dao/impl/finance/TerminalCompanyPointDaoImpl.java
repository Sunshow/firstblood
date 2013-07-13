package web.dao.impl.finance;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.finance.TerminalCompanyPointDao;
import com.lehecai.admin.web.domain.finance.TerminalCompanyPoint;
import com.lehecai.core.lottery.LotteryType;

public class TerminalCompanyPointDaoImpl extends HibernateDaoSupport implements TerminalCompanyPointDao {
	@Override
	public TerminalCompanyPoint get(Long ID) {
		return getHibernateTemplate().get(TerminalCompanyPoint.class, ID);
	}

	@Override
	public void save(TerminalCompanyPoint terminalCompanyPoint) {
		getHibernateTemplate().save(terminalCompanyPoint);
	}
	
	@Override
	public void del(TerminalCompanyPoint terminalCompanyPoint) {
		getHibernateTemplate().delete(terminalCompanyPoint);
	}

	@Override
	public TerminalCompanyPoint merge(TerminalCompanyPoint terminalCompanyPoint) {
		return (TerminalCompanyPoint)getHibernateTemplate().merge(terminalCompanyPoint);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<TerminalCompanyPoint> list(final TerminalCompanyPoint terminalCompanyPoint,final PageBean pageBean){
		return (List<TerminalCompanyPoint>)getHibernateTemplate().execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from TerminalCompanyPoint t where 1 = 1");
						if(terminalCompanyPoint != null && terminalCompanyPoint.getCompanyId() != null){
							hql.append(" and t.companyId = :companyId");
						}
						if(terminalCompanyPoint != null && terminalCompanyPoint.getLotteryType() != null && terminalCompanyPoint.getLotteryType().getValue() != LotteryType.ALL.getValue()){
							hql.append(" and t.lotteryType = :lotteryType");
						}
						
						hql.append(" order by t.createTime desc");
						Query query = session.createQuery(hql.toString());
						if(terminalCompanyPoint != null && terminalCompanyPoint.getCompanyId() != null){
							query.setParameter("companyId", terminalCompanyPoint.getCompanyId());
						}
						if(terminalCompanyPoint != null && terminalCompanyPoint.getLotteryType() != null && terminalCompanyPoint.getLotteryType().getValue() != LotteryType.ALL.getValue()){
							query.setParameter("lotteryType", terminalCompanyPoint.getLotteryType());
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
	public PageBean getPageBean(final TerminalCompanyPoint terminalCompanyPoint, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(t) from TerminalCompanyPoint t where 1 = 1");
						if(terminalCompanyPoint != null && terminalCompanyPoint.getCompanyId() != null){
							hql.append(" and t.companyId = :companyId");
						}
						if(terminalCompanyPoint != null && terminalCompanyPoint.getLotteryType() != null && terminalCompanyPoint.getLotteryType().getValue() != LotteryType.ALL.getValue()){
							hql.append(" and t.lotteryType = :lotteryType");
						}
						hql.append(" order by t.createTime desc");
						Query query = session.createQuery(hql.toString());
						if(terminalCompanyPoint != null && terminalCompanyPoint.getCompanyId() != null){
							query.setParameter("companyId", terminalCompanyPoint.getCompanyId());
						}
						if(terminalCompanyPoint != null && terminalCompanyPoint.getLotteryType() != null && terminalCompanyPoint.getLotteryType().getValue() != LotteryType.ALL.getValue()){
							query.setParameter("lotteryType", terminalCompanyPoint.getLotteryType());
						}
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
