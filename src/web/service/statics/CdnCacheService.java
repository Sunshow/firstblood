package web.service.statics;

import java.util.List;

import com.lehecai.admin.web.domain.cdn.CdnCacheGroup;
import com.lehecai.admin.web.domain.cdn.CdnWebsiteGroup;

public interface CdnCacheService {
	List<CdnCacheGroup> cdnCacheGroupList(String jsonPath, String fileName);
	List<CdnWebsiteGroup> cdnWebsiteGroupList(String jsonPath, String fileName);
	String path(String jsonPach, String jsonFile);
	void createFile(String fileName, String content);
	void delFile(String fileName);
	@SuppressWarnings("unchecked")
	void manage(List groupList, String jsonPath, String fileName);
}
