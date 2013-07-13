package web.multiconfirm.param;

import java.util.Map;

import com.lehecai.admin.web.multiconfirm.MulticonfirmConfig;
import com.lehecai.admin.web.multiconfirm.MulticonfirmRecord;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTask;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTaskStatus;

public interface IMulticonfirmParam {
	/**
	 * 通过配置信息,参数列表,任务状态获取任务信息
	 * @param multiconfirmConfig
	 * @param paramMap
	 * @param status
	 * @param sb
	 * @return
	 */
	public MulticonfirmTask getTask(MulticonfirmConfig multiconfirmConfig, Map<?, ?> paramMap, MulticonfirmTaskStatus status, StringBuffer sb);
	/**
	 * 通过参数列表和任务具体信息,添加结果到库
	 * @param task
	 * @param paramMap
	 * @param userId
	 * @return
	 */
	public MulticonfirmRecord manageRecord(MulticonfirmTask task, Map<?, ?> paramMap, Long userId);
}
