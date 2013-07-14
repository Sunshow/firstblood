package com.bjgl.web.dao.impl.user;

import com.bjgl.web.dao.impl.AbstractBaseDaoImpl;
import com.bjgl.web.dao.user.PermissionItemDao;
import com.bjgl.web.entity.user.PermissionItem;

import java.util.List;

public class PermissionItemDaoImpl extends AbstractBaseDaoImpl<PermissionItem> implements PermissionItemDao {

    @Override
    public List<PermissionItem> findByPermissionId(Long permissionId) {
        PermissionItem example = new PermissionItem();
        example.setPermissionId(permissionId);

        return this.findByExample(example, null);
    }

}
