/**
 * 
 */
package web.activiti.listener;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.DelegateTask;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.process.WorkProcessUser;
import com.lehecai.admin.web.enums.ProcessUserType;
import com.lehecai.admin.web.service.process.WorkProcessUserService;

/**
 * @author qatang
 *
 */
public class ProcessTaskListener extends AbstractTaskListener {
	@Autowired
	private WorkProcessUserService workProcessUserService;
	
	@Override
	protected void doNotify(DelegateTask delegateTask) {
		String processId = StringUtils.split(delegateTask.getProcessDefinitionId(), ":")[0];
		PageBean pageBean = new PageBean();
		pageBean.setPageFlag(false);
		
		List<WorkProcessUser> roleList = workProcessUserService.list(processId, delegateTask.getTaskDefinitionKey(), ProcessUserType.ROLE, pageBean);
		List<String> roles = new ArrayList<String>();
		for (WorkProcessUser r : roleList) {
			roles.add(r.getOperateId() + "");
		}
		delegateTask.addCandidateGroups(roles);
		
		List<WorkProcessUser> userList = workProcessUserService.list(processId, delegateTask.getTaskDefinitionKey(), ProcessUserType.USER, pageBean);
		List<String> users = new ArrayList<String>();
		for (WorkProcessUser u : userList) {
			users.add(u.getOperateId() + "");
		}
		delegateTask.addCandidateUsers(users);
	}
	
}
