package web.service.impl.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.business.MailDao;
import com.lehecai.admin.web.domain.business.Mail;
import com.lehecai.admin.web.service.business.MailService;
import com.lehecai.admin.web.utils.StringUtil;

public class MailServiceImpl implements MailService {
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.MailService#add()
	 */
	private MailDao mailDao;
	
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.MailService#manage(com.lehecai.admin.web.domain.business.Mail)
	 */
	public List<Mail> manage(Mail mail){
		List<Mail> mailList = new ArrayList<Mail>();
		mail.setId(null);//每次创建一条新的邮件
		mail.setValid(true);//预留字段，默认为true
		if(mail.getMailTo().indexOf(",") != -1){
			String[] mails = StringUtil.split(mail.getMailTo(), ',');
			for(int i = 0;i < mails.length;i ++){
				String mailStr = mails[i];
				Mail mailTmp = new Mail();
				mailTmp.setMailTo(mailStr);
				mailTmp.setContent(mail.getContent());
				mailTmp.setMailFrom(mail.getMailFrom());
				mailTmp.setSubject(mail.getSubject());
				mailTmp.setMemo(mail.getMemo());
				mailTmp.setTextType(mail.getTextType());
				mailTmp.setValid(mail.isValid());
				mailTmp.setStatus(mail.getStatus());
				mailList.add(mailDao.merge(mailTmp));
			}
		}else{		
			mailList.add(mailDao.merge(mail));
		}
		
		return mailList;
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.MailService#getMailDao()
	 */
	public MailDao getMailDao() {
		return mailDao;
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.MailService#setMailDao(com.lehecai.admin.web.dao.business.MailDao)
	 */
	public void setMailDao(MailDao mailDao) {
		this.mailDao = mailDao;
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.MailService#list(com.lehecai.admin.web.domain.business.Mail, com.lehecai.admin.web.bean.PageBean)
	 */
	@Override
	public List<Mail> list(String mailTo, String subject, Integer status, Date beginDate, Date endDate, PageBean pageBean) {
		// TODO Auto-generated method stub
		return mailDao.list(mailTo, subject, status, beginDate, endDate, pageBean);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.MailService#get(java.lang.Long)
	 */
	@Override
	public Mail get(Long ID) {
		// TODO Auto-generated method stub
		return mailDao.get(ID);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.MailService#del(com.lehecai.admin.web.domain.business.Mail)
	 */
	@Override
	public void del(Mail mail) {
		// TODO Auto-generated method stub
		mailDao.del(mail);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.MailService#getPageBean(com.lehecai.admin.web.domain.business.Mail, com.lehecai.admin.web.bean.PageBean)
	 */
	@Override
	public PageBean getPageBean(String mailTo, String subject, Integer status, Date beginDate, Date endDate, PageBean pageBean) {
		// TODO Auto-generated method stub
		return mailDao.getPageBean(mailTo, subject, status, beginDate, endDate, pageBean);
	}

	@Override
	public void update(Mail mail) {
		// TODO Auto-generated method stub
		mailDao.merge(mail);
	}
}
