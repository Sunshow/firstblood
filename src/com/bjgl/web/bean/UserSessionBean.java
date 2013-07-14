package com.bjgl.web.bean;

import java.io.Serializable;
import java.util.List;

import com.bjgl.web.entity.user.Menu;
import com.bjgl.web.entity.user.Permission;
import com.bjgl.web.entity.user.PermissionItem;
import com.bjgl.web.entity.user.Role;
import com.bjgl.web.entity.user.User;

public class UserSessionBean implements Serializable {

    private static final long serialVersionUID = -3764402465865930790L;
    private User user;
    private List<Role> roleList;
    private List<Menu> menus;
    private List<Permission> permissions;
    private List<PermissionItem> permissionItems;

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

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<PermissionItem> getPermissionItems() {
        return permissionItems;
    }

    public void setPermissionItems(List<PermissionItem> permissionItems) {
        this.permissionItems = permissionItems;
    }
}
