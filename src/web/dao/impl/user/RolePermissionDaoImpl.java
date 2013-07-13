package web.dao.impl.user;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.user.RolePermissionDao;
import com.lehecai.admin.web.domain.user.Role;
import com.lehecai.admin.web.domain.user.RolePermission;

public class RolePermissionDaoImpl extends HibernateDaoSupport implements RolePermissionDao{

	@SuppressWarnings("unchecked")
	public List<RolePermission> getPermissionsByRole(final Role role) {
		 return (List<RolePermission>) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException {
							StringBuffer hql = new StringBuffer("from RolePermission u where u.roleId = :roleId");
							Query query = session.createQuery(hql.toString());
							query.setParameter("roleId", role.getId());
							return query.list();//查询以数组的形式返回
						}
					});
	}

	public void delPermissionsByRole(Role role) {
		List<RolePermission> rpList = getPermissionsByRole(role);
		if(rpList != null && rpList.size() > 0) {
			for(RolePermission rp : rpList) {
				getHibernateTemplate().delete(rp);
			}
		}
	}
	
	@Override
	public void merge(RolePermission rolePermission) {
		getHibernateTemplate().merge(rolePermission);
	}

    @Override
    public void delete(RolePermission rolePermission) {
        getHibernateTemplate().delete(rolePermission);
    }
}
