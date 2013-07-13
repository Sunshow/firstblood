package web.domain.cdn;

import net.sf.json.JSONObject;

import com.lehecai.admin.web.enums.CdnType;

public class CdnWebsiteItem {
	private Long id;
	private String name;
	private CdnType cdnClecs;
	private String siteUrl;
	
	public static CdnWebsiteItem convertFromJSONObject(JSONObject object) {
		if (object == null || object.isNullObject()) {
			return null;
		}
		CdnWebsiteItem cdnWebsiteItem = new CdnWebsiteItem();
		
		cdnWebsiteItem.id = object.getLong("id");
		JSONObject json = object.getJSONObject("cdnClecs");
		if (json == null || json.isEmpty()) {
			cdnWebsiteItem.setCdnClecs(CdnType.CHINACACHE);
		} else {
			cdnWebsiteItem.setCdnClecs(CdnType.getItem(json.getInt("value")));
		}
		cdnWebsiteItem.name = object.getString("name");
		cdnWebsiteItem.siteUrl = object.getString("siteUrl");
		return cdnWebsiteItem;
	}
	
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
	public String getSiteUrl() {
		return siteUrl;
	}
	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}
	public CdnType getCdnClecs() {
		return cdnClecs;
	}
	public void setCdnClecs(CdnType cdnClecs) {
		this.cdnClecs = cdnClecs;
	}
	
}
