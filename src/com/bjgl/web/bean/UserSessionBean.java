package com.bjgl.web.bean;

import com.bjgl.web.entity.user.Role;
import com.bjgl.web.entity.user.User;

import java.io.Serializable;
import java.util.List;

public class UserSessionBean implements Serializable {

    private static final long serialVersionUID = -3764402465865930790L;
    private User user;
    private List<Role> roleList;

    private List<Long> permissionIdList;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public List<Long> getPermissionIdList() {
        return permissionIdList;
    }

    public void setPermissionIdList(List<Long> permissionIdList) {
        this.permissionIdList = permissionIdList;
    }
}
