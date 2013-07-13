package com.bjgl.web.dao.impl.user;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.bjgl.web.dao.user.UserRoleDao;
import com.bjgl.web.entity.user.User;
import com.bjgl.web.entity.user.UserRole;

public class UserRoleDaoImpl extends HibernateDaoSupport implements UserRoleDao{

	@SuppressWarnings("unchecked")
	public List<UserRole> getRolesByUser(final User user) {
		 return (List<UserRole>) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException {
							StringBuffer hql = new StringBuffer("from UserRole u where u.userId = :userId");
							Query query = session.createQuery(hql.toString());
							query.setParameter("userId", user.getId());
							return query.list();//查询以数组的形式返回
						}
					});
	}

	@Override
	public void merge(UserRole userRole) {
		getHibernateTemplate().merge(userRole);
	}
}
