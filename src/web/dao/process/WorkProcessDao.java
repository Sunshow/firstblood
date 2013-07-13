package web.dao.process;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.process.WorkProcess;

public interface WorkProcessDao {
	public List<WorkProcess> list(String processId, PageBean pageBean);
	public PageBean getPageBean(String processId, PageBean pageBean);
	public void manage(WorkProcess workProcess);
	public WorkProcess get(Long id);
	public WorkProcess getByProcessId(String processId);
	public void del(WorkProcess workProcess);
}