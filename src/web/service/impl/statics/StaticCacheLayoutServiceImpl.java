package web.service.impl.statics;

import java.util.List;

import com.lehecai.admin.web.dao.statics.StaticCacheLayoutDao;
import com.lehecai.admin.web.domain.statics.StaticCache;
import com.lehecai.admin.web.domain.statics.StaticCacheLayout;
import com.lehecai.admin.web.domain.statics.StaticCacheLayoutItem;
import com.lehecai.admin.web.service.statics.StaticCacheLayoutService;

public class StaticCacheLayoutServiceImpl implements StaticCacheLayoutService{
	private StaticCacheLayoutDao staticCacheLayoutDao;
	
	public void manage(StaticCacheLayout staticCacheLayout){
		if (staticCacheLayout.getId() == null) {
			staticCacheLayoutDao.save(staticCacheLayout);
		} else {
			staticCacheLayoutDao.update(staticCacheLayout);
		}
	}

	@Override
	public List<StaticCacheLayout> list(StaticCacheLayout staticCacheLayout) {
		return staticCacheLayoutDao.list(staticCacheLayout);
	}

	@Override
	public StaticCacheLayout get(Long id) {
		return staticCacheLayoutDao.get(id);
	}

	@Override
	public void del(StaticCacheLayout staticCacheLayout) {
		if (staticCacheLayout.getTheLevel() == 1) {
			List<StaticCacheLayout> list = staticCacheLayoutDao.list(staticCacheLayout);
			if (list != null && list.size() > 0) {
				for (StaticCacheLayout s : list) {
					staticCacheLayoutDao.del(s);
				}
			}
		}
		staticCacheLayoutDao.del(staticCacheLayout);
	}

	public StaticCacheLayoutDao getStaticCacheLayoutDao() {
		return staticCacheLayoutDao;
	}

	public void setStaticCacheLayoutDao(StaticCacheLayoutDao staticCacheLayoutDao) {
		this.staticCacheLayoutDao = staticCacheLayoutDao;
	}

	@Override
	public void delItems(Long layoutId, List<StaticCache> staticCacheIdList) {
		for (StaticCache sc : staticCacheIdList) {
			StaticCacheLayoutItem item = staticCacheLayoutDao.getItem(sc.getId(), layoutId);
			staticCacheLayoutDao.delItem(item);
		}
	}

	@Override
	public List<StaticCacheLayoutItem> getStaitcCachesByLayoutId(Long layoutId) {
		return staticCacheLayoutDao.getStaticCachesByLayoutId(layoutId);
	}

	@Override
	public void insertItems(Long layoutId, List<StaticCache> staticCacheIdList) {
		for (StaticCache sc : staticCacheIdList) {
			StaticCacheLayoutItem item = new StaticCacheLayoutItem();
			item.setLayoutId(layoutId);
			item.setStaticCacheId(sc.getId());
			staticCacheLayoutDao.insertItem(item);
		}
	}
}
