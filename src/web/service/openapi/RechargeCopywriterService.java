package web.service.openapi;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.openapi.RechargeCopywriter;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 2013-7-2
 * @author likunpeng
 * 充值文案信息
 */
public interface RechargeCopywriterService {
	
	/**
	 * 查询充值文案信息
	 */
	public Map<String, Object> getList(RechargeCopywriter rechargeCopywriter, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 根据id查询充值文案信息
	 */
	public RechargeCopywriter get(Long id) throws ApiRemoteCallFailedException;
	
	/**
	 * 添加充值文案信息
	 */
	public Long addRechargeCopywriterInfo(RechargeCopywriter rechargeCopywriter) throws ApiRemoteCallFailedException;
	
	/**
	 * 编辑充值文案信息
	 */
	public boolean updateRechargeCopywriterInfo(RechargeCopywriter rechargeCopywriter) throws ApiRemoteCallFailedException;
}
