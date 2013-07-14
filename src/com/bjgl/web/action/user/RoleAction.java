package com.bjgl.web.action.user;

import com.bjgl.web.action.BaseAction;
import com.bjgl.web.bean.TreeViewBean;
import com.bjgl.web.bean.UserSessionBean;
import com.bjgl.web.constant.Global;
import com.bjgl.web.entity.user.*;
import com.bjgl.web.service.user.*;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;

    private RoleService roleService;

    private RolePermissionService rolePermissionService;

    private MenuService menuService;

	private PermissionService permissionService;
    private PermissionItemService permissionItemService;
	private Role role;
	
	private String func;//用于区分输入的操作
	
	private List<Long> permissions;
	private List<Long> permissionsItem;
	
	private List<Role> roleList;
	
	public String handle(){
		logger.info("进入查询角色");
        roleList = roleService.findByExample(role, null);
		return LIST;
	}
	
	public String manage() {
		logger.info("进入更新角色信息");
		List<Permission> permList = new ArrayList<Permission>();
		List<PermissionItem> permItemList = new ArrayList<PermissionItem>();
		List<RolePermission> rolePermList = new ArrayList<RolePermission>();
		if (role != null) {
			if (role.getName() == null || "".equals(role.getName())) {
				logger.error("角色名称为空");
				super.setErrorMessage("角色名称不能为空");
				return "failure";
			}
			if (permissions != null && permissions.size() != 0) {

                // 一次性读出所有权限
                List<Permission> allPermissionList = permissionService.findByExample(null, null);

                Map<Long, Permission> allPermissionMap = new HashMap<Long, Permission>();
                Map<Long, List<Permission>> menuPermissionListMap = new HashMap<Long, List<Permission>>();

                // 转置map
                if (allPermissionList != null) {
                    for (Permission permission : allPermissionList) {
                        allPermissionMap.put(permission.getId(), permission);

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

                Map<Long, PermissionItem> allPermissionItemMap = new HashMap<Long, PermissionItem>();
                Map<Long, List<PermissionItem>> permissionItemListMap = new HashMap<Long, List<PermissionItem>>();

                // 转置map
                if (allPermissionItemList != null) {
                    for (PermissionItem permissionItem : allPermissionItemList) {
                        allPermissionItemMap.put(permissionItem.getId(), permissionItem);

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

				for (Long permissionID:permissions) {
					Permission perm = allPermissionMap.get(permissionID);

					RolePermission rp = new RolePermission();
					rp.setRoleId(role.getId());
					rp.setPermissionId(perm.getId());
					StringBuffer strPermissionItems = new StringBuffer("");
					List<String> list = new ArrayList<String>();
					boolean flag = false;
					if (permissionsItem != null && permissionsItem.size() != 0) {
						List<PermissionItem> pits = permissionItemListMap.get(permissionID);
						if (pits != null && pits.size() != 0) {
                            for (Long permissionItemID : permissionsItem) {
                                for (PermissionItem p : pits) {
                                    if (p.getId().toString().equals(permissionItemID.toString())) {
                                        flag = true;
                                        list.add(permissionItemID.toString());
                                        PermissionItem permItem = allPermissionItemMap.get(permissionItemID);
                                        permItemList.add(permItem);
                                        strPermissionItems.append(permissionItemID).append(",");
                                    }
                                }
                            }
							if (flag) {
								if (strPermissionItems.lastIndexOf(",") != -1) {
									strPermissionItems.deleteCharAt(strPermissionItems.lastIndexOf(","));
								}
								rp.setPermissionItemIds(strPermissionItems.toString());
							}
						}
					}
					rolePermList.add(rp);
					//perm.setPermissionItemStr(list);
					permList.add(perm);
				}
			}
			try {
				permissionService.manage(role,rolePermList);
			} catch (Exception e) {
				logger.error(e.getMessage());
				super.setErrorMessage(e.getMessage());
				return "failure";
			}
			//如果admin修改，其他角色的role，会导致admin的权限错误,如果修改的是自己的权限就更新session，如果修改的为其他角色则不更新
			UserSessionBean userSessionBean = (UserSessionBean)super.getSession().get(Global.USER_SESSION);
            /*
			if (userSessionBean != null && role.getId() != null && userSessionBean.getRole().getId().longValue() == role.getId().longValue()) {
				userSessionBean.setRole(role);
				userSessionBean.setPermissionList(permList);
				userSessionBean.setPermissionItems(permItemList);
			}
			*/
		} else {
			logger.error("添加角色错误，提交表单为空");
			super.setErrorMessage("添加角色错误，提交表单不能为空");
			return "failure";
		}
		super.setForwardUrl("/user/role.do");
		super.setSuccessMessage("编辑角色成功");
		logger.info("更新角色信息结束");
		return "success";
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
					role.setRestriction(true);//设置限制IP
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
		HttpServletResponse response = ServletActionContext.getResponse();
		List<String> perms = new ArrayList<String>();
		List<String> permsItem = new ArrayList<String>();
		StringBuffer strPermissionItems = new StringBuffer("");
		if (role != null && role.getId() != null) {
			List<RolePermission> rps = rolePermissionService.findByRoleId(role.getId());

			for(int i = 0; i < rps.size(); i++){
				perms.add(String.valueOf(rps.get(i).getPermissionId()));
				String permissionItemIds = rps.get(i).getPermissionItemIds();
				if(!StringUtils.isEmpty(permissionItemIds)){
					strPermissionItems.append(permissionItemIds).append(",");
				}
			}
			if (strPermissionItems.lastIndexOf(",") != -1) {						
				strPermissionItems.deleteCharAt(strPermissionItems.lastIndexOf(","));
			}
			if(!StringUtils.isEmpty(strPermissionItems.toString())){
				String[] arrPermsItem = StringUtils.split(strPermissionItems.toString(), ',');
				for(String pi : arrPermsItem){
					permsItem.add(pi);
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

		List<TreeViewBean> list = new ArrayList<TreeViewBean>();
		List<Menu> menuList = menuService.findByExample(null, null);

		if (menuList != null) {
			for (Menu menu: menuList) {
				TreeViewBean treeViewBean = new TreeViewBean();
				treeViewBean.setId(menu.getId().toString());
				treeViewBean.setText(menu.getName());
				treeViewBean.setHasChildren(false);
				treeViewBean.setClasses("");
				
				List<TreeViewBean> list2 = new ArrayList<TreeViewBean>();
				List<Permission> permissionList = menuPermissionListMap.get(menu.getId());

				if (permissionList != null) {
					for (Permission permission: permissionList) {
						TreeViewBean treeViewBean2 = new TreeViewBean();
						treeViewBean2.setId(permission.getId().toString());
						if (perms != null) {
							boolean permsFlag = false;
							for(String permID:perms) {
								if (permission.getId().toString().equals(permID)) {
									treeViewBean2.setText("<input type='checkbox' name='permissions' value='"+ permission.getId().toString() +"' checked='checked' class='perms'>&nbsp;" + permission.getName());
									permsFlag = true;
									break;
								}
							}
							if (!permsFlag) {
								treeViewBean2.setText("<input type='checkbox' name='permissions' value='"+ permission.getId().toString() +"' class='perms'>&nbsp;" + permission.getName());
							}
						} else {				
							treeViewBean2.setText("<input type='checkbox' name='permissions' value='"+ permission.getId().toString() +"' class='perms'>&nbsp;" + permission.getName());
						}
						treeViewBean2.setHasChildren(false);
						treeViewBean2.setClasses("");
						
						List<TreeViewBean> list3 = new ArrayList<TreeViewBean>();
						Permission _queryPermission = new Permission();
						_queryPermission.setId(permission.getId());

						List<PermissionItem> permissionItems = permissionItemListMap.get(permission.getId());

						if (permissionItems != null) {
							for(PermissionItem permissionItem: permissionItems) {
								TreeViewBean treeViewBean3 = new TreeViewBean();
								treeViewBean3.setId(permissionItem.getId().toString());
								if (permsItem != null) {
									boolean permsItemFlag = false;
									for(String permItemID:permsItem) {
										if (permissionItem.getId().toString().equals(permItemID)) {
											treeViewBean3.setText("<input type='checkbox' name='permissionsItem' value='"+ permissionItem.getId().toString() +"' checked='checked' class='perms'>&nbsp;" + permissionItem.getName());
											permsItemFlag = true;
											break;
										}
									}
									if (!permsItemFlag) {
										treeViewBean3.setText("<input type='checkbox' name='permissionsItem' value='"+ permissionItem.getId().toString() +"' class='perms'>&nbsp;" + permissionItem.getName());
									}
								} else {						
									treeViewBean3.setText("<input type='checkbox' name='permissionsItem' value='"+ permissionItem.getId().toString() +"' class='perms'>&nbsp;" + permissionItem.getName());
								}
								treeViewBean3.setHasChildren(false);
								treeViewBean3.setClasses("");
								list3.add(treeViewBean3);
							}
						}
						
						if (list3.size() == 0) {
							list3 = null;
						}
						treeViewBean2.setChildren(list3);
						list2.add(treeViewBean2);
					}
				}
				treeViewBean.setChildren(list2);
				list.add(treeViewBean);
			}
		}
		JSONArray ja = JSONArray.fromObject(list);
		super.writeRs(response, ja);
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

	public List<Long> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Long> permissions) {
		this.permissions = permissions;
	}
	public List<Long> getPermissionsItem() {
		return permissionsItem;
	}
	public void setPermissionsItem(List<Long> permissionsItem) {
		this.permissionsItem = permissionsItem;
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
