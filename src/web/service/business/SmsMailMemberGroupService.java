package web.service.business;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.SmsMailMemberGroup;

/**
 * 短信邮件会员组业务逻辑层接口
 * @author yanweijie
 *
 */
public interface SmsMailMemberGroupService {

	/**
	 * 查询所有短信邮件会员组
	 * @param pageBean 分页对象
	 * @param smsMailMemberGroup 短信邮件会员组对象
	 */
	List<SmsMailMemberGroup> findSmsMailMemberGroupList(PageBean pageBean, String name, String valid);
	
	/**
	 * 根据短信邮件会员组名称查询短信邮件会员组
	 * @param name 短信邮件会员组名称
	 */
	SmsMailMemberGroup findSmsMailMemberGroupByName(final String name);
	
	/**
	 * 封装多条件查询分页信息
	 * @param pageBean	分页对象
	 * @param name	短信邮件会员组名称
	 * @param valid	是否有效
	 * @return
	 */
	PageBean getPageBean(final PageBean pageBean, final String name, final String valid);
	
	/**
	 * 根据短信邮件会员组编号查询短信邮件会员组
	 * @param id 短信邮件会员组编号
	 */
	SmsMailMemberGroup get(Long id);
	
	/**
	 * 添加/修改短信邮件会员组
	 * @param smsMailMemberGroup 短信邮件会员组对象
	 */
	void merge(SmsMailMemberGroup smsMailMemberGroup);
	
	/**
	 * 删除短信邮件会员组
	 * @param smsMailMemberGroup 短信邮件会员组对象
	 */
	void del(SmsMailMemberGroup smsMailMemberGroup);
}
