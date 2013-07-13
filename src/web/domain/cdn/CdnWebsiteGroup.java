package web.domain.cdn;

import java.util.ArrayList;
import java.util.List;

public class CdnWebsiteGroup {
	private String id;
	private String name;
	private String memo;
	List<CdnWebsiteItem> itemList = new ArrayList<CdnWebsiteItem>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public List<CdnWebsiteItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<CdnWebsiteItem> itemList) {
		this.itemList = itemList;
	}
}
