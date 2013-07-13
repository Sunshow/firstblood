package web.domain.statics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StaticCacheLayout implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private Integer theLevel;
	private Long parentId;
	private List<StaticCacheLayout> children;
	private Integer orderView;
	private boolean valid;
	private String memo;
	private List<StaticCache> items;
	
	
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
	public Integer getTheLevel() {
		return theLevel;
	}
	public void setTheLevel(Integer theLevel) {
		this.theLevel = theLevel;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
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
	public List<StaticCacheLayout> getChildren() {
		if(children == null){
			children = new ArrayList<StaticCacheLayout>();
		}
		return children;
	}
	public void setChildren(List<StaticCacheLayout> children) {
		this.children = children;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<StaticCache> getItems() {
		if (items == null) {
			items = new ArrayList<StaticCache>();
		}
		return items;
	}
	public void setItems(List<StaticCache> items) {
		this.items = items;
	}
}
