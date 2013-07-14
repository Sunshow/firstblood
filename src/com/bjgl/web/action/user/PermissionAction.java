package com.bjgl.web.action.user;

import java.util.List;

import com.bjgl.web.action.BaseAction;
import com.bjgl.web.entity.user.Menu;
import com.bjgl.web.entity.user.Permission;
import com.bjgl.web.service.user.MenuService;
import com.bjgl.web.service.user.PermissionService;

public class PermissionAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;

    private MenuService menuService;

	private PermissionService permissionService;
	private Permission permission;
	private Menu menu;
	
	private List<Permission> permissionList;
	
	public String handle(){
		logger.info("进入查询权限列表");
		if (permission != null && permission.getMenuId() != null) {
			menu = menuService.findById(permission.getMenuId());
		}
        permissionList = permissionService.findByExample(permission, null);
		return "list";
	}
	
	public String manage() {
		logger.info("进入更新权限");
		if (permission != null) {
			if (permission.getName() == null || "".equals(permission.getName())) {
				logger.error("权限名称为空");
				super.setErrorMessage("权限名称不能为空");
				return "failure";
			}
			if (permission.getActionName() == null || "".equals(permission.getActionName())) {
				logger.error("权限action名称为空");
				super.setErrorMessage("权限action名称不能为空");
				return "failure";
			}
			permissionService.update(permission);
		} else {
			logger.error("更新权限错误，提交表单为空");
			super.setErrorMessage("更新权限错误，提交的表单不能为空");
			return "failure";
		}
		super.setForwardUrl("/user/permission.do?permission.menuID="+permission.getMenuId());
		logger.info("更新权限结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入权限信息");
		if (permission != null) {
			if (permission.getId() != null) {				
				permission = permissionService.findById(permission.getId());
			} else {
				permission.setValid(true);
				permission.setOrderView(0);
			}
		}
		return "inputForm";
	}
	
	public String view() {
		logger.info("进入查看权限详情");
		if (permission != null && permission.getId() != null) {
			permission = permissionService.findById(permission.getId());
		} else {
			logger.error("查看权限详情，编码为空");
			super.setErrorMessage("查看权限详情，编码不能为空");
			return "failure";
		}
		logger.info("查看权限详情结束");
		return "view";
	}
	
	public String del() {
		logger.info("进入删除权限");
		if (permission != null && permission.getId() != null) {
			permissionService.delete(permission.getId());
		} else {
			logger.error("删除权限，编码为空");
			super.setErrorMessage("删除权限，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/user/permission.do");
		logger.info("删除权限结束");
		return "forward";
	}
	
	
	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }

    public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }
}
