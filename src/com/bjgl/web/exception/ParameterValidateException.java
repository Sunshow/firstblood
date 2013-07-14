package com.bjgl.web.exception;

public class ParameterValidateException extends StrutsRequestException {

    private static final long serialVersionUID = 4677927674758190477L;

    public ParameterValidateException(String message) {
		this(message, null);
	}

    public ParameterValidateException(String message, String forward) {
        super(message, forward);
    }

	public ParameterValidateException() {
		this("参数验证出错");
	}
}
