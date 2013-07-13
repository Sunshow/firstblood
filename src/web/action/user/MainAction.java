package web.action.user;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.MenuBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.user.Menu;
import com.lehecai.admin.web.domain.user.Permission;

public class MainAction extends BaseAction {
	private static final long serialVersionUID = -8830679912602886965L;
	private Logger logger = LoggerFactory.getLogger(MainAction.class);
	
	private UserSessionBean userSessionBean;
	private List<MenuBean> menus;

	public String handle() {
		userSessionBean = (UserSessionBean)super.getSession().get(Global.USER_SESSION);
		if (userSessionBean == null) {
			logger.error("session丢失");
			super.setErrorMessage("您的session丢失，请重新登录");
			return "index";
		}
		menus = new ArrayList<MenuBean>();
		for (Menu menu : userSessionBean.getMenus()) {
			MenuBean menuBean = new MenuBean();
			menuBean.setMenu(menu);
			List<Permission> permissions = getSelfPermissionItems(menu,userSessionBean.getPermissions());
			menuBean.setPermissions(permissions);
			menus.add(menuBean);
		}
		
		return "login";
	}
	
	public List<Permission> getSelfPermissionItems(Menu menu, List<Permission> permissions) {
		List<Permission> list = new ArrayList<Permission>();
		for (Permission permission : permissions) {
			if (permission.getMenuID().longValue() ==  menu.getId().longValue() && permission.isMenuItem()) {
				list.add(permission);
			}
		}
		return list;
	}
	
	
	public UserSessionBean getUserSessionBean() {
		return userSessionBean;
	}

	public void setUserSessionBean(UserSessionBean userSessionBean) {
		this.userSessionBean = userSessionBean;
	}

	public List<MenuBean> getMenus() {
		return menus;
	}

	public void setMenus(List<MenuBean> menus) {
		this.menus = menus;
	}
}
