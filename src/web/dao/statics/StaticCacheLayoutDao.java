package web.dao.statics;

import java.util.List;

import com.lehecai.admin.web.domain.statics.StaticCacheLayout;
import com.lehecai.admin.web.domain.statics.StaticCacheLayoutItem;

public interface StaticCacheLayoutDao {
	void save(StaticCacheLayout staticCacheLayout);
	void update(StaticCacheLayout staticCacheLayout);
	List<StaticCacheLayout> list(StaticCacheLayout staticCacheLayout);
	StaticCacheLayout get(Long id);
	void del(StaticCacheLayout staticCacheLayout);
	List<StaticCacheLayoutItem> getStaticCachesByLayoutId(Long layoutId);
	StaticCacheLayoutItem getItem(Long staticCacheId, Long layoutId);
	void insertItem(StaticCacheLayoutItem staticCacheLayoutItem);
	void delItem(StaticCacheLayoutItem staticCacheLayoutItem);
}