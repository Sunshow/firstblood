package com.bjgl.web.service.impl.user;

import com.bjgl.web.bean.PageBean;
import com.bjgl.web.dao.user.*;
import com.bjgl.web.entity.user.Permission;
import com.bjgl.web.entity.user.User;
import com.bjgl.web.entity.user.UserRole;
import com.bjgl.web.service.impl.AbstractBaseServiceImpl;
import com.bjgl.web.service.user.PermissionService;

import java.util.Date;
import java.util.List;

public class PermissionServiceImpl extends AbstractBaseServiceImpl<Permission> implements PermissionService {
	private MenuDao menuDao;
	private RoleDao roleDao;
	private UserDao userDao;
	private UserRoleDao userRoleDao;
	private PermissionItemDao permissionItemDao;

	public void manage(User user){
		userDao.update(user);
	}

	@Override
	public List<User> list(String userName, String name, Date beginDate,
			Date endDate, Long roleID, String valid, PageBean pageBean) {
		//return userDao.list(userName, name, beginDate, endDate, roleID, valid, pageBean);
        return null;
	}
	
	@Override
	public PageBean getPageBean(String userName, String name, Date beginDate,
			Date endDate, Long roleID, String valid, PageBean pageBean) {
		//return userDao.getPageBean(userName, name, beginDate, endDate, roleID, valid, pageBean);
        return null;
	}

	
	public void manage(User user, UserRole userRole){
        /*
		User saveUser = userDao.mergePK(user);
		userRole.setUserId(saveUser.getId());
		userRoleDao.merge(userRole);
		*/
	}


	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public UserRoleDao getUserRoleDao() {
		return userRoleDao;
	}

	public void setUserRoleDao(UserRoleDao userRoleDao) {
		this.userRoleDao = userRoleDao;
	}

	public PermissionItemDao getPermissionItemDao() {
		return permissionItemDao;
	}

	public void setPermissionItemDao(PermissionItemDao permissionItemDao) {
		this.permissionItemDao = permissionItemDao;
	
	}

	public MenuDao getMenuDao() {
		return menuDao;
	}

	public void setMenuDao(MenuDao menuDao) {
		this.menuDao = menuDao;
	}

	public RoleDao getRoleDao() {
		return roleDao;
	}

	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}
}
