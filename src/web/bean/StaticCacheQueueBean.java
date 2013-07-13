package web.bean;

import java.io.Serializable;

import com.lehecai.admin.web.domain.statics.StaticCache;
import com.lehecai.admin.web.domain.user.User;

public class StaticCacheQueueBean implements Serializable {
	private static final long serialVersionUID = 7379945499955865885L;

	private StaticCache staticCache; //需要更新的静态缓存
	
	private String templateDir;
	private String staticDir;
	private String rootDir;
	
	private User user;
	
	private Integer forcedUpdate;//是否强制更新，1，强制更新，0，不强制更新
	
	public StaticCache getStaticCache() {
		return staticCache;
	}

	public void setStaticCache(StaticCache staticCache) {
		this.staticCache = staticCache;
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

	public Integer getForcedUpdate() {
		return forcedUpdate;
	}

	public void setForcedUpdate(Integer forcedUpdate) {
		this.forcedUpdate = forcedUpdate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
