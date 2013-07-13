package web.service.impl.process;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.process.AmountSettingDao;
import com.lehecai.admin.web.domain.process.AmountSetting;
import com.lehecai.admin.web.enums.ProcessUserType;
import com.lehecai.admin.web.service.process.AmountSettingService;

public class AmountSettingServiceImpl implements AmountSettingService {
	private final Logger logger = LoggerFactory.getLogger(AmountSettingServiceImpl.class);
	
	private AmountSettingDao amountSettingDao;
	
	@Override
	public Double auditAmount(String processId, String taskId, Long operateId,Long roleId){
		Double amount = null;
		amount = auditUserAmount(processId, taskId, operateId);
		if(amount == null){
			amount = auditRoleAmount(processId, taskId, roleId);
		}
		return amount;
	}
	
	private Double auditUserAmount(String processId, String taskId, Long operateId) {
		Double amount = null;
		if (processId == null || processId.equals("") || taskId == null || taskId.equals("") || operateId == null || operateId == 0) {
			logger.error("查询额度失败，参数不合法");
			return amount;
		}
		amount = amountSettingDao.getOneTimeAmount(processId, taskId, operateId, ProcessUserType.USER);
		List<AmountSetting> amountSettingList = amountSettingDao.list(processId, taskId, operateId, null);
		for (AmountSetting amountSetting : amountSettingList) {
			amountSetting = updateBeginAndEndTime(amountSetting);
			if (amountSetting.getRestAmount() != null) {
				if(amount == null) amount = amountSetting.getRestAmount();
				if (amountSetting.getRestAmount() < amount) {
					amount = amountSetting.getRestAmount();
				}
			}
		}
		return amount;
	}
	
	private Double auditRoleAmount(String processId, String taskId, Long roleId) {
		Double amount = null;
		if (processId == null || processId.equals("") || taskId == null || taskId.equals("") || roleId == null || roleId == 0) {
			logger.error("查询额度失败，参数不合法");
			return amount;
		}
		amount = amountSettingDao.getOneTimeAmount(processId, taskId, roleId, ProcessUserType.ROLE);
		return amount;
	}
	
	@Override
	public void manageAmountAdd(String processId, String taskId, Long operateId, Double amount) {
		if (processId == null || processId.equals("") || taskId == null || taskId.equals("") || operateId == null || operateId == 0) {
			logger.error("增加额度失败，参数不合法");
			return;
		}
		List<AmountSetting> amountSettingList = amountSettingDao.list(processId, taskId, operateId, null);
		for (AmountSetting amountSetting : amountSettingList) {
			amountSetting = updateBeginAndEndTime(amountSetting);
			if (amountSetting != null) {
				BigDecimal bigDecimalAmount = new BigDecimal(amount);
				BigDecimal bigDecimalRestAmount = new BigDecimal(amountSetting.getRestAmount());
				if (bigDecimalRestAmount.add(bigDecimalAmount).doubleValue() > amountSetting.getCycleAmount()) {
					amountSetting.setRestAmount(amountSetting.getCycleAmount());
				} else {
					amountSetting.setRestAmount(bigDecimalRestAmount.add(bigDecimalAmount).doubleValue());
				}
				this.manage(amountSetting);
			}
		}
	}
	
	@Override
	public void manageAmountMinus(String processId, String taskId, Long operateId, Double amount) {
		if (processId == null || processId.equals("") || taskId == null || taskId.equals("") || operateId == null || operateId == 0) {
			logger.error("增加额度失败，参数不合法");
			return;
		}
		List<AmountSetting> amountSettingList = amountSettingDao.list(processId, taskId, operateId, null);
		for (AmountSetting amountSetting : amountSettingList) {
			amountSetting = updateBeginAndEndTime(amountSetting);
			if (amountSetting != null) {
				BigDecimal bigDecimalAmount = new BigDecimal(amount);
				BigDecimal bigDecimalRestAmount = new BigDecimal(amountSetting.getRestAmount());
				if (bigDecimalRestAmount.subtract(bigDecimalAmount).doubleValue() < 0D) {
					amountSetting.setRestAmount(0D);
				} else {
					amountSetting.setRestAmount(bigDecimalRestAmount.subtract(bigDecimalAmount).doubleValue());
				}
				this.manage(amountSetting);
			}
		}
	}

	@Override
	public void del(AmountSetting amountSetting) {
		amountSettingDao.del(amountSetting);
		
	}

	@Override
	public AmountSetting update(Long id) {
		AmountSetting amountSetting = amountSettingDao.get(id);
		amountSetting = updateBeginAndEndTime(amountSetting);
		return amountSetting;
		
	}
	
	private AmountSetting updateBeginAndEndTime(AmountSetting amountSetting){
		if (amountSetting == null) {
			logger.error("额度配置更新开始结束区间，记录为空");
			return null;
		}
		
		Date date = new Date();
		if (amountSetting.getBeginTime().compareTo(date) > 0 || amountSetting.getEndTime().compareTo(date) < 0) {
			amountSetting.setBeginTime(date);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if (amountSetting.getCycleYear() != null && amountSetting.getCycleYear() != 0) {
				cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + amountSetting.getCycleYear());
			}
			if (amountSetting.getCycleMonth() != null && amountSetting.getCycleMonth() != 0) {
				cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + amountSetting.getCycleMonth());
			}
			if (amountSetting.getCycleDay() != null && amountSetting.getCycleDay() != 0) {
				cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + amountSetting.getCycleDay());
			}
			amountSetting.setEndTime(cal.getTime());
			amountSetting.setRestAmount(amountSetting.getCycleAmount());
			this.manage(amountSetting);
		}
		return amountSetting;
	}

	@Override
	public void manage(AmountSetting amountSetting) {
		amountSettingDao.merge(amountSetting);
		
	}

	@Override
	public PageBean getPageBean(String processId, String taskId, Long operateId, PageBean pageBean) {
		return amountSettingDao.getPageBean(processId, taskId, operateId, pageBean);
	}

	@Override
	public List<AmountSetting> updateAndQueryList(String processId, String taskId, Long operateId, PageBean pageBean) {
		List<AmountSetting> amountSettingList = amountSettingDao.list(processId, taskId, operateId, pageBean);
		if(amountSettingList != null && amountSettingList.size() > 0){
			for(AmountSetting amountSetting : amountSettingList){
				updateBeginAndEndTime(amountSetting);
			}
		}
		return amountSettingList;
	}

	public AmountSettingDao getAmountSettingDao() {
		return amountSettingDao;
	}

	public void setAmountSettingDao(AmountSettingDao amountSettingDao) {
		this.amountSettingDao = amountSettingDao;
	}

}
