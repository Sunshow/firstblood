package com.bjgl.web.service.impl.user;

import com.bjgl.web.dao.user.UserDao;
import com.bjgl.web.entity.user.User;
import com.bjgl.web.service.impl.AbstractBaseServiceImpl;
import com.bjgl.web.service.user.UserService;
import com.bjgl.web.utils.CharsetConstant;
import com.bjgl.web.utils.CoreStringUtils;

/**
 * User: sunshow
 * Date: 13-7-14
 * Time: 上午9:49
 */
public class UserServiceImpl extends AbstractBaseServiceImpl<User> implements UserService {

    protected UserDao getUserDao() {
        return (UserDao)dao;
    }

    @Override
    public User findByUsername(String username) {
        return this.getUserDao().findByUsername(username);
    }

    @Override
    public User login(String username, String password) {
        User user = this.findByUsername(username);
        if (user != null) {
            if (CoreStringUtils.md5(password, CharsetConstant.CHARSET_UTF8).equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}
