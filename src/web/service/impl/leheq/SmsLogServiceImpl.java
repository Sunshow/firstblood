package web.service.impl.leheq;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.leheq.SmsLogDao;
import com.lehecai.admin.web.domain.leheq.SmsLog;
import com.lehecai.admin.web.service.leheq.SmsLogService;
import com.lehecai.core.YesNoStatus;

/**
 * 短信日志业务逻辑层实现类
 * @author yanweijie
 *
 */
public class SmsLogServiceImpl implements SmsLogService {
	
	private SmsLogDao smsLogDao;

	/**
	 * 条件并分页查询短信日志
	 */
	public List<SmsLog> findSmsLogList(String smsto, Date beginSendTime, Date endSendTime, 
			YesNoStatus result, PageBean pageBean) {
		return smsLogDao.findSmsLogList(smsto, beginSendTime, 
				endSendTime, result, pageBean);
	}

	/**
	 * 查询短信日志详细信息
	 */
	public SmsLog get(Integer id) {
		return smsLogDao.get(id);
	}
	
	/**
	 * 条件并分页查询短信日志分页
	 */
	public PageBean getPageBean(String smsto, Date beginSendTime, Date endSendTime, 
			YesNoStatus result, PageBean pageBean) {
		return smsLogDao.getPageBean(smsto, beginSendTime, 
				endSendTime, result, pageBean);
	}
	
	public SmsLogDao getSmsLogDao() {
		return smsLogDao;
	}

	public void setSmsLogDao(SmsLogDao smsLogDao) {
		this.smsLogDao = smsLogDao;
	}
}
