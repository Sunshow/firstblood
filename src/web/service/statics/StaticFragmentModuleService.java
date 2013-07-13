package web.service.statics;

import java.util.List;

import com.lehecai.admin.web.domain.statics.StaticFragmentModule;
import com.lehecai.admin.web.domain.statics.StaticFragmentModuleItem;

/**
 * 静态碎片模板业务逻辑层
 * @author yanweijie
 *
 */
public interface StaticFragmentModuleService {
	/**
	 * 查询静态碎片模板列表
	 */
	List<StaticFragmentModule> findModuleList();
	
	/**
	 * 查询静态碎片模板
	 */
	StaticFragmentModule getModule(Long id);
	
	/**
	 * 根据静态碎片模板名称查询静态碎片模板
	 */
	StaticFragmentModule getModuleByName(String moduleName);
	
	/**
	 * 更新静态碎片模板
	 */
	void mergeModule(StaticFragmentModule staticFragmentModule);
	
	/**
	 * 查询静态碎片模板自定义属性列表
	 */
	List<StaticFragmentModuleItem> findItemList(Long moduleId);
	
	/**
	 * 查询静态碎片模板自定义属性
	 */
	StaticFragmentModuleItem getItem(Long itemId);
	
	/**
	 * 更新静态碎片模板自定以属性
	 */
	void mergeItem(StaticFragmentModuleItem staticFragmentModuleItem);
	
	/**
	 * 删除静态碎片模板属性
	 */
	void deleteItem(Long itemId);
}
