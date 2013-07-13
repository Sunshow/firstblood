package web.domain.cdn;

import java.util.ArrayList;
import java.util.List;

public class CdnCacheGroup {
	private Long id;
	private String name;
	private String memo;
	List<CdnCacheItem> itemList = new ArrayList<CdnCacheItem>();
	
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
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public List<CdnCacheItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<CdnCacheItem> itemList) {
		this.itemList = itemList;
	}
	
}
