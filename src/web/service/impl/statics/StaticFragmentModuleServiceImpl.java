package web.service.impl.statics;

import java.util.List;

import com.lehecai.admin.web.dao.statics.StaticFragmentModuleDao;
import com.lehecai.admin.web.domain.statics.StaticFragmentModule;
import com.lehecai.admin.web.domain.statics.StaticFragmentModuleItem;
import com.lehecai.admin.web.service.statics.StaticFragmentModuleService;

/**
 * 静态碎片模板业务逻辑层实现类
 * @author yanweijie
 *
 */
public class StaticFragmentModuleServiceImpl implements StaticFragmentModuleService {

	private StaticFragmentModuleDao staticFragmentModuleDao;
	
	/**
	 * 查询静态碎片模板列表
	 */
	public List<StaticFragmentModule> findModuleList() {
		return staticFragmentModuleDao.findModuleList();
	}
	
	/**
	 * 查询静态碎片模板
	 */
	public StaticFragmentModule getModule(Long id) {
		return staticFragmentModuleDao.getModule(id);
	}
	
	/**
	 * 根据静态碎片模板名称查询静态碎片模板
	 */
	public StaticFragmentModule getModuleByName(String moduleName) {
		return staticFragmentModuleDao.getModuleByName(moduleName);
	}
	
	/**
	 * 更新静态碎片模板
	 */
	public void mergeModule(StaticFragmentModule staticFragmentModule) {
		staticFragmentModuleDao.mergeModule(staticFragmentModule);
	}
	
	/**
	 * 查询静态碎片模板自定义属性列表
	 */
	public List<StaticFragmentModuleItem> findItemList(Long moduleId) {
		return staticFragmentModuleDao.findItemList(moduleId);
	}
	
	/**
	 * 查询静态碎片模板自定义属性
	 */
	public StaticFragmentModuleItem getItem(Long id) {
		return staticFragmentModuleDao.getItem(id);
	}
	
	/**
	 * 更新静态碎片模板自定以属性
	 */
	public void mergeItem(StaticFragmentModuleItem staticFragmentModuleItem) {
		staticFragmentModuleDao.mergeItem(staticFragmentModuleItem);
	}

	/**
	 * 删除静态碎片模板属性
	 */
	public void deleteItem(Long itemId) {
		staticFragmentModuleDao.deleteItem(staticFragmentModuleDao.getItem(itemId));
	}
	
	public StaticFragmentModuleDao getStaticFragmentModuleDao() {
		return staticFragmentModuleDao;
	}

	public void setStaticFragmentModuleDao(StaticFragmentModuleDao staticFragmentModuleDao) {
		this.staticFragmentModuleDao = staticFragmentModuleDao;
	}
}
