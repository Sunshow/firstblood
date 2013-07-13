package com.bjgl.web.bean;

import java.io.Serializable;

/**
 * 结果封装对象
 * @author leiming
 *
 */
public class ResultBean implements Serializable{
	private static final long serialVersionUID = -3002848523297074065L;
	
	private boolean result;
	private Integer code;
	private String message;
	private String data;
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	
	
}
