package web.bean;

import java.io.Serializable;
import java.util.List;

import com.lehecai.admin.web.domain.user.Menu;
import com.lehecai.admin.web.domain.user.Permission;

public class MenuBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3764402465865930790L;

	private Menu menu;
	private List<Permission> permissions;
	
	public List<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	public Menu getMenu() {
		return menu;
	}
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
}
