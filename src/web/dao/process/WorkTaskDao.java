package web.dao.process;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.process.WorkTask;

public interface WorkTaskDao {
	public List<WorkTask> list(String processId, String taskId, PageBean pageBean);
	public PageBean getPageBean(String processId, String taskId, PageBean pageBean);
	public void manage(WorkTask workTask);
	public WorkTask get(Long id);
	public WorkTask getByTaskId(String taskId, String processId);
	public void del(WorkTask workTask);
}