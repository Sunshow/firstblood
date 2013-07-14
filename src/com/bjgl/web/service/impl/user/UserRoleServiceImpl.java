package com.bjgl.web.service.impl.user;

import com.bjgl.web.dao.user.UserRoleDao;
import com.bjgl.web.entity.user.UserRole;
import com.bjgl.web.service.impl.AbstractBaseServiceImpl;
import com.bjgl.web.service.user.UserRoleService;

import java.util.List;

/**
 * User: sunshow
 * Date: 13-7-14
 * Time: 上午9:49
 */
public class UserRoleServiceImpl extends AbstractBaseServiceImpl<UserRole> implements UserRoleService {

    protected UserRoleDao getUserRoleDao() {
        return (UserRoleDao)this.dao;
    }

    @Override
    public List<UserRole> findByUserId(Long userId) {
        return this.getUserRoleDao().findByUserId(userId);
    }
}
