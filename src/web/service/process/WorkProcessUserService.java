
package web.service.process;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.process.WorkProcessUser;
import com.lehecai.admin.web.enums.ProcessUserType;

public interface WorkProcessUserService {
	
	public List<WorkProcessUser> list(String processId, String taskId, ProcessUserType processUserType, PageBean pageBean);
	public List<WorkProcessUser> list(WorkProcessUser workProcessUser, PageBean pageBean);
	public PageBean getPageBean(String processId, String taskId, ProcessUserType processUserType, PageBean pageBean);
	public PageBean getPageBean(WorkProcessUser workProcessUser, PageBean pageBean);
	public void manage(WorkProcessUser workProcessUser);
	public WorkProcessUser get(Long id);
	public WorkProcessUser getByItem(String processId, String taskId, ProcessUserType processUserType);
	public void del(WorkProcessUser workProcessUser);
	public List<WorkProcessUser> list(String processId, String taskId, ProcessUserType processUserType, Long userId);
	
}
