package web.dao.impl.cms;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.cms.VoteDao;
import com.lehecai.admin.web.domain.cms.Vote;

public class VoteDaoImpl extends HibernateDaoSupport implements VoteDao {

	@Override
	public void merge(Vote vote) {
		// TODO Auto-generated method stub
		getHibernateTemplate().merge(vote);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Vote> list(final String title, final Integer voteTypeId, final String valid, final Date fromCreateDate, final Date toCreateDate, final Date fromBeginDate, final Date toBeginDate, final Date fromEndDate, final Date toEndDate, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (List<Vote>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from Vote u where 1 = 1");

						hql.append(" order by u.createTime desc,u.id");
						Query query = session.createQuery(hql.toString());
						if(pageBean.isPageFlag()){
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
	public Vote get(Long ID) {
		// TODO Auto-generated method stub
		return getHibernateTemplate().get(Vote.class, ID);
	}

	@Override
	public void del(Vote vote) {
		// TODO Auto-generated method stub
		getHibernateTemplate().delete(vote);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final String title, final Integer voteTypeId, final String valid, final Date fromCreateDate, final Date toCreateDate, final Date fromBeginDate, final Date toBeginDate, final Date fromEndDate, final Date toEndDate, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from Vote u where 1 = 1");
						
						Query query = session.createQuery(hql.toString());

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
