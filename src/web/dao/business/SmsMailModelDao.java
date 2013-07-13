package web.dao.business;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.SmsMailModel;

public interface SmsMailModelDao {
	SmsMailModel merge(SmsMailModel smsMailModel);
	List<SmsMailModel> list(String title, String content, Integer modelType, Integer textTypeId, Date createTimeFrom, Date createTimeTo, Date updateTimeFrom, Date updateTimeTo, PageBean pageBean);
	PageBean getPageBean(String title, String content, Integer modelType, Integer textTypeId, Date createTimeFrom, Date createTimeTo, Date updateTimeFrom, Date updateTimeTo, PageBean pageBean);
	SmsMailModel get(Long id);
	void del(SmsMailModel smsMailModel);
}