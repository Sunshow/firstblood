package web.service.user;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.user.SourceRebateConfig;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 分成配置业务逻辑层
 * @author yanweijie
 *
 */
public interface SourceRebateConfigService {
	
	/**
	 * 多条件分页查询分成配置
	 * @param sourceRebateConfig
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> findSourceRebateConfigList(SourceRebateConfig sourceRebateConfig, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	
	/**
	 * 查询分成配置
	 * @param sourceRebateConfig
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public SourceRebateConfig getSourceRebateConfig(SourceRebateConfig sourceRebateConfig) throws ApiRemoteCallFailedException;
	
	/**
	 * 添加分成配置
	 * @param sourceRebateConfig
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean add(SourceRebateConfig sourceRebateConfig) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改分成配置
	 * @param sourceRebateConfig
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean update(SourceRebateConfig sourceRebateConfig) throws ApiRemoteCallFailedException;
}
