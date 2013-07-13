package com.bjgl.web.dao.user;

import java.util.List;

import com.bjgl.web.entity.user.Permission;
import com.bjgl.web.entity.user.PermissionItem;

public interface PermissionItemDao {
	void merge(PermissionItem permissionItem);
	List<PermissionItem> list(PermissionItem permissionItem);
	PermissionItem get(Long ID);
	void del(PermissionItem permissionItem);
	List<PermissionItem> list(Permission permission);
}