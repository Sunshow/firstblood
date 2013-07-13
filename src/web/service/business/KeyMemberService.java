package web.service.business;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.KeyMember;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 重点会员业务逻辑层接口
 * @author yanweijie
 *
 */
public interface KeyMemberService {

	/**
	 * 查询所有重点会员
	 */
	List<KeyMember> findList();
	
	/**
	 * 多条件并分页查询重点会员
	 * @param keyMemberUids	所有重点会员会员编码
	 * @param uid			会员编码
	 * @param userName		会员用户名
	 * @param rbeginDate	注册起始时间
	 * @param rendDate		注册结束时间
	 * @param lbeginDate	最后登录起始时间
	 * @param lendDate		最后登录结束时间
	 * @param orderStr		排序字段
	 * @param orderView		排序方式
	 * @param pageBean		分页对象
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	Map<String, Object> fuzzyQueryResult(List<String> keyMemberUids, Long uid, String userName, Date rbeginDate, Date rendDate,
                                         Date lbeginDate, Date lendDate, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;
	
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
	 * @param id 重点会员编号
	 */
	void deleteKeyMember(Long id);
}
