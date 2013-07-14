/**
 * 
 */
package com.bjgl.web.entity.user;

import java.io.Serializable;

/**
 * 用户角色对应表
 * @author chirowong
 *
 */ 
public class UserRole implements Serializable{

	private static final long serialVersionUID = -699033612844698418L;
	private Long id;
	private Long userId;
	private Long roleId;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
