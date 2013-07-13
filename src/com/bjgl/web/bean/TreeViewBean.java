package com.bjgl.web.bean;

import java.io.Serializable;
import java.util.List;

public class TreeViewBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4978471057820080944L;
	
	private String id;
	private String text;
	private boolean hasChildren;	
	private String classes;
	private List<TreeViewBean> children;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isHasChildren() {
		return hasChildren;
	}
	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	public String getClasses() {
		return classes;
	}
	public void setClasses(String classes) {
		this.classes = classes;
	}
	public List<TreeViewBean> getChildren() {
		return children;
	}
	public void setChildren(List<TreeViewBean> children) {
		this.children = children;
	}
}
