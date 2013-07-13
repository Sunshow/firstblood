package web.bean;

import com.lehecai.core.memcached.IMemcachedObject;

public class StaticCacheMemBean implements IMemcachedObject {
	private static final long serialVersionUID = 1L;
	private String json;
	
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
}
