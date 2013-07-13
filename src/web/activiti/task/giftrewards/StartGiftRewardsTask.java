/**
 * 
 */
package web.activiti.task.giftrewards;

import java.util.Map;

import com.lehecai.admin.web.activiti.task.CommonProcessTask;


/**
 * @author chirowong
 *
 */
public class StartGiftRewardsTask extends CommonProcessTask {
	
	public void start(Map<String, Object> variables) {
		logger.info("启动giftRewardsProcess工作流");
	    this.start("giftRewardsProcess", variables);
	    logger.info("完成提交彩金赠送工单，启动彩金赠送工作流程成功");
	}

}
