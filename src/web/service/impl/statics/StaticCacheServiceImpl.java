package web.service.impl.statics;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.dao.statics.StaticCacheDao;
import com.lehecai.admin.web.domain.statics.StaticCache;
import com.lehecai.admin.web.service.statics.StaticCacheService;
import com.lehecai.admin.web.utils.JsonUtil;
import com.lehecai.admin.web.utils.VelocityUtil;

public class StaticCacheServiceImpl implements StaticCacheService{
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private StaticCacheDao staticCacheDao;
	
	private VelocityUtil velocityUtil;
	
	public void manage(StaticCache staticCache){
		staticCacheDao.merge(staticCache);
	}

	public StaticCacheDao getStaticCacheDao() {
		return staticCacheDao;
	}

	public void setStaticCacheDao(StaticCacheDao staticCacheDao) {
		this.staticCacheDao = staticCacheDao;
	}

	@Override
	public List<StaticCache> list(StaticCache staticCache) {
		return staticCacheDao.list(staticCache);
	}

	@Override
	public StaticCache get(Long ID) {
		return staticCacheDao.get(ID);
	}

	@Override
	public void del(StaticCache staticCache) {
		staticCacheDao.del(staticCache);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void markup(String jsonData,String templateStr,String targetUrl) throws Exception{
		Map<String, Object> contextValue = null;
		try {
			contextValue = JsonUtil.getMap4Json(jsonData);
		} catch (Exception e) {
			logger.error("json转换为map时出错,json:{}", jsonData);
			logger.error(e.getMessage(), e);
		}
		if (contextValue == null) {
			logger.error("记录日志：JsonUtil.getMap4Json转换为map时返回空,json:{}", jsonData);
		}
		velocityUtil.build(targetUrl, templateStr, contextValue);
	}

	@Override
	public StaticCache getBySlug(String slug) {
		return staticCacheDao.getBySlug(slug);
	}

	@Override
	public StaticCache getByTargetUrl(String targetUrl) {
		return staticCacheDao.getByTargetUrl(targetUrl);
	}

	public VelocityUtil getVelocityUtil() {
		return velocityUtil;
	}

	public void setVelocityUtil(VelocityUtil velocityUtil) {
		this.velocityUtil = velocityUtil;
	}
}
