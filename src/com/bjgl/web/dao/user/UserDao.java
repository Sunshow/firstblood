package com.bjgl.web.dao.user;

import com.bjgl.web.dao.BaseDao;
import com.bjgl.web.entity.user.User;

public interface UserDao extends BaseDao<User> {

	public User findByUsername(String username);
}