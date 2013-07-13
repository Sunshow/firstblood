package web.dao.impl.business;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.business.KeyMemberDao;
import com.lehecai.admin.web.domain.business.KeyMember;

/**
 * 重点会员数据访问层实现类
 * @author yanweijie
 *
 */
public class KeyMemberDaoImpl extends HibernateDaoSupport implements KeyMemberDao {

	/**
	 * 查询所有重点会员
	 */
	@SuppressWarnings("unchecked")
	public List<KeyMember> findList() {
		return super.getHibernateTemplate().executeFind(new HibernateCallback<List<KeyMember>>() {

			@Override
			public List<KeyMember> doInHibernate(Session session)
					throws HibernateException, SQLException {
				String hql = "from KeyMember";
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
	}
	
	/**
	 * 根据会员编号查询重点会员
	 * @param uid 会员编号
	 */
	public KeyMember getByUid(final Long uid) {
		return super.getHibernateTemplate().execute(new HibernateCallback<KeyMember>() {

			@Override
			public KeyMember doInHibernate(Session session)
					throws HibernateException, SQLException {
				String hql = "from KeyMember where uid = :uid";
				Query query = session.createQuery(hql);
				query.setParameter("uid", uid);
				
				return (KeyMember)query.uniqueResult();
			}
		});
	}
	
	/**
	 * 根据会员用户名查询重点会员
	 * @param userName 会员用户名
	 */
	public KeyMember getByUserName(final String userName) {
		return super.getHibernateTemplate().execute(new HibernateCallback<KeyMember>() {

			@Override
			public KeyMember doInHibernate(Session session)
					throws HibernateException, SQLException {
				String hql = "from KeyMember where userName = :userName";
				Query query = session.createQuery(hql);
				query.setParameter("userName", userName);
				
				return (KeyMember)query.uniqueResult();
			}
		});
	}
	
	/**
	 * 根据重点会员编号查询重点会员信息
	 * @param id 重点会员编号
	 */
	public KeyMember getById(Long id) {
		return super.getHibernateTemplate().get(KeyMember.class, id);
	}
	
	/**
	 * 添加/修改重点会员(备注信息)
	 * @param keyMember 重点会员
	 */
	public void mergeKeyMember(KeyMember keyMember) {
		super.getHibernateTemplate().merge(keyMember);
	}
	
	/**
	 * 删除重点会员
	 * @param keyMember 重点会员
	 */
	public void deleteKeyMember(KeyMember keyMember) {
		super.getHibernateTemplate().delete(keyMember);
	}
}
