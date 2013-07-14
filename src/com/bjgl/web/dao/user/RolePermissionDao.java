package com.bjgl.web.dao.user;

import com.bjgl.web.dao.BaseDao;
import com.bjgl.web.entity.user.RolePermission;

import java.util.List;

public interface RolePermissionDao extends BaseDao<RolePermission> {

    public List<RolePermission> findByRoleId(Long roleId);

}
