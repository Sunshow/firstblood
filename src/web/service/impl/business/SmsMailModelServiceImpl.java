package web.service.impl.business;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.business.SmsMailModelDao;
import com.lehecai.admin.web.domain.business.SmsMailModel;
import com.lehecai.admin.web.service.business.SmsMailModelService;

public class SmsMailModelServiceImpl implements SmsMailModelService {
	private SmsMailModelDao smsMailModelDao;

	

	@Override
	public PageBean getPageBean(String title, String content, Integer modelType, Integer textTypeId,
			Date createTimeFrom, Date createTimeTo, Date updateTimeFrom, Date updateTimeTo, PageBean pageBean) {
		return smsMailModelDao.getPageBean(title, content, modelType, textTypeId, createTimeFrom, createTimeTo, updateTimeFrom, updateTimeTo, pageBean);
	}

	@Override
	public List<SmsMailModel> list(String title, String content, Integer modelType, Integer textTypeId,
			Date createTimeFrom, Date createTimeTo, Date updateTimeFrom, Date updateTimeTo, PageBean pageBean) {
		return smsMailModelDao.list(title, content, modelType, textTypeId, createTimeFrom, createTimeTo, updateTimeFrom, updateTimeTo, pageBean);
	}
	
	@Override
	public SmsMailModel merge(SmsMailModel smsMailModel) {
		return smsMailModelDao.merge(smsMailModel);
	}
	
	@Override
	public SmsMailModel get(Long id) {
		// TODO Auto-generated method stub
		return (SmsMailModel) smsMailModelDao.get(id);
	}
	
	@Override
	public void del(SmsMailModel smsMailModel) {
		smsMailModelDao.del(smsMailModel);
		
	}
	
	public SmsMailModelDao getSmsMailModelDao() {
		return smsMailModelDao;
	}

	public void setSmsMailModelDao(SmsMailModelDao smsMailModelDao) {
		this.smsMailModelDao = smsMailModelDao;
	}

}
