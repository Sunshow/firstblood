package com.bjgl.web.dao.impl.user;

import com.bjgl.web.dao.impl.AbstractBaseDaoImpl;
import com.bjgl.web.dao.user.RolePermissionDao;
import com.bjgl.web.entity.user.RolePermission;

import java.util.List;

public class RolePermissionDaoImpl extends AbstractBaseDaoImpl<RolePermission> implements RolePermissionDao {

    @Override
    public List<RolePermission> findByRoleId(Long roleId) {
        RolePermission example = new RolePermission();
        example.setRoleId(roleId);

        return this.findByExample(example, null);
    }
}
