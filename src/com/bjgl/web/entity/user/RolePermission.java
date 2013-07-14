package com.bjgl.web.entity.user;

import java.io.Serializable;

/**
 * @author chirowong
 *
 */
public class RolePermission implements Serializable{

	private static final long serialVersionUID = 1939749703899092509L;
	private Long id;
	private Long roleId;
	private Long permissionId;
	private String permissionItemIds;
	
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public Long getPermissionId() {
		return permissionId;
	}
	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPermissionItemIds() {
		return permissionItemIds;
	}
	public void setPermissionItemIds(String permissionItemIds) {
		this.permissionItemIds = permissionItemIds;
	}
}
