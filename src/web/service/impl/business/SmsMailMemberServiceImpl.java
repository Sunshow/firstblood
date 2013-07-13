package web.service.impl.business;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.business.SmsMailMemberDao;
import com.lehecai.admin.web.domain.business.SmsMailMember;
import com.lehecai.admin.web.service.business.SmsMailMemberService;

/**
 * 短信邮件会员组业务逻辑层实现类
 * @author yanweijie
 *
 */
public class SmsMailMemberServiceImpl implements SmsMailMemberService {

	private SmsMailMemberDao smsMailMemberDao;
	
	/**
	 * 查询所有短信邮件会员组
	 * @param pageBean 分页对象
	 * @param groupId 短信邮件会员组Id
	 */
	@Override
	public List<SmsMailMember> findSmsMailMemberList(PageBean pageBean, Long groupId) {
		return smsMailMemberDao.findSmsMailMemberList(pageBean, groupId);
	}
	
	/**
	 * 根据短信邮件会员编号查询短信邮件会员
	 * @param userId 短信邮件会员编号
	 */
	public List<SmsMailMember> findSmsMailMemberByUid(final Long uid){
		return smsMailMemberDao.findSmsMailMemberByUid(uid);
	}
	
	/**
	 * 根据短信邮件会员用户名查询短信邮件会员
	 * @param userName 短信邮件会员用户名
	 */
	public SmsMailMember findSmsMailMemberByUserName(String userName, Long groupId) {
		return smsMailMemberDao.findSmsMailMemberByUserName(userName, groupId);
	}
	
	/**
	 * 封装多条件查询分页信息
	 * @param pageBean	分页对象
	 * @param groupId	短信邮件会员组Id
	 * @return
	 */
	public PageBean getPageBean(final PageBean pageBean,Long groupId) {
		return smsMailMemberDao.getPageBean(pageBean, groupId);
	}

	/**
	 * 根据短信邮件会员组编号查询短信邮件会员
	 * @param id 短信邮件会员编号
	 */
	@Override
	public SmsMailMember get(Long id) {
		return smsMailMemberDao.get(id);
	}

	/**
	 * 添加/修改短信邮件会员
	 * @param smsMailMember 短信邮件会员对象
	 */
	@Override
	public void merge(SmsMailMember smsMailMember) {
		smsMailMemberDao.merge(smsMailMember);

	}

	/**
	 * 删除短信邮件会员
	 * @param smsMailMember 短信邮件会员对象
	 */
	@Override
	public void del(SmsMailMember smsMailMember) {
		smsMailMemberDao.del(smsMailMember);

	}
	
	public SmsMailMemberDao getSmsMailMemberDao() {
		return smsMailMemberDao;
	}

	public void setSmsMailMemberDao(SmsMailMemberDao smsMailMemberDao) {
		this.smsMailMemberDao = smsMailMemberDao;
	}
}
