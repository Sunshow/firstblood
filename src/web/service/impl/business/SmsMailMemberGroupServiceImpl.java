package web.service.impl.business;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.business.SmsMailMemberDao;
import com.lehecai.admin.web.dao.business.SmsMailMemberGroupDao;
import com.lehecai.admin.web.domain.business.SmsMailMember;
import com.lehecai.admin.web.domain.business.SmsMailMemberGroup;
import com.lehecai.admin.web.service.business.SmsMailMemberGroupService;

/**
 * 短信邮件会员组业务逻辑层实现类
 * @author yanweijie
 *
 */
public class SmsMailMemberGroupServiceImpl implements SmsMailMemberGroupService {

	private SmsMailMemberGroupDao smsMailMemberGroupDao;
	private SmsMailMemberDao smsMailMemberDao;
	
	/**
	 * 查询所有短信邮件会员组
	 * @param pageBean 分页对象
	 * @param smsMailMemberGroup 短信邮件会员组对象
	 */
	@Override
	public List<SmsMailMemberGroup> findSmsMailMemberGroupList(PageBean pageBean, String name,
			String valid) {
		return smsMailMemberGroupDao.findSmsMailMemberGroupList(pageBean, name, valid);
	}
	
	/**
	 * 根据短信邮件会员组名称查询短信邮件会员组
	 * @param name 短信邮件会员组名称
	 */
	public SmsMailMemberGroup findSmsMailMemberGroupByName(final String name) {
		return smsMailMemberGroupDao.findSmsMailMemberGroupByName(name);
	}
	
	/**
	 * 封装多条件查询分页信息
	 * @param pageBean	分页对象
	 * @param name	短信邮件会员组名称
	 * @param valid	是否有效
	 * @return
	 */
	public PageBean getPageBean(final PageBean pageBean, final String name,final String valid) {
		return smsMailMemberGroupDao.getPageBean(pageBean, name, valid);
	}

	/**
	 * 根据短信邮件会员组编号查询短信邮件会员组
	 * @param id 短信邮件会员组编号
	 */
	@Override
	public SmsMailMemberGroup get(Long id) {
		return smsMailMemberGroupDao.get(id);
	}

	/**
	 * 添加/修改短信邮件会员组
	 * @param smsMailMemberGroup 短信邮件会员组对象
	 */
	@Override
	public void merge(SmsMailMemberGroup smsMailMemberGroup) {
		smsMailMemberGroupDao.merge(smsMailMemberGroup);

	}

	/**
	 * 删除短信邮件会员组
	 * @param smsMailMemberGroup 短信邮件会员组对象
	 */
	@Override
	public void del(SmsMailMemberGroup smsMailMemberGroup) {
		List<SmsMailMember> smsMailMemberList = smsMailMemberDao.findSmsMailMemberList(null, 
				smsMailMemberGroup.getId());//删除短信邮件会员
		for (SmsMailMember member : smsMailMemberList) {
			smsMailMemberDao.del(member);
		}
		smsMailMemberGroupDao.del(smsMailMemberGroup);

	}
	
	public SmsMailMemberGroupDao getSmsMailMemberGroupDao() {
		return smsMailMemberGroupDao;
	}

	public void setSmsMailMemberGroupDao(SmsMailMemberGroupDao smsMailMemberGroupDao) {
		this.smsMailMemberGroupDao = smsMailMemberGroupDao;
	}

	public SmsMailMemberDao getSmsMailMemberDao() {
		return smsMailMemberDao;
	}

	public void setSmsMailMemberDao(SmsMailMemberDao smsMailMemberDao) {
		this.smsMailMemberDao = smsMailMemberDao;
	}
}
