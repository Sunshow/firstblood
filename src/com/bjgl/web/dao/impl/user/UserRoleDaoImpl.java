package com.bjgl.web.dao.impl.user;

import com.bjgl.web.dao.impl.AbstractBaseDaoImpl;
import com.bjgl.web.dao.user.UserRoleDao;
import com.bjgl.web.entity.user.UserRole;

import java.util.List;

public class UserRoleDaoImpl extends AbstractBaseDaoImpl<UserRole> implements UserRoleDao {

    @Override
    public List<UserRole> findByUserId(Long userId) {
        UserRole example = new UserRole();
        example.setUserId(userId);

        return this.findByExample(example, null);
    }

}
