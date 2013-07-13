package web.domain.statics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.enums.ExecuteType;
import com.lehecai.admin.web.enums.TemplateType;

public class StaticCache implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String slug;
	private ExecuteType executeType;//执行策略，1.数据源获取数据组合模板生成，2.直接调用数据源生成,3.直接输入json数据
	private String dataUrl;
	private TemplateType templateType;//模板类型，1.vm文件，2.模板内容
	private String templateUrl;
	private String targetUrl;
	private Date updateTime;
	private Long userID;
	private String userName;
	private Integer stLevel;
	private Long parentID;
	private List<StaticCache> children;
	private Integer orderView;
	private boolean valid;
	private String memo;
	
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
	public ExecuteType getExecuteType() {
		return executeType;
	}
	public void setExecuteType(ExecuteType executeType) {
		this.executeType = executeType;
	}
	public String getDataUrl() {
		return dataUrl;
	}
	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}
	public String getTemplateUrl() {
		return templateUrl;
	}
	public void setTemplateUrl(String templateUrl) {
		this.templateUrl = templateUrl;
	}
	public String getTargetUrl() {
		return targetUrl;
	}
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
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
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Long getUserID() {
		return userID;
	}
	public void setUserID(Long userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	public Integer getStLevel() {
		return stLevel;
	}
	public void setStLevel(Integer stLevel) {
		this.stLevel = stLevel;
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
	public TemplateType getTemplateType() {
		return templateType;
	}
	public void setTemplateType(TemplateType templateType) {
		this.templateType = templateType;
	}
	public List<StaticCache> getChildren() {
		if(children == null){
			children = new ArrayList<StaticCache>();
		}
		return children;
	}
	public void setChildren(List<StaticCache> children) {
		this.children = children;
	}
}
