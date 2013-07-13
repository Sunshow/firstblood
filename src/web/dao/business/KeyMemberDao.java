package web.dao.business;

import java.util.List;

import com.lehecai.admin.web.domain.business.KeyMember;

/**
 * 重点会员数据访问层接口
 * @author yanweijie
 *
 */
public interface KeyMemberDao {

	/**
	 * 查询所有重点会员
	 */
	List<KeyMember> findList();
	
	/**
	 * 根据会员编号查询重点会员
	 * @param uid 会员编号
	 */
	KeyMember getByUid(Long uid);
	
	/**
	 * 根据会员用户名查询重点会员
	 * @param userName 会员用户名
	 */
	KeyMember getByUserName(String userName);
	
	/**
	 * 根据重点会员编号查询重点会员信息
	 * @param id 重点会员编号
	 */
	KeyMember getById(Long id);
	
	/**
	 * 修改重点会员(备注信息)
	 * @param keyMember 重点会员
	 */
	void mergeKeyMember(KeyMember keyMember);
	
	/**
	 * 删除重点会员
	 * @param keyMember 重点会员
	 */
	void deleteKeyMember(KeyMember keyMember);
}
