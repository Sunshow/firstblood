/**
 * 
 */
package web.activiti.task.giftcards;

import java.util.Map;

import com.lehecai.admin.web.activiti.task.CommonProcessTask;


/**
 * @author chirowong
 *
 */
public class StartGiftCardsTask extends CommonProcessTask {
	
	public void start(Map<String, Object> variables) {
		logger.info("启动giftCardsProcess工作流");
	    this.start("giftCardsProcess", variables);
	    logger.info("完成提交彩金卡申请工单，启动彩金卡申请工作流程成功");
	}

}
