package web.service.business;

import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface RebuildUploadService {
	
	/**
	 * 重建上传文件缓存
	 * @throws ApiRemoteCallFailedException 
	 */
	public String rebuildUpload(Long id) throws ApiRemoteCallFailedException;
}
