package com.bjgl.web.service.impl.log;

import java.util.Date;
import java.util.List;

import com.bjgl.web.bean.PageBean;
import com.bjgl.web.dao.log.LogDao;
import com.bjgl.web.entity.log.Log;
import com.bjgl.web.service.log.LogService;


public class LogServiceImpl implements LogService {
	private LogDao logDao;
	@Override
	public Log get(Long ID) {
		// TODO Auto-generated method stub
		return logDao.get(ID);
	}

	@Override
	public List<Log> list(String userName, String name, Date beginDate,
			Date endDate, Long logType, String url, String actionName, String params, String ip, PageBean pageBean) {
		// TODO Auto-generated method stub
		return logDao.list(userName, name, beginDate, endDate, logType, url, actionName, params, ip, pageBean);
	}

	public LogDao getLogDao() {
		return logDao;
	}

	public void setLogDao(LogDao logDao) {
		this.logDao = logDao;
	}

	@Override
	public void save(Log log) {
		// TODO Auto-generated method stub
		logDao.save(log);
	}

	@Override
	public PageBean getPageBean(String userName, String name, Date beginDate,
			Date endDate, Long logType, String url, String actionName, String params, String ip, PageBean pageBean) {
		// TODO Auto-generated method stub
		return logDao.getPageBean(userName, name, beginDate, endDate, logType, url, actionName, params, ip, pageBean);
	}
}
