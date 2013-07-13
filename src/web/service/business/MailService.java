package web.service.business;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.Mail;

public interface MailService {
	List<Mail> manage(Mail mail);
	List<Mail> list(String mailTo, String subject, Integer status, Date beginDate, Date endDate, PageBean pageBean);
	Mail get(Long ID);
	void del(Mail mail);
	PageBean getPageBean(String mailTo, String subject, Integer status, Date beginDate, Date endDate, PageBean pageBean);
	void update(Mail mail);
}