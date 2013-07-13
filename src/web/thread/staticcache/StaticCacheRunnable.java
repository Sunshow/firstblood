package web.thread.staticcache;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lehecai.admin.web.bean.StaticCacheErrorBean;
import com.lehecai.admin.web.bean.StaticCacheQueueBean;
import com.lehecai.admin.web.domain.statics.StaticCache;
import com.lehecai.admin.web.enums.ExecuteType;
import com.lehecai.admin.web.enums.TemplateType;
import com.lehecai.admin.web.service.statics.StaticCacheService;
import com.lehecai.core.service.cache.CacheService;
import com.lehecai.core.thread.AbstractThreadRunnable;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreFileUtils;
import com.lehecai.core.util.CoreHttpUtils;
import com.lehecai.core.util.CoreStringUtils;
import com.lehecai.core.warning.IWarningTool;
import com.lehecai.core.warning.WarningType;

public class StaticCacheRunnable extends AbstractThreadRunnable {
	private List<StaticCacheQueueBean> staticCacheQueueBeanList = new ArrayList<StaticCacheQueueBean>();
	
	private int errorTimes = 3;
	
	private IWarningTool warningTool;
	
	private CacheService cacheService;
	
	private String dataCacheName = "staticCacheDataCache";
	private String errorCacheName = "staticCacheErrorCache";
	
	private StaticCacheService staticCacheService;
	
	private Object staticCacheLock = new Object();
	@Override
	protected void executeRun() {
		running = true;
		while (running) {
			Set<Long> updatedStaticCacheIdSet = new HashSet<Long>();
			while (true) {
				StaticCacheQueueBean staticCacheQueueBean = null;
				synchronized (staticCacheLock) {
					if(staticCacheQueueBeanList.size() > 0){
						logger.info("获取一个待更新的静态缓存");
						staticCacheQueueBean = staticCacheQueueBeanList.remove(0);
					} 
				}
				if (staticCacheQueueBean == null || !running) {
					logger.info("未查询到静态缓存更新任务或标志位running=false,静态缓存线程进入循环等待状态");
					break;
				}
				if (updatedStaticCacheIdSet.size() > 0) {
					if (updatedStaticCacheIdSet.contains(staticCacheQueueBean.getStaticCache().getId())) {
						logger.info("静态缓存[{}]已经更新，无需重复更新", staticCacheQueueBean.getStaticCache().getName());
						continue;
					}
				}
				
				boolean flag = false;
				try {
					flag = updateStaticCache(staticCacheQueueBean);
				} catch (Exception e) {
					logger.error("静态缓存更新线程方法updateStaticCache全局异常：" + e.getMessage(), e);
				}
				if (flag) {
					updatedStaticCacheIdSet.add(staticCacheQueueBean.getStaticCache().getId());
					logger.info("静态缓存[{}]更新成功", staticCacheQueueBean.getStaticCache().getName());
				} else {
					logger.error("静态缓存[{}]更新失败", staticCacheQueueBean.getStaticCache().getName());
				}
			}
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	private boolean updateStaticCache(StaticCacheQueueBean staticCacheQueueBean) {
		StaticCache sc = staticCacheQueueBean.getStaticCache();
		logger.info("staticCacheService查询的实体:id:{},name:{}", sc.getId() ,sc.getName());
		if (sc.getStLevel() == 1) {
			logger.info("staticCacheService查询的实体:StLevel:1,continue 本次循环");
			return true;
		}
		String json = null;
		String key = null;
		
		try {
			logger.info("ExecuteType:{}", sc.getExecuteType().getValue());
			if (sc != null && sc.getExecuteType() != null 
					&& sc.getExecuteType().getValue() == ExecuteType.JSONTYPE.getValue()) {//JSONTYPE为输入数据模板生成
				logger.info("实际执行ExecuteType:{}",  ExecuteType.JSONTYPE.getName());
				json = sc.getDataUrl();
			} else if (sc != null && sc.getExecuteType() != null 
					&& sc.getExecuteType().getValue() == ExecuteType.DATATYPE.getValue()) {//DATATYPE为连接请求模板生成
				logger.info("实际执行ExecuteType:{}",  ExecuteType.DATATYPE.getName());
				
				String errorKey = sc.getId().toString();
				StaticCacheErrorBean staticCacheErrorBean = null;
				try {
					staticCacheErrorBean = this.getCacheService().getObject(StaticCacheErrorBean.class, this.getErrorCacheName(), errorKey);
				} catch (Exception e) {
					logger.error("读取缓存出错", e);
				}
				if (staticCacheErrorBean != null && staticCacheErrorBean.getTimes() >= errorTimes) {
					logger.error("静态缓存[{}]连续多次请求异常，暂时不更新", sc.getName());
					if (staticCacheErrorBean.isNotify()) {
						return false;
					}
					
					try {
						warningTool.sendMail(WarningType.STATIC_CACHE_ERROR, "静态缓存[" + sc.getName() + "]连续" + errorTimes + "次请求异常，暂时停止更新");
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}

					staticCacheErrorBean.setNotify(true);
					try {
						this.getCacheService().setObject(this.getErrorCacheName(), errorKey, staticCacheErrorBean);
					} catch (Exception e) {
						logger.error("更新缓存中是否发送通知出错", e);
					}

					return false;
				}
				
				List<String> jsonList = null;
				try {
					jsonList = CoreHttpUtils.getUrl(sc.getDataUrl(), "", CharsetConstant.CHARSET_UTF8, 30000);
				} catch (SocketTimeoutException e) {
					logger.error("静态缓存[{}]更新失败，更新缓存中异常记录的次数", sc.getName());
					if (staticCacheErrorBean != null) {
						try {
							staticCacheErrorBean.setTimes(staticCacheErrorBean.getTimes() + 1);
							this.getCacheService().setObject(this.getErrorCacheName(), errorKey, staticCacheErrorBean);
						} catch (Exception e1) {
							logger.error("更新缓存中异常记录的次数出错", e1);
						}
					} else {
						try {
							staticCacheErrorBean = new StaticCacheErrorBean();
							staticCacheErrorBean.setTimes(1);
							this.getCacheService().setObject(this.getErrorCacheName(), errorKey, staticCacheErrorBean);
						} catch (Exception e1) {
							logger.error("更新缓存中异常记录的次数出错", e1);
						}
					}
				}
				if (jsonList != null && jsonList.size() > 0) {
					if (staticCacheErrorBean != null) {
						logger.info("静态缓存[{}]更新成功，删除缓存中异常记录", sc.getName());
						try {
							this.getCacheService().remove(this.getErrorCacheName(), errorKey);
						} catch (Exception e) {
							logger.error("删除缓存出错", e);
						}
					}
					json = jsonList.get(0);
				}
			} else { //直接生成
				logger.info("实际执行ExecuteType:{}", sc.getExecuteType().getValue());
				
				String errorKey = sc.getId().toString();
				StaticCacheErrorBean staticCacheErrorBean = null;
				try {
					staticCacheErrorBean = this.getCacheService().getObject(StaticCacheErrorBean.class, this.getErrorCacheName(), errorKey);
				} catch (Exception e) {
					logger.error("读取缓存出错", e);
				}
				if (staticCacheErrorBean != null && staticCacheErrorBean.getTimes() >= errorTimes) {
					logger.error("静态缓存[{}]连续多次请求异常，暂时不更新", sc.getName());
					if (staticCacheErrorBean.isNotify()) {
						return false;
					}
					try {
						warningTool.sendMail(WarningType.STATIC_CACHE_ERROR, "静态缓存[" + sc.getName() + "]连续" + errorTimes + "次请求异常，暂时停止更新");
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}

					staticCacheErrorBean.setNotify(true);
					try {
						this.getCacheService().setObject(this.getErrorCacheName(), errorKey, staticCacheErrorBean);
					} catch (Exception e) {
						logger.error("更新缓存中是否发送通知出错", e);
					}

					return false;
				}
				
				try {
					CoreHttpUtils.getUrl(sc.getDataUrl(), "", CharsetConstant.CHARSET_UTF8, 30000);
				} catch (SocketTimeoutException e) {
					logger.error("静态缓存[{}]更新失败，更新缓存中异常记录的次数", sc.getName());
					if (staticCacheErrorBean != null) {
						try {
							staticCacheErrorBean.setTimes(staticCacheErrorBean.getTimes() + 1);
							this.getCacheService().setObject(this.getErrorCacheName(), errorKey, staticCacheErrorBean);
						} catch (Exception e1) {
							logger.error("更新缓存中异常记录的次数出错", e1);
						}
					} else {
						try {
							staticCacheErrorBean = new StaticCacheErrorBean();
							staticCacheErrorBean.setTimes(1);
							this.getCacheService().setObject(this.getErrorCacheName(), errorKey, staticCacheErrorBean);
						} catch (Exception e1) {
							logger.error("更新缓存中异常记录的次数出错", e1);
						}
					}
					return false;
				}
				if (staticCacheErrorBean != null) {
					logger.info("静态缓存[{}]更新成功，删除缓存中异常记录", sc.getName());
					try {
						this.getCacheService().remove(this.getErrorCacheName(), errorKey);
					} catch (Exception e) {
						logger.error("删除缓存出错", e);
					}
				}
				sc.setUpdateTime(new Date());
				//sc.setUserID(staticCacheQueueBean.getUser().getId());
				//sc.setUserName(staticCacheQueueBean.getUser().getName());
				logger.info("开始保存入库");
				logger.info("sc.id:{}", sc.getId());
				//logger.info("sc.userid:{}", sc.getUserID());
				//logger.info("sc.username:{}", sc.getUserName());
				staticCacheService.manage(sc);
				logger.info("结束保存入库");
				return true;
			}
		} catch (IOException e) {
			logger.error("{}:请求连接失败:{}", sc.getName(), sc.getDataUrl());
			logger.error(e.getMessage(), e);
			return false;
		}
		logger.info("得到的json:{}", json);
		if (json == null || "".equals(json)) {
			logger.error("{}:未得到任何数据:{}", sc.getName(), sc.getDataUrl());
			return false;
		}
		
		key = CoreStringUtils.md5(json + sc.getId(), CharsetConstant.CHARSET_UTF8);
		String cachedId = null;
		try {
			cachedId = this.getCacheService().getObject(String.class, this.getDataCacheName(), key);
		} catch (Exception e) {
			logger.error("读取缓存出错", e);
		}
		if (cachedId != null && staticCacheQueueBean.getForcedUpdate() != 1) {
			logger.info("MemoryCached中已存在key={}的json数据,无需再次更新", key);
			return true;
		}
		logger.info("rootDir:{}", staticCacheQueueBean.getRootDir());//C:/Documents and Settings/yanweijie/Workspaces/MyEclipse 8.5/padmin/WebRoot/
		logger.info("templateDir:{}", staticCacheQueueBean.getTemplateDir());// /template
		logger.info("staticDir:{}", staticCacheQueueBean.getStaticDir());// /static
		
		//String templateUrl = (rootDir + templateDir.replace('/', File.separatorChar));
		//since 2012-08-24
		//modified by chirowong
		//templateUrl修改为全路径，不需要拼写rootDir
		//String templateUrl = (staticCacheQueueBean.getRootDir() + staticCacheQueueBean.getTemplateDir());
		String templateUrl =  staticCacheQueueBean.getTemplateDir();
		//C:/Documents and Settings/yanweijie/Workspaces/MyEclipse 8.5/padmin/WebRoot/template
		String targetUrl = (staticCacheQueueBean.getRootDir() + staticCacheQueueBean.getStaticDir() + sc.getTargetUrl());
		//C:/Documents and Settings/yanweijie/Workspaces/MyEclipse 8.5/padmin/WebRoot/static/cooperator/sogou/openprize.html
		String templateStr = sc.getTemplateUrl();// /sogou/openprize.vm
		
		
		logger.info("templateUrl:{}", templateUrl);
		logger.info("targetUrl:{}", targetUrl);
		logger.info("templateStr:{}", templateStr);
		logger.info("TemplateType:{}", sc.getTemplateType().getValue());
		
		if (sc.getTemplateType().getValue() == TemplateType.SOURCETYPE.getValue()) {
			CoreFileUtils.createFile(targetUrl, json, CharsetConstant.CHARSET_UTF8);
		} else if (sc.getTemplateType().getValue() == TemplateType.JSONPTYPE.getValue()) {
			String jsonpStr = templateStr + "('" + json + "');";
			CoreFileUtils.createFile(targetUrl, jsonpStr, CharsetConstant.CHARSET_UTF8);
		} else {
			if (sc.getTemplateType().getValue() == TemplateType.VMTYPE.getValue()) {
				logger.info("TemplateType为{}", TemplateType.VMTYPE.getName());
				logger.info("读取{}文件", (templateUrl + templateStr));
				try {
					if(!templateStr.startsWith("/")){
						templateStr = "/"+templateStr;
					}
					templateStr = CoreFileUtils.readFile(templateUrl + templateStr, CharsetConstant.CHARSET_UTF8);
				} catch (IOException e) {
					logger.error("{}:加载模板失败:{}", sc.getName(), (templateUrl + templateStr));
					logger.error(e.getMessage(), e);
					return false;
				}
				logger.info("读取文件的结果为{}", templateStr);
			}
			if (templateStr == null || "".equals(templateStr)) {
				logger.error("{}:未获得模板内容:{}", sc.getName(), (templateUrl + templateStr));
				return false;
			}
			if (sc != null && sc.getExecuteType() != null && sc.getExecuteType().getValue() != ExecuteType.DIRECTTYPE.getValue()) {	
				logger.info("开始创建文件");
				try {
					staticCacheService.markup(json, templateStr, targetUrl);
				} catch (Exception e) {
					logger.error("{}:生成静态文件失败:", sc.getName());
					logger.error("{}:失败时提供的json:{}", sc.getName(), json);
					logger.error("{}:失败时提供的模板内容:{}", sc.getName(), templateStr);
					logger.error("{}:失败时生成的目标文件:{}",sc.getName(), targetUrl);
					logger.error(e.getMessage(), e);
					return false;
				}
				logger.info("结束创建文件");
			}
		}
		
		logger.info("MemoryCached中不存在key={}的json数据,更新mc", key);
		try {
			this.getCacheService().setObject(this.getDataCacheName(), key, sc.getId().toString());
		} catch (Exception e) {
			logger.error("设置缓存出错", e);
		}
		
		sc.setUpdateTime(new Date());
		//sc.setUserID(staticCacheQueueBean.getUser().getId());
		//sc.setUserName(staticCacheQueueBean.getUser().getName());
		logger.info("开始保存入库");
		logger.info("sc.id:{}", sc.getId());
		//logger.info("sc.userid:{}", sc.getUserID());
		//logger.info("sc.username:{}", sc.getUserName());
		staticCacheService.manage(sc);
		logger.info("结束保存入库");
		return true;
	}
	
	public void addStaticCache(StaticCacheQueueBean bean) {
		synchronized (staticCacheLock) {
			staticCacheQueueBeanList.add(bean);
		}
	}

	public StaticCacheService getStaticCacheService() {
		return staticCacheService;
	}

	public void setStaticCacheService(StaticCacheService staticCacheService) {
		this.staticCacheService = staticCacheService;
	}

	public int getErrorTimes() {
		return errorTimes;
	}

	public void setErrorTimes(int errorTimes) {
		this.errorTimes = errorTimes;
	}

	public void setWarningTool(IWarningTool warningTool) {
		this.warningTool = warningTool;
	}

	public CacheService getCacheService() {
		return cacheService;
	}

	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	public String getDataCacheName() {
		return dataCacheName;
	}

	public void setDataCacheName(String dataCacheName) {
		this.dataCacheName = dataCacheName;
	}

	public String getErrorCacheName() {
		return errorCacheName;
	}

	public void setErrorCacheName(String errorCacheName) {
		this.errorCacheName = errorCacheName;
	}

}
