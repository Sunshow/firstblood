package web.service.business;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.Sms;

public interface SmsService {
	List<Sms> manage(Sms sms);
	List<Sms> list(String smsTo, String subject, Integer status, Date beginDate, Date endDate, PageBean pageBean);
	Sms get(Long ID);
	void del(Sms sms);
	PageBean getPageBean(String smsTo, String subject, Integer status, Date beginDate, Date endDate, PageBean pageBean);
	void update(Sms sms);
}