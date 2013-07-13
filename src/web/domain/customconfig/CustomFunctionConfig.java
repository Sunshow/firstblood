/**
 * 
 */
package web.domain.customconfig;

import java.io.Serializable;

/**
 * @author chirowong
 * 用于设置某些功能只能拥有某些权限的用户打开 其他用户无法打开
 */
public class CustomFunctionConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3674309388834960786L;
	private Long id;
	private FunctionType functionType;//功能描述
	private String roles; //角色列表
	private String users; //用户列表
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public FunctionType getFunctionType() {
		return functionType;
	}
	public void setFunctionType(FunctionType functionType) {
		this.functionType = functionType;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getUsers() {
		return users;
	}
	public void setUsers(String users) {
		this.users = users;
	}
}
