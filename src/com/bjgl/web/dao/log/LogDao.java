package com.bjgl.web.dao.log;

import java.util.Date;
import java.util.List;

import com.bjgl.web.bean.PageBean;
import com.bjgl.web.entity.log.Log;

public interface LogDao {
	List<Log> list(String userName,String name,Date beginDate,Date endDate,Long logType, String url, String actionName, String params, String ip, PageBean pageBean);
	PageBean getPageBean(String userName,String name,Date beginDate,Date endDate,Long logType, String url, String actionName, String params, String ip, PageBean pageBean);
	Log get(Long ID);
	void save(Log log);
}
