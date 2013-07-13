package web.activiti.task.commission;

import java.util.Map;

import com.lehecai.admin.web.activiti.task.CommonProcessTask;

public class StartCommissionTask extends CommonProcessTask {
	
	public void start(Map<String, Object> variables) {
		logger.info("启动commissionTaskProcess工作流");
	    this.start("commissionTaskProcess", variables);
	    logger.info("完成佣金派发工单，启动佣金派发工作流程成功");
	}
}
