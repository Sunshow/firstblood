package web.service.user;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface SourceService {

	public Map<String, Object> getResult(Long id, Long partnerId, String name, Integer status, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	public boolean update(Long id, Long partnerId, String name, Integer status) throws ApiRemoteCallFailedException;

	public boolean create(Long id, Long partnerId, String name, Integer status) throws ApiRemoteCallFailedException;
	
	public boolean createExistSource(Long id, Long partnerId) throws ApiRemoteCallFailedException;
	
	public boolean refreshSource(Long partnerId);
}
