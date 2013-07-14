package com.bjgl.web.service.impl.user;

import com.bjgl.web.dao.user.RolePermissionDao;
import com.bjgl.web.entity.user.RolePermission;
import com.bjgl.web.service.impl.AbstractBaseServiceImpl;
import com.bjgl.web.service.user.RolePermissionService;

import java.util.List;

/**
 * User: sunshow
 * Date: 13-7-14
 * Time: 上午9:49
 */
public class RolePermissionServiceImpl extends AbstractBaseServiceImpl<RolePermission> implements RolePermissionService {

    protected RolePermissionDao getRolePermissionDao() {
        return (RolePermissionDao)this.dao;
    }

    @Override
    public List<RolePermission> findByRoleId(Long roleId) {
        return this.getRolePermissionDao().findByRoleId(roleId);
    }
}
