package web.domain.statics;

import java.io.Serializable;

public class StaticCacheLayoutItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long layoutId;
	private Long staticCacheId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getLayoutId() {
		return layoutId;
	}
	public void setLayoutId(Long layoutId) {
		this.layoutId = layoutId;
	}
	public Long getStaticCacheId() {
		return staticCacheId;
	}
	public void setStaticCacheId(Long staticCacheId) {
		this.staticCacheId = staticCacheId;
	}
}
