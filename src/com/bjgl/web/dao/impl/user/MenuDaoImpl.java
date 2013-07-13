package com.bjgl.web.dao.impl.user;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.bjgl.web.dao.user.MenuDao;
import com.bjgl.web.entity.user.Menu;

public class MenuDaoImpl extends HibernateDaoSupport implements MenuDao {

	@Override
	public void merge(Menu menu) {
		getHibernateTemplate().merge(menu);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Menu> list(Menu menu) {
		StringBuffer sb = new StringBuffer();
		sb.append("from Menu u ");
		sb.append(" order by u.orderView desc,u.id");
		return getHibernateTemplate().find(sb.toString());
	}

	@Override
	public Menu get(Long ID) {
		return getHibernateTemplate().get(Menu.class, ID);
	}

	@Override
	public void del(Menu menu) {
		getHibernateTemplate().delete(menu);
	}

}
