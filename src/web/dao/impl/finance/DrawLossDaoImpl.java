package web.dao.impl.finance;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.finance.DrawLossDao;
import com.lehecai.admin.web.domain.finance.DrawLoss;
import com.lehecai.core.lottery.LotteryType;

public class DrawLossDaoImpl extends HibernateDaoSupport implements DrawLossDao {

	@Override
	public void del(DrawLoss item) {
		getHibernateTemplate().delete(item);
	}

	@Override
	public DrawLoss get(Long ID) {
		return getHibernateTemplate().get(DrawLoss.class, ID);
	}

	@Override
	public DrawLoss merge(DrawLoss item) {
		return (DrawLoss)getHibernateTemplate().merge(item);
	}

	@Override
	public void save(DrawLoss item) {
		getHibernateTemplate().save(item);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<DrawLoss> list(final DrawLoss drawLoss,final Date beginDate,final Date endDate,final PageBean pageBean){
		return (List<DrawLoss>)getHibernateTemplate().execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from DrawLoss t where 1 = 1");
						LotteryType lotteryType = drawLoss.getLotteryType();
						if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
							hql.append(" and t.lotteryType = :lotteryType");
						}
						if(beginDate != null){
							hql.append(" and t.createTime >= :beginDate");
						}
						if(endDate != null){
							hql.append(" and t.createTime < :endDate");
						}
						hql.append(" order by t.id desc");
						Query query = session.createQuery(hql.toString());

						if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
							query.setParameter("lotteryType", lotteryType);
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
	public PageBean getPageBean(final DrawLoss drawLoss,final Date beginDate,final Date endDate, final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(t) from DrawLoss t where 1 = 1");

						LotteryType lotteryType = drawLoss.getLotteryType();
						if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
							hql.append(" and t.lotteryType = :lotteryType");
						}
						if(beginDate != null){
							hql.append(" and t.createTime >= :beginDate");
						}
						if(endDate != null){
							hql.append(" and t.createTime < :endDate");
						}
						hql.append(" order by t.id desc");
						Query query = session.createQuery(hql.toString());

						if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
							query.setParameter("lotteryType", lotteryType);
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
	public DrawLoss getTotal(final DrawLoss drawLoss,final Date beginDate,final Date endDate, final PageBean pageBean) {
		return (DrawLoss) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select sum(drawMoney),sum(drawPlanMoney),sum(drawLossMoney) from DrawLoss t where 1 = 1");

						LotteryType lotteryType = drawLoss.getLotteryType();
						if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
							hql.append(" and t.lotteryType = :lotteryType");
						}
						if(beginDate != null){
							hql.append(" and t.createTime >= :beginDate");
						}
						if(endDate != null){
							hql.append(" and t.createTime < :endDate");
						}
						hql.append(" order by t.id desc");
						
						Query query = session.createQuery(hql.toString());

						if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
							query.setParameter("lotteryType", lotteryType);
						}
						if(beginDate != null){
							query.setParameter("beginDate", beginDate);
						}
						if(endDate != null){
							query.setParameter("endDate", endDate);
						}
						
						List list = query.list();
						DrawLoss item = new DrawLoss();
						if(list != null && list.size() > 0){
							Object obj[] = (Object[])list.get(0);
							Double drawMoney = (Double)obj[0];
							if(drawMoney == null) drawMoney = 0.0D;
							item.setDrawMoney(drawMoney);
							Double drawPlanMoney = (Double)obj[1];
							if(drawPlanMoney == null) drawPlanMoney = 0.0D;
							item.setDrawPlanMoney(drawPlanMoney);
							Double drawLossMoney = (Double)obj[2];
							if(drawLossMoney == null) drawLossMoney = 0.0D;
							item.setDrawLossMoney(drawLossMoney);
						}
						return item;
					}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean getByLotteryTypeAmountCheckDate(final LotteryType lotteryType,
			final String amountCheckDate) {
		return (Boolean)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				StringBuffer hql = new StringBuffer("from DrawLoss t where 1 = 1 ");
				if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
					hql.append(" and t.lotteryType = :lotteryType");
				}
				if(amountCheckDate != null && !"".equals(amountCheckDate)){
					hql.append(" and t.amountCheckDate = :amountCheckDate");
				}
				Query query = session.createQuery(hql.toString());
				if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
					query.setParameter("lotteryType", lotteryType);
				}
				if(amountCheckDate != null && !"".equals(amountCheckDate)){
					query.setParameter("amountCheckDate", amountCheckDate);
				}
				List list = query.list();
				if(list != null && list.size() > 0){
					return true;
				}
				return false;
			}
			
		});
	}
}