package com.bjgl.web.dao.user;

import java.util.List;

import com.bjgl.web.entity.user.Role;
import com.bjgl.web.entity.user.RolePermission;

public interface RolePermissionDao {
	List<RolePermission> getPermissionsByRole(Role role);
	void delPermissionsByRole(Role role);
	void merge(RolePermission rolePermission);
    void delete(RolePermission rolePermission);
}
