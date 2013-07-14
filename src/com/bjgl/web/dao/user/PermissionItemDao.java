package com.bjgl.web.dao.user;

import com.bjgl.web.dao.BaseDao;
import com.bjgl.web.entity.user.PermissionItem;

import java.util.List;

public interface PermissionItemDao extends BaseDao<PermissionItem> {

    public List<PermissionItem> findByPermissionId(Long permissionId);

}