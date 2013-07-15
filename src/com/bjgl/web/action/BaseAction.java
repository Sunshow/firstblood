package com.bjgl.web.action;


import com.bjgl.web.bean.PageBean;
import com.bjgl.web.exception.ParameterValidateException;
import com.bjgl.web.exception.StrutsRequestException;
import com.opensymphony.xwork2.ActionSupport;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class BaseAction extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 2489105406760450598L;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    protected final static String INDEX = "index";
    protected final static String INPUT = "input";
    protected final static String HANDLE = "handle";
    protected final static String FORWARD = "forward";
    protected final static String MAIN = "main";
    protected final static String LIST = "list";
    protected final static String VIEW = "view";
    protected final static String FAILURE = "failure";

    private String action = HANDLE;
    private String forwardUrl = "";
    private String errorMessage;
    private String successMessage;
    @SuppressWarnings("unchecked")
    private Map session;

    private PageBean pageBean;
    private String pageString;
    private String simplePageString; //不带form的pageString,为支持多个分页标签使用

    protected String executeMethod(String method) throws Exception {
        logger.info("1 Enter Class : " + this.getClass());
        logger.info("2 Execute Method : " + method);
        Class<?>[] c = null;
        Method m = this.getClass().getMethod(method, c);
        Object[] o = null;
        String result = null;
        try {
            result = (String) m.invoke(this, o);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof StrutsRequestException) {
                StrutsRequestException ex = (StrutsRequestException)e.getTargetException();
                logger.error("Struts Request Error", ex);
                this.addActionError(this.getText("error.message"));
                this.setErrorMessage(ex.getMessage());
                if (StringUtils.isBlank(ex.getForward())) {
                    result = ERROR;
                } else {
                    result = ex.getForward();
                }
            } else {
                logger.error(e.getMessage(), e);
                this.addActionError(this.getText("error.message"));
                this.setErrorMessage(e.getTargetException().getMessage());
                result = ERROR;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            this.addActionError(this.getText("error.message"));
            this.setErrorMessage(e.getMessage());
            result = ERROR;
        }
        logger.info("3 Return Result:" + result);
        logger.info("4 Exit Class : " + this.getClass());
        return result;
    }

    public String execute() {
        try {
            return this.executeMethod(this.getAction());
        } catch (Throwable e) {
            logger.error("ActionError", e);
            this.addActionError(this.getText("error.message"));
            return ERROR;
        }
    }

    protected void emptyCheck(Object o, String forward, String message) {
        if (o == null) {
            throw new ParameterValidateException(message, forward);
        }
        if (o instanceof String) {
            if (StringUtils.isBlank((String)o)) {
                throw new ParameterValidateException(message, forward);
            }
        }
    }

    protected void errorForward(String forward, String message) {
        throw new StrutsRequestException(message, forward);
    }

    protected String getContextPath() {
        HttpServletRequest request = ServletActionContext.getRequest();
        return request.getContextPath();
    }

    protected String getContextURI() {
        HttpServletRequest request = ServletActionContext.getRequest();
        return StringUtils.substringAfter(request.getRequestURI(), this.getContextPath());
    }

    public void writeRs(HttpServletResponse response, JSONObject rs) {
        writeRs(response, rs.toString());
    }

    public void writeRs(HttpServletResponse response, JSONArray rs) {
        writeRs(response, rs.toString());
    }

    public void writeRs(HttpServletResponse response, String rs) {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/html;charset=utf-8");
        try {
            response.getWriter().print(rs.toString());
            response.getWriter().close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getForwardUrl() {
        return forwardUrl;
    }

    public void setForwardUrl(String forwardUrl) {
        this.forwardUrl = forwardUrl;
    }

    @SuppressWarnings("unchecked")
    public Map getSession() {
        return session;
    }

    @SuppressWarnings("unchecked")
    public void setSession(Map session) {
        this.session = session;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public PageBean getPageBean() {
        if (pageBean == null) {
            pageBean = new PageBean();
        }
        return pageBean;
    }

    public void setPageBean(PageBean pageBean) {
        this.pageBean = pageBean;
    }

    public String getPageString() {
        return pageString;
    }

    public void setPageString(String pageString) {
        this.pageString = pageString;
    }

    public String getSimplePageString() {
        return simplePageString;
    }

    public void setSimplePageString(String simplePageString) {
        this.simplePageString = simplePageString;
    }

}
