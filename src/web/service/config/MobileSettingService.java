/**
 * 
 */
package web.service.config;

import java.util.List;
import com.lehecai.admin.web.config.MobilePlatformInfo;
import com.lehecai.core.exception.ApiRemoteCallFailedException;


/**
 * 配置服务实现-移动平台
 */
public interface MobileSettingService {

	/**
	 * 获取配置
	 * @param group
	 * @param item
	 * @return
	 */
	public MobilePlatformInfo get(String group, String item) throws ApiRemoteCallFailedException;
	
	/**
	 * 查询
	 * @return
	 */
	public List<MobilePlatformInfo>  mget();
	
	/**
	 * 新增并保存
	 * @param mobilePlatformInfo
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean merge(MobilePlatformInfo mobilePlatformInfo) throws ApiRemoteCallFailedException;

	/**
	 * 修改并保存
	 * @param mobilePlatformInfo
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean update(MobilePlatformInfo mobilePlatformInfo)throws ApiRemoteCallFailedException;
	
}
