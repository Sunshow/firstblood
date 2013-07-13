package web.domain.cdn;

public class CdnCacheItem {
	
	private Long id;
	private String websiteGroupId;
	private String websiteGroupName;
	private String name;
	private String urls;
	private String dirs;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrls() {
		return urls;
	}
	public void setUrls(String urls) {
		this.urls = urls;
	}
	public String getDirs() {
		return dirs;
	}
	public void setDirs(String dirs) {
		this.dirs = dirs;
	}
	public String getWebsiteGroupName() {
		return websiteGroupName;
	}
	public void setWebsiteGroupName(String websiteGroupName) {
		this.websiteGroupName = websiteGroupName;
	}
	public String getWebsiteGroupId() {
		return websiteGroupId;
	}
	public void setWebsiteGroupId(String websiteGroupId) {
		this.websiteGroupId = websiteGroupId;
	}
}
