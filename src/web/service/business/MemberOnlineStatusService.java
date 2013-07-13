package web.service.business;

import java.util.List;

import com.lehecai.core.api.user.MemberOnlineStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 会员在线状态业务逻辑层接口
 * @author yanweijie
 *
 */
public interface MemberOnlineStatusService {

	/**
	 * 根据会员编号查询会员在线状态
	 * @param uid 会员编号
	 */
	List<MemberOnlineStatus> findOnlineStatusByUid(long uid) throws ApiRemoteCallFailedException ;
	
	/**
	 * 根据会员编号删除会员所有登陆点
	 * @param uid 会员编号
	 */
	boolean deleteAllLogin(long uid) throws ApiRemoteCallFailedException ;
	
	/**
	 * 删除会员某一登陆点
	 * @param key String
	 * @param type int 
	 */
	boolean deleteLoing(String key, int type) throws ApiRemoteCallFailedException ;
}
