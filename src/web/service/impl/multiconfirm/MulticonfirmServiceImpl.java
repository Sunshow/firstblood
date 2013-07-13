package web.service.impl.multiconfirm;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.multiconfirm.MulticonfirmConfigDao;
import com.lehecai.admin.web.dao.multiconfirm.MulticonfirmRecordDao;
import com.lehecai.admin.web.dao.multiconfirm.MulticonfirmTaskDao;
import com.lehecai.admin.web.multiconfirm.MulticonfirmConfig;
import com.lehecai.admin.web.multiconfirm.MulticonfirmConfigType;
import com.lehecai.admin.web.multiconfirm.MulticonfirmRecord;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTask;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTaskStatus;
import com.lehecai.admin.web.service.multiconfirm.MulticonfirmService;

public class MulticonfirmServiceImpl implements MulticonfirmService{

	private MulticonfirmConfigDao multiconfirmConfigDao;
	private MulticonfirmTaskDao multiconfirmTaskDao;
	private MulticonfirmRecordDao multiconfirmRecordDao;
	
	@Override
	public MulticonfirmConfig getConfig(String configKey) {
		return multiconfirmConfigDao.get(configKey);
	}
	
	@Override
	public MulticonfirmConfig getConfig(Long id) {
		return multiconfirmConfigDao.get(id);
	}

	@Override
	public MulticonfirmConfig manageConfig(MulticonfirmConfig multiconfirmConfig) {
		return multiconfirmConfigDao.manageConfig(multiconfirmConfig);
	}

	@Override
	public List<MulticonfirmConfig> getConfigList(Long id, String configKey,
			String configName, MulticonfirmConfigType mct, Date createTimeFrom, Date createTimeTo, PageBean pageBean) {
		return multiconfirmConfigDao.getConfigList(id, configKey, configName, mct, createTimeFrom, createTimeTo, pageBean);
	}

	@Override
	public void delConfig(MulticonfirmConfig multiconfirmConfig) {
		multiconfirmConfigDao.del(multiconfirmConfig);
	}

	@Override
	public PageBean getConfigPageBean(Long id, String configKey,
			String configName, MulticonfirmConfigType mct, Date createTimeFrom, Date createTimeTo, PageBean pageBean) {
		return multiconfirmConfigDao.getConfigPageBean(id, configKey, configName, mct, createTimeFrom, createTimeTo, pageBean);
	}

	@Override
	public MulticonfirmTask getTask(String taskKey, MulticonfirmTaskStatus taskStatus) {
		return multiconfirmTaskDao.getTask(taskKey, taskStatus);
	}

	@Override
	public void manageStatus(MulticonfirmTask task, MulticonfirmTaskStatus taskStatus) {
		if (task != null && task.getId() != null && task.getId() != 0) {
			task.setTaskStatus(taskStatus);
			multiconfirmTaskDao.manage(task);
		} else {
			return;
		}
		
	}

	@Override
	public MulticonfirmTask manageTask(String taskKey, MulticonfirmConfig multiconfirmConfig) {
		if (multiconfirmConfig == null || multiconfirmConfig.getId() == null || multiconfirmConfig.getId() == 0) {
			return null;
		}
		multiconfirmConfig = multiconfirmConfigDao.get(multiconfirmConfig.getId());
		MulticonfirmTask task = new MulticonfirmTask();
		Calendar cal = Calendar.getInstance();
		task.setCreateTime(cal.getTime());
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + multiconfirmConfig.getTimeout());
		task.setTimeoutTime(cal.getTime());
		task.setConfigConfirmCount(multiconfirmConfig.getConfirmCount());
		task.setConfigId(multiconfirmConfig.getId());
		task.setTaskKey(taskKey);
		task.setTaskStatus(MulticonfirmTaskStatus.OPEN);
		return multiconfirmTaskDao.manage(task);
	}
	
	@Override
	public MulticonfirmTask manageTask(MulticonfirmTask task) {
		if (task == null || task.getId() == null || task.getId() == 0) {
			return null;
		}
		return multiconfirmTaskDao.manage(task);
	}
	
	@Override
	public List<MulticonfirmTask> getTaskList(Long id, Long configId, String taskKey,
			MulticonfirmTaskStatus taskStatus, Date createTimeFrom, Date createTimeTo,
			Date timeoutTimeFrom, Date timeoutTimeTo, PageBean pageBean) {
		return multiconfirmTaskDao.getTaskList(id, configId, taskKey, taskStatus, createTimeFrom, createTimeTo, timeoutTimeFrom, timeoutTimeTo, pageBean);
	}

	@Override
	public PageBean getTaskPageBean(Long id, Long configId, String taskKey,
			MulticonfirmTaskStatus taskStatus, Date createTimeFrom, Date createTimeTo,
			Date timeoutTimeFrom, Date timeoutTimeTo, PageBean pageBean) {
		return multiconfirmTaskDao.getTaskPageBean(id, configId, taskKey, taskStatus, createTimeFrom, createTimeTo, timeoutTimeFrom, timeoutTimeTo, pageBean);
	}

	@Override
	public MulticonfirmTask getTask(Long id) {
		return multiconfirmTaskDao.getTask(id);
	}
	
	@Override
	public boolean auditTimeout(MulticonfirmTask task) {
		if (task == null || task.getTimeoutTime() == null) {
			return true;
		}
		Calendar cal = Calendar.getInstance();
		Calendar calTimeout = Calendar.getInstance();
		calTimeout.setTime(task.getTimeoutTime());
		if (cal.before(calTimeout)) {
			return false;
		} else {
			if (task.getTaskStatus().getValue() == MulticonfirmTaskStatus.OPEN.getValue()) {
				task.setTaskStatus(MulticonfirmTaskStatus.TIMEOUT);
				multiconfirmTaskDao.manage(task);
			}
			return true;
		}
	}

	@Override
	public boolean auditConfirm(MulticonfirmTask task, StringBuffer sb) {
		if (task == null || task.getId() == null || task.getId() == 0) {
			return false;
		}
		if (sb == null) {
			sb = new StringBuffer();
		}
		task = multiconfirmTaskDao.getTask(task.getId());
		List<MulticonfirmRecord> list = multiconfirmRecordDao.getRecordList(task, null);
		if (list != null && list.size() >= task.getConfigConfirmCount()) {
			return true;
		} else {
			sb.append("当前任务未达到验证条件,进度").append(list.size()).append("/").append(task.getConfigConfirmCount());
			return false;
		}
	}

	@Override
	public MulticonfirmRecord manageRecord(MulticonfirmTask task, String result, Long userId) {
		MulticonfirmRecord record = multiconfirmRecordDao.get(task, userId);
		if (record != null && record.getId() != null && record.getId() != 0) {
			record.setResult(result);
			record.setUpdateTime(new Date());
		} else {
			record = new MulticonfirmRecord();
			record.setCreateTime(new Date());
			record.setResult(result);
			record.setTaskId(task.getId());
			record.setUpdateTime(new Date());
			record.setUserId(userId);
		}
		return multiconfirmRecordDao.manage(record);
	}

	@Override
	public List<MulticonfirmRecord> getRecordList(MulticonfirmTask task, PageBean pageBean) {
		return multiconfirmRecordDao.getRecordList(task, pageBean);
	}
	
	@Override
	public PageBean getRecordPageBean(MulticonfirmTask task, PageBean pageBean) {
		return multiconfirmRecordDao.getRecordPageBean(task, pageBean);
	}

	public MulticonfirmConfigDao getMulticonfirmConfigDao() {
		return multiconfirmConfigDao;
	}

	public void setMulticonfirmConfigDao(MulticonfirmConfigDao multiconfirmConfigDao) {
		this.multiconfirmConfigDao = multiconfirmConfigDao;
	}

	public MulticonfirmTaskDao getMulticonfirmTaskDao() {
		return multiconfirmTaskDao;
	}

	public void setMulticonfirmTaskDao(MulticonfirmTaskDao multiconfirmTaskDao) {
		this.multiconfirmTaskDao = multiconfirmTaskDao;
	}

	public MulticonfirmRecordDao getMulticonfirmRecordDao() {
		return multiconfirmRecordDao;
	}

	public void setMulticonfirmRecordDao(MulticonfirmRecordDao multiconfirmRecordDao) {
		this.multiconfirmRecordDao = multiconfirmRecordDao;
	}

}
