package web.action.statics;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.statics.StaticFragmentModule;
import com.lehecai.admin.web.domain.statics.StaticFragmentModuleItem;
import com.lehecai.admin.web.enums.ItemType;
import com.lehecai.admin.web.service.statics.StaticFragmentModuleService;
import com.lehecai.core.YesNoStatus;

/**
 * 静态碎片模板action
 * @author yanweijie
 *
 */
public class StaticFragmentModuleAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(StaticFragmentModuleAction.class);
	
	private StaticFragmentModuleService staticFragmentModuleService;

	private List<StaticFragmentModule> staticFragmentModuleList;
	private List<StaticFragmentModuleItem> staticFragmentModuleItemList;
	private List<String> itemKeys;
	private List<String> itemNames;
	private List<Integer> itemTypeValues;
	
	private StaticFragmentModule staticFragmentModule;
	private StaticFragmentModuleItem staticFragmentModuleItem;
	
	private Long moduleId;
	
	private String staticDir;
	
	/**
	 * 查询所有静态碎片模板
	 * @return
	 */
	public String handle () {
		logger.info("进入查询所有静态碎片模板");
		staticFragmentModuleList = staticFragmentModuleService.findModuleList();
		
		return "list_module";
	}
	
	/**
	 * 输入静态碎片模板信息
	 * @return
	 */
	public String inputModule () {
		logger.info("进入输入静态碎片模板信息");
		if (staticFragmentModule != null && staticFragmentModule.getId() != null) {		//修改静态碎片模板
			staticFragmentModule = staticFragmentModuleService.getModule(staticFragmentModule.getId());	//根据静态碎片模板编码查询静态碎片模板
		}
		
		return "input_module";
	}
	
	/**
	 * 更新静态碎片模板信息
	 * @return
	 */
	public String manageModule () {
		logger.info("进入更新静态碎片模板信息");
		if (staticFragmentModule == null) {
			logger.error("静态碎片模板为空");
			super.setErrorMessage("静态碎片模板不能为空");
			return "failure";
		}
		if (staticFragmentModule.getModuleName() == null || staticFragmentModule.getModuleName().equals("")) {
			logger.error("静态碎片模板名称为空");
			super.setErrorMessage("静态碎片模板名称不能为空");
			return "failure";
		}
		StaticFragmentModule tempModule = null;
		if (staticFragmentModule.getId() != null && staticFragmentModule.getId() != 0L) {	//修改静态碎片模板
			tempModule = staticFragmentModuleService.getModule(staticFragmentModule.getId());//根据静态碎片模板编码查询静态碎片模板
		}
		String moduleName = "";
		if (tempModule != null) {			//修改静态碎片模板
			if (!staticFragmentModule.getModuleName().equals(tempModule.getModuleName())) {
				moduleName = staticFragmentModule.getModuleName();
			}
		} else {							//添加静态碎片模板
			moduleName = staticFragmentModule.getModuleName();
		}
		if (moduleName != null && !moduleName.equals("")) {
			tempModule = staticFragmentModuleService.getModuleByName(moduleName);
			if (tempModule != null) {		//检验新添加的或者是有修改的静态模板名称是否已存在
				logger.error("模板名称 {} 已经存在!", moduleName);
				super.setErrorMessage("模板名称  " + moduleName + " 已经存在!");
				return "failure";
			}
		}
		staticFragmentModuleService.mergeModule(staticFragmentModule);
		
		super.setForwardUrl("/statics/staticFragmentModule.do");
		return "forward";
	}
	
	/**
	 * 输入静态碎片模板属性
	 * @return
	 */
	public String inputItem () {
		logger.info("进入输入静态碎片模板属性信息");
		
		if (moduleId == null || moduleId == 0L) {
			logger.error("静态碎片模板编码为空");
			super.setErrorMessage("静态碎片模板编码不能为空");
			return "failure";
		}
		
		staticFragmentModuleItemList = staticFragmentModuleService.findItemList(moduleId);//根据静态碎片模板编码查询静态碎片模板属性列表
		
		return "input_item";
	}
	
	/**
	 * 更新静态碎片模板属性
	 * @return
	 */
	public String manageItem () {
		if (itemKeys == null || itemKeys.size() == 0) {
			logger.error("静态碎片模板属性为空");
			super.setErrorMessage("静态碎片模板属性不能为空");
			return "failure";
		}
		
		List<StaticFragmentModuleItem> tempStaticFragmentModuleItemList = staticFragmentModuleService.findItemList(moduleId);	//根据静态碎片模板编码查询静态碎片模板属性列表
		for (StaticFragmentModuleItem tempStaticFragmentModuleItem : tempStaticFragmentModuleItemList) {
			staticFragmentModuleService.deleteItem(tempStaticFragmentModuleItem.getId());	//循环删除旧的静态碎片模板属性
		}
		
		for (int i = 0 ; i < itemKeys.size() ; i ++ ) {
			staticFragmentModuleItem = new StaticFragmentModuleItem();
			
			staticFragmentModuleItem.setItemKey(itemKeys.get(i));							//设置属性名称
			staticFragmentModuleItem.setItemName(itemNames.get(i));							//设置属性名称
			staticFragmentModuleItem.setItemType(ItemType.getItem(itemTypeValues.get(i)));	//设置属性类型
			staticFragmentModuleItem.setModuleId(moduleId);									//设置模板编码
			staticFragmentModuleService.mergeItem(staticFragmentModuleItem); //循环添加新的静态碎片模板属性
		}
		
		super.setForwardUrl("/statics/staticFragmentModule.do?action=inputItem&moduleId=" + moduleId);
		return "success";
	}
	
	
	public List<ItemType> getItemTypes () {
		return ItemType.list;
	}
	
	public ItemType getTextItemType () {
		return ItemType.TEXTTYPE;
	}
	
	public ItemType getLinkItemType () {
		return ItemType.LINKTYPE;
	}
	
	public ItemType getYesNoType() {
		return ItemType.YESNOTYPE;
	}
	
	public YesNoStatus getYesStatus() {
		return YesNoStatus.YES;
	}
	
	public YesNoStatus getNoStatus() {
		return YesNoStatus.NO;
	}
	
	public StaticFragmentModuleService getStaticFragmentModuleService() {
		return staticFragmentModuleService;
	}

	public void setStaticFragmentModuleService(
			StaticFragmentModuleService staticFragmentModuleService) {
		this.staticFragmentModuleService = staticFragmentModuleService;
	}

	public List<StaticFragmentModule> getStaticFragmentModuleList() {
		return staticFragmentModuleList;
	}

	public void setStaticFragmentModuleList(
			List<StaticFragmentModule> staticFragmentModuleList) {
		this.staticFragmentModuleList = staticFragmentModuleList;
	}
	
	public List<StaticFragmentModuleItem> getStaticFragmentModuleItemList() {
		return staticFragmentModuleItemList;
	}

	public void setStaticFragmentModuleItemList(
			List<StaticFragmentModuleItem> staticFragmentModuleItemList) {
		this.staticFragmentModuleItemList = staticFragmentModuleItemList;
	}
	
	public List<String> getItemKeys() {
		return itemKeys;
	}

	public void setItemKeys(List<String> itemKeys) {
		this.itemKeys = itemKeys;
	}

	public List<String> getItemNames() {
		return itemNames;
	}

	public void setItemNames(List<String> itemNames) {
		this.itemNames = itemNames;
	}

	public List<Integer> getItemTypeValues() {
		return itemTypeValues;
	}

	public void setItemTypeValues(List<Integer> itemTypeValues) {
		this.itemTypeValues = itemTypeValues;
	}

	public StaticFragmentModule getStaticFragmentModule() {
		return staticFragmentModule;
	}

	public void setStaticFragmentModule(StaticFragmentModule staticFragmentModule) {
		this.staticFragmentModule = staticFragmentModule;
	}

	public StaticFragmentModuleItem getStaticFragmentModuleItem() {
		return staticFragmentModuleItem;
	}

	public void setStaticFragmentModuleItem(StaticFragmentModuleItem staticFragmentModuleItem) {
		this.staticFragmentModuleItem = staticFragmentModuleItem;
	}

	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	public String getStaticDir() {
		return staticDir;
	}

	public void setStaticDir(String staticDir) {
		this.staticDir = staticDir;
	}
}
