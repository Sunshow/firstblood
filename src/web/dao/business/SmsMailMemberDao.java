package web.dao.business;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.SmsMailMember;

/**
 * 短信邮件会员数据访问层接口
 * @author yanweijie
 *
 */
public interface SmsMailMemberDao {

	/**
	 * 查询所有短信邮件会员
	 * @param pageBean 分页对象
	 * @param groupId 短信邮件会员组编号
	 */
	List<SmsMailMember> findSmsMailMemberList(PageBean pageBean, Long groupId);
	
	/**
	 * 根据短信邮件会员编号查询短信邮件会员
	 * @param userId 短信邮件会员编号
	 */
	List<SmsMailMember> findSmsMailMemberByUid(final Long uid);
	
	/**
	 * 根据短信邮件会员用户名查询短信邮件会员
	 * @param userName 短信邮件会员用户名
	 */
	SmsMailMember findSmsMailMemberByUserName(String userName, Long groupId);
	
	/**
	 * 封装多条件查询分页信息
	 * @param pageBean	分页对象
	  * @param groupId 短信邮件会员组编号
	 * @return
	 */
	PageBean getPageBean(final PageBean pageBean, Long groupId);
	
	/**
	 * 根据短信邮件会员组编号查询短信邮件会员
	 * @param id 短信邮件会员编号
	 */
	SmsMailMember get(Long id);
	
	/**
	 * 添加/修改短信邮件会员
	 * @param SmsMailMemberGroup 短信邮件会员对象
	 */
	void merge(SmsMailMember smsMailMember);
	
	/**
	 * 删除短信邮件会员
	 */
	void del(SmsMailMember smsMailMember);
}
