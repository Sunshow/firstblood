package com.bjgl.web.service.user;

import com.bjgl.web.entity.user.PermissionItem;
import com.bjgl.web.service.BaseService;

import java.util.List;

/**
 * User: sunshow
 * Date: 13-7-14
 * Time: 上午9:48
 */
public interface PermissionItemService extends BaseService<PermissionItem> {

    public List<PermissionItem> findByPermissionId(Long permissionId);

}
