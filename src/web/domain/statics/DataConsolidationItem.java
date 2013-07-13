package web.domain.statics;

import java.io.Serializable;

public class DataConsolidationItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long dataId;
	private String dataKey;
	private String url;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getDataId() {
		return dataId;
	}
	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDataKey() {
		return dataKey;
	}
	public void setDataKey(String dataKey) {
		this.dataKey = dataKey;
	}
}
