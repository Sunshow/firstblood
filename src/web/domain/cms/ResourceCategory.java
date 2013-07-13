package web.domain.cms;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ResourceCategory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7523487116027920700L;
	private Long id;
	private String name;
	private Integer reLevel;
	private Long parentID;
	private Integer orderView;
	private boolean valid;
	private String memo;
	private List<ResourceCategory> children;
	private String directory;
	
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
	public Integer getReLevel() {
		return reLevel;
	}
	public void setReLevel(Integer reLevel) {
		this.reLevel = reLevel;
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
	public List<ResourceCategory> getChildren() {
		if(children == null){
			children = new ArrayList<ResourceCategory>();
		}
		return children;
	}
	public void setChildren(List<ResourceCategory> children) {
		this.children = children;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public String getDirectory() {
		return directory;
	}
}
