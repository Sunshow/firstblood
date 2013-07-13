/**
 * 
 */
package web.service.base;

import com.lehecai.core.api.base.SiteConfig;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author Sunshow
 *
 */
public interface SiteConfigService {
	
	/**
	 * 从API读取站点配置
	 * @return
	 */
	public SiteConfig getSiteConfig() throws ApiRemoteCallFailedException;
}
