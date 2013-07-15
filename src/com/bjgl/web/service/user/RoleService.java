package com.bjgl.web.service.user;

import com.bjgl.web.entity.user.Role;
import com.bjgl.web.entity.user.RolePermission;
import com.bjgl.web.service.BaseService;

import java.util.List;

/**
 * User: sunshow
 * Date: 13-7-14
 * Time: 上午9:48
 */
public interface RoleService extends BaseService<Role> {

    public void saveOrUpdate(Role role, List<RolePermission> rolePermissions);

}
