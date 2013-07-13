package web.service.impl.business;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.business.ChuangShiManDaoSmsDao;
import com.lehecai.admin.web.domain.business.ChuangShiManDaoSms;
import com.lehecai.admin.web.service.business.ChuangShiManDaoSmsService;

public class ChuangShiManDaoSmsServiceImpl implements ChuangShiManDaoSmsService {

	private ChuangShiManDaoSmsDao chuangShiManDaoSmsDao;
	public void manage(ChuangShiManDaoSms sms){
		chuangShiManDaoSmsDao.merge(sms);
	}

	@Override
	public List<ChuangShiManDaoSms> list(String sender, Date beginDate, Date endDate, PageBean pageBean) {
		return chuangShiManDaoSmsDao.list(sender, beginDate, endDate, pageBean);
	}

	@Override
	public ChuangShiManDaoSms get(Long ID) {
		return chuangShiManDaoSmsDao.get(ID);
	}

	@Override
	public void del(ChuangShiManDaoSms sms) {
		chuangShiManDaoSmsDao.del(sms);
	}

	@Override
	public PageBean getPageBean(String sender, Date beginDate, Date endDate, PageBean pageBean) {
		return chuangShiManDaoSmsDao.getPageBean(sender, beginDate, endDate, pageBean);
	}

	public ChuangShiManDaoSmsDao getChuangShiManDaoSmsDao() {
		return chuangShiManDaoSmsDao;
	}

	public void setChuangShiManDaoSmsDao(ChuangShiManDaoSmsDao chuangShiManDaoSmsDao) {
		this.chuangShiManDaoSmsDao = chuangShiManDaoSmsDao;
	}
}
