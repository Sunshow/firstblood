package com.bjgl.web.action.user;

import java.util.List;

import com.bjgl.web.action.BaseAction;
import com.bjgl.web.entity.user.Menu;
import com.bjgl.web.service.user.PermissionService;

public class MenuAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;

	private PermissionService permissionService;
	private Menu menu;
	
	private List<Menu> menus;
	
	public String handle(){
		logger.info("进入查询菜单列表");
		menus = permissionService.listMenus(menu);
		return "list";
	}
	
	public String manage() {
		logger.info("进入更新菜单信息");
		if (menu != null) {
			if (menu.getName() == null || "".equals(menu.getName())) {
				logger.error("菜单名称为空");
				super.setErrorMessage("菜单名称不能为空");
				return "failure";
			}
			permissionService.manage(menu);
		} else {
			logger.error("更新菜单错误，提交表单为空");
			super.setErrorMessage("更新菜单错误，提交表单不能为空");
			return "failure";
		}
		super.setForwardUrl("/user/menu.do");
		logger.info("更新菜单信息结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入菜单信息");
		if (menu != null) {
			if (menu.getId() != null) {			
				menu = permissionService.getMenu(menu.getId());
			}
		} else {
			menu = new Menu();
			menu.setValid(true);
			menu.setOrderView(0);
		}
		return "inputForm";
	}
	
	public String view() {
		logger.info("进入查询菜单详情");
		if (menu != null && menu.getId() != null) {
			menu = permissionService.getMenu(menu.getId());
		} else {
			logger.error("查询菜单详情，编码为空");
			super.setErrorMessage("查询菜单详情，编码为空");
			return "failure";
		}
		logger.info("查询菜单详情结束");
		return "view";
	}
	
	public String del() {
		logger.info("进入删除菜单");
		if (menu != null && menu.getId() != null) {
			menu = permissionService.getMenu(menu.getId());
			permissionService.del(menu);
		} else {
			logger.error("删除菜单，编码为空");
			super.setErrorMessage("删除菜单，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/user/menu.do");
		logger.info("删除菜单结束");
		return "forward";
	}
	
	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}
}
