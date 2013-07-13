package web.action.statics;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.StaticCacheQueueBean;
import com.lehecai.admin.web.domain.statics.StaticCache;
import com.lehecai.admin.web.enums.StaticCacheType;
import com.lehecai.admin.web.service.statics.StaticCacheService;
import com.lehecai.admin.web.thread.staticcache.StaticCacheRunnable;
import com.lehecai.core.service.memcached.MemcachedService;
import com.opensymphony.xwork2.Action;

public class UpdateRemoteStaticCacheAction extends BaseAction {

	private static final long serialVersionUID = 2436161530465382824L;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private StaticCacheService staticCacheService;
	private MemcachedService memcachedService;
	private StaticCacheRunnable staticCacheRunnable;
	
	private String scId;
	private Integer scType;
	
	private String templateDir;
	private String staticDir;
	private String rootDir;
	
	private Integer rc = 0;
	private String message = "success";
	
	private Integer forcedUpdate = 0;//是否强制更新，1，强制更新，0，不强制更新
	
	public String handle() {
		logger.info("进入更新静态缓存");
		HttpServletResponse response = ServletActionContext.getResponse();
		StaticCache sc = null;
		PrintWriter out = null;
		response.setContentType("text/html; charset=utf-8");

		JSONArray jsonArray = new JSONArray();
		logger.info("scId:{}", scId);
		if(scId == null || "".equals(scId)){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("rc", "0");
			jsonObject.put("message", "传递过来的编码为空");
			jsonArray.add(jsonObject);
			logger.error("rc:{}", "0");
			logger.error("message:{}", "传递过来的编码为空");
		}else{
			logger.info("scType:{}", scType);
			if(scType != null && scType == StaticCacheType.SLUGTYPE.getValue()){
				logger.info("scId:{}", scId);
				sc = staticCacheService.getBySlug(scId);
			}else{
				logger.info("scId:{}", scId);
				try {
					sc = staticCacheService.get(Long.valueOf(scId));
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					sc = null;
				}
				logger.info("scname:{}", sc.getName());
			}	
			if(sc == null){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("rc", "0");
				jsonObject.put("message", "未查到对应的静态缓存:id:" + scId);
				jsonArray.add(jsonObject);
				logger.error("rc:{}", "0");
				logger.error("message:{},scId:{}", "未查到对应的静态缓存", scId);
			}else{
				if(sc.getStLevel() == 2){
					JSONObject jsonObject = new JSONObject();
					if (markup(sc, rootDir)) {
						staticCacheRunnable.executeNotify();
						jsonObject.put("rc", "1");
						jsonObject.put("message", "success");
					} else {
						jsonObject.put("rc", "0");
						jsonObject.put("message", "failure: id:" + sc.getId() + ",name:" + sc.getName());
					}
					jsonArray.add(jsonObject);
				}else{
					List<StaticCache> list = staticCacheService.list(sc);
					if (list == null || list.size() == 0) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("rc", "0");
						jsonObject.put("message", "未查到对应的子类静态缓存:id:" + scId);
						jsonArray.add(jsonObject);
						logger.error("rc:{}", "0");
						logger.error("message:{},scId:{}", "未查到对应的静态缓存", scId);
					} else {
						for(StaticCache staticCache : list){
							JSONObject jsonObject = new JSONObject();
							if (markup(staticCache, rootDir)) {
								jsonObject.put("rc", "1");
								jsonObject.put("message", "success");
							} else {
								jsonObject.put("rc", "0");
								jsonObject.put("message", "failure: id:" + staticCache.getId() + ",name:" + staticCache.getName());
							}
							jsonArray.add(jsonObject);
						}
						staticCacheRunnable.executeNotify();
					}
				}
			}
		}
		try {
			out = response.getWriter();
			//不能用println，会多打出一个换行
			out.print(jsonArray.toString());
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.error("response输入错误");
			logger.error(e.getMessage(), e);
		}
		logger.info("更新静态缓存结束");
		return  Action.NONE;
	}
	
	public boolean markup(StaticCache sc,String rootDir) {

		logger.info("staticCaches中sc的id:{}", sc.getId());
		logger.info("staticCacheService查询的实体:id:{},name:{}", sc.getId() ,sc.getName());
		StaticCacheQueueBean staticCacheQueueBean = new StaticCacheQueueBean();
		staticCacheQueueBean.setStaticCache(sc);
		staticCacheQueueBean.setForcedUpdate(forcedUpdate);
		staticCacheQueueBean.setRootDir(rootDir);
		staticCacheQueueBean.setStaticDir(staticDir);
		staticCacheQueueBean.setTemplateDir(templateDir);
		
		staticCacheRunnable.addStaticCache(staticCacheQueueBean);
		return true;
	}
	public StaticCacheService getStaticCacheService() {
		return staticCacheService;
	}

	public void setStaticCacheService(StaticCacheService staticCacheService) {
		this.staticCacheService = staticCacheService;
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
	public Integer getScType() {
		return scType;
	}
	public void setScType(Integer scType) {
		this.scType = scType;
	}
	public String getScId() {
		return scId;
	}
	public void setScId(String scId) {
		this.scId = scId;
	}

	public Integer getRc() {
		return rc;
	}

	public void setRc(Integer rc) {
		this.rc = rc;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
