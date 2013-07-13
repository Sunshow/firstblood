package web.dao.impl.finance;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.finance.TerminalAccountCheckItemDao;
import com.lehecai.admin.web.domain.finance.TerminalAccountCheckItem;

public class TerminalAccountCheckItemDaoImpl extends HibernateDaoSupport implements TerminalAccountCheckItemDao {

	@Override
	public void del(TerminalAccountCheckItem item) {
		getHibernateTemplate().delete(item);
	}

	@Override
	public TerminalAccountCheckItem get(Long ID) {
		return getHibernateTemplate().get(TerminalAccountCheckItem.class, ID);
	}

	@Override
	public TerminalAccountCheckItem merge(TerminalAccountCheckItem item) {
		return (TerminalAccountCheckItem)getHibernateTemplate().merge(item);
	}

	@Override
	public void save(TerminalAccountCheckItem item) {
		getHibernateTemplate().save(item);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<TerminalAccountCheckItem> list(final TerminalAccountCheckItem terminalAccountCheckItem,final Date beginDate,final Date endDate,final PageBean pageBean){
		return (List<TerminalAccountCheckItem>)getHibernateTemplate().execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from TerminalAccountCheckItem t where 1 = 1");
						Long terminalCompanyId = terminalAccountCheckItem.getTerminalCompanyId();
						if(terminalCompanyId != null && terminalCompanyId.longValue() != 0){
							hql.append(" and t.terminalCompanyId = :terminalCompanyId");
						}
						if(beginDate != null){
							hql.append(" and t.createTime >= :beginDate");
						}
						if(endDate != null){
							hql.append(" and t.createTime < :endDate");
						}
						hql.append(" order by t.id desc");
						Query query = session.createQuery(hql.toString());

						if(terminalCompanyId != null && terminalCompanyId.longValue() != 0){
							query.setParameter("terminalCompanyId", terminalCompanyId);
						}
						if(beginDate != null){
							query.setParameter("beginDate", beginDate);
						}
						if(endDate != null){
							query.setParameter("endDate", endDate);
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
	public PageBean getPageBean(final TerminalAccountCheckItem terminalAccountCheckItem,final Date beginDate,final Date endDate, final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(t) from TerminalAccountCheckItem t where 1 = 1");

						Long terminalCompanyId = terminalAccountCheckItem.getTerminalCompanyId();
						if(terminalCompanyId != null && terminalCompanyId.longValue() != 0){
							hql.append(" and t.terminalCompanyId = :terminalCompanyId");
						}
						if(beginDate != null){
							hql.append(" and t.createTime >= :beginDate");
						}
						if(endDate != null){
							hql.append(" and t.createTime < :endDate");
						}
						hql.append(" order by t.id desc");
						Query query = session.createQuery(hql.toString());

						if(terminalCompanyId != null && terminalCompanyId.longValue() != 0){
							query.setParameter("terminalCompanyId", terminalCompanyId);
						}
						if(beginDate != null){
							query.setParameter("beginDate", beginDate);
						}
						if(endDate != null){
							query.setParameter("endDate", endDate);
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
	
	@SuppressWarnings("unchecked")
	@Override
	public TerminalAccountCheckItem getTotal(final TerminalAccountCheckItem terminalAccountCheckItem,final Date beginDate,final Date endDate, final PageBean pageBean) {
		return (TerminalAccountCheckItem) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select sum(terminalDrawMoney) as terminalDrawMoney,sum(terminalPrizeMoney) as terminalPrizeMoney,sum(lehecaiDrawMoney) as lehecaiDrawMoney,sum(lehecaiPrizeMoney),sum(drawMoneyDiff) as drawMoneyDiff,sum(prizeMoneyDiff) as prizeMoneyDiff,sum(commission) as commission from TerminalAccountCheckItem t where 1 = 1");

						Long terminalCompanyId = terminalAccountCheckItem.getTerminalCompanyId();
						if(terminalCompanyId != null && terminalCompanyId.longValue() != 0){
							hql.append(" and t.terminalCompanyId = :terminalCompanyId");
						}
						if(beginDate != null){
							hql.append(" and t.createTime >= :beginDate");
						}
						if(endDate != null){
							hql.append(" and t.createTime < :endDate");
						}
						hql.append(" order by t.id desc");
						
						Query query = session.createQuery(hql.toString());

						if(terminalCompanyId != null && terminalCompanyId.longValue() != 0){
							query.setParameter("terminalCompanyId", terminalCompanyId);
						}
						if(beginDate != null){
							query.setParameter("beginDate", beginDate);
						}
						if(endDate != null){
							query.setParameter("endDate", endDate);
						}
						
						List list = query.list();
						TerminalAccountCheckItem item = new TerminalAccountCheckItem();
						if(list != null && list.size() > 0){
							Object obj[] = (Object[])list.get(0);
							Double terminalDrawMoney = (Double)obj[0];
							if(terminalDrawMoney == null) terminalDrawMoney = 0.0D;
							item.setTerminalDrawMoney(terminalDrawMoney);
							Double terminalPrizeMoney = (Double)obj[1];
							if(terminalPrizeMoney == null) terminalPrizeMoney = 0.0D;
							item.setTerminalPrizeMoney(terminalPrizeMoney);
							Double lehecaiDrawMoney = (Double)obj[2];
							if(lehecaiDrawMoney == null) lehecaiDrawMoney = 0.0D;
							item.setLehecaiDrawMoney(lehecaiDrawMoney);
							Double lehecaiPrizeMoney = (Double)obj[3];
							if(lehecaiPrizeMoney == null) lehecaiPrizeMoney = 0.0D;
							item.setLehecaiPrizeMoney(lehecaiPrizeMoney);
							Double drawMoneyDiff = (Double)obj[4];
							if(drawMoneyDiff == null) drawMoneyDiff = 0.0D;
							item.setDrawMoneyDiff(drawMoneyDiff);
							Double prizeMoneyDiff = (Double)obj[5];
							if(prizeMoneyDiff == null) prizeMoneyDiff = 0.0D;
							item.setPrizeMoneyDiff(prizeMoneyDiff);
							Double commission = (Double)obj[6];
							if(commission == null) commission = 0.0D;
							item.setCommission(commission);
						}
						return item;
					}
		});
	}
}