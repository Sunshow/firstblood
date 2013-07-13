package web.dao.impl.user;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.user.RoleDao;
import com.lehecai.admin.web.domain.user.Role;

public class RoleDaoImpl extends HibernateDaoSupport implements RoleDao {

	@Override
	public void merge(Role role) {
		getHibernateTemplate().merge(role);
	}
	
	@Override
	public Role mergePK(Role role) {
		return getHibernateTemplate().merge(role);
	}

	/**
	 * 查询所有角色
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Role> list(Role role) {
		StringBuffer sb = new StringBuffer();
		sb.append("from Role u where u.valid=1");		
		return getHibernateTemplate().find(sb.toString());
	}

	@Override
	public Role get(Long ID) {
		return getHibernateTemplate().get(Role.class, ID);
	}

	@Override
	public void del(Role role) {
		getHibernateTemplate().delete(role);
	}

}
