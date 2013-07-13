package web.service.impl.process;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.process.AmountSettingDao;
import com.lehecai.admin.web.dao.process.WorkProcessUserDao;
import com.lehecai.admin.web.domain.process.AmountSetting;
import com.lehecai.admin.web.domain.process.WorkProcessUser;
import com.lehecai.admin.web.enums.ProcessUserType;
import com.lehecai.admin.web.service.process.WorkProcessUserService;

public class WorkProcessUserServiceImpl implements WorkProcessUserService {

	private WorkProcessUserDao workProcessUserDao;
	private AmountSettingDao amountSettingDao;
	@Override
	public void del(WorkProcessUser workProcessUser) {
		if (workProcessUser.getProcessUserType().getValue() == ProcessUserType.USER.getValue()) {
			List<AmountSetting> amountSettingList = amountSettingDao.list(workProcessUser.getProcessId(), workProcessUser.getTaskId(), workProcessUser.getOperateId(), null);
			for (AmountSetting amountSetting : amountSettingList) {
				amountSettingDao.del(amountSetting);
			}
		}
		workProcessUserDao.del(workProcessUser);
	}

	@Override
	public WorkProcessUser get(Long id) {
		return workProcessUserDao.get(id);
	}

	@Override
	public WorkProcessUser getByItem(String processId, String taskId,
			ProcessUserType processUserType) {
		return workProcessUserDao.getByItem(processId, taskId, processUserType);
	}

	@Override
	public PageBean getPageBean(String processId, String taskId,
			ProcessUserType processUserType, PageBean pageBean) {
		return workProcessUserDao.getPageBean(processId, taskId, processUserType, pageBean);
	}

	@Override
	public List<WorkProcessUser> list(String processId, String taskId,
			ProcessUserType processUserType, PageBean pageBean) {
		return workProcessUserDao.list(processId, taskId, processUserType, pageBean);
	}
	@Override
	public PageBean getPageBean(WorkProcessUser workProcessUser, PageBean pageBean) {
		return workProcessUserDao.getPageBean(workProcessUser, pageBean);
	}

	@Override
	public void manage(WorkProcessUser workProcessUser) {
		workProcessUserDao.manage(workProcessUser);
	}
	
	@Override
	public List<WorkProcessUser> list(WorkProcessUser workProcessUser,
			PageBean pageBean) {
		return workProcessUserDao.list(workProcessUser, pageBean);
	}
	
	@Override
	public List<WorkProcessUser> list(String processId, String taskId,
			ProcessUserType processUserType, Long userId) {
		return workProcessUserDao.list(processId, taskId, processUserType, userId);
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
