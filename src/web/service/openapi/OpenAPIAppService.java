package web.service.openapi;

import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.openapi.OpenAPIApp;
import com.lehecai.core.api.openapi.OpenAPIAppStatus;
import com.lehecai.core.api.openapi.OpenAPIAppType;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface OpenAPIAppService {
	
	/**
	 * 按条件获取所有的OpenAPIAPP
	 * @param appName
	 * @param appOwner
	 * @param source
	 * @param status
	 * @param level
	 * @param appKey
	 * @param appId
	 * @param appType
	 * @param orderStr
	 * @param orderView
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	List<OpenAPIApp> findAllOpenAPIAppList(String appName, long appOwner, long source, OpenAPIAppStatus status, long level,
                                           long appKey, long appId, OpenAPIAppType appType, String orderStr, String orderView) throws ApiRemoteCallFailedException;
	
	/**
	 * 分页并多条件查询应用
	 */
	Map<String, Object> findOpenAPIAppList(String appName, long appOwner, long source, OpenAPIAppStatus status, long level,
                                           long appKey, long appId, OpenAPIAppType appType, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 添加应用
	 */
	boolean addOpenAPIApp(OpenAPIApp openAPIApp) throws ApiRemoteCallFailedException;
	
	/**
	 * 编辑应用
	 */
	boolean updateOpenAPIApp(OpenAPIApp openAPIApp) throws ApiRemoteCallFailedException;

	Map<String, Object> findOpenAPIAppVersionList(Long id, PageBean pageBean)
			throws ApiRemoteCallFailedException;
}