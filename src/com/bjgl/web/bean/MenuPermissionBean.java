package com.bjgl.web.bean;

import com.bjgl.web.entity.user.Menu;
import com.bjgl.web.entity.user.Permission;
import com.bjgl.web.service.user.MenuService;
import com.bjgl.web.service.user.PermissionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存放Menu和Permission的缓存
 * User: sunshow
 * Date: 13-7-14
 * Time: 下午12:04
 */
public class MenuPermissionBean {

    private MenuService menuService;
    private PermissionService permissionService;

    private Map<Long, Menu> menuMap;

    private Map<Long, Permission> permissionMap;

    public void init() {
        synchronized (this) {
            Map<Long, Menu> menuMap = new HashMap<Long, Menu>();
            // 读取所有Menu
            List<Menu> menuList = menuService.findByExample(null, null);
            if (menuList != null) {
                for (Menu menu : menuList) {
                    menuMap.put(menu.getId(), menu);
                }
            }
            this.menuMap = menuMap;

            Map<Long, Permission> permissionMap = new HashMap<Long, Permission>();
            // 读取所有Permission
            List<Permission> permissionList = permissionService.findByExample(null, null);
            if (permissionList != null) {
                for (Permission permission : permissionList) {
                    permissionMap.put(permission.getId(), permission);
                }
            }
            this.permissionMap = permissionMap;
        }
    }

    protected Map<Long, Menu> getMenuMap() {
        Map<Long, Menu> menuMap;
        synchronized (this) {
            menuMap = this.menuMap;
        }
        return menuMap;
    }

    protected Map<Long, Permission> getPermissionMap() {
        Map<Long, Permission> permissionMap;
        synchronized (this) {
            permissionMap = this.permissionMap;
        }
        return permissionMap;
    }

    public List<Menu> getMenuListById(List<Long> menuIdList) {
        if (menuIdList == null) {
            return null;
        }

        Map<Long, Menu> menuMap = this.getMenuMap();
        if (menuMap == null) {
            return null;
        }
        List<Menu> menuList = new ArrayList<Menu>();
        for (Long menuId : menuIdList) {
            Menu menu = menuMap.get(menuId);
            menuList.add(menu);
        }
        return menuList;
    }

    public List<Permission> getPermissionListById(List<Long> permissionIdList) {
        if (permissionIdList == null) {
            return null;
        }

        Map<Long, Permission> permissionMap = this.getPermissionMap();
        if (permissionMap == null) {
            return null;
        }
        List<Permission> permissionList = new ArrayList<Permission>();
        for (Long permissionId : permissionIdList) {
            Permission permission = permissionMap.get(permissionId);
            permissionList.add(permission);
        }
        return permissionList;
    }

    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
}
