package com.bjgl.web.service.user;

import com.bjgl.web.entity.user.UserRole;
import com.bjgl.web.service.BaseService;

import java.util.List;

/**
 * User: sunshow
 * Date: 13-7-14
 * Time: 上午9:48
 */
public interface UserRoleService extends BaseService<UserRole> {

    public List<UserRole> findByUserId(Long userId);

}
