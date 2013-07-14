package com.bjgl.web.dao.user;

import com.bjgl.web.dao.BaseDao;
import com.bjgl.web.entity.user.UserRole;

import java.util.List;

/**
 * 用户角色数据访问接口
 * @author chirowong
 *
 */
public interface UserRoleDao extends BaseDao<UserRole> {

	public List<UserRole> findByUserId(Long userId);

}
