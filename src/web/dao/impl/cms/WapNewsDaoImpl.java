package web.dao.impl.cms;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.cms.WapNewsDao;
import com.lehecai.admin.web.domain.cms.WapNews;
import com.lehecai.admin.web.enums.ContentType;
import com.lehecai.admin.web.enums.HeadNewsType;

public class WapNewsDaoImpl extends HibernateDaoSupport implements WapNewsDao {

	@Override
	public WapNews merge(WapNews wapNews) {
		return (WapNews)getHibernateTemplate().merge(wapNews);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WapNews> list(final WapNews wapNews, final PageBean pageBean) {
		return (List<WapNews>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from WapNews u where 1 = 1");
						if(wapNews != null && wapNews.getCateID() != null){
							hql.append(" and u.cateID = :cateID");
						}
						if(wapNews != null && wapNews.isValid()){
							hql.append(" and u.valid = 1");
						}
						hql.append(" order by u.orderView desc,u.createTime desc");
						Query query = session.createQuery(hql.toString());
								
						if(wapNews != null && wapNews.getCateID() != null){
							query.setParameter("cateID", wapNews.getCateID());
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
	public List<WapNews> listByCateId(final List<Long> cateIds, final Integer headNews, final ContentType contentType, final Integer valid, final PageBean pageBean) {
		return (List<WapNews>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
					throws HibernateException {
						StringBuffer hql = new StringBuffer("from WapNews u where 1 = 1");
						if (cateIds != null && cateIds.size() > 0) {
							hql.append(" and u.cateID in (:cateID)");
						}
						if (headNews != null && headNews != HeadNewsType.ALL.getValue()) {
							hql.append(" and u.headNews = :headNews");
						}
						if (contentType != null) {
							hql.append(" and u.contentType = :contentType");
						}
						if (valid != null) {
							hql.append(" and u.valid = :valid");
						}
						hql.append(" order by u.orderView desc,u.updateTime desc");
						Query query = session.createQuery(hql.toString());
						
						if(cateIds != null && cateIds.size() > 0){
							query.setParameterList("cateID", cateIds);
						}
						if (headNews != null && headNews != HeadNewsType.ALL.getValue()) {
							if (headNews == HeadNewsType.HEADNEWS.getValue()) {
								query.setParameter("headNews", true);
							} else {
								query.setParameter("headNews", false);
							}
						}
						if (contentType != null) {
							query.setParameter("contentType", contentType);
						}
						if (valid != null) {
							if (valid == 1) {
								query.setParameter("valid", true);
							} else if (valid == 0) {
								query.setParameter("valid", false);
							} else {
								query.setParameter("valid", true);
							}
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
	public List<WapNews> listByHome(final boolean home, final ContentType contentType, final PageBean pageBean) {
		return (List<WapNews>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
					throws HibernateException {
						StringBuffer hql = new StringBuffer("from WapNews u where 1 = 1 ");
						hql.append(" and u.homePage = :homePage");
						if (contentType != null) {
							hql.append(" and u.contentType = :contentType");
						}
						hql.append(" and u.valid = :valid");
						
						hql.append(" order by u.orderView desc,u.updateTime desc");
						Query query = session.createQuery(hql.toString());
						query.setParameter("homePage", home);
							
						if (contentType != null) {
							query.setParameter("contentType", contentType);
						}
						query.setParameter("valid", true);
							
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

	@Override
	public WapNews get(Long ID) {
		return getHibernateTemplate().get(WapNews.class, ID);
	}

	@Override
	public void del(WapNews wapNews) {
		getHibernateTemplate().delete(wapNews);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBeanByHome(final ContentType contentType, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
					throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from WapNews u where 1 = 1");
						
						hql.append(" and u.homePage = :homePage");
						if (contentType != null) {
							hql.append(" and u.contentType = :contentType");
						}
						hql.append(" and u.valid = :valid");
						
						hql.append(" order by u.orderView desc,u.updateTime desc");
						
						Query query = session.createQuery(hql.toString());
						
						query.setParameter("homePage", true);
							
						if (contentType != null) {
							query.setParameter("contentType", contentType);
						}
						query.setParameter("valid", true);
						
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
	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final WapNews wapNews, final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from WapNews u where 1 = 1");
						
						if(wapNews != null && wapNews.getCateID() != null){
							hql.append(" and u.cateID = :cateID");
						}
						Query query = session.createQuery(hql.toString());
								
						if(wapNews != null && wapNews.getCateID() != null){
							query.setParameter("cateID", wapNews.getCateID());
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

	@SuppressWarnings("unchecked")
	@Override
	public PageBean countByCateId(final List<Long> cateIds, final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
					throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from WapNews u where 1 = 1");
						if(cateIds != null && cateIds.size() > 0){
								hql.append(" and u.cateID in (:cateID)");
						}
						hql.append(" and u.valid = 1");
						hql.append(" order by u.createTime desc");
						Query query = session.createQuery(hql.toString());
						
						if(cateIds != null && cateIds.size() > 0){
							query.setParameterList("cateID", cateIds);
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
	public WapNews getPrev(final Long cateId, final Date updateTime, final Integer orderView) {
		return (WapNews) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
					throws HibernateException {
						StringBuffer hql = new StringBuffer("from WapNews u where 1 = 1 and u.valid = 1");
						if (cateId != null && cateId != 0) {
							hql.append(" and u.cateID = :cateID ");
						}
						if (orderView != null && updateTime != null) {
								hql.append(" and ( (u.orderView = :orderView and u.updateTime > :updateTime) or (u.orderView > :orderView1) ) order by u.orderView asc,u.updateTime asc");
						}
						Query query = session.createQuery(hql.toString());
						
						if (cateId != null && cateId != 0) {
							query.setParameter("cateID", cateId);
						}
						if (orderView != null && updateTime != null) {
							query.setParameter("orderView", orderView);
							query.setParameter("orderView1", orderView);
							query.setParameter("updateTime", updateTime);
						}
						
						query.setMaxResults(1);
						List<WapNews> list = query.list();
						WapNews rtn = null;
						if (list != null && list.size() >0) {
							rtn = list.get(0);
						}
						return rtn;
					}
				});
	}

	@SuppressWarnings("unchecked")
	@Override
	public WapNews getNext(final Long cateId, final Date updateTime, final Integer orderView) {
		return (WapNews) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
					throws HibernateException {
						StringBuffer hql = new StringBuffer("from WapNews u where 1 = 1 and u.valid = 1");
						if (cateId != null && cateId != 0) {
							hql.append(" and u.cateID = :cateID ");
						}
						if (orderView != null && updateTime != null) {
								hql.append(" and ( (u.orderView = :orderView and u.updateTime < :updateTime) or (u.orderView < :orderView1) ) order by u.orderView desc,u.updateTime desc");
						}
						Query query = session.createQuery(hql.toString());
						
						if (cateId != null && cateId != 0) {
							query.setParameter("cateID", cateId);
						}
						if (orderView != null && updateTime != null) {
							query.setParameter("orderView", orderView);
							query.setParameter("orderView1", orderView);
							query.setParameter("updateTime", updateTime);
						}
						
						query.setMaxResults(1);
						List<WapNews> list = query.list();
						WapNews rtn = null;
						if (list != null && list.size() >0) {
							rtn = list.get(0);
						}
						return rtn;
					}
				});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WapNews> listByCondition(final WapNews wapNews, final List<Long> cateIds,
			final Date beginTime, final Date endTime, final String orderStr, final String orderView,
			final PageBean pageBean) {
		return (List<WapNews>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from WapNews u where 1 = 1");
						if(cateIds != null && cateIds.size() > 0){
							hql.append(" and u.cateID in (:cateID)");
						}
						if(wapNews != null && wapNews.isValid()){
							hql.append(" and u.valid = 1");
						}
						if(wapNews != null && wapNews.getAuthor() != null && !wapNews.getAuthor().equals("")){
							hql.append(" and u.author = :author");
						}
						if(wapNews != null && wapNews.getTitle() != null && !wapNews.getTitle().equals("")){
							hql.append(" and u.title like '%").append(wapNews.getTitle()).append("%'");
						}
						if(wapNews != null && wapNews.getId() != null){
							hql.append(" and u.id = :id");
						}
						if(beginTime != null){
							hql.append(" and u.createTime >= :beginTime");
						}
						if(endTime != null){
							hql.append(" and u.createTime < :endTime");
						}
						if(wapNews.getUserId() != null){
							hql.append(" and u.userId = :userId");
						}
						hql.append(" order by u.").append(orderStr).append(" ").append(orderView);
						Query query = session.createQuery(hql.toString());
								
						if(cateIds != null && cateIds.size() > 0){
							query.setParameterList("cateID", cateIds);
						}
						if(wapNews != null && wapNews.getAuthor() != null && !wapNews.getAuthor().equals("")){
							query.setParameter("author", wapNews.getAuthor());
						}
						if(wapNews != null && wapNews.getId() != null){
							query.setParameter("id", wapNews.getId());
						}
						if(beginTime != null){
							query.setParameter("beginTime", beginTime);
						}
						if(endTime != null){
							query.setParameter("endTime", endTime);
						}
						if(wapNews.getUserId() != null){
							query.setParameter("userId", wapNews.getUserId());
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
	public PageBean getPageBeanByCondition(final WapNews wapNews, final List<Long> cateIds,
			final Date beginTime, final Date endTime, final String orderStr, final String orderView,
			final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from WapNews u where 1 = 1");
						
						if(cateIds != null && cateIds.size() > 0){
							hql.append(" and u.cateID in (:cateID)");
						}
						if(wapNews != null && wapNews.isValid()){
							hql.append(" and u.valid = 1");
						}
						if(wapNews != null && wapNews.getAuthor() != null && !wapNews.getAuthor().equals("")){
							hql.append(" and u.author = :author");
						}
						if(wapNews != null && wapNews.getTitle() != null && !wapNews.getTitle().equals("")){
							hql.append(" and u.title like '%").append(wapNews.getTitle()).append("%'");
						}
						if(wapNews != null && wapNews.getId() != null){
							hql.append(" and u.id = :id");
						}
						
						if(beginTime != null){
							hql.append(" and u.createTime >= :beginTime");
						}
						if(endTime != null){
							hql.append(" and u.createTime < :endTime");
						}
						if(wapNews.getUserId() != null){
							hql.append(" and u.userId = :userId");
						}
						hql.append(" order by u.").append(orderStr).append(" ").append(orderView);
						Query query = session.createQuery(hql.toString());
								
						if(cateIds != null && cateIds.size() > 0){
							query.setParameterList("cateID", cateIds);
						}
						if(wapNews != null && wapNews.getAuthor() != null && !wapNews.getAuthor().equals("")){
							query.setParameter("author", wapNews.getAuthor());
						}
						if(wapNews != null && wapNews.getId() != null){
							query.setParameter("id", wapNews.getId());
						}
					
						if(beginTime != null){
							query.setParameter("beginTime", beginTime);
						}
						if(endTime != null){
							query.setParameter("endTime", endTime);
						}
						if(wapNews.getUserId() != null){
							query.setParameter("userId", wapNews.getUserId());
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
}
