package com.bjgl.web.service.impl.user;

import com.bjgl.web.dao.user.PermissionItemDao;
import com.bjgl.web.entity.user.PermissionItem;
import com.bjgl.web.service.impl.AbstractBaseServiceImpl;
import com.bjgl.web.service.user.PermissionItemService;

import java.util.List;

/**
 * User: sunshow
 * Date: 13-7-14
 * Time: 上午9:49
 */
public class PermissionItemServiceImpl extends AbstractBaseServiceImpl<PermissionItem> implements PermissionItemService {

    protected PermissionItemDao getPermissionItemDao() {
        return (PermissionItemDao)this.dao;
    }

    @Override
    public List<PermissionItem> findByPermissionId(Long permissionId) {
        return this.getPermissionItemDao().findByPermissionId(permissionId);
    }
}
