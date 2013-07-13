package web.domain.user;

import java.io.Serializable;

public class Role implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3481413914584370939L;
	private Long id;
	private String name;
	private boolean valid;
	private String memo;
	private String permission;
	private boolean restriction;
	private String restrictionIp;
	
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
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public boolean isRestriction() {
		return restriction;
	}
	public void setRestriction(boolean restriction) {
		this.restriction = restriction;
	}
	public String getRestrictionIp() {
		return restrictionIp;
	}
	public void setRestrictionIp(String restrictionIp) {
		this.restrictionIp = restrictionIp;
	}
	
}
