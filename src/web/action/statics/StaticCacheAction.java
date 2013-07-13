package web.action.statics;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.statics.StaticCache;
import com.lehecai.admin.web.enums.ExecuteType;
import com.lehecai.admin.web.enums.TemplateType;
import com.lehecai.admin.web.service.statics.StaticCacheService;

public class StaticCacheAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(StaticCacheAction.class);
	
	private StaticCacheService staticCacheService;
	
	private StaticCache staticCache;
	private StaticCache parentStaticCache;
	private Integer executeTypeId;
	private Integer templateTypeId;
	
	private List<StaticCache> staticCaches;
	
	private static String[] allowedFileExts = new String[] {
		".json",
		".htm",
		".html",
		".shtml",
		".txt",
		".xml"
	};
	
	public String handle() {
		logger.info("进入查询静态缓存列表");
		List<StaticCache> list = staticCacheService.list(staticCache);
		staticCaches = new ArrayList<StaticCache>();
		Map<Long, StaticCache> parentMap = new HashMap<Long, StaticCache>();
		if (list != null && list.size() != 0) {
			for (StaticCache sc : list) {
				if (sc.getStLevel() == 1) {
					parentMap.put(sc.getId(), sc);			
					staticCaches.add(sc);
				} else {
					StaticCache parent = parentMap.get(sc.getParentID());
					if (parent != null) {
						List<StaticCache> children = parent.getChildren();
						children.add(sc);
					}
				}
			}
		}
		logger.info("查询静态缓存列表结束");
		return "list";
	}
	
	/**
	 * 添加/修改缓存静态类别
	 * @return
	 */
	public String manage() {
		logger.info("进入更新静态缓存");
		if (staticCache != null) {
			if ("".equals(staticCache.getTargetUrl())) {
				staticCache.setTargetUrl(null);
			}
			if (staticCache.getName() == null || "".equals(staticCache.getName())) {
				logger.error("名称为空");
				super.setErrorMessage("名称不能为空");
				return "failure";
			}
			if (staticCache.getSlug() == null || "".equals(staticCache.getSlug())) {
				logger.error("别名为空");
				super.setErrorMessage("别名不能为空");
				return "failure";
			}
			
			if (staticCache.getId() == null) {
				StaticCache staticCacheTmp = staticCacheService.getBySlug(staticCache.getSlug());
				if (staticCacheTmp != null && staticCacheTmp.getId() != null) {
					logger.error("别名被占用");
					super.setErrorMessage("别名已使用");
					return "failure";
				}
			}
			if (staticCache.getStLevel() != 1 && (staticCache.getDataUrl() == null || "".equals(staticCache.getDataUrl()))) {
				logger.error("来源链接为空");
				super.setErrorMessage("来源链接不能为空");
				return "failure";
			}
			if (staticCache.getStLevel() != 1 && executeTypeId != null && executeTypeId != getDirectExecuteType().getValue()) {
				if (staticCache.getTemplateUrl() == null || "".equals(staticCache.getTemplateUrl())) {
					logger.error("模板名称为空");
					super.setErrorMessage("模板名称不能为空");
					return "failure";
				}
				if (staticCache.getTargetUrl() == null || "".equals(staticCache.getTargetUrl())) {
					logger.error("生成位置为空");
					super.setErrorMessage("生成位置不能为空");
					return "failure";
				}
				boolean isAllowedFileExt = false;
				for (String ext : allowedFileExts) {
					if (staticCache.getTargetUrl().endsWith(ext)) {
						isAllowedFileExt = true;
						break;
					}
				}
				if (!isAllowedFileExt) {
					logger.error("生成的文件扩展名不允许");
					super.setErrorMessage("不允许的生成文件扩展名");
					return "failure";
				}
				if (staticCache.getId() == null) {
					StaticCache staticCacheTmp = staticCacheService.getByTargetUrl(staticCache.getTargetUrl());
					if (staticCacheTmp != null && staticCacheTmp.getId() != null) {
						logger.error("生成位置被占用");
						super.setErrorMessage("生成位置已使用");
						return "failure";
					}
				}
			}
			if (staticCache.getParentID() == null) {
				staticCache.setParentID(0L);
			}
			if (staticCache.getStLevel() != 1) {
				staticCache.setExecuteType(ExecuteType.getItem(executeTypeId));
				staticCache.setTemplateType(TemplateType.getItem(templateTypeId));
			}
			if (staticCache.getDataUrl() != null && !staticCache.getDataUrl().equals("")) {
				staticCache.setDataUrl(staticCache.getDataUrl().trim());
			}
			staticCacheService.manage(staticCache);
		} else {
			logger.error("更新静态缓存，提交的表单为空");
			super.setErrorMessage("更新静态缓存，提交表单不能为空");
			return "failure";
		}
		super.setForwardUrl("/statics/staticCache.do");
		
		logger.info("更新静态缓存结束");
		return "success";
	}
	
	/**
	 * 转向添加/修改静态缓存类别
	 */
	public String input() {
		logger.info("进入输入静态缓存信息");
		if (staticCache != null) {
			if (staticCache.getId() != null) {//修改
				staticCache = staticCacheService.get(staticCache.getId());
				if (staticCache.getStLevel() != 1) {				
					executeTypeId = staticCache.getExecuteType().getValue();
					templateTypeId = staticCache.getTemplateType().getValue();
				}
			} else {//添加
				staticCache.setValid(true);
				if (staticCache.getStLevel() != 1) {
					templateTypeId = getVmTemplateType().getValue();
				}
				staticCache.setOrderView(0);
			}
		}
		return "inputForm";
	}
	
	public String view() {
		logger.info("进入查看静态缓存详情");
		if (staticCache != null && staticCache.getId() != null) {
			staticCache = staticCacheService.get(staticCache.getId());
			if (staticCache.getParentID() != null && staticCache.getParentID() != 0) {				
				parentStaticCache = staticCacheService.get(staticCache.getParentID());
			}
		} else {
			logger.error("查看静态缓存详情，编码为空");
			super.setErrorMessage("查看静态缓存详情，编码不能为空");
			return "failure";
		}
		
		logger.info("查看静态缓存详情结束");
		return "view";
	}
	
	public String del() {
		logger.info("进入删除静态缓存");
		if (staticCache != null && staticCache.getId() != null) {
			staticCache = staticCacheService.get(staticCache.getId());
			staticCacheService.del(staticCache);
		} else {
			logger.error("删除静态缓存，编码为空");
			super.setErrorMessage("删除静态缓存，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/statics/staticCache.do");
		
		logger.info("删除静态缓存结束");
		return "forward";
	}
	
	public void check() {
		logger.info("进入检验缓存别名");
		HttpServletResponse response = ServletActionContext.getResponse();
		boolean flag = true;
		StaticCache staticCacheTmp = staticCacheService.getBySlug(staticCache.getSlug());
		if (staticCacheTmp != null && staticCacheTmp.getId() != null) {
			flag = false;
		}
		PrintWriter out = null;
		response.setContentType("text/html; charset=utf-8");
		try {
			out = response.getWriter();
			//不能用println，会多打出一个换行
			out.print(flag);
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public void checkTargetUrl() {
		logger.info("进入检验缓存目标文件");
		HttpServletResponse response = ServletActionContext.getResponse();
		boolean flag = true;
		StaticCache staticCacheIdTmp = null;
		if (staticCache != null && staticCache.getId() != null) {
			staticCacheIdTmp = staticCacheService.get(staticCache.getId());
			
		}
		if (staticCacheIdTmp == null || !staticCacheIdTmp.getTargetUrl().equals(staticCache.getTargetUrl())) {		
			StaticCache staticCacheTmp = staticCacheService.getByTargetUrl(staticCache.getTargetUrl());
			if (staticCacheTmp != null && staticCacheTmp.getId() != null) {
				flag = false;
			}
		}
		PrintWriter out = null;
		response.setContentType("text/html; charset=utf-8");
		try {
			out = response.getWriter();
			//不能用println，会多打出一个换行
			out.print(flag);
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public List<TemplateType> getTemplateTypeList() {
		return TemplateType.list;
	}
	
	public StaticCacheService getStaticCacheService() {
		return staticCacheService;
	}
	public void setStaticCacheService(StaticCacheService staticCacheService) {
		this.staticCacheService = staticCacheService;
	}
	public StaticCache getStaticCache() {
		return staticCache;
	}
	public void setStaticCache(StaticCache staticCache) {
		this.staticCache = staticCache;
	}
	public List<StaticCache> getStaticCaches() {
		return staticCaches;
	}
	public void setStaticCaches(List<StaticCache> staticCaches) {
		this.staticCaches = staticCaches;
	}
	public StaticCache getParentStaticCache() {
		return parentStaticCache;
	}
	public void setParentStaticCache(StaticCache parentStaticCache) {
		this.parentStaticCache = parentStaticCache;
	}
	public List<ExecuteType> getExecuteTypes() {
		return ExecuteType.list;
	}
	public ExecuteType getDirectExecuteType() {
		return ExecuteType.DIRECTTYPE;
	}
	public TemplateType getVmTemplateType() {
		return TemplateType.VMTYPE;
	}
	public TemplateType getDataTemplateType() {
		return TemplateType.DATATYPE;
	}
	public Integer getExecuteTypeId() {
		return executeTypeId;
	}
	public void setExecuteTypeId(Integer executeTypeId) {
		this.executeTypeId = executeTypeId;
	}
	public Integer getTemplateTypeId() {
		return templateTypeId;
	}
	public void setTemplateTypeId(Integer templateTypeId) {
		this.templateTypeId = templateTypeId;
	}
}
