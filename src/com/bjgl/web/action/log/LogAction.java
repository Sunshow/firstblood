package com.bjgl.web.action.log;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bjgl.web.action.BaseAction;
import com.bjgl.web.bean.PageBean;
import com.bjgl.web.entity.log.Log;
import com.bjgl.web.enums.LogType;
import com.bjgl.web.service.log.LogService;
import com.bjgl.web.utils.PageUtil;

public class LogAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private Logger logger = LoggerFactory.getLogger(LogAction.class);
	
	private LogService logService;
	
	private Log log;
	
	private List<Log> logs;
	
	private String userName;
	private String name;
	private Date beginDate;
	private Date endDate;
	private Long logTypeId;
	private String url;
	private String actionName;
	private String params;
	private String ip;
	
	public String handle() {
		logger.info("进入查询日志信息列表");
		
		return "list";
	}
	
	public String query() {
		logger.info("进入查询日志信息列表");
		HttpServletRequest request = ServletActionContext.getRequest();

		logs = logService.list(userName, name, beginDate, endDate, logTypeId, url, actionName, params, ip,super.getPageBean());
		PageBean pageBean = logService.getPageBean(userName, name, beginDate, endDate, logTypeId, url, actionName, params, ip,super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		return "list";
	}
	
	public String view(){
		if(log != null && log.getId() != null){
			log = logService.get(log.getId());
		}else{
			return "failure";
		}
		return "view";
	}
	public LogService getLogService() {
		return logService;
	}
	public void setLogService(LogService logService) {
		this.logService = logService;
	}
	public Log getLog() {
		return log;
	}
	public void setLog(Log log) {
		this.log = log;
	}
	public List<Log> getLogs() {
		return logs;
	}
	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Long getLogTypeId() {
		return logTypeId;
	}
	public void setLogTypeId(Long logTypeId) {
		this.logTypeId = logTypeId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public List<LogType> getLogTypes(){
		return LogType.list;
	}
}
