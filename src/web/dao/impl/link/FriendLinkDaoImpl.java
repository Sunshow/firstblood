package web.dao.impl.link;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.link.FriendLinkDao;
import com.lehecai.admin.web.domain.link.FriendLink;

/**
 * 友情链接数据访问层实现类
 * @author yanweijie
 *
 */
public class FriendLinkDaoImpl extends HibernateDaoSupport implements FriendLinkDao {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	/**
	 * 查询所有友情链接
	 */
	@SuppressWarnings("unchecked")
	public List<FriendLink> findFriendLinkList(final FriendLink friendLink, final PageBean pageBean){
		
		return super.getHibernateTemplate().executeFind(new HibernateCallback<List<FriendLink>>() {

			@Override
			public List<FriendLink> doInHibernate(Session session)
					throws HibernateException, SQLException {
				
				Query query = null;
				
				StringBuffer hql = new StringBuffer("from FriendLink where 1=1");
				if (friendLink != null) {
					hql.append("and onIndex = :onIndex");
				}
				hql.append(" order by orderView desc");
				
				query = session.createQuery(hql.toString());
				if (friendLink != null) {
					query.setParameter("onIndex", friendLink.isOnIndex());
				}
				if ( pageBean.isPageFlag() ) {
					query.setMaxResults(pageBean.getPageSize());
				}
				
				return query.list();
				
			}
		});
	}
	
	/**
	 * 根据友情链接编号查询友情链接
	 * @param id 友情链接编号
	 */
	public FriendLink get(Long id){
		
		return super.getHibernateTemplate().get(FriendLink.class, id);
		
	}
	
	/**
	 * 添加/修改友情链接
	 * @param friendlink 友情链接对象
	 */
	public void merge(FriendLink friendLink){
		
		if ( friendLink.getId() == null ) {
			logger.info("Dao 开始添加友情链接...............");
		} else {
			logger.info("Dao 开始修改友情链接...............");
		}
		
		super.getHibernateTemplate().merge(friendLink);
		
		if ( friendLink.getId() == null ) {
			logger.info("Dao 添加友情链接结束...............");
		} else {
			logger.info("Dao 修改友情链接结束...............");
		}
	}
	
	/**
	 * 删除友情链接
	 */
	public void del(FriendLink friendLink){
		
		logger.info("Dao 开始删除友情链接...............");
		
		super.getHibernateTemplate().delete(friendLink);
		
		logger.info("Dao 删除友情链接结束...............");
	}
}
