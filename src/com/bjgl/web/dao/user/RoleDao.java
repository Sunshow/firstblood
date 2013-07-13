package com.bjgl.web.dao.user;

import java.util.List;

import com.bjgl.web.entity.user.Role;

public interface RoleDao {
	void merge(Role role);
	Role mergePK(Role role);
	/**
	 * 查询所有角色
	 */
	List<Role> list(Role role);
	Role get(Long ID);
	void del(Role role);
}