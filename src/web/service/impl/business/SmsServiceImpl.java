package web.service.impl.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.business.SmsDao;
import com.lehecai.admin.web.domain.business.Sms;
import com.lehecai.admin.web.service.business.SmsService;

public class SmsServiceImpl implements SmsService {
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.SMSService#add()
	 */
	private SmsDao smsDao;
	
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.SMSService#manage(com.lehecai.admin.web.domain.business.SMS)
	 */
	public List<Sms> manage(Sms sms){
		List<Sms> smsList = new ArrayList<Sms>();
		sms.setValid(true);//预留字段，默认为true
		
		if(sms.getId() != null){
			sms.setId(null);
		}
		smsList.add(smsDao.merge(sms));
		return smsList;
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.SMSService#list(com.lehecai.admin.web.domain.business.SMS, com.lehecai.admin.web.bean.PageBean)
	 */
	@Override
	public List<Sms> list(String smsTo, String subject, Integer status, Date beginDate, Date endDate, PageBean pageBean) {
		// TODO Auto-generated method stub
		return smsDao.list(smsTo, status, beginDate, endDate, pageBean);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.SMSService#get(java.lang.Long)
	 */
	@Override
	public Sms get(Long ID) {
		// TODO Auto-generated method stub
		return smsDao.get(ID);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.SMSService#del(com.lehecai.admin.web.domain.business.SMS)
	 */
	@Override
	public void del(Sms sms) {
		// TODO Auto-generated method stub
		smsDao.del(sms);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.SMSService#getPageBean(com.lehecai.admin.web.domain.business.SMS, com.lehecai.admin.web.bean.PageBean)
	 */
	@Override
	public PageBean getPageBean(String smsTo, String subject, Integer status, Date beginDate, Date endDate, PageBean pageBean) {
		// TODO Auto-generated method stub
		return smsDao.getPageBean(smsTo, status, beginDate, endDate, pageBean);
	}

	public SmsDao getSmsDao() {
		return smsDao;
	}

	public void setSmsDao(SmsDao smsDao) {
		this.smsDao = smsDao;
	}

	@Override
	public void update(Sms sms) {
		// TODO Auto-generated method stub
		smsDao.merge(sms);
	}
}
