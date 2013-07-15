package com.bjgl.web.action.user;

import com.bjgl.web.action.BaseAction;
import com.bjgl.web.bean.TreeViewBean;
import com.bjgl.web.entity.user.*;
import com.bjgl.web.service.user.*;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import java.util.*;

public class RoleAction extends BaseAction {
    private static final long serialVersionUID = 2436161530465382824L;

    private RoleService roleService;

    private RolePermissionService rolePermissionService;

    private MenuService menuService;

    private PermissionService permissionService;
    private PermissionItemService permissionItemService;
    private Role role;

    private String func;//用于区分输入的操作

    private List<Long> permissionIdList;
    private List<Long> permissionItemIdList;

    private List<Role> roleList;

    public String handle() {
        logger.info("进入查询角色");
        roleList = roleService.findByExample(role, null);
        return LIST;
    }

    public String manage() {
        logger.info("进入更新角色信息");

        if (role == null) {
            this.errorForward(FAILURE, "添加角色错误，提交表单不能为空");
        }

        if (StringUtils.isBlank(role.getName())) {
            this.errorForward(FAILURE, "角色名称不能为空");
        }

        List<RolePermission> rolePermissionList = new ArrayList<RolePermission>();

        if (permissionIdList != null && !permissionIdList.isEmpty()) {

            // 一次性读出所有子权限
            List<PermissionItem> allPermissionItemList = permissionItemService.findByExample(null, null);

            Map<Long, Set<Long>> permissionIdAndPermissionItemIdSetMap = new HashMap<Long, Set<Long>>();

            for (PermissionItem permissionItem : allPermissionItemList) {
                Long permissionId = permissionItem.getPermissionId();
                Long permissionItemId = permissionItem.getId();

                if (!permissionIdAndPermissionItemIdSetMap.containsKey(permissionId)) {
                    permissionIdAndPermissionItemIdSetMap.put(permissionId, new HashSet<Long>());
                }

                Set<Long> permissionItemIdSet = permissionIdAndPermissionItemIdSetMap.get(permissionId);
                permissionItemIdSet.add(permissionItemId);
            }

            for (Long permissionId : permissionIdList) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(role.getId());
                rolePermission.setPermissionId(permissionId);

                if (permissionItemIdList != null) {
                    Set<Long> permissionItemIdSet = permissionIdAndPermissionItemIdSetMap.get(permissionId);

                    List<Long> idList = new ArrayList<Long>();
                    for (Long permissionItemId : permissionItemIdList) {
                        if (permissionItemIdSet.contains(permissionItemId)) {
                            idList.add(permissionItemId);
                        }
                    }

                    rolePermission.setPermissionItemIds(StringUtils.join(idList, ","));
                }

                rolePermissionList.add(rolePermission);
            }
        }
        try {
            roleService.saveOrUpdate(role, rolePermissionList);
        } catch (Exception e) {
            logger.error(e.getMessage());
            super.setErrorMessage(e.getMessage());
            return "failure";
        }

        super.setForwardUrl("/user/role.do");
        super.setSuccessMessage("编辑角色成功");
        logger.info("更新角色信息结束");
        return SUCCESS;
    }

    /**
     * 转向添加/修改角色
     */
    public String input() {
        logger.info("进入输入角色信息");
        if (role != null) {
            if (role.getId() != null) {
                if ("copy".equals(func)) {//复制
                    role.setValid(true);//设置有效
                    role.setRestriction(false);//设置限制IP
                } else {//修改
                    role = roleService.findById(role.getId());
                }
            }
        } else {
            role = new Role();
            role.setRestriction(false);
            role.setValid(true);
        }
        return "inputForm";
    }

    public String view() {
        logger.info("进入查看角色详情");
        if (role != null && role.getId() != null) {
            role = roleService.findById(role.getId());
        } else {
            this.errorForward(INDEX, "查看角色详情，编码不能为空");
        }
        logger.info("查看角色详情结束");
        return VIEW;
    }

    public String del() {
        logger.info("进入删除角色");
        if (role != null && role.getId() != null) {
            roleService.delete(role.getId());
        } else {
            this.errorForward(INDEX, "删除角色，编码不能为空");
        }
        super.setForwardUrl("/user/role.do");
        logger.info("删除角色结束");
        return FORWARD;
    }

    public void findPermissions() {
        logger.info("进入查询权限");

        // 获取当前角色已拥有的权限和子权限
        Set<Long> rolePermissionIdSet = new HashSet<Long>();
        Set<Long> rolePermissionItemIdSet = new HashSet<Long>();
        if (role != null && role.getId() != null) {
            List<RolePermission> rolePermissionList = rolePermissionService.findByRoleId(role.getId());

            for (RolePermission rolePermission : rolePermissionList) {
                rolePermissionIdSet.add(rolePermission.getPermissionId());

                if (StringUtils.isNotBlank(rolePermission.getPermissionItemIds())) {
                    String[] ids = StringUtils.split(rolePermission.getPermissionItemIds(), ",");
                    for (String id : ids) {
                        rolePermissionItemIdSet.add(Long.valueOf(id));
                    }
                }
            }
        }

        // 一次性读出所有权限
        List<Permission> allPermissionList = permissionService.findByExample(null, null);

        Map<Long, List<Permission>> menuPermissionListMap = new HashMap<Long, List<Permission>>();

        // 转置map
        if (allPermissionList != null) {
            for (Permission permission : allPermissionList) {
                Long menuId = permission.getMenuId();
                List<Permission> menuPermissionList;
                if (menuPermissionListMap.containsKey(menuId)) {
                    menuPermissionList = menuPermissionListMap.get(menuId);
                } else {
                    menuPermissionList = new ArrayList<Permission>();
                    menuPermissionListMap.put(menuId, menuPermissionList);
                }
                menuPermissionList.add(permission);
            }
        }

        // 一次性读出所有子权限
        List<PermissionItem> allPermissionItemList = permissionItemService.findByExample(null, null);

        Map<Long, List<PermissionItem>> permissionItemListMap = new HashMap<Long, List<PermissionItem>>();

        // 转置map
        if (allPermissionItemList != null) {
            for (PermissionItem permissionItem : allPermissionItemList) {
                Long permissionId = permissionItem.getPermissionId();
                List<PermissionItem> permissionItemList;
                if (permissionItemListMap.containsKey(permissionId)) {
                    permissionItemList = permissionItemListMap.get(permissionId);
                } else {
                    permissionItemList = new ArrayList<PermissionItem>();
                    permissionItemListMap.put(permissionId, permissionItemList);
                }
                permissionItemList.add(permissionItem);
            }
        }

        List<TreeViewBean> menuTreeViewBeanList = new ArrayList<TreeViewBean>();
        List<Menu> menuList = menuService.findByExample(null, null);

        if (menuList != null) {
            for (Menu menu : menuList) {
                TreeViewBean menuTreeViewBean = new TreeViewBean();
                menuTreeViewBean.setId(menu.getId().toString());
                menuTreeViewBean.setText(menu.getName());
                menuTreeViewBean.setHasChildren(false);
                menuTreeViewBean.setClasses(StringUtils.EMPTY);

                List<TreeViewBean> permissionTreeViewBeanList = new ArrayList<TreeViewBean>();
                List<Permission> menuPermissionList = menuPermissionListMap.get(menu.getId());

                if (menuPermissionList != null) {
                    for (Permission permission : menuPermissionList) {
                        TreeViewBean permissionTreeViewBean = new TreeViewBean();
                        permissionTreeViewBean.setId(permission.getId().toString());
                        permissionTreeViewBean.setHasChildren(false);
                        permissionTreeViewBean.setClasses("");

                        StringBuffer permissionNodeBuffer = new StringBuffer();
                        permissionNodeBuffer.append("<input type='checkbox' name='permissionIdList' value='").append(permission.getId()).append("' class='perms'");
                        if (rolePermissionIdSet.contains(permission.getId())) {
                            permissionNodeBuffer.append(" checked='checked'");
                        }
                        permissionNodeBuffer.append("/>&nbsp;").append(permission.getName());

                        permissionTreeViewBean.setText(permissionNodeBuffer.toString());

                        List<TreeViewBean> permissionItemTreeViewBeanList = new ArrayList<TreeViewBean>();

                        List<PermissionItem> permissionItemList = permissionItemListMap.get(permission.getId());

                        if (permissionItemList != null) {
                            for (PermissionItem permissionItem : permissionItemList) {
                                TreeViewBean permissionItemTreeViewBean = new TreeViewBean();
                                permissionItemTreeViewBean.setId(permissionItem.getId().toString());
                                permissionItemTreeViewBean.setHasChildren(false);
                                permissionItemTreeViewBean.setClasses("");

                                StringBuffer permissionItemNodeBuffer = new StringBuffer();
                                permissionItemNodeBuffer.append("<input type='checkbox' name='permissionItemIdList' value='").append(permissionItem.getId()).append("' class='perms'");
                                if (rolePermissionItemIdSet.contains(permissionItem.getId())) {
                                    permissionItemNodeBuffer.append(" checked='checked'");
                                }
                                permissionItemNodeBuffer.append("/>&nbsp;").append(permissionItem.getName());

                                permissionItemTreeViewBean.setText(permissionItemNodeBuffer.toString());

                                permissionItemTreeViewBeanList.add(permissionItemTreeViewBean);
                            }
                        }

                        if (!permissionItemTreeViewBeanList.isEmpty()) {
                            permissionTreeViewBean.setChildren(permissionItemTreeViewBeanList);
                        }
                        permissionTreeViewBeanList.add(permissionTreeViewBean);
                    }
                }
                menuTreeViewBean.setChildren(permissionTreeViewBeanList);
                menuTreeViewBeanList.add(menuTreeViewBean);
            }
        }

        super.writeRs(ServletActionContext.getResponse(), JSONArray.fromObject(menuTreeViewBeanList));
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public PermissionService getPermissionService() {
        return permissionService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public List<Long> getPermissionIdList() {
        return permissionIdList;
    }

    public void setPermissionIdList(List<Long> permissionIdList) {
        this.permissionIdList = permissionIdList;
    }

    public List<Long> getPermissionItemIdList() {
        return permissionItemIdList;
    }

    public void setPermissionItemIdList(List<Long> permissionItemIdList) {
        this.permissionItemIdList = permissionItemIdList;
    }

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    public void setRolePermissionService(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    public void setPermissionItemService(PermissionItemService permissionItemService) {
        this.permissionItemService = permissionItemService;
    }
}
