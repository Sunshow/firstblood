package web.service.business;

import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 合买红人业务层接口
 * @author yanweijie
 *
 */
public interface SyndicateStarService {

	/**
	 * 分页并条件查询合买红人
	 */
	Map<String, Object> findSyndicateStartList(Long uid, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 添加合买红人
	 */
	boolean addSyndicateStar(Long uid) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改合买红人
	 */
	boolean updateSyndicateStar(List<String> uids, int priority) throws ApiRemoteCallFailedException;
	
	/**
	 * 删除合买红人
	 */
	boolean deleteSyndicateStar(Long uid) throws ApiRemoteCallFailedException;
}
