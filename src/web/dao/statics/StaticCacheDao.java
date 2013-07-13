package web.dao.statics;

import java.util.List;

import com.lehecai.admin.web.domain.statics.StaticCache;

public interface StaticCacheDao {
	void merge(StaticCache staticCache);
	List<StaticCache> list(StaticCache staticCache);
	StaticCache get(Long ID);
	void del(StaticCache staticCache);
	StaticCache getBySlug(String slug);
	StaticCache getByTargetUrl(String targetUrl);
}