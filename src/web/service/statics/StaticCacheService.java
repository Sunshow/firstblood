package web.service.statics;

import java.util.List;

import com.lehecai.admin.web.domain.statics.StaticCache;

public interface StaticCacheService {
	void manage(StaticCache staticCache);
	List<StaticCache> list(StaticCache staticCache); 
	StaticCache get(Long ID);
	void del(StaticCache staticCache);
	void markup(String jsonData, String templateStr, String targetUrl) throws Exception;
	StaticCache getBySlug(String slug);
	StaticCache getByTargetUrl(String targetUrl);
}