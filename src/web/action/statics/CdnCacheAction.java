package web.action.statics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cdn.CdnCacheGroup;
import com.lehecai.admin.web.domain.cdn.CdnCacheItem;
import com.lehecai.admin.web.domain.cdn.CdnWebsiteGroup;
import com.lehecai.admin.web.domain.cdn.CdnWebsiteItem;
import com.lehecai.admin.web.enums.CdnType;
import com.lehecai.admin.web.service.cdn.ChinaCacheCdnService;
import com.lehecai.admin.web.service.cdn.ChinaNetCenterCdnService;
import com.lehecai.admin.web.service.statics.CdnCacheService;

public class CdnCacheAction extends BaseAction {
	private static final long serialVersionUID = -318384970308376825L;
	private static final Logger logger = LoggerFactory.getLogger(CdnCacheAction.class);
	
	private CdnCacheService cdnCacheService;
	
	private List<CdnCacheGroup> groupList;
	
	private List<CdnWebsiteGroup> cdnWebsiteGroups;
	
	private CdnCacheGroup cdnCacheGroup;
	
	private CdnCacheItem cdnCacheItem;
	
	private String jsonPath;
	
	private String groupFileName;
	
	private String siteFileName;
	
	public String handle() {
		logger.info("进入查询CDN组列表");
		groupList = cdnCacheService.cdnCacheGroupList(jsonPath, groupFileName);
		cdnWebsiteGroups = cdnCacheService.cdnWebsiteGroupList(jsonPath, siteFileName);
		return "list";
	}
	
	public String groupManage() {
		logger.info("进入更新CDN组");
		groupList = cdnCacheService.cdnCacheGroupList(jsonPath, groupFileName);
		super.setForwardUrl("/statics/cdnCache.do");
		if (cdnCacheGroup != null) {
			if (cdnCacheGroup.getId() != null && cdnCacheGroup.getId().intValue() != 0) {
				for (int i = 0;i<groupList.size();i++) {
					CdnCacheGroup fc = (CdnCacheGroup)groupList.get(i);
					if (cdnCacheGroup.getId().longValue() == fc.getId().longValue()) {
						fc.setName(cdnCacheGroup.getName());
						fc.setMemo(cdnCacheGroup.getMemo());
						cdnCacheService.manage(groupList, jsonPath, groupFileName);
						
						logger.info("修改CDN组成功");
						super.setSuccessMessage("修改CDN组成功");
						return "success";
					}
				}
				logger.error("修改CDN组失败");
				super.setErrorMessage("修改CDN组失败");
				return "failure";
			} else {
				cdnCacheGroup.setId(getTimestamp());
				groupList.add(cdnCacheGroup);
				cdnCacheService.manage(groupList, jsonPath, groupFileName);
				logger.info("添加CDN组成功");
				super.setSuccessMessage("添加CDN组成功");
			}
		} else {
			logger.error("更新CDN组，提交的表单为空");
			super.setErrorMessage("更新CDN组，提交的表单不能为空");
			return "failure";
		}
		logger.info("更新CDN组结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入CDN组信息");
		if (cdnCacheGroup != null) {
			if (cdnCacheGroup.getId() != null && cdnCacheGroup.getId() != 0) {
				groupList = cdnCacheService.cdnCacheGroupList(jsonPath, groupFileName);
				for (int i = 0;i<groupList.size();i++) {
					CdnCacheGroup f = (CdnCacheGroup)groupList.get(i);
					if (cdnCacheGroup.getId().equals(f.getId())) {
						cdnCacheGroup = f;
					}
				}
				super.setForwardUrl("cdn/cdnCacheGroup!update.do");
				return "inputGroup";
			}
		}
		super.setForwardUrl("cdn/cdnCacheGroup!add.do");
		return "inputGroup";
	}
	
	public String del() {
		logger.info("进入删除CDN组");
		super.setForwardUrl("/statics/cdnCache.do");
		if (cdnCacheGroup != null) {
			if (cdnCacheGroup.getId() != null) {
				groupList = cdnCacheService.cdnCacheGroupList(jsonPath, groupFileName);
				for (int i = 0;i<groupList.size();i++) {
					CdnCacheGroup fc = (CdnCacheGroup)groupList.get(i);
					if (cdnCacheGroup.getId().equals(fc.getId())) {
						groupList.remove(i);
						cdnCacheService.manage(groupList, jsonPath, groupFileName);
						
						logger.info("删除CDN组成功");
						super.setSuccessMessage("删除CDN组成功");
						return "success";
					}
				}
			}
		}
		
		logger.error("删除CDN组失败");
		super.setErrorMessage("删除CDN组失败");
		
		logger.info("删除CDN组结束");
		return "failure";
	}
	
	private long getTimestamp() {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			return calendar.getTimeInMillis();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public String inputItem() {
		logger.info("进入输入CDN项");
		cdnWebsiteGroups = cdnCacheService.cdnWebsiteGroupList(jsonPath, siteFileName);
		if (cdnCacheGroup != null) {
			if (cdnCacheGroup != null && cdnCacheGroup.getId() != null && cdnCacheGroup.getId() != 0
					&& cdnCacheItem != null && cdnCacheItem.getId() != null && cdnCacheItem.getId() != 0
			) {
				groupList = cdnCacheService.cdnCacheGroupList(jsonPath, groupFileName);
				for (int i = 0;i<groupList.size();i++) {
					CdnCacheGroup f = (CdnCacheGroup)groupList.get(i);
					if (cdnCacheGroup.getId().equals(f.getId())) {
						List<CdnCacheItem> items = f.getItemList();
						for (int j = 0; j < items.size(); j++) {
							CdnCacheItem item = items.get(j);
							if (item.getId().longValue() == cdnCacheItem.getId().longValue()) {
								cdnCacheItem = item;
								return "inputItem";
							}
						}
					}
				}
			}
		}
		return "inputItem";
	}
	
	public String itemManage() {
		logger.info("进入更新CDN项");
		groupList = cdnCacheService.cdnCacheGroupList(jsonPath, groupFileName);
		super.setForwardUrl("/statics/cdnCache.do");
		if (cdnCacheGroup != null) {
			if (cdnCacheGroup.getId() != null && cdnCacheGroup.getId().intValue() != 0) {
				//更新缓存项
				if (cdnCacheItem != null && cdnCacheItem.getId() != null && cdnCacheItem.getId() != 0) {
					for (int i = 0;i<groupList.size();i++) {
						CdnCacheGroup ccg = (CdnCacheGroup)groupList.get(i);
						if (cdnCacheGroup.getId().equals(ccg.getId())) {
							List<CdnCacheItem> itemList = ccg.getItemList();
							for (int j = 0; j < itemList.size(); j++) {
								CdnCacheItem cci = itemList.get(j);
								if (cdnCacheItem.getId().longValue() == cci.getId().longValue()) {
									cci.setWebsiteGroupId(cdnCacheItem.getWebsiteGroupId());
									cci.setWebsiteGroupName(cdnCacheItem.getWebsiteGroupName());
									cci.setName(cdnCacheItem.getName());
									cci.setUrls(cdnCacheItem.getUrls());
									cci.setDirs(cdnCacheItem.getDirs());
									cdnCacheService.manage(groupList, jsonPath, groupFileName);
									
									logger.info("修改CDN项成功");
									super.setSuccessMessage("修改CDN项成功");
									return "success";
								}
							}
							
							logger.error("未找到CDN项");
							super.setErrorMessage("未找到CDN项");
							return "failure";
						}
					}
				} else {//新增缓存项
					for (int i = 0;i<groupList.size();i++) {
						CdnCacheGroup fc = (CdnCacheGroup)groupList.get(i);
						if (cdnCacheGroup.getId().equals(fc.getId())) {
							cdnCacheItem.setId(getTimestamp());
							fc.getItemList().add(cdnCacheItem);
							cdnCacheService.manage(groupList, jsonPath, groupFileName);
							
							logger.info("添加CDN项成功");
							super.setSuccessMessage("添加CDN项成功");
							logger.info("退出添加CDN项");
							return "success";
						}
					}
					logger.error("未找到CDN组");
					super.setErrorMessage("未找到CDN组");
					return "failure";
				}
			} else {
				logger.error("CDN组ID为空");
				super.setErrorMessage("CDN组ID为空");
				return "failure";
			}
		} else {
			logger.error("更新CDN项，提交的表单为空");
			super.setErrorMessage("更新CDN项，提交的表单为空");
		}
		logger.info("退出更新CDN项");
		return "failure";
	}
	
	public String delItem() {
		logger.info("进入删除CDN项");
		groupList = cdnCacheService.cdnCacheGroupList(jsonPath, groupFileName);
		super.setForwardUrl("/statics/cdnCache.do");
		if (cdnCacheGroup != null) {
			if (cdnCacheGroup != null && cdnCacheGroup.getId() != null && cdnCacheGroup.getId() != 0
					&& cdnCacheItem != null && cdnCacheItem.getId() != null && cdnCacheItem.getId() != 0
			) {
				for (int i = 0;i<groupList.size();i++) {
					CdnCacheGroup f = (CdnCacheGroup)groupList.get(i);
					if (cdnCacheGroup.getId().equals(f.getId())) {
						List<CdnCacheItem> items = f.getItemList();
						for (int j = 0; j < items.size(); j++) {
							CdnCacheItem item = items.get(j);
							if (item.getId().longValue() == cdnCacheItem.getId().longValue()) {
								items.remove(j);
								cdnCacheService.manage(groupList, jsonPath, groupFileName);
								
								logger.info("删除CDN项成功");
								super.setSuccessMessage("删除CDN项成功");
								return "success";
							}
						}
					}
				}
			}
		}
		logger.error("删除CDN项失败");
		super.setErrorMessage("删除CDN项失败");
		
		logger.info("删除CDN项结束");
		return "failure";
	}
	
	public String cdnGroup() {
		logger.info("进入更新CDN组缓存");
		groupList = cdnCacheService.cdnCacheGroupList(jsonPath, groupFileName);
		if (groupList == null) {
			logger.info("groupList为null");
			super.setErrorMessage("CDN组列表为空");
			return "failure";
		}
		if (cdnCacheGroup != null) {
			if (cdnCacheGroup.getId() != null) {
				for (int i = 0;i<groupList.size();i++) {
					CdnCacheGroup fc = (CdnCacheGroup)groupList.get(i);
					if (fc == null) {
						logger.info("fc为null");
						super.setErrorMessage("CDN组中的元素为空");
						return "failure";
					}
					//匹配要更新的缓存组
					if (cdnCacheGroup.getId().equals(fc.getId())) {
						//遍历每一条缓存，进行多站点更新
						List<CdnCacheItem> cacheItemList = fc.getItemList();
						StringBuffer siteMsg = new StringBuffer("CDN站点更新概况：");
						for (CdnCacheItem cdnCacheItem : cacheItemList) {
							List<CdnWebsiteItem> cdnWebsiteItems = getWebsiteGrounp(cdnCacheItem.getWebsiteGroupId());
							
							Map<Integer, List<CdnWebsiteItem>> cacheMap = new HashMap<Integer, List<CdnWebsiteItem>>(); 
							cacheMap.put(CdnType.CHINACACHE.getValue(), new ArrayList<CdnWebsiteItem>());
							cacheMap.put(CdnType.CHINANETCENTER.getValue(), new ArrayList<CdnWebsiteItem>());
							for (CdnWebsiteItem c : cdnWebsiteItems) {
								if (c.getCdnClecs() != null) {
									cacheMap.get(c.getCdnClecs().getValue()).add(c);
								} else {
									cacheMap.get(CdnType.CHINACACHE.getValue()).add(c);
								}
							}
							
							List<CdnCacheItem> cdnCacheItemList = new ArrayList<CdnCacheItem>();
							cdnCacheItemList.add(cdnCacheItem);
							//ChinaNetCenter
							if (cacheMap.get(CdnType.CHINANETCENTER.getValue()) != null && cacheMap.get(CdnType.CHINANETCENTER.getValue()).size() > 0) {
								Map<String, Map<String, String>> chinaNetCenterResMap = ChinaNetCenterCdnService.request(cacheMap.get(CdnType.CHINANETCENTER.getValue()), cdnCacheItemList);
								
								Set<String> chinaNetCenterSet = chinaNetCenterResMap.keySet();
								Iterator<String> chinaNetCenterIt = chinaNetCenterSet.iterator();
								while (chinaNetCenterIt.hasNext()) {
									String key = chinaNetCenterIt.next();
									Map<String, String> siteMap = chinaNetCenterResMap.get(key);
									logger.info(siteMap.get(ChinaNetCenterCdnService.RESP_MSG));
									if (ChinaNetCenterCdnService.FAILED.equals(siteMap.get(ChinaNetCenterCdnService.RESP_CODE))) {
										siteMsg.append("\\n").append("CDN缓存[").append(cdnCacheItem.getName()).append("]更新失败msg:").append(siteMap.get(ChinaNetCenterCdnService.RESP_MSG));
									} else if (ChinaNetCenterCdnService.EXCEPTION.equals(siteMap.get(ChinaNetCenterCdnService.RESP_CODE))) {
										siteMsg.append("\\n").append(siteMap.get(ChinaNetCenterCdnService.RESP_SITE)).append("[").append(siteMap.get(ChinaNetCenterCdnService.RESP_MSG)).append("]");
									} else if (ChinaNetCenterCdnService.CONNECTION.equals(siteMap.get(ChinaNetCenterCdnService.RESP_CODE))) {
										siteMsg.append("\\n").append(siteMap.get(ChinaNetCenterCdnService.RESP_SITE)).append("[AllUrls:").append(siteMap.get(ChinaNetCenterCdnService.RESP_AMOUNT_URLS_NUM))
										.append(",SuccessUrls:").append(siteMap.get(ChinaNetCenterCdnService.RESP_SUCCESS_URLS_NUM)).append("; AllDirs:").append(siteMap.get(ChinaNetCenterCdnService.RESP_AMOUNT_DIRS_NUM))
										.append(",SuccessDirs:").append(siteMap.get(ChinaNetCenterCdnService.RESP_SUCCESS_DIRS_NUM)).append("; urlExceed:").append(siteMap.get(ChinaNetCenterCdnService.RESP_URLEXCEED))
										.append("; dirExceed:").append(siteMap.get(ChinaNetCenterCdnService.RESP_DIREXCEED)).append("]");
									}
								}
							}
							
							if (cacheMap.get(CdnType.CHINACACHE.getValue()) != null && cacheMap.get(CdnType.CHINACACHE.getValue()).size() > 0) {
								//ChinaCache
								Map<String, Map<String, String>> resMap = ChinaCacheCdnService.request(cacheMap.get(CdnType.CHINACACHE.getValue()), cdnCacheItemList);
								
								Set<String> set = resMap.keySet();
								Iterator<String> it = set.iterator();
								while (it.hasNext()) {
									String key = it.next();
									Map<String, String> siteMap = resMap.get(key);
									logger.info(siteMap.get(ChinaCacheCdnService.RESP_MSG));
									if (ChinaCacheCdnService.FAILED.equals(siteMap.get(ChinaCacheCdnService.RESP_CODE))) {
										siteMsg.append("\\n").append("CDN缓存[").append(cdnCacheItem.getName()).append("]更新失败msg:").append(siteMap.get(ChinaCacheCdnService.RESP_MSG));
									} else if (ChinaCacheCdnService.EXCEPTION.equals(siteMap.get(ChinaCacheCdnService.RESP_CODE))) {
										siteMsg.append("\\n").append(siteMap.get(ChinaCacheCdnService.RESP_SITE)).append("[").append(siteMap.get(ChinaCacheCdnService.RESP_MSG)).append("]");
									} else if (ChinaCacheCdnService.CONNECTION.equals(siteMap.get(ChinaCacheCdnService.RESP_CODE))) {
										siteMsg.append("\\n").append(siteMap.get(ChinaCacheCdnService.RESP_SITE)).append("[AllUrls:").append(siteMap.get(ChinaCacheCdnService.RESP_AMOUNT_URLS_NUM))
										.append(",SuccessUrls:").append(siteMap.get(ChinaCacheCdnService.RESP_SUCCESS_URLS_NUM)).append("; AllDirs:").append(siteMap.get(ChinaCacheCdnService.RESP_AMOUNT_DIRS_NUM))
										.append(",SuccessDirs:").append(siteMap.get(ChinaCacheCdnService.RESP_SUCCESS_DIRS_NUM)).append("; urlExceed:").append(siteMap.get(ChinaCacheCdnService.RESP_URLEXCEED))
										.append("; dirExceed:").append(siteMap.get(ChinaCacheCdnService.RESP_DIREXCEED)).append("]");
									}
								}
							}
						}
						super.setErrorMessage(siteMsg.toString());
						return "list";
					}
				}
				cdnCacheService.manage(groupList, jsonPath, groupFileName);
			}
		}
		logger.error("CDN组缓存更新失败");
		super.setErrorMessage("CDN组缓存更新失败");
		
		logger.info("更新CDN组缓存结束");
		return "list";
	}

	public String cdnItem() {
		logger.info("进入更新CDN缓存");
		groupList = cdnCacheService.cdnCacheGroupList(jsonPath, groupFileName);
		List<CdnCacheItem> items = new ArrayList<CdnCacheItem>();
		
		//更新已有单个缓存
		if (cdnCacheItem.getId() != null && cdnCacheItem.getId().longValue() != 0) {
			for (int i = 0;i<groupList.size();i++) {
				CdnCacheGroup f = (CdnCacheGroup)groupList.get(i);
				if (cdnCacheGroup.getId().equals(f.getId())) {
					List<CdnCacheItem> itemTemp = f.getItemList();
					for (int j = 0; j < itemTemp.size(); j++) {
						CdnCacheItem item = itemTemp.get(j);
						if (item.getId().longValue() == cdnCacheItem.getId().longValue()) {
							cdnCacheItem = item;
						}
					}
				}
			}
		} else {//更新指定地址缓存
			cdnCacheItem.setName("指定地址缓存");
		}
		items.add(cdnCacheItem);
		List<CdnWebsiteItem> cdnWebsiteItems = getWebsiteGrounp(cdnCacheItem.getWebsiteGroupId());
		
		Map<Integer, List<CdnWebsiteItem>> cacheMap = new HashMap<Integer, List<CdnWebsiteItem>>(); 
		cacheMap.put(CdnType.CHINACACHE.getValue(), new ArrayList<CdnWebsiteItem>());
		cacheMap.put(CdnType.CHINANETCENTER.getValue(), new ArrayList<CdnWebsiteItem>());
		for (CdnWebsiteItem c : cdnWebsiteItems) {
			if (c.getCdnClecs() != null) {
				cacheMap.get(c.getCdnClecs().getValue()).add(c);
			} else {
				cacheMap.get(CdnType.CHINACACHE.getValue()).add(c);
			}
		}
		
		StringBuffer siteMsg = new StringBuffer("站点更新概况："); 
		//ChinaNetCenter
		if (cacheMap.get(CdnType.CHINANETCENTER.getValue()) != null && cacheMap.get(CdnType.CHINANETCENTER.getValue()).size() > 0) {
			Map<String, Map<String, String>> chinaNetCenterResMap = ChinaNetCenterCdnService.request(cacheMap.get(CdnType.CHINANETCENTER.getValue()), items);
		
			Set<String> chinaNetCenterSet = chinaNetCenterResMap.keySet();
			Iterator<String> chinaNetCenterIt = chinaNetCenterSet.iterator();
			while (chinaNetCenterIt.hasNext()) {
				String key = chinaNetCenterIt.next();
				Map<String, String> siteMap = chinaNetCenterResMap.get(key);
				logger.info(siteMap.get(ChinaNetCenterCdnService.RESP_MSG));
				if (ChinaNetCenterCdnService.FAILED.equals(siteMap.get(ChinaNetCenterCdnService.RESP_CODE))) {
					logger.error("CDN缓存更新失败，msg：{}", siteMap.get(ChinaNetCenterCdnService.RESP_MSG));
					super.setErrorMessage("CDN缓存更新失败msg:"+siteMap.get(ChinaNetCenterCdnService.RESP_MSG));
					break;
				} else if (ChinaNetCenterCdnService.EXCEPTION.equals(siteMap.get(ChinaNetCenterCdnService.RESP_CODE))) {
					siteMsg.append("\\n").append(siteMap.get(ChinaNetCenterCdnService.RESP_SITE)).append("[").append(siteMap.get(ChinaNetCenterCdnService.RESP_MSG)).append("]");
				} else if (ChinaNetCenterCdnService.CONNECTION.equals(siteMap.get(ChinaNetCenterCdnService.RESP_CODE))) {
					siteMsg.append("\\n").append(siteMap.get(ChinaNetCenterCdnService.RESP_SITE)).append("[AllUrls:").append(siteMap.get(ChinaNetCenterCdnService.RESP_AMOUNT_URLS_NUM))
					.append(",SuccessUrls:").append(siteMap.get(ChinaNetCenterCdnService.RESP_SUCCESS_URLS_NUM)).append("; AllDirs:").append(siteMap.get(ChinaNetCenterCdnService.RESP_AMOUNT_DIRS_NUM))
					.append(",SuccessDirs:").append(siteMap.get(ChinaNetCenterCdnService.RESP_SUCCESS_DIRS_NUM)).append("; urlExceed:").append(siteMap.get(ChinaNetCenterCdnService.RESP_URLEXCEED))
					.append("; dirExceed:").append(siteMap.get(ChinaNetCenterCdnService.RESP_DIREXCEED)).append("]");
				}
			}
		}
		
		//ChinaCache
		if (cacheMap.get(CdnType.CHINACACHE.getValue()) != null && cacheMap.get(CdnType.CHINACACHE.getValue()).size() > 0) {
			
			Map<String, Map<String, String>> resMap = ChinaCacheCdnService.request(cacheMap.get(CdnType.CHINACACHE.getValue()), items);
			
			Set<String> set = resMap.keySet();
			Iterator<String> it = set.iterator();
			while (it.hasNext()) {
				String key = it.next();
				Map<String, String> siteMap = resMap.get(key);
				logger.info(siteMap.get(ChinaCacheCdnService.RESP_MSG));
				if (ChinaCacheCdnService.FAILED.equals(siteMap.get(ChinaCacheCdnService.RESP_CODE))) {
					logger.error("CDN缓存更新失败，msg：{}", siteMap.get(ChinaCacheCdnService.RESP_MSG));
					super.setErrorMessage("CDN缓存更新失败msg:"+siteMap.get(ChinaCacheCdnService.RESP_MSG));
					return "list";
				} else if (ChinaCacheCdnService.EXCEPTION.equals(siteMap.get(ChinaCacheCdnService.RESP_CODE))) {
					siteMsg.append("\\n").append(siteMap.get(ChinaCacheCdnService.RESP_SITE)).append("[").append(siteMap.get(ChinaCacheCdnService.RESP_MSG)).append("]");
				} else if (ChinaCacheCdnService.CONNECTION.equals(siteMap.get(ChinaCacheCdnService.RESP_CODE))) {
					siteMsg.append("\\n").append(siteMap.get(ChinaCacheCdnService.RESP_SITE)).append("[AllUrls:").append(siteMap.get(ChinaCacheCdnService.RESP_AMOUNT_URLS_NUM))
					.append(",SuccessUrls:").append(siteMap.get(ChinaCacheCdnService.RESP_SUCCESS_URLS_NUM)).append("; AllDirs:").append(siteMap.get(ChinaCacheCdnService.RESP_AMOUNT_DIRS_NUM))
					.append(",SuccessDirs:").append(siteMap.get(ChinaCacheCdnService.RESP_SUCCESS_DIRS_NUM)).append("; urlExceed:").append(siteMap.get(ChinaCacheCdnService.RESP_URLEXCEED))
					.append("; dirExceed:").append(siteMap.get(ChinaCacheCdnService.RESP_DIREXCEED)).append("]");
				}
			}
		}
		super.setErrorMessage(siteMsg.toString());
		logger.info("更新CDN缓存结束");
		return "list";
	}
	
	private List<CdnWebsiteItem> getWebsiteGrounp(String websiteGroupId) {
		if (websiteGroupId != null && !websiteGroupId.isEmpty()) {
			cdnWebsiteGroups = cdnCacheService.cdnWebsiteGroupList(jsonPath, siteFileName);
			for (int i = 0;i<cdnWebsiteGroups.size();i++) {
				CdnWebsiteGroup fc = (CdnWebsiteGroup)cdnWebsiteGroups.get(i);
				if (websiteGroupId.equals(fc.getId())) {
					return fc.getItemList();
				}
			}
		}
		return null;
	}

	public CdnCacheService getCdnCacheService() {
		return cdnCacheService;
	}

	public void setCdnCacheService(CdnCacheService cdnCacheService) {
		this.cdnCacheService = cdnCacheService;
	}

	public List<CdnCacheGroup> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<CdnCacheGroup> groupList) {
		this.groupList = groupList;
	}

	public CdnCacheGroup getCdnCacheGroup() {
		return cdnCacheGroup;
	}

	public void setCdnCacheGroup(CdnCacheGroup cdnCacheGroup) {
		this.cdnCacheGroup = cdnCacheGroup;
	}

	public String getJsonPath() {
		return jsonPath;
	}

	public void setJsonPath(String jsonPath) {
		this.jsonPath = jsonPath;
	}

	public String getGroupFileName() {
		return groupFileName;
	}

	public void setGroupFileName(String groupFileName) {
		this.groupFileName = groupFileName;
	}

	public CdnCacheItem getCdnCacheItem() {
		return cdnCacheItem;
	}

	public void setCdnCacheItem(CdnCacheItem cdnCacheItem) {
		this.cdnCacheItem = cdnCacheItem;
	}

	public String getSiteFileName() {
		return siteFileName;
	}

	public void setSiteFileName(String siteFileName) {
		this.siteFileName = siteFileName;
	}

	public List<CdnWebsiteGroup> getCdnWebsiteGroups() {
		return cdnWebsiteGroups;
	}

	public void setCdnWebsiteGroups(List<CdnWebsiteGroup> cdnWebsiteGroups) {
		this.cdnWebsiteGroups = cdnWebsiteGroups;
	}
}
