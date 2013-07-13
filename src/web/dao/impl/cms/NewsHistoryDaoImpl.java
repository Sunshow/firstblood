package web.dao.impl.cms;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.cms.NewsHistoryDao;
import com.lehecai.admin.web.domain.cms.NewsHistory;

public class NewsHistoryDaoImpl extends HibernateDaoSupport implements NewsHistoryDao {

	@Override
	public NewsHistory merge(NewsHistory newsHistory) {
		return (NewsHistory)getHibernateTemplate().merge(newsHistory);
	}
	
	@Override
	public void del(NewsHistory newsHistory) {
		getHibernateTemplate().delete(newsHistory);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<NewsHistory> listByCount(final Long newsId, final Integer count) {
		return (List<NewsHistory>) getHibernateTemplate().execute(
				new HibernateCallback<Object>() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from NewsHistory u where 1 = 1");
						if(newsId != null && newsId != 0){
							hql.append(" and u.newsId = :newsId");
						}
						hql.append(" order by u.version desc");
						Query query = session.createQuery(hql.toString());
						if(newsId != null && newsId != 0){
							query.setParameter("newsId", newsId);
						}	
						if (count != null && count != 0) {
							return query.setMaxResults(count).list();
						} else {
							return query.list();
						}
					}
				});
	}

	@Override
	public NewsHistory get(Long id) {
		return (NewsHistory)getHibernateTemplate().get(NewsHistory.class, id);
	}

	@SuppressWarnings({ })
	@Override
	public PageBean getPageBean(final NewsHistory newsHistory,
			final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback<Object>() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from NewsHistory u where 1 = 1");
						if(newsHistory != null && newsHistory.getId() != null && newsHistory.getId() != 0){
							hql.append(" and u.id = :id");
						}
						if(newsHistory != null && newsHistory.getNewsId() != null && newsHistory.getNewsId() != 0){
							hql.append(" and u.newsId = :newsId");
						}
						if(newsHistory != null && newsHistory.getTitle() != null && !newsHistory.getTitle().equals("")){
							hql.append(" and u.title like '%").append(newsHistory.getTitle()).append("%'");
						}
						if(newsHistory != null && newsHistory.getAuthor() != null && !newsHistory.getAuthor().equals("")){
							hql.append(" and u.author = :author");
						}
						hql.append(" order by u.version desc");
						Query query = session.createQuery(hql.toString());
						if(newsHistory != null && newsHistory.getId() != null && newsHistory.getId() != 0){
							query.setParameter("id", newsHistory.getId());
						}
						if(newsHistory != null && newsHistory.getNewsId() != null && newsHistory.getNewsId() != 0){
							query.setParameter("newsId", newsHistory.getNewsId());
						}
						if(newsHistory != null && newsHistory.getAuthor() != null && !newsHistory.getAuthor().equals("")){
							query.setParameter("author", newsHistory.getAuthor());
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
	public List<NewsHistory> list(final NewsHistory newsHistory,
			final PageBean pageBean) {
		return (List<NewsHistory>) getHibernateTemplate().execute(
				new HibernateCallback<Object>() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from NewsHistory u where 1 = 1");
						if(newsHistory != null && newsHistory.getId() != null && newsHistory.getId() != 0){
							hql.append(" and u.id = :id");
						}
						if(newsHistory != null && newsHistory.getNewsId() != null && newsHistory.getNewsId() != 0){
							hql.append(" and u.newsId = :newsId");
						}
						if(newsHistory != null && newsHistory.getTitle() != null && !newsHistory.getTitle().equals("")){
							hql.append(" and u.title like '%").append(newsHistory.getTitle()).append("%'");
						}
						if(newsHistory != null && newsHistory.getAuthor() != null && !newsHistory.getAuthor().equals("")){
							hql.append(" and u.author = :author");
						}
						hql.append(" order by u.version desc");
						Query query = session.createQuery(hql.toString());
						if(newsHistory != null && newsHistory.getId() != null && newsHistory.getId() != 0){
							query.setParameter("id", newsHistory.getId());
						}
						if(newsHistory != null && newsHistory.getNewsId() != null && newsHistory.getNewsId() != 0){
							query.setParameter("newsId", newsHistory.getNewsId());
						}
						if(newsHistory != null && newsHistory.getAuthor() != null && !newsHistory.getAuthor().equals("")){
							query.setParameter("author", newsHistory.getAuthor());
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
}
