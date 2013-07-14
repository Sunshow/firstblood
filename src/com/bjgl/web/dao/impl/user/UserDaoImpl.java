package com.bjgl.web.dao.impl.user;

import com.bjgl.web.dao.impl.AbstractBaseDaoImpl;
import com.bjgl.web.dao.user.UserDao;
import com.bjgl.web.entity.user.User;

import java.util.List;

public class UserDaoImpl extends AbstractBaseDaoImpl<User> implements UserDao {

    @Override
    public User findByUsername(String username) {
        User example = new User();
        example.setUsername(username);
        List<User> userList = this.findByExample(example, null);
        if (userList == null || userList.isEmpty()) {
            return null;
        }
        return userList.iterator().next();
    }

}
