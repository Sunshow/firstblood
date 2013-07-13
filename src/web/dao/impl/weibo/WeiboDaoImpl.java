/**
 * 
 */
package web.dao.impl.weibo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.weibo.WeiboDao;
import com.lehecai.admin.web.domain.weibo.TokenInfo;
import com.lehecai.admin.web.domain.weibo.WeiboLottery;
import com.lehecai.core.lottery.WeiboType;

/**
 * @author qatang
 *
 */
public class WeiboDaoImpl extends HibernateDaoSupport implements WeiboDao {

	/**
	 * 更新认证信息
	 * @param tokenInfo
	 */
	@Override
	public void merge(TokenInfo tokenInfo) {
		getHibernateTemplate().merge(tokenInfo);
	}

	/**
	 * 根据微博类型及用户编号查询认证信息
	 * @param uid
	 * @param weiboType
	 * @return
	 */
	@Override
	public TokenInfo getToken(final String uid, final WeiboType weiboType) {
		return getHibernateTemplate().execute(
				new HibernateCallback<TokenInfo>() {
					public TokenInfo doInHibernate(Session session)
					throws HibernateException {
						StringBuffer hql = new StringBuffer("from TokenInfo u where 1 = 1");
						hql.append(" and u.uid = :uid");
						hql.append(" and u.weiboType = :weiboType");
						Query query = session.createQuery(hql.toString());
						query.setParameter("uid", uid);
						query.setParameter("weiboType", weiboType);
						return (TokenInfo) query.uniqueResult();
					}
				});
	}
	
	/**
	 * 根据微博类型查询认证列表
	 * @param weiboType
	 * @return
	 */
	@Override
	public List<TokenInfo> getTokenList(final WeiboType weiboType) {
		return getHibernateTemplate().execute(
				new HibernateCallback<List<TokenInfo>>() {
					public List<TokenInfo> doInHibernate(Session session)
					throws HibernateException {
						StringBuffer hql = new StringBuffer("from TokenInfo u where 1 = 1");
						hql.append(" and u.weiboType = :weiboType");
						Query query = session.createQuery(hql.toString());
						query.setParameter("weiboType", weiboType);
						
						List<TokenInfo> tokenInfoList = new ArrayList<TokenInfo>();
						for (Object obj : query.list()) {
							tokenInfoList.add((TokenInfo) obj);
						}
						return tokenInfoList;
					}
				});
	}
	
	/**
	 * 查询所有认证
	 * @param weiboType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TokenInfo> findTokenInfoList() {
		return getHibernateTemplate().executeFind(
				new HibernateCallback<List<TokenInfo>>() {
					public List<TokenInfo> doInHibernate(Session session)
					throws HibernateException {
						StringBuffer hql = new StringBuffer("from TokenInfo u where 1 = 1");
						Query query = session.createQuery(hql.toString());
						return query.list();
					}
				});
	}
	
	/**
	 * 添加微博分享彩种记录
	 */
	public void addWeiboLottery(WeiboLottery weiboLottery) {
		super.getHibernateTemplate().save(weiboLottery);
	}
	
	/**
	 * 根据微博编号查询微博分享彩种记录
	 */
	@SuppressWarnings("unchecked")
	public List<WeiboLottery> findWeiboLotteryList(final String uid, final WeiboType weiboType) {
		return super.getHibernateTemplate().executeFind(new HibernateCallback<List<WeiboLottery>>() {

			@Override
			public List<WeiboLottery> doInHibernate(Session session)
					throws HibernateException, SQLException {
				StringBuffer hql = new StringBuffer("from WeiboLottery w where 1 = 1");
				hql.append(" and w.uid = :uid");
				hql.append(" and w.weiboType = :weiboType");
				
				Query query = session.createQuery(hql.toString());
				query.setParameter("uid", uid);
				query.setParameter("weiboType", weiboType);
				
				return query.list();
			}
		});
	}
	
	/**
	 * 删除微博分享彩种记录
	 */
	@Override
	public void deleteWeiboLottery(WeiboLottery weiboLottery) {
		super.getHibernateTemplate().delete(weiboLottery);
	}

}
