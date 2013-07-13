package web.bean;

import com.lehecai.core.memcached.IMemcachedObject;

public class StaticCacheErrorBean implements IMemcachedObject {
	private static final long serialVersionUID = 1L;
	private int times;
	private boolean notify;
	
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public boolean isNotify() {
		return notify;
	}
	public void setNotify(boolean notify) {
		this.notify = notify;
	}
	
}
