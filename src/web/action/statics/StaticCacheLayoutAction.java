package web.action.statics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.statics.StaticCache;
import com.lehecai.admin.web.domain.statics.StaticCacheLayout;
import com.lehecai.admin.web.domain.statics.StaticCacheLayoutItem;
import com.lehecai.admin.web.service.statics.StaticCacheLayoutService;
import com.lehecai.admin.web.service.statics.StaticCacheService;

public class StaticCacheLayoutAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	private final Logger logger = LoggerFactory.getLogger(StaticCacheLayoutAction.class);
	
	private StaticCacheLayoutService staticCacheLayoutService;
	private StaticCacheService staticCacheService;
	
	private StaticCacheLayout staticCacheLayout;
	private StaticCacheLayout parentStaticCacheLayout;
	
	private List<StaticCacheLayout> staticCacheLayouts;
	
	private List<StaticCache> unassignedStaticCaches;
	private List<StaticCache> assignedStaticCaches;
	
	public String handle() {
		logger.info("进入查询静态缓存布局列表");
		List<StaticCacheLayout> list = staticCacheLayoutService.list(null);
		staticCacheLayouts = new ArrayList<StaticCacheLayout>();
		Map<Long, StaticCacheLayout> parentMap = new HashMap<Long, StaticCacheLayout>();
		if (list != null && list.size() != 0) {
			for (StaticCacheLayout sc : list) {
				if (sc.getTheLevel() == 1) {
					parentMap.put(sc.getId(), sc);			
					staticCacheLayouts.add(sc);
				} else {
					StaticCacheLayout parent = parentMap.get(sc.getParentId());
					if (parent != null) {
						List<StaticCacheLayout> children = parent.getChildren();
						children.add(sc);
					}
				}
			}
		}
		logger.info("查询静态缓存布局列表结束");
		return "list";
	}
	
	/**
	 * 添加/修改缓存静态布局
	 * @return
	 */
	public String manage() {
		logger.info("进入更新静态缓存布局");
		if (staticCacheLayout != null) {
			if (staticCacheLayout.getName() == null || "".equals(staticCacheLayout.getName())) {
				logger.error("名称为空");
				super.setErrorMessage("名称不能为空");
				return "failure";
			}
			
			if (staticCacheLayout.getParentId() == null) {
				staticCacheLayout.setParentId(0L);
			}
			staticCacheLayoutService.manage(staticCacheLayout);
		} else {
			logger.error("更新静态缓存布局，提交表单为空");
			super.setErrorMessage("更新静态缓存布局，提交表单不能为空");
			return "failure";
		}
		super.setForwardUrl("/statics/staticCacheLayout.do");
		logger.info("更新静态缓存布局结束");
		return "success";
	}
	
	/**
	 * 转向添加/修改静态缓存类别
	 */
	public String input() {
		logger.info("进入输入静态缓存布局信息");
		if (staticCacheLayout != null) {
			if (staticCacheLayout.getId() != null) {				
				staticCacheLayout = staticCacheLayoutService.get(staticCacheLayout.getId());
			}else{
				staticCacheLayout.setValid(true);
				staticCacheLayout.setOrderView(0);
			}
		}
		return "inputForm";
	}
	
	
	public String del() {
		logger.info("进入删除静态缓存布局");
		if (staticCacheLayout != null && staticCacheLayout.getId() != null) {
			staticCacheLayout = staticCacheLayoutService.get(staticCacheLayout.getId());
			staticCacheLayoutService.del(staticCacheLayout);
		} else {
			logger.error("删除静态缓存布局，编码为空");
			super.setErrorMessage("删除的静态缓存布局，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/statics/staticCacheLayout.do");
		logger.info("删除静态缓存布局结束");
		return "forward";
	}
	
	
	public String join() {
		logger.info("进入关联静态缓存");
		if (staticCacheLayout == null || staticCacheLayout.getId() == null) {
			return "failure";
		}
		logger.info("查询staticCacheLayout.id={}的静态缓存子项", staticCacheLayout.getId());
		List<StaticCacheLayoutItem> items =  staticCacheLayoutService.getStaitcCachesByLayoutId(staticCacheLayout.getId());
		
		//得到已分配静态缓存列表
		assignedStaticCaches = new ArrayList<StaticCache>();
		logger.info("循环查询得到子项静态缓存实体");
		for (StaticCacheLayoutItem item : items) {
			assignedStaticCaches.add(staticCacheService.get(item.getStaticCacheId()));
		}
		
		//得到未分配静态缓存列表
		logger.info("查询所有配静态缓存");
		List<StaticCache> allStaticCacheList = staticCacheService.list(null);
		unassignedStaticCaches = new ArrayList<StaticCache>();
		Map<Long, StaticCache> unassignedParentMap = new HashMap<Long, StaticCache>();
		logger.info("对未分配静态缓存进行父子排序（从所有的静态缓存中，排除掉已分配的静态缓存）");
		if(allStaticCacheList != null && allStaticCacheList.size() != 0){
			for(StaticCache sc : allStaticCacheList){
				boolean flag = false;
				for (StaticCacheLayoutItem item : items) {
					if (sc.getId().longValue() == item.getStaticCacheId().longValue()) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					if(sc.getStLevel() == 1){
						unassignedParentMap.put(sc.getId(), sc);			
						unassignedStaticCaches.add(sc);				
					} else {
						StaticCache parent = unassignedParentMap.get(sc.getParentID());
						if (parent != null) {
							List<StaticCache> children = parent.getChildren();
							children.add(sc);
						}
					}
				}
			}
		}
		logger.info("关联静态缓存结束");
		return "join";
	}
	
	public String insertItem() {
		logger.info("进入增加静态缓存布局关联");
		if (unassignedStaticCaches == null || unassignedStaticCaches.size() == 0) {
			logger.error("未选择一个未分配的静态缓存");
			super.setErrorMessage("未选择一个未分配的静态缓存");
			return "failure";
		}
		staticCacheLayoutService.insertItems(staticCacheLayout.getId(), unassignedStaticCaches);
		super.setForwardUrl("/statics/staticCacheLayout.do?action=join&staticCacheLayout.id=" + staticCacheLayout.getId());
		logger.info("增加静态缓存布局关联结束");
		return "forward";
	}
	
	public String delItem() {
		logger.info("进入删除静态缓存布局关联");
		if (assignedStaticCaches == null || assignedStaticCaches.size() == 0) {
			logger.error("未选择一个已分配的静态缓存");
			super.setErrorMessage("未选择一个已分配的静态缓存");
			return "failure";
		}
		staticCacheLayoutService.delItems(staticCacheLayout.getId(), assignedStaticCaches);
		super.setForwardUrl("/statics/staticCacheLayout.do?action=join&staticCacheLayout.id=" + staticCacheLayout.getId());
		logger.info("删除静态缓存布局关联结束");
		return "forward";
	}

	
	public StaticCacheLayoutService getStaticCacheLayoutService() {
		return staticCacheLayoutService;
	}

	public void setStaticCacheLayoutService(
			StaticCacheLayoutService staticCacheLayoutService) {
		this.staticCacheLayoutService = staticCacheLayoutService;
	}

	public StaticCacheLayout getStaticCacheLayout() {
		return staticCacheLayout;
	}

	public void setStaticCacheLayout(StaticCacheLayout staticCacheLayout) {
		this.staticCacheLayout = staticCacheLayout;
	}

	public StaticCacheLayout getParentStaticCacheLayout() {
		return parentStaticCacheLayout;
	}

	public void setParentStaticCacheLayout(StaticCacheLayout parentStaticCacheLayout) {
		this.parentStaticCacheLayout = parentStaticCacheLayout;
	}

	public List<StaticCacheLayout> getStaticCacheLayouts() {
		return staticCacheLayouts;
	}

	public void setStaticCacheLayouts(List<StaticCacheLayout> staticCacheLayouts) {
		this.staticCacheLayouts = staticCacheLayouts;
	}

	public StaticCacheService getStaticCacheService() {
		return staticCacheService;
	}

	public void setStaticCacheService(StaticCacheService staticCacheService) {
		this.staticCacheService = staticCacheService;
	}

	public List<StaticCache> getUnassignedStaticCaches() {
		return unassignedStaticCaches;
	}

	public void setUnassignedStaticCaches(List<StaticCache> unassignedStaticCaches) {
		this.unassignedStaticCaches = unassignedStaticCaches;
	}

	public List<StaticCache> getAssignedStaticCaches() {
		return assignedStaticCaches;
	}

	public void setAssignedStaticCaches(List<StaticCache> assignedStaticCaches) {
		this.assignedStaticCaches = assignedStaticCaches;
	}
}
