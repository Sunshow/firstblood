package web.service.multiconfirm;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.multiconfirm.MulticonfirmConfig;
import com.lehecai.admin.web.multiconfirm.MulticonfirmConfigType;
import com.lehecai.admin.web.multiconfirm.MulticonfirmRecord;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTask;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTaskStatus;

/**
 * 多次确认Service
 * @author liurd
 *
 */
public interface MulticonfirmService {

	/**
	 * 得到配置信息
	 * @param id
	 * @return
	 */
	MulticonfirmConfig getConfig(Long id);

	/**
	 * 得到配置信息
	 * @param configKey
	 * @return
	 */
	MulticonfirmConfig getConfig(String configKey);

	/**
	 * 添加更新配置信息
	 * @param multiconfirmConfig
	 * @return
	 */
	MulticonfirmConfig manageConfig(MulticonfirmConfig multiconfirmConfig);

	/**
	 * 查询配置列表
	 * @param id
	 * @param configKey
	 * @param configName
	 * @param mct 
	 * @param createTimeTo 
	 * @param createTimeFrom 
	 * @param pageBean
	 * @return
	 */
	List<MulticonfirmConfig> getConfigList(Long id, String configKey, String configName, MulticonfirmConfigType mct, Date createTimeFrom, Date createTimeTo, PageBean pageBean);

	/**
	 * 删除配置信息
	 * @param multiconfirmConfig
	 */
	void delConfig(MulticonfirmConfig multiconfirmConfig);

	/***
	 * 分页信息
	 * @param id
	 * @param configKey
	 * @param configName
	 * @param mct 
	 * @param createTimeTo 
	 * @param createTimeFrom 
	 * @param pageBean
	 * @return
	 */
	PageBean getConfigPageBean(Long id, String configKey, String configName, MulticonfirmConfigType mct, Date createTimeFrom, Date createTimeTo, PageBean pageBean);

	/**
	 * 得到任务详细信息
	 * @param taskKey
	 * @param open 
	 * @return
	 */
	MulticonfirmTask getTask(String taskKey, MulticonfirmTaskStatus taskStatus);

	/**
	 * 更新任务状态
	 * @param task
	 * @param taskStatus
	 */
	void manageStatus(MulticonfirmTask task, MulticonfirmTaskStatus taskStatus);

	/**
	 * 创建任务
	 * @param taskKey
	 * @param multiconfirmConfig
	 * @return
	 */
	MulticonfirmTask manageTask(String taskKey, MulticonfirmConfig multiconfirmConfig);

	/**
	 * 添加记录
	 * @param task
	 * @param result
	 * @param userId
	 */
	MulticonfirmRecord manageRecord(MulticonfirmTask task, String result, Long userId);

	/**
	 * 查询任务列表
	 * @param id
	 * @param configId
	 * @param taskKey
	 * @param taskStatus
	 * @param updateTimeTo 
	 * @param updateTimeFrom 
	 * @param createTimeTo 
	 * @param createTimeFrom 
	 * @param pageBean
	 * @return
	 */
	List<MulticonfirmTask> getTaskList(Long id, Long configId, String taskKey, MulticonfirmTaskStatus taskStatus, Date createTimeFrom, Date createTimeTo, Date timeoutTimeFrom, Date timeoutTimeTo, PageBean pageBean);

	/**
	 * 分页信息
	 * @param id
	 * @param configId
	 * @param taskKey
	 * @param taskStatus
	 * @param updateTimeTo 
	 * @param updateTimeFrom 
	 * @param createTimeTo 
	 * @param createTimeFrom 
	 * @param pageBean
	 * @return
	 */
	PageBean getTaskPageBean(Long id, Long configId, String taskKey, MulticonfirmTaskStatus taskStatus, Date createTimeFrom, Date createTimeTo, Date timeoutTimeFrom, Date timeoutTimeTo, PageBean pageBean);

	/**
	 * 得到任务
	 * @param id
	 * @return
	 */
	MulticonfirmTask getTask(Long id);

	/**
	 * 查询记录列表
	 * @param task
	 * @param pageBean 
	 * @return
	 */
	List<MulticonfirmRecord> getRecordList(MulticonfirmTask task, PageBean pageBean);
	
	/**
	 * 记录分页信息
	 * @param task
	 * @param pageBean 
	 * @return
	 */
	PageBean getRecordPageBean(MulticonfirmTask task, PageBean pageBean);

	/**
	 * 任务是否超时
	 * @param task
	 * @return
	 */
	boolean auditTimeout(MulticonfirmTask task);

	/**
	 * 任务是否到达确认条件
	 * @param task
	 * @param message 
	 * @return
	 */
	boolean auditConfirm(MulticonfirmTask task, StringBuffer message);

	/**
	 * 更新task
	 * @param task
	 * @return
	 */
	MulticonfirmTask manageTask(MulticonfirmTask task);

}
