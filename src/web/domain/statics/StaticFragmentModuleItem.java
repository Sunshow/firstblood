package web.domain.statics;

import java.io.Serializable;

import com.lehecai.admin.web.enums.ItemType;

public class StaticFragmentModuleItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String itemName;		//属性名称
	private String itemKey;			//属性标识
	private ItemType itemType;		//属性类型
	private Long moduleId;			//模块编码
	
	public StaticFragmentModuleItem () {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemKey() {
		return itemKey;
	}

	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

}
