package com.bjgl.web.service.impl.user;

import com.bjgl.web.bean.PageBean;
import com.bjgl.web.dao.user.*;
import com.bjgl.web.entity.user.*;
import com.bjgl.web.service.user.PermissionService;
import com.bjgl.web.utils.CharsetConstant;
import com.bjgl.web.utils.CoreStringUtils;

import java.util.*;

public class PermissionServiceImpl implements PermissionService {
	private MenuDao menuDao;
	private RoleDao roleDao;
	private UserDao userDao;
	private UserRoleDao userRoleDao;
	private RolePermissionDao rolePermissionDao;
	private PermissionItemDao permissionItemDao;
	private PermissionDao permissionDao;
	
	public void manage(Menu menu) {
		menuDao.merge(menu);
	}

	public List<Menu> listMenus(Menu menu){
		return menuDao.list(menu);
	}

	public Menu getMenu(Long ID){
		return menuDao.get(ID);
	}

	public void del(Menu menu){
		// TODO Auto-generated method stub
		
	}

	public void manage(Permission permission){
		permissionDao.merge(permission);
	}

	public List<Permission> listPermissions(Permission permission) {
		return permissionDao.list(permission);
	}

	public Permission getPermission(Long ID){
		return permissionDao.get(ID);
	}

	public void del(Permission permission){
		permissionDao.del(permission);
	}

	public void manage(PermissionItem permissionItem){
		permissionItemDao.merge(permissionItem);
	}

	public List<PermissionItem> listPermissionItems(
			PermissionItem permissionItem){
		return permissionItemDao.list(permissionItem);
	}

	public PermissionItem getPermissionItem(Long ID){
		return permissionItemDao.get(ID);
	}

	public void del(PermissionItem permissionItem){
		permissionItemDao.del(permissionItem);
	}

	public List<PermissionItem> listPermissionItems(Permission permission) {
		return permissionItemDao.list(permission);
	}

	public void manage(RolePermission rolePermission){
		rolePermissionDao.merge(rolePermission);
	}

	public void manageBatch(List<RolePermission> rolePremissionList) {
		// TODO Auto-generated method stub
		
	}

	public void del(RolePermission rolePremission){
		// TODO Auto-generated method stub
		
	}

	public void delBatch(List<RolePermission> rolePremissionList) {
		// TODO Auto-generated method stub
		
	}

	public List<RolePermission> getPermissionsByRole(Role role) {
		return rolePermissionDao.getPermissionsByRole(role);
	}

	public void manage(Role role){
		// TODO Auto-generated method stub

	}

	public void manage(Role role, List<RolePermission> rolePermissions) {

        // 先保存角色
        roleDao.save(role);
        Role saveRole = role;

        // 先取出已有的角色权限
        List<RolePermission> srcRolePermissions = this.getPermissionsByRole(role);

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
            rolePermissionDao.merge(rolePermission);
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

	public PermissionDao getPermissionDao() {
		return permissionDao;
	}

	public void setPermissionDao(PermissionDao permissionDao) {
		this.permissionDao = permissionDao;
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
