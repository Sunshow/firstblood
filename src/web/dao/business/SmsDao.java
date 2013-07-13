package web.dao.business;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.Sms;

public interface SmsDao {
	Sms merge(Sms sms);
	List<Sms> list(String smsTo, Integer status, Date beginDate, Date endDate, PageBean pageBean);
	Sms get(Long ID);
	void del(Sms sms);
	PageBean getPageBean(String smsTo, Integer status, Date beginDate, Date endDate, PageBean pageBean);
}