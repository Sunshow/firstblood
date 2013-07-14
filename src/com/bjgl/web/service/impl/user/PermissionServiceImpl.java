package com.bjgl.web.service.impl.user;

import com.bjgl.web.bean.PageBean;
import com.bjgl.web.dao.user.*;
import com.bjgl.web.entity.user.*;
import com.bjgl.web.service.impl.AbstractBaseServiceImpl;
import com.bjgl.web.service.user.PermissionService;

import java.util.*;

public class PermissionServiceImpl extends AbstractBaseServiceImpl<Permission> implements PermissionService {
	private MenuDao menuDao;
	private RoleDao roleDao;
	private UserDao userDao;
	private UserRoleDao userRoleDao;
	private RolePermissionDao rolePermissionDao;
	private PermissionItemDao permissionItemDao;

	public void manage(Role role, List<RolePermission> rolePermissions) {

        // 先保存角色
        roleDao.save(role);
        Role saveRole = role;

        // 先取出已有的角色权限
        List<RolePermission> srcRolePermissions = rolePermissionDao.findByRoleId(role.getId());

        // 按照permission转置map
        Map<Long, RolePermission> srcRolePermissionMap = new HashMap<Long, RolePermission>();
        if (srcRolePermissions != null) {
            for (RolePermission rp : srcRolePermissions) {
                srcRolePermissionMap.put(rp.getPermissionId(), rp);
            }
        }

        // 转置要修改成的数据
        Map<Long, RolePermission> desRolePermissionMap = new HashMap<Long, RolePermission>();
        if (rolePermissions != null) {
            for (RolePermission rp : rolePermissions) {
                desRolePermissionMap.put(rp.getPermissionId(), rp);
            }
        }


        List<RolePermission> mergeRolePermissionList = new ArrayList<RolePermission>();
        List<RolePermission> deleteRolePermissionList = new ArrayList<RolePermission>();

        // 查找出已被删除的权限
        if (srcRolePermissions != null) {
            for (RolePermission srcRolePermission : srcRolePermissions) {
                if (!desRolePermissionMap.containsKey(srcRolePermission.getPermissionId())) {
                    // 已被删除
                    deleteRolePermissionList.add(srcRolePermission);
                    continue;
                }

                RolePermission desRolePermission = desRolePermissionMap.get(srcRolePermission.getPermissionId());

                // 判断是否需要更新
                if (desRolePermission.getPermissionItemIds() == null) {
                    if (srcRolePermission.getPermissionItemIds() == null) {
                        // 均为空，不需要更新
                        continue;
                    }
                    // 发生了变化需要更新
                    srcRolePermission.setPermissionItemIds(desRolePermission.getPermissionItemIds());
                    mergeRolePermissionList.add(srcRolePermission);
                    continue;
                }

                // 以下都有子权限
                if (desRolePermission.getPermissionItemIds().equals(srcRolePermission.getPermissionItemIds())) {
                    // 没变化
                    continue;
                }

                // 发生了变化需要更新
                srcRolePermission.setPermissionItemIds(desRolePermission.getPermissionItemIds());
                mergeRolePermissionList.add(srcRolePermission);
            }
        }
        
        // 查找出要新增的权限
        if (rolePermissions != null) {
            for (RolePermission desRolePermission : rolePermissions) {
                if (!srcRolePermissionMap.containsKey(desRolePermission.getPermissionId())) {
                    // 不存在的权限，需要添加
                    if (desRolePermission.getRoleId() == null) {
                        desRolePermission.setRoleId(saveRole.getId());
                    }
                    mergeRolePermissionList.add(desRolePermission);
                }
            }
        }

        // 执行删除
        for (RolePermission rolePermission : deleteRolePermissionList) {
            rolePermissionDao.delete(rolePermission);
        }

        // 执行保存和更新
        for (RolePermission rolePermission : mergeRolePermissionList) {
            rolePermissionDao.save(rolePermission);
        }
	}

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

	public RolePermissionDao getRolePermissionDao() {
		return rolePermissionDao;
	}

	public void setRolePermissionDao(RolePermissionDao rolePermissionDao) {
		this.rolePermissionDao = rolePermissionDao;
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
