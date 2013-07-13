package web.dao.impl.agent;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.agent.AgentDao;
import com.lehecai.admin.web.domain.agent.AgentLink;
import com.lehecai.admin.web.domain.agent.AgentLinkType;

public class AgentDaoImpl extends HibernateDaoSupport implements AgentDao {

	@Override
	public void delLinkType(AgentLinkType agentLinkType) {
		// TODO Auto-generated method stub
		getHibernateTemplate().delete(agentLinkType);
	}

	@Override
	public AgentLinkType getLinkType(Long ID) {
		// TODO Auto-generated method stub
		return getHibernateTemplate().get(AgentLinkType.class, ID);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AgentLinkType> listLinkType(AgentLinkType agentLinkType) {
		// TODO Auto-generated method stub
		return (List<AgentLinkType>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from AgentLinkType u where 1 = 1");
						Query query = session.createQuery(hql.toString());
						return query.list();
					}
				});
	}

	@Override
	public void mergeLinkType(AgentLinkType agentLinkType) {
		// TODO Auto-generated method stub
		getHibernateTemplate().merge(agentLinkType);
	}
	@Override
	public void delLink(AgentLink agentLink) {
		// TODO Auto-generated method stub
		getHibernateTemplate().delete(agentLink);
	}

	@Override
	public AgentLink getLink(Long ID) {
		// TODO Auto-generated method stub
		return getHibernateTemplate().get(AgentLink.class, ID);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AgentLink> listLink(final Long linkTypeId, final Date fromCreateDate, final Date toCreateDate, final Date fromUpdateDate, final Date toUpdateDate, final String url, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (List<AgentLink>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from AgentLink u where 1 = 1");
						if(linkTypeId != null){
							hql.append(" and u.linkTypeId = :linkTypeId");
						}
						if(fromCreateDate != null){
							hql.append(" and u.createTime >= :fromCreateDate");
						}
						if(toCreateDate != null){
							hql.append(" and u.createTime < :toCreateDate");
						}
						if(fromUpdateDate != null){
							hql.append(" and u.updateTime >= :fromUpdateDate");
						}
						if(toUpdateDate != null){
							hql.append(" and u.updateTime < :toUpdateDate");
						}
						if(url != null && !"".equals(url)){
							hql.append(" and u.url like :url");
						}
						Query query = session.createQuery(hql.toString());
						if(linkTypeId != null){
							query.setParameter("linkTypeId", linkTypeId);
						}
						if(fromCreateDate != null){
							query.setParameter("fromCreateDate", fromCreateDate);
						}
						if(toCreateDate != null){
							query.setParameter("toCreateDate", toCreateDate);
						}
						if(fromUpdateDate != null){
							query.setParameter("fromUpdateDate", fromUpdateDate);
						}
						if(toUpdateDate != null){
							query.setParameter("toUpdateDate", toUpdateDate);
						}
						if(url != null && !"".equals(url)){
							query.setParameter("url", "%"+url+"%");
						}
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

	@SuppressWarnings("unchecked")
	@Override
	public PageBean getLinkPageBean(final Long linkTypeId, final Date fromCreateDate, final Date toCreateDate, final Date fromUpdateDate, final Date toUpdateDate, final String url, final  PageBean pageBean) {
		// TODO Auto-generated method stub
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from AgentLink u where 1 = 1");

						if(linkTypeId != null){
							hql.append(" and u.linkTypeId = :linkTypeId");
						}
						if(fromCreateDate != null){
							hql.append(" and u.createTime >= :fromCreateDate");
						}
						if(toCreateDate != null){
							hql.append(" and u.createTime < :toCreateDate");
						}
						if(fromUpdateDate != null){
							hql.append(" and u.updateTime >= :fromUpdateDate");
						}
						if(toUpdateDate != null){
							hql.append(" and u.updateTime < :toUpdateDate");
						}
						if(url != null && !"".equals(url)){
							hql.append(" and u.url like :url");
						}
						Query query = session.createQuery(hql.toString());
						if(linkTypeId != null){
							query.setParameter("linkTypeId", linkTypeId);
						}
						if(fromCreateDate != null){
							query.setParameter("fromCreateDate", fromCreateDate);
						}
						if(toCreateDate != null){
							query.setParameter("toCreateDate", toCreateDate);
						}
						if(fromUpdateDate != null){
							query.setParameter("fromUpdateDate", fromUpdateDate);
						}
						if(toUpdateDate != null){
							query.setParameter("toUpdateDate", toUpdateDate);
						}
						if(url != null && !"".equals(url)){
							query.setParameter("url", "%"+url+"%");
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

	@Override
	public AgentLink mergeLink(AgentLink agentLink) {
		// TODO Auto-generated method stub
		return getHibernateTemplate().merge(agentLink);
	}
}
