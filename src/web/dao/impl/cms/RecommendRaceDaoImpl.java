package web.dao.impl.cms;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.cms.RecommendRaceDao;
import com.lehecai.admin.web.domain.cms.RecommendRace;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.type.cooperator.Cooperator;

/**
 * 推荐赛程数据访问层实现类
 * @author yanweijie
 *
 */
public class RecommendRaceDaoImpl extends HibernateDaoSupport implements RecommendRaceDao {


	/**
	 * 更新推荐赛程
	 */
	public void merge(RecommendRace recommendRace) {
		super.getHibernateTemplate().merge(recommendRace);
	}
	
	/**
	 * 根据Id获取赛程
	 */
	public RecommendRace getById (Long id) {
		return super.getHibernateTemplate().get(RecommendRace.class, id);
	}
	
	/**
	 * 删除推荐赛程
	 */
	public void delete(RecommendRace recommendRace) {
		super.getHibernateTemplate().delete(recommendRace);
	}
	
	/**
	 * 查询所有赛程
	 */
	@SuppressWarnings("unchecked")
	public List<RecommendRace> findList(final Cooperator cooperator, final LotteryType lotteryType) {
		return super.getHibernateTemplate().executeFind(new HibernateCallback<List<RecommendRace>>() {

			@Override
			public List<RecommendRace> doInHibernate(Session session) throws HibernateException,
					SQLException {
				StringBuffer hql = new StringBuffer("from RecommendRace r where 1 = 1");
				if (cooperator != null) {
					hql.append(" and r.cooperatorId = :cooperatorId");
				}
				if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
					hql.append(" and r.lotteryType = :lotteryTypeValue");
				}
				hql.append(" order by r.id desc");
				
				
				Query query = session.createQuery(hql.toString());
				
				if (cooperator != null) {
					query.setParameter("cooperatorId", Long.valueOf(cooperator.getValue()));
				}
				if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
					query.setParameter("lotteryTypeValue", lotteryType);
				}
				
				return query.list();
			}
		});
	}
}
