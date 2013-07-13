package web.service.impl.process;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.process.AmountSettingDao;
import com.lehecai.admin.web.dao.process.WorkProcessUserDao;
import com.lehecai.admin.web.dao.process.WorkTaskDao;
import com.lehecai.admin.web.domain.process.AmountSetting;
import com.lehecai.admin.web.domain.process.WorkProcessUser;
import com.lehecai.admin.web.domain.process.WorkTask;
import com.lehecai.admin.web.enums.ProcessUserType;
import com.lehecai.admin.web.service.process.WorkTaskService;

public class WorkTaskServiceImpl implements WorkTaskService {

	private WorkTaskDao workTaskDao;
	private WorkProcessUserDao workProcessUserDao;
	private AmountSettingDao amountSettingDao;
	@Override
	public void del(WorkTask workTask) {
		PageBean pageBean = new PageBean();
		pageBean.setPageFlag(false);
		List<WorkProcessUser> userList = workProcessUserDao.list(workTask.getProcessId(), workTask.getTaskId(), null, pageBean);
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
		workTaskDao.del(workTask);
	}

	@Override
	public WorkTask get(Long id) {
		return workTaskDao.get(id);
	}

	@Override
	public WorkTask getByTaskIdAndProcessId(String taskId, String processId) {
		return workTaskDao.getByTaskId(taskId, processId);
	}

	@Override
	public PageBean getPageBean(String processId, String taskId,
			PageBean pageBean) {
		return workTaskDao.getPageBean(processId, taskId, pageBean);
	}

	@Override
	public List<WorkTask> list(String processId, String taskId,
			PageBean pageBean) {
		return workTaskDao.list(processId, taskId, pageBean);
	}

	@Override
	public void manage(WorkTask workTask) {
		workTaskDao.manage(workTask);
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