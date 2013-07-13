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
import com.lehecai.admin.web.domain.statics.StaticCacheLayout;
import com.lehecai.admin.web.domain.statics.StaticCacheLayoutItem;
import com.lehecai.admin.web.service.statics.StaticCacheLayoutService;
import com.lehecai.admin.web.service.statics.StaticCacheService;
import com.lehecai.admin.web.thread.staticcache.StaticCacheRunnable;
import com.lehecai.core.service.memcached.MemcachedService;

public class UpdateStaticCacheWithLayoutAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(UpdateStaticCacheWithLayoutAction.class);
	private static final long serialVersionUID = 2436161530465382824L;
	
	private StaticCacheService staticCacheService;
	private StaticCacheLayoutService staticCacheLayoutService;
	private MemcachedService memcachedService;
	
	private StaticCacheRunnable staticCacheRunnable;
	
	private StaticCache staticCache;
	
	private List<StaticCache> staticCaches; //存放所有需要更新的静态缓存的id
	private List<StaticCacheLayout> staticCacheLayouts;
	
	private String templateDir;
	private String staticDir;
	private String rootDir;
	
	private Integer forcedUpdate;//是否强制更新，1，强制更新，0，不强制更新
	
	/**
	 * 转向静态缓存列表
	 * @return
	 */
	public String handle() {
		logger.info("进入查询静态缓存布局");
		List<StaticCacheLayout> staticCacheLayoutList = staticCacheLayoutService.list(null);
		staticCacheLayouts = new ArrayList<StaticCacheLayout>();
		Map<Long, StaticCacheLayout> parentMap = new HashMap<Long, StaticCacheLayout>();
		if(staticCacheLayoutList != null && staticCacheLayoutList.size() != 0){
			for(StaticCacheLayout layout : staticCacheLayoutList){
				if(layout.getTheLevel() == 1){
					parentMap.put(layout.getId(), layout);			
					staticCacheLayouts.add(layout);				
				} else {
					List<StaticCacheLayoutItem> items = staticCacheLayoutService.getStaitcCachesByLayoutId(layout.getId());
					List<StaticCache> staticCaches = new ArrayList<StaticCache>();
					for (StaticCacheLayoutItem item : items) {
						staticCaches.add(staticCacheService.get(item.getStaticCacheId()));
					}
					layout.setItems(staticCaches);
					StaticCacheLayout parent = parentMap.get(layout.getParentId());
					if (parent != null) {
						List<StaticCacheLayout> children = parent.getChildren();
						children.add(layout);
					}
				}
			}
		}
		logger.info("查询静态缓存布局结束");
		return "list";
	}
	
	/**
	 * 更新静态缓存
	 * @return
	 */
	public String manage() {
		logger.info("进入更新静态缓存");
		UserSessionBean userSessionBean = (UserSessionBean)super.getSession().get(Global.USER_SESSION);
		logger.info("userSessionBean.getUser().getId():{}", userSessionBean.getUser().getId());
		logger.info("userSessionBean.getUser().getName():{}", userSessionBean.getUser().getName());
		
		if (staticCaches != null && staticCaches.size() != 0) {
			logger.info("staticCaches.size:{}", staticCaches.size());
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
		super.setForwardUrl("/statics/updateStaticCacheWithLayout.do");
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

	public StaticCacheLayoutService getStaticCacheLayoutService() {
		return staticCacheLayoutService;
	}

	public void setStaticCacheLayoutService(
			StaticCacheLayoutService staticCacheLayoutService) {
		this.staticCacheLayoutService = staticCacheLayoutService;
	}

	public List<StaticCacheLayout> getStaticCacheLayouts() {
		return staticCacheLayouts;
	}

	public void setStaticCacheLayouts(List<StaticCacheLayout> staticCacheLayouts) {
		this.staticCacheLayouts = staticCacheLayouts;
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
