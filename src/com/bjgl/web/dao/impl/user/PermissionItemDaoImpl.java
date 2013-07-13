package com.bjgl.web.dao.impl.user;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.bjgl.web.dao.user.PermissionItemDao;
import com.bjgl.web.entity.user.Permission;
import com.bjgl.web.entity.user.PermissionItem;

public class PermissionItemDaoImpl extends HibernateDaoSupport implements PermissionItemDao {

	@Override
	public void merge(PermissionItem permissionItem) {
		// TODO Auto-generated method stub
		getHibernateTemplate().merge(permissionItem);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PermissionItem> list(final PermissionItem permissionItem) {
		// TODO Auto-generated method stub
		return (List<PermissionItem>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from PermissionItem p where 1 = 1");
						if(permissionItem != null && permissionItem.getPermissionID() != null){
							hql.append(" and p.permissionID = :permissionID");
						}
						hql.append(" order by p.orderView desc,p.id");
						Query query = session.createQuery(hql.toString());
								
						if(permissionItem != null && permissionItem.getPermissionID() != null){
							query.setParameter("permissionID", permissionItem.getPermissionID());
						}
						return query.list();
					}
				});
	}

	@Override
	public PermissionItem get(Long ID) {
		// TODO Auto-generated method stub
		return getHibernateTemplate().get(PermissionItem.class, ID);
	}

	@Override
	public void del(PermissionItem permissionItem) {
		// TODO Auto-generated method stub
		getHibernateTemplate().delete(permissionItem);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PermissionItem> list(final Permission permission) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		return (List<PermissionItem>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from PermissionItem p where 1 = 1");
						if(permission != null && permission.getId() != null){
							hql.append(" and p.permissionID = :permissionID");
						}
						hql.append(" order by p.orderView desc,p.id");
						Query query = session.createQuery(hql.toString());
								
						if(permission != null && permission.getId() != null){
							query.setParameter("permissionID", permission.getId());
						}
						return query.list();
					}
				});
	}

}
