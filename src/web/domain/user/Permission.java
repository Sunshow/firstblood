package web.domain.user;

import java.io.Serializable;
import java.util.List;

public class Permission implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6100412628585480536L;
	private Long id;
	private String name;
	private String url;
	private Long menuID;
	private Integer orderView;
	private String actionName;
	private String paramName;
	private String paramValue;
	private boolean valid;
	private String memo;
	private boolean menuItem;
	private List<String> permissionItemStr;
	
	public Long getId() {
		return id;
	}
	public void setId(Long iD) {
		id = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Long getMenuID() {
		return menuID;
	}
	public void setMenuID(Long menuID) {
		this.menuID = menuID;
	}
	public Integer getOrderView() {
		if(orderView == null){
			orderView = 0;
		}
		return orderView;
	}
	public void setOrderView(Integer orderView) {
		this.orderView = orderView;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	public boolean isMenuItem() {
		return menuItem;
	}
	public void setMenuItem(boolean menuItem) {
		this.menuItem = menuItem;
	}
	public List<String> getPermissionItemStr() {
		return permissionItemStr;
	}
	public void setPermissionItemStr(List<String> permissionItemStr) {
		this.permissionItemStr = permissionItemStr;
	}
}
