package web.service.impl.process;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.process.AmountSettingDao;
import com.lehecai.admin.web.dao.process.WorkProcessDao;
import com.lehecai.admin.web.dao.process.WorkProcessUserDao;
import com.lehecai.admin.web.dao.process.WorkTaskDao;
import com.lehecai.admin.web.domain.process.AmountSetting;
import com.lehecai.admin.web.domain.process.WorkProcess;
import com.lehecai.admin.web.domain.process.WorkProcessUser;
import com.lehecai.admin.web.domain.process.WorkTask;
import com.lehecai.admin.web.enums.ProcessUserType;
import com.lehecai.admin.web.service.process.WorkProcessService;

public class WorkProcessServiceImpl implements WorkProcessService {

	private WorkProcessDao workProcessDao;
	private WorkTaskDao workTaskDao;
	private WorkProcessUserDao workProcessUserDao;
	private AmountSettingDao amountSettingDao;
	@Override
	public void del(WorkProcess workProcess) {
		List<WorkTask> taskList = workTaskDao.list(workProcess.getProcessId(), null, null);
		for (WorkTask w : taskList) {
			if (w != null && w.getId() != null && w.getId() != 0) {
				PageBean pageBean = new PageBean();
				pageBean.setPageFlag(false);
				List<WorkProcessUser> userList = workProcessUserDao.list(w.getProcessId(), w.getTaskId(), null, pageBean);
				for (WorkProcessUser wpu : userList) {
					if (wpu != null && wpu.getId() != null && wpu.getId() != 0) {
						if (wpu.getProcessUserType().getValue() == ProcessUserType.USER.getValue()) {
							List<AmountSetting> amountSettingList = amountSettingDao.list(wpu.getProcessId(), wpu.getTaskId(), wpu.getOperateId(), null);
							for (AmountSetting amountSetting : amountSettingList) {
								amountSettingDao.del(amountSetting);
							}
						}
						workProcessUserDao.del(wpu);
					}
				}
				workTaskDao.del(w);
			}
		}
		workProcessDao.del(workProcess);
	}

	@Override
	public WorkProcess getByProcessId(String processId) {
		return workProcessDao.getByProcessId(processId);
	}

	@Override
	public PageBean getPageBean(String processId, PageBean pageBean) {
		return workProcessDao.getPageBean(processId, pageBean);
	}

	@Override
	public List<WorkProcess> list(String processId, PageBean pageBean) {
		return workProcessDao.list(processId, pageBean);
	}

	@Override
	public void manage(WorkProcess workProcess) {
		workProcessDao.manage(workProcess);
	}

	@Override
	public WorkProcess get(Long id) {
		return workProcessDao.get(id);
	}

	public WorkProcessDao getWorkProcessDao() {
		return workProcessDao;
	}

	public void setWorkProcessDao(WorkProcessDao workProcessDao) {
		this.workProcessDao = workProcessDao;
	}

	public WorkTaskDao getWorkTaskDao() {
		return workTaskDao;
	}

	public void setWorkTaskDao(WorkTaskDao workTaskDao) {
		this.workTaskDao = workTaskDao;
	}

	public WorkProcessUserDao getWorkProcessUserDao() {
		return workProcessUserDao;
	}

	public void setWorkProcessUserDao(WorkProcessUserDao workProcessUserDao) {
		this.workProcessUserDao = workProcessUserDao;
	}

	public AmountSettingDao getAmountSettingDao() {
		return amountSettingDao;
	}

	public void setAmountSettingDao(AmountSettingDao amountSettingDao) {
		this.amountSettingDao = amountSettingDao;
	}

}
