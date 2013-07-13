package web.service.statics;

import java.util.List;

import com.lehecai.admin.web.domain.statics.StaticCache;
import com.lehecai.admin.web.domain.statics.StaticCacheLayout;
import com.lehecai.admin.web.domain.statics.StaticCacheLayoutItem;

public interface StaticCacheLayoutService {
	void manage(StaticCacheLayout staticCacheLayout);
	List<StaticCacheLayout> list(StaticCacheLayout staticCacheLayout); 
	StaticCacheLayout get(Long id);
	void del(StaticCacheLayout staticCacheLayout);
	List<StaticCacheLayoutItem> getStaitcCachesByLayoutId(Long layoutId);
	void insertItems(Long layoutId, List<StaticCache> staticCacheIdList);
	void delItems(Long layoutId, List<StaticCache> staticCacheIdList);
}