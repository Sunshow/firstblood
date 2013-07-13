package web.service.customconfig;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.customconfig.PayType;

public interface PayTypeService {
	public PayType get(Integer ID);
	public void manage(PayType payType);
	public void del(PayType payType);
	public PayType update(PayType payType);
	public List<PayType> list(PayType payType, PageBean pageBean);
	public PageBean getPageBean(PayType payType, PageBean pageBean);
}
