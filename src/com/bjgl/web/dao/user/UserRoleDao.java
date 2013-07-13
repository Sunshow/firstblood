package com.bjgl.web.dao.user;

import java.util.List;

import com.bjgl.web.entity.user.User;
import com.bjgl.web.entity.user.UserRole;

/**
 * 用户角色数据访问接口
 * @author chirowong
 *
 */
public interface UserRoleDao {
	List<UserRole> getRolesByUser(User user);
	void merge(UserRole userRole);
}
