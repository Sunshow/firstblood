package com.bjgl.web.exception;

public class StrutsRequestException extends RuntimeException {

    private static final long serialVersionUID = -3317543104097388510L;

    private String forward;

    public StrutsRequestException(String message) {
		this(message, null);
	}

    public StrutsRequestException(String message, String forward) {
        super(message);
        this.setForward(forward);
    }

	public StrutsRequestException() {
		this("Struts请求出错");
	}

    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }
}
