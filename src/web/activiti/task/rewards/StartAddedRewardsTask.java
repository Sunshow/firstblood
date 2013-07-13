/**
 * 
 */
package web.activiti.task.rewards;

import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import com.lehecai.admin.web.activiti.task.CommonProcessTask;


/**
 * @author qatang
 *
 */
public class StartAddedRewardsTask extends CommonProcessTask {
	
	public ProcessInstance start(Map<String, Object> variables) {
		logger.info("启动addedRewards工作流");
		ProcessInstance processInstance = this.start("addedRewardsProcess", variables);
	    logger.info("完成创建补充派奖工单，启动补充派奖工作流程成功");
	    return processInstance;
	}
	
	public Task getCurrentTask(String processInstanceId) {
		logger.info("根据processInstanceId得到当前任务");
		return taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
	}

}
