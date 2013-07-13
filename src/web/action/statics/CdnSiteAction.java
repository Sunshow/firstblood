package web.action.statics;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cdn.CdnWebsiteGroup;
import com.lehecai.admin.web.domain.cdn.CdnWebsiteItem;
import com.lehecai.admin.web.enums.CdnType;
import com.lehecai.admin.web.service.statics.CdnCacheService;

public class CdnSiteAction extends BaseAction {
	private static final long serialVersionUID = -318384970308376825L;
	private Logger logger = LoggerFactory.getLogger(CdnSiteAction.class);
	
	private CdnCacheService cdnCacheService;
	
	private List<CdnWebsiteGroup> groupList;
	
	private CdnWebsiteGroup cdnWebsiteGroup;
	
	private Integer cdnClecsValue;
	
	private CdnWebsiteItem cdnWebsiteItem;
	
	private String jsonPath;
	
	private String groupFileName;
	
	public String handle() {
		logger.info("进入查询CDN站点组列表");
		groupList = cdnCacheService.cdnWebsiteGroupList(jsonPath, groupFileName);
		return "list";
	}
	
	public String groupManage() {
		logger.info("进入更新CDN站点组");
		groupList = cdnCacheService.cdnWebsiteGroupList(jsonPath, groupFileName);
		super.setForwardUrl("/statics/cdnSite.do");
		if(cdnWebsiteGroup != null){
			if (cdnWebsiteGroup.getId() != null && !cdnWebsiteGroup.getId().isEmpty()) {
				for(int i = 0;i<groupList.size();i++){
					CdnWebsiteGroup fc = (CdnWebsiteGroup)groupList.get(i);
					if(cdnWebsiteGroup.getId().equals(fc.getId())){
						fc.setName(cdnWebsiteGroup.getName());
						fc.setMemo(cdnWebsiteGroup.getMemo());
						cdnCacheService.manage(groupList, jsonPath, groupFileName);
						
						logger.info("修改CDN站点组成功");
						super.setSuccessMessage("修改CDN站点组成功");
						return "success";
					}
				}
				logger.error("修改CDN站点组失败");
				super.setErrorMessage("修改CDN站点组失败");
				return "failure";
			} else {
				cdnWebsiteGroup.setId(getTimestamp()+"");
				groupList.add(cdnWebsiteGroup);
				cdnCacheService.manage(groupList, jsonPath, groupFileName);
				
				logger.info("添加CDN站点组成功");
				super.setSuccessMessage("添加CDN站点组成功");
			}
		} else {
			logger.error("更新CDN站点组，提交的表单为空");
			super.setErrorMessage("更新CDN站点组，提交的表单不能为空");
			return "failure";
		}
		logger.info("更新CDN站点组结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入CDN站点组信息");
		if(cdnWebsiteGroup != null){
			if(cdnWebsiteGroup.getId() != null && !cdnWebsiteGroup.getId().isEmpty()){
				groupList = cdnCacheService.cdnWebsiteGroupList(jsonPath, groupFileName);
				for(int i = 0;i<groupList.size();i++){
					CdnWebsiteGroup f = (CdnWebsiteGroup)groupList.get(i);
					if(cdnWebsiteGroup.getId().equals(f.getId())){
						cdnWebsiteGroup = f;
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
		logger.info("进入删除CDN站点组");
		super.setForwardUrl("/statics/cdnSite.do");
		if(cdnWebsiteGroup != null){
			if(cdnWebsiteGroup.getId() != null && !cdnWebsiteGroup.getId().isEmpty()){
				groupList = cdnCacheService.cdnWebsiteGroupList(jsonPath, groupFileName);
				for(int i = 0;i<groupList.size();i++){
					CdnWebsiteGroup fc = (CdnWebsiteGroup)groupList.get(i);
					if(cdnWebsiteGroup.getId().equals(fc.getId())){
						groupList.remove(i);
						cdnCacheService.manage(groupList, jsonPath, groupFileName);
						
						logger.info("删除CDN站点组成功");
						super.setSuccessMessage("删除CDN站点组成功");
						return "success";
					}
				}
			}
		}
		logger.error("删除CDN站点组失败");
		super.setErrorMessage("删除CDN站点组失败");
		
		logger.info("删除CDN站点组结束");
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
		logger.info("进入输入CDN站点域");
		if(cdnWebsiteGroup != null){
			if(cdnWebsiteGroup != null && cdnWebsiteGroup.getId() != null && !cdnWebsiteGroup.getId().isEmpty()
					&& cdnWebsiteItem != null && cdnWebsiteItem.getId() != null && cdnWebsiteItem.getId() != 0
			){
				groupList = cdnCacheService.cdnWebsiteGroupList(jsonPath, groupFileName);
				for(int i = 0;i<groupList.size();i++){
					CdnWebsiteGroup f = (CdnWebsiteGroup)groupList.get(i);
					if(cdnWebsiteGroup.getId().equals(f.getId())){
						List<CdnWebsiteItem> items = f.getItemList();
						for (int j = 0; j < items.size(); j++) {
							CdnWebsiteItem item = items.get(j);
							if (item.getId().longValue() == cdnWebsiteItem.getId().longValue()) {
								cdnWebsiteItem = item;
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
		logger.info("进入更新CDN站点域");
		groupList = cdnCacheService.cdnWebsiteGroupList(jsonPath, groupFileName);
		super.setForwardUrl("/statics/cdnSite.do");
		if(cdnWebsiteGroup != null){
			if (cdnWebsiteGroup.getId() != null && !cdnWebsiteGroup.getId().isEmpty()) {
				if (cdnWebsiteItem != null && cdnWebsiteItem.getId() != null && cdnWebsiteItem.getId() != 0) {
					for(int i = 0;i<groupList.size();i++){
						CdnWebsiteGroup ccg = (CdnWebsiteGroup)groupList.get(i);
						if(cdnWebsiteGroup.getId().equals(ccg.getId())){
							List<CdnWebsiteItem> itemList = ccg.getItemList();
							for (int j = 0; j < itemList.size(); j++) {
								CdnWebsiteItem cci = itemList.get(j);
								if (cdnWebsiteItem.getId().longValue() == cci.getId().longValue()) {
									cci.setName(cdnWebsiteItem.getName());
									cci.setSiteUrl(cdnWebsiteItem.getSiteUrl());
									if (cdnClecsValue != null && cdnClecsValue != 0 && cdnClecsValue != CdnType.ALL.getValue()) {
										cci.setCdnClecs(CdnType.getItem(cdnClecsValue));
									} else {
										logger.info("运营商输入错误");
										super.setSuccessMessage("运营商输入错误");
										return "failure";
									}
									cdnCacheService.manage(groupList, jsonPath, groupFileName);
									logger.info("修改CDN站点域成功");
									super.setSuccessMessage("修改CDN站点域成功");
									return "success";
								}
							}
							logger.error("未找到CDN站点域");
							super.setErrorMessage("修改站点域错误，未找到CDN站点域");
							return "failure";
						}
					}
				} else {
					for(int i = 0;i<groupList.size();i++){
						CdnWebsiteGroup fc = (CdnWebsiteGroup)groupList.get(i);
						if(cdnWebsiteGroup.getId().equals(fc.getId())){
							cdnWebsiteItem.setId(getTimestamp());
							CdnType ct = null;
							if (cdnClecsValue != null && cdnClecsValue != CdnType.ALL.getValue()) {
								ct = CdnType.getItem(cdnClecsValue);
							} else {
								ct = CdnType.CHINACACHE;
							}
							cdnWebsiteItem.setCdnClecs(ct);
							fc.getItemList().add(cdnWebsiteItem);
							cdnCacheService.manage(groupList, jsonPath, groupFileName);
							logger.info("添加CDN站点域成功");
							super.setSuccessMessage("添加CDN站点域成功");
							return "success";
						}
					}
					logger.error("添加站点域错误，未找到站点组");
					super.setErrorMessage("添加站点域错误，未找到站点组");
					return "failure";
				}
			} else {
				logger.error("CDN站点组ID为空");
				super.setErrorMessage("CDN站点组ID不能为空");
				return "failure";
			}
		} else {
			logger.error("更新CDN站点域，提交的表单为空");
			super.setErrorMessage("更新CDN站点域，提交的表单不能为空");
		}
		logger.info("更新CDN站点域结束");
		return "failure";
	}
	
	public String delItem() {
		logger.info("进入删除CDN站点域");
		groupList = cdnCacheService.cdnWebsiteGroupList(jsonPath, groupFileName);
		super.setForwardUrl("/statics/cdnSite.do");
		if(cdnWebsiteGroup != null){
			if(cdnWebsiteGroup != null && cdnWebsiteGroup.getId() != null && !cdnWebsiteGroup.getId().isEmpty()
					&& cdnWebsiteItem != null && cdnWebsiteItem.getId() != null && cdnWebsiteItem.getId() != 0
			){
				for(int i = 0;i<groupList.size();i++){
					CdnWebsiteGroup f = (CdnWebsiteGroup)groupList.get(i);
					if(cdnWebsiteGroup.getId().equals(f.getId())){
						List<CdnWebsiteItem> items = f.getItemList();
						for (int j = 0; j < items.size(); j++) {
							CdnWebsiteItem item = items.get(j);
							if (item.getId().longValue() == cdnWebsiteItem.getId().longValue()) {
								items.remove(j);
								cdnCacheService.manage(groupList, jsonPath, groupFileName);
								
								logger.info("删除CDN站点域成功");
								super.setSuccessMessage("删除CDN站点域成功");
								return "success";
							}
						}
					}
				}
			}
		}
		logger.error("删除CDN站点域失败");
		super.setErrorMessage("删除CDN站点域站点域失败");
		
		logger.info("删除CDN站点域结束");
		return "failure";
	}

	public CdnCacheService getCdnCacheService() {
		return cdnCacheService;
	}

	public void setCdnCacheService(CdnCacheService cdnCacheService) {
		this.cdnCacheService = cdnCacheService;
	}

	public List<CdnWebsiteGroup> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<CdnWebsiteGroup> groupList) {
		this.groupList = groupList;
	}

	public CdnWebsiteGroup getCdnWebsiteGroup() {
		return cdnWebsiteGroup;
	}

	public void setCdnWebsiteGroup(CdnWebsiteGroup cdnWebsiteGroup) {
		this.cdnWebsiteGroup = cdnWebsiteGroup;
	}

	public CdnWebsiteItem getCdnWebsiteItem() {
		return cdnWebsiteItem;
	}

	public void setCdnWebsiteItem(CdnWebsiteItem cdnWebsiteItem) {
		this.cdnWebsiteItem = cdnWebsiteItem;
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
	
	public List<CdnType> getCdnClecses() {
		List<CdnType> cdnClecses = CdnType.list;
		cdnClecses.remove(CdnType.ALL);
		return cdnClecses;
	}

	public Integer getCdnClecsValue() {
		return cdnClecsValue;
	}

	public void setCdnClecsValue(Integer cdnClecsValue) {
		this.cdnClecsValue = cdnClecsValue;
	}
}
