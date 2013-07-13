package web.service.impl.customconfig;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.customconfig.PayTypeDao;
import com.lehecai.admin.web.domain.customconfig.PayType;
import com.lehecai.admin.web.service.customconfig.PayTypeService;

public class PayTypeServiceImpl implements PayTypeService {
	
	private PayTypeDao payTypeDao;
	
	@Override
	public void del(PayType payType) {
		payTypeDao.del(payType);
	}

	@Override
	public PayType get(Integer ID) {
		return payTypeDao.get(ID);
	}

	@Override
	public void manage(PayType payType) {
		payTypeDao.merge(payType);
	}
	
	@Override
	public PayType update(PayType payType) {
		return payTypeDao.merge(payType);
	}
	
	@Override
	public List<PayType> list(PayType payType,PageBean pageBean){
		return payTypeDao.list(payType, pageBean);
	}
	
	@Override
	public PageBean getPageBean(PayType payType,
			PageBean pageBean) {
		return payTypeDao.getPageBean(payType, pageBean);
	}

	public PayTypeDao getPayTypeDao() {
		return payTypeDao;
	}

	public void setPayTypeDao(
			PayTypeDao payTypeDao) {
		this.payTypeDao = payTypeDao;
	}
}
