package web.dao.impl.cms;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.cms.NewsDao;
import com.lehecai.admin.web.domain.cms.News;
import com.lehecai.admin.web.domain.cms.NewsHistory;

public class NewsDaoImpl extends HibernateDaoSupport implements NewsDao {

	@Override
	public News merge(News news) {
		// TODO Auto-generated method stub
		return (News)getHibernateTemplate().merge(news);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<News> list(final News news, final PageBean pageBean) {
		return (List<News>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from News u where 1 = 1");
						if(news != null && news.getCateID() != null){
							hql.append(" and u.cateID = :cateID");
						}
						if(news != null && news.isValid()){
							hql.append(" and u.valid = 1");
						}
						if(news != null && news.getAuthor() != null && !news.getAuthor().equals("")){
							hql.append(" and u.author = :author");
						}
						if(news != null && news.getTitle() != null && !news.getTitle().equals("")){
							hql.append(" and u.title like '%").append(news.getTitle()).append("%'");
						}
						if(news != null && news.getId() != null){
							hql.append(" and u.id = :id");
						}
						if(news != null && news.getUserId() != null){
							hql.append(" and u.userId = :userId");
						}
						hql.append(" order by u.orderView desc,u.createTime desc");
						Query query = session.createQuery(hql.toString());
								
						if(news != null && news.getCateID() != null){
							query.setParameter("cateID", news.getCateID());
						}
						if(news != null && news.getAuthor() != null && !news.getAuthor().equals("")){
							query.setParameter("author", news.getAuthor());
						}
						if(news != null && news.getId() != null){
							query.setParameter("id", news.getId());
						}
						if(news != null && news.getUserId() != null){
							query.setParameter("userId", news.getUserId());
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
	public List<News> listByCateId(final List<Long> cateIds, final PageBean pageBean) {
		return (List<News>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
					throws HibernateException {
						StringBuffer hql = new StringBuffer("from News u where 1 = 1");
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
	public List<News> listByCateIdOrderView(final List<Long> cateIds, final PageBean pageBean) {
		return (List<News>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
					throws HibernateException {
						StringBuffer hql = new StringBuffer("from News u where 1 = 1");
						if(cateIds != null && cateIds.size() > 0){
								hql.append(" and u.cateID in (:cateID)");
						}
						hql.append(" and u.valid = 1");
						hql.append(" order by u.orderView desc, u.createTime desc");
						Query query = session.createQuery(hql.toString());
						
						if(cateIds != null && cateIds.size() > 0){
							query.setParameterList("cateID", cateIds);
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
	@Override
	public News get(Long ID) {
		return getHibernateTemplate().get(News.class, ID);
	}

	@Override
	public void del(News news) {
		getHibernateTemplate().delete(news);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final News news, final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from News u where 1 = 1");
						
						if(news != null && news.getCateID() != null){
							hql.append(" and u.cateID = :cateID");
						}
						if(news != null && news.isValid()){
							hql.append(" and u.valid = 1");
						}
						if(news != null && news.getAuthor() != null && !news.getAuthor().equals("")){
							hql.append(" and u.author = :author");
						}
						if(news != null && news.getTitle() != null && !news.getTitle().equals("")){
							hql.append(" and u.title like '%").append(news.getTitle()).append("%'");
						}
						if(news != null && news.getId() != null){
							hql.append(" and u.id = :id");
						}
						if(news != null && news.getUserId() != null){
							hql.append(" and u.userId = :userId");
						}
						hql.append(" order by u.orderView desc,u.createTime desc");
						Query query = session.createQuery(hql.toString());
								
						if(news != null && news.getCateID() != null){
							query.setParameter("cateID", news.getCateID());
						}
						if(news != null && news.getAuthor() != null && !news.getAuthor().equals("")){
							query.setParameter("author", news.getAuthor());
						}
						if(news != null && news.getId() != null){
							query.setParameter("id", news.getId());
						}
						if(news != null && news.getUserId() != null){
							query.setParameter("userId", news.getUserId());
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
						StringBuffer hql = new StringBuffer("select count(u) from News u where 1 = 1");
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
	public List<News> listByCondition(final News news, final List<Long> cateIds,final Date beginTime,final Date endTime,final String orderStr,final String orderView,
			final PageBean pageBean) {
		return (List<News>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from News u where 1 = 1");
						if(cateIds != null && cateIds.size() > 0){
							hql.append(" and u.cateID in (:cateID)");
						}
						if(news != null && news.isValid()){
							hql.append(" and u.valid = 1");
						}
						if(news != null && news.getAuthor() != null && !news.getAuthor().equals("")){
							hql.append(" and u.author = :author");
						}
						if(news != null && news.getTitle() != null && !news.getTitle().equals("")){
							hql.append(" and u.title like '%").append(news.getTitle()).append("%'");
						}
						if(news != null && news.getId() != null){
							hql.append(" and u.id = :id");
						}
						if(news != null && news.getUserId() != null){
							hql.append(" and u.userId = :userId");
						}
						if(beginTime != null){
							hql.append(" and u.createTime >= :beginTime");
						}
						if(endTime != null){
							hql.append(" and u.createTime < :endTime");
						}
						hql.append(" order by u.").append(orderStr).append(" ").append(orderView);
						Query query = session.createQuery(hql.toString());
								
						if(cateIds != null && cateIds.size() > 0){
							query.setParameterList("cateID", cateIds);
						}
						if(news != null && news.getAuthor() != null && !news.getAuthor().equals("")){
							query.setParameter("author", news.getAuthor());
						}
						if(news != null && news.getId() != null){
							query.setParameter("id", news.getId());
						}
						if(news != null && news.getUserId() != null){
							query.setParameter("userId", news.getUserId());
						}
						if(beginTime != null){
							query.setParameter("beginTime", beginTime);
						}
						if(endTime != null){
							query.setParameter("endTime", endTime);
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
	public PageBean getPageBeanByCondition(final News news, final List<Long> cateIds,final Date beginTime,final Date endTime,final String orderStr,final String orderView,
			final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from News u where 1 = 1");
						
						if(cateIds != null && cateIds.size() > 0){
							hql.append(" and u.cateID in (:cateID)");
						}
						if(news != null && news.isValid()){
							hql.append(" and u.valid = 1");
						}
						if(news != null && news.getAuthor() != null && !news.getAuthor().equals("")){
							hql.append(" and u.author = :author");
						}
						if(news != null && news.getTitle() != null && !news.getTitle().equals("")){
							hql.append(" and u.title like '%").append(news.getTitle()).append("%'");
						}
						if(news != null && news.getId() != null){
							hql.append(" and u.id = :id");
						}
						if(news != null && news.getUserId() != null){
							hql.append(" and u.userId = :userId");
						}
						if(beginTime != null){
							hql.append(" and u.createTime >= :beginTime");
						}
						if(endTime != null){
							hql.append(" and u.createTime < :endTime");
						}
						hql.append(" order by u.").append(orderStr).append(" ").append(orderView);
						Query query = session.createQuery(hql.toString());
								
						if(cateIds != null && cateIds.size() > 0){
							query.setParameterList("cateID", cateIds);
						}
						if(news != null && news.getAuthor() != null && !news.getAuthor().equals("")){
							query.setParameter("author", news.getAuthor());
						}
						if(news != null && news.getId() != null){
							query.setParameter("id", news.getId());
						}
						if(news != null && news.getUserId() != null){
							query.setParameter("userId", news.getUserId());
						}
						if(beginTime != null){
							query.setParameter("beginTime", beginTime);
						}
						if(endTime != null){
							query.setParameter("endTime", endTime);
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
	public Long getClickByCondition(final News news, final List<Long> cateIds,final Date beginTime,final Date endTime,final String orderStr,final String orderView) {
		return (Long) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select sum(u.click) from News u where 1 = 1");
						
						if(cateIds != null && cateIds.size() > 0){
							hql.append(" and u.cateID in (:cateID)");
						}
						if(news != null && news.isValid()){
							hql.append(" and u.valid = 1");
						}
						if(news != null && news.getAuthor() != null && !news.getAuthor().equals("")){
							hql.append(" and u.author = :author");
						}
						if(news != null && news.getTitle() != null && !news.getTitle().equals("")){
							hql.append(" and u.title like '%").append(news.getTitle()).append("%'");
						}
						if(news != null && news.getId() != null){
							hql.append(" and u.id = :id");
						}
						if(news != null && news.getUserId() != null){
							hql.append(" and u.userId = :userId");
						}
						if(beginTime != null){
							hql.append(" and u.createTime >= :beginTime");
						}
						if(endTime != null){
							hql.append(" and u.createTime < :endTime");
						}
						hql.append(" order by u.").append(orderStr).append(" ").append(orderView);
						Query query = session.createQuery(hql.toString());
								
						if(cateIds != null && cateIds.size() > 0){
							query.setParameterList("cateID", cateIds);
						}
						if(news != null && news.getAuthor() != null && !news.getAuthor().equals("")){
							query.setParameter("author", news.getAuthor());
						}
						if(news != null && news.getId() != null){
							query.setParameter("id", news.getId());
						}
						if(news != null && news.getUserId() != null){
							query.setParameter("userId", news.getUserId());
						}
						if(beginTime != null){
							query.setParameter("beginTime", beginTime);
						}
						if(endTime != null){
							query.setParameter("endTime", endTime);
						}
						
						Long totalClick = (Long)query.iterate().next();
						return totalClick;
					}
				});
	}

	@Override
	public NewsHistory mergeHistory(NewsHistory newsHistory) {
		return (NewsHistory)getHibernateTemplate().merge(newsHistory);
	}
}
