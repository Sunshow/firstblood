package com.bjgl.web.service.user;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bjgl.web.bean.PageBean;
import com.bjgl.web.entity.user.Menu;
import com.bjgl.web.entity.user.Permission;
import com.bjgl.web.entity.user.PermissionItem;
import com.bjgl.web.entity.user.Role;
import com.bjgl.web.entity.user.RolePermission;
import com.bjgl.web.entity.user.User;
import com.bjgl.web.entity.user.UserRole;

public interface PermissionService {

	/*menu*/
	void manage(Menu menu);
	List<Menu> listMenus(Menu menu); 
	Menu getMenu(Long ID);
	void del(Menu menu);
	
	
	void manage(Permission permission);
	List<Permission> listPermissions(Permission permission); 
	Permission getPermission(Long ID);
	void del(Permission permission);
	
	void manage(PermissionItem permissionItem);
	List<PermissionItem> listPermissionItems(PermissionItem permissionItem); 
	PermissionItem getPermissionItem(Long ID);
	void del(PermissionItem permissionItem);
	List<PermissionItem> listPermissionItems(Permission permission);
	
	void manage(RolePermission rolePremission);
	void manageBatch(List<RolePermission> rolePremissionList);
	void del(RolePermission rolePremission);
	void delBatch(List<RolePermission> rolePremissionList);
	List<RolePermission> getPermissionsByRole(Role role);
	
	void manage(Role role);
	void manage(Role role,List<RolePermission> rolePermissions);
	List<Role> listRoles(Role role); 
	Role getRole(Long ID);
	void del(Role role);
	
	void manage(UserRole userRole);
	void delUserRole(UserRole userRole);
	List<UserRole> getRolesByUser(User user);
	
	
	void manage(User user);
	User getUser(Long ID);
	void delUser(User user);
	User getByUserName(String userName);
	User login(String userName, String password);
	/**
	 * 封装多条件查询分页信息
	 * @param userName	用户名
	 * @param name	姓名
	 * @param beginDate	起始创建时间
	 * @param endDate 结束创建时间
	 * @param roleID 角色编号
	 * @param valid	是否有效
	 * @param pageBean
	 * @return
	 */
	PageBean getPageBean(String userName, String name, Date beginDate,
			Date endDate, Long roleID, String valid, PageBean pageBean);
	/**
	 * 多条件分页查询用户
	 * @param userName	用户名
	 * @param name	姓名
	 * @param beginDate	起始创建时间
	 * @param endDate 结束创建时间
	 * @param roleID 角色编号
	 * @param valid	是否有效
	 * @param pageBean
	 * @return
	 */
	List<User> list(String userName, String name, Date beginDate,
			Date endDate, Long roleID, String valid, PageBean pageBean);
	
	void manage(User user,UserRole userRole);
}