package web.service.business;

import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.GroupKeywords;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 分组关键词业务逻辑层接口
 * @author He Wang
 *
 */
public interface GroupKeywordsService {

	/**
	 * 查询分组关键词
	 * @param groupKeywords
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> queryGroupKeywordsList(GroupKeywords groupKeywords, PageBean pageBean) throws ApiRemoteCallFailedException ;

	/**
	 * 保存分组关键词
	 * @param groupKeywords
	 * @throws ApiRemoteCallFailedException
	 */
	public void save(GroupKeywords groupKeywords) throws ApiRemoteCallFailedException;
	
	/**
	 * 删除分组关键词
	 * @param groupKeywords
	 * @throws ApiRemoteCallFailedException
	 */
	void del(GroupKeywords groupKeywords) throws ApiRemoteCallFailedException;
	
	/**
	 * 批量增加分组关键词
	 * @param groupKeywordsList
	 * @param successList
	 * @param failureList
	 * @param insertIdMap
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean batchSave(List<GroupKeywords> groupKeywordsList, List<String> successList, List<String> failureList, Map<String, String> insertIdMap) throws ApiRemoteCallFailedException;

}
