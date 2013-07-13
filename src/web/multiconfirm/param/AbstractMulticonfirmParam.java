package web.multiconfirm.param;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.lehecai.admin.web.multiconfirm.MulticonfirmConfig;
import com.lehecai.admin.web.multiconfirm.MulticonfirmRecord;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTask;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTaskStatus;
import com.lehecai.admin.web.service.multiconfirm.MulticonfirmService;
import com.lehecai.core.util.CoreDateUtils;

public abstract class AbstractMulticonfirmParam implements IMulticonfirmParam{
	
	private MulticonfirmService multiconfirmService;
	
	/**
	 * 根据配置信息和参数列表回去任务KEY
	 * @param multiconfirmConfig
	 * @param paramMap
	 * @return
	 */
	protected abstract String getTaskKey(MulticonfirmConfig multiconfirmConfig, Map<?, ?> paramMap);
	/**
	 * 根据参数列表生成具体的入库的结果
	 * @param map
	 * @return
	 */
	protected abstract String getResult(Map<?, ?> map);
	
	@Override
	public MulticonfirmTask getTask(MulticonfirmConfig multiconfirmConfig,
			Map<?, ?> paramMap, MulticonfirmTaskStatus status, StringBuffer sb) {
		String taskKey = getTaskKey(multiconfirmConfig, paramMap);
		MulticonfirmTask task = multiconfirmService.getTask(taskKey, status);
		if (task != null) {
			if(multiconfirmService.auditTimeout(task)) {
				sb.append("任务超时").append("<br />");
				task = null;
			}
		}
		
		if (task == null) {
			task = multiconfirmService.manageTask(taskKey, multiconfirmConfig);
			sb.append("创建新任务");
		} 
		sb.append("<br />任务id：").append(task.getId()).append("<br />任务Key：").append(task.getTaskKey())
			.append("<br />创建时间：").append(CoreDateUtils.formatDate(task.getCreateTime(), CoreDateUtils.DATETIME))
			.append("<br />超时时间：").append(CoreDateUtils.formatDate(task.getTimeoutTime(), CoreDateUtils.DATETIME))
			.append("<br />多人确认次数：").append(task.getConfigConfirmCount())
			.append("<br />");
		return task;
	}
	
	@Override
	public MulticonfirmRecord manageRecord(MulticonfirmTask task, Map<?, ?> paramMap, Long userId) {
		String result = getResult(paramMap);
		if (StringUtils.isEmpty(result)) {
			return null;
		}
		return multiconfirmService.manageRecord(task, result, userId);
	}

	public MulticonfirmService getMulticonfirmService() {
		return multiconfirmService;
	}

	public void setMulticonfirmService(MulticonfirmService multiconfirmService) {
		this.multiconfirmService = multiconfirmService;
	}

}
