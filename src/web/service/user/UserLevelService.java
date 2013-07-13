package web.service.user;

import java.util.List;
import java.util.Map;

import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface UserLevelService {
	
	/**
	 * 查询用户的奖牌战绩
	 * @param uids
	 * @return
	 */
	public Map<String, Object> getUsersLevel(List<String> uids) throws ApiRemoteCallFailedException;

}
