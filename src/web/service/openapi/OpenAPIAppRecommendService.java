package web.service.openapi;

import java.util.Date;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.openapi.OpenAPIAppRecommend;
import com.lehecai.core.api.openapi.OpenAPIAppRecommendType;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 2013-05-09
 * @author He Wang
 *
 */
public interface OpenAPIAppRecommendService {

	
	/**
	 * 按条件查询推荐应用信息
	 * @param appStatus
	 * @param appIsOpen
	 * @param appId
	 * @param appType
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	Map<String, Object> findOpenAPIAppRecommendList(YesNoStatus appStatus, YesNoStatus appIsOpen, Long appId, OpenAPIAppRecommendType appType, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 添加应用
	 */
	boolean addOpenAPIAppRecommend(OpenAPIAppRecommend openAPIAppRecommend) throws ApiRemoteCallFailedException;
	
	/**
	 * 编辑应用
	 * @param openAPIApp
	 * @param sign
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean updateOpenAPIAppRecommend(OpenAPIAppRecommend openAPIAppRecommend) throws ApiRemoteCallFailedException;
	
	/**
	 * 更改状态（是否通过审核）
	 * @param openAPIApp
	 * @param sign
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean auditOpenAPIAppRecommend(OpenAPIAppRecommend openAPIAppRecommend) throws ApiRemoteCallFailedException;
	
	/**
	 * 查看下载记录
	 * @param sign
	 * @param appId
	 * @param beginDate
	 * @param endDate
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	Map<String, Object> findOpenAPIAppRecommendDownloadList(String sign, Long appId, Date beginDate, Date endDate, PageBean pageBean) throws ApiRemoteCallFailedException;
	

}