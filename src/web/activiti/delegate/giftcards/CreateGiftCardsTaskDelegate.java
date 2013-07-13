/**
 * 
 */
package web.activiti.delegate.giftcards;

import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.activiti.constant.ProcessStatusType;
import com.lehecai.admin.web.activiti.delegate.AbstractDelegate;
import com.lehecai.admin.web.activiti.entity.GiftCardsTask;
import com.lehecai.admin.web.activiti.form.GiftCardsTaskForm;
import com.lehecai.admin.web.activiti.service.GiftCardsTaskService;

/**
 * @author chirowong
 *
 */
public class CreateGiftCardsTaskDelegate extends AbstractDelegate {
	@Autowired
	private GiftCardsTaskService giftCardsTaskService;
	
	@Override
	protected void doExecution(DelegateExecution execution) throws Exception {
		GiftCardsTaskForm giftCardsTaskForm = (GiftCardsTaskForm)execution.getVariable("giftCardsTaskForm");
		
		GiftCardsTask task = giftCardsTaskForm.getGiftCardsTask();
		task.setCreatedTime(new Date());
		//存储processId目的为history查询能够对应数据
		task.setProcessId(execution.getProcessInstanceId());
		task.setStatus(ProcessStatusType.HANDLE.getValue());
		giftCardsTaskService.create(task);
		execution.setVariable("giftCardsTaskForm", giftCardsTaskForm);
	}
}
