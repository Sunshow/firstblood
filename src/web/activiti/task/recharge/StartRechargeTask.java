/**
 * 
 */
package web.activiti.task.recharge;

import java.util.Map;

import com.lehecai.admin.web.activiti.task.CommonProcessTask;


/**
 * @author qatang
 *
 */
public class StartRechargeTask extends CommonProcessTask {
	
	public void start(Map<String, Object> variables) {
		logger.info("启动rechargeProcess工作流");
	    this.start("rechargeProcess", variables);
	    logger.info("完成提交汇款充值工单，启动汇款充值工作流程成功");
	}

}
