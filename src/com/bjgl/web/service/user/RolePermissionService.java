package com.bjgl.web.service.user;

import com.bjgl.web.entity.user.RolePermission;
import com.bjgl.web.service.BaseService;

import java.util.List;

/**
 * User: sunshow
 * Date: 13-7-14
 * Time: 上午9:48
 */
public interface RolePermissionService extends BaseService<RolePermission> {

    public List<RolePermission> findByRoleId(Long roleId);

}
