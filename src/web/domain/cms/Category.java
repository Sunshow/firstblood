package web.domain.cms;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lehecai.admin.web.enums.StaticPageType;


public class Category implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7523487116027920700L;
	private Long id;
	private String name;
	private Integer caLevel;
	private Long parentID;
	private String templatePath;
	private String targetPath;
	private StaticPageType staticPageType;
	private Integer orderView;
	private boolean valid;
	private String memo;
	private List<Category> children;
	private Long roleId;
	
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
	public Integer getCaLevel() {
		return caLevel;
	}
	public void setCaLevel(Integer caLevel) {
		this.caLevel = caLevel;
	}
	public Long getParentID() {
		return parentID;
	}
	public void setParentID(Long parentID) {
		this.parentID = parentID;
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
	public List<Category> getChildren() {
		if(children == null){
			children = new ArrayList<Category>();
		}
		return children;
	}
	public void setChildren(List<Category> children) {
		this.children = children;
	}
	public String getTemplatePath() {
		return templatePath;
	}
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}
	public String getTargetPath() {
		return targetPath;
	}
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}
	public StaticPageType getStaticPageType() {
		return staticPageType;
	}
	public void setStaticPageType(StaticPageType staticPageType) {
		this.staticPageType = staticPageType;
	}
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
}
