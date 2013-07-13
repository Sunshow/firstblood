package web.dao.business;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.ChuangShiManDaoSms;

public interface ChuangShiManDaoSmsDao {
	ChuangShiManDaoSms merge(ChuangShiManDaoSms sms);
	List<ChuangShiManDaoSms> list(String sender, Date beginDate, Date endDate, PageBean pageBean);
	ChuangShiManDaoSms get(Long ID);
	void del(ChuangShiManDaoSms sms);
	PageBean getPageBean(String sender, Date beginDate, Date endDate, PageBean pageBean);
}