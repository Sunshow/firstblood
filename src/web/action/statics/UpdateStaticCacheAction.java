package web.action.statics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.StaticCacheQueueBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.statics.StaticCache;
import com.lehecai.admin.web.service.statics.StaticCacheService;
import com.lehecai.admin.web.thread.staticcache.StaticCacheRunnable;
import com.lehecai.core.service.memcached.MemcachedService;

public class UpdateStaticCacheAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(UpdateStaticCacheAction.class);
	private static final long serialVersionUID = 2436161530465382824L;
	
	private StaticCacheRunnable staticCacheRunnable;
	private StaticCacheService staticCacheService;
	private MemcachedService memcachedService;
	
	private StaticCache staticCache;
	
	private List<StaticCache> staticCaches; //存放所有需要更新的静态缓存的id
	
	private String templateDir;
	private String staticDir;
	private String rootDir;
	
	private Integer forcedUpdate;//是否强制更新，1，强制更新，0，不强制更新
	/**
	 * 转向静态缓存列表
	 * @return
	 */
	public String handle() {
		logger.info("进入查询静态缓存列表");
		List<StaticCache> list = staticCacheService.list(staticCache);
		staticCaches = new ArrayList<StaticCache>();
		Map<Long, StaticCache> parentMap = new HashMap<Long, StaticCache>();
		if(list != null && list.size() != 0){
			for(StaticCache sc : list){
				if(sc.getStLevel() == 1){
					parentMap.put(sc.getId(), sc);			
					staticCaches.add(sc);				
				} else {
					StaticCache parent = parentMap.get(sc.getParentID());
					if (parent != null) {
						List<StaticCache> children = parent.getChildren();
						children.add(sc);
					}
				}
			}
		}
		logger.info("查询静态缓存列表结束");
		return "list";
	}
	
	/**
	 * 更新静态缓存
	 * @return
	 */
	/**
	 * @return
	 */
	public String manage() {
		logger.info("进入更新静态缓存");
		UserSessionBean userSessionBean = (UserSessionBean)super.getSession().get(Global.USER_SESSION);
		logger.info("userSessionBean.getUser().getId():{}", userSessionBean.getUser().getId());
		logger.info("userSessionBean.getUser().getName():{}", userSessionBean.getUser().getName());
		logger.info("staticCaches.size:{}", staticCaches.size());
		
		if (staticCaches != null && staticCaches.size() != 0) {
			for (StaticCache sc : staticCaches) {
				logger.info("staticCaches中sc的id:{}", sc.getId());
				sc = staticCacheService.get(sc.getId());
				logger.info("staticCacheService查询的实体:id:{},name:{}", sc.getId() ,sc.getName());
				StaticCacheQueueBean staticCacheQueueBean = new StaticCacheQueueBean();
				staticCacheQueueBean.setStaticCache(sc);
				staticCacheQueueBean.setForcedUpdate(forcedUpdate);
				staticCacheQueueBean.setRootDir(rootDir);
				staticCacheQueueBean.setStaticDir(staticDir);
				staticCacheQueueBean.setTemplateDir(templateDir);
				staticCacheQueueBean.setUser(userSessionBean.getUser());
				
				staticCacheRunnable.addStaticCache(staticCacheQueueBean);
			}
			staticCacheRunnable.executeNotify();
		} else {
			super.setErrorMessage("您未选择任何一个选项");
			return "failure";
		}
		super.setForwardUrl("/statics/updateStaticCache.do");
		logger.info("更新静态缓存结束");
		return "success";
	}
	
	public StaticCacheService getStaticCacheService() {
		return staticCacheService;
	}
	public void setStaticCacheService(StaticCacheService staticCacheService) {
		this.staticCacheService = staticCacheService;
	}
	public StaticCache getStaticCache() {
		return staticCache;
	}
	public void setStaticCache(StaticCache staticCache) {
		this.staticCache = staticCache;
	}
	public List<StaticCache> getStaticCaches() {
		return staticCaches;
	}
	public void setStaticCaches(List<StaticCache> staticCaches) {
		this.staticCaches = staticCaches;
	}
	public String getTemplateDir() {
		return templateDir;
	}
	public void setTemplateDir(String templateDir) {
		this.templateDir = templateDir;
	}
	public String getStaticDir() {
		return staticDir;
	}
	public void setStaticDir(String staticDir) {
		this.staticDir = staticDir;
	}
	public String getRootDir() {
		return rootDir;
	}
	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public MemcachedService getMemcachedService() {
		return memcachedService;
	}

	public void setMemcachedService(MemcachedService memcachedService) {
		this.memcachedService = memcachedService;
	}

	public Integer getForcedUpdate() {
		return forcedUpdate;
	}

	public void setForcedUpdate(Integer forcedUpdate) {
		this.forcedUpdate = forcedUpdate;
	}

	public StaticCacheRunnable getStaticCacheRunnable() {
		return staticCacheRunnable;
	}

	public void setStaticCacheRunnable(StaticCacheRunnable staticCacheRunnable) {
		this.staticCacheRunnable = staticCacheRunnable;
	}
}
