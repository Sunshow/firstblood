package web.action.business;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts2.ServletActionContext;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.business.GroupKeywords;
import com.lehecai.admin.web.domain.business.GroupType;
import com.lehecai.admin.web.service.business.GroupKeywordsService;
import com.lehecai.admin.web.utils.ExcelUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.opensymphony.xwork2.Action;

/**
 * 分组关键词
 * @author He Wang
 *
 */
public class GroupKeywordsAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	
	private GroupKeywordsService groupKeywordsService;
	
	private List<GroupType> groupTypeList;
	private List<GroupKeywords> groupKeywordsList;
	
	//分组关键词对象
	private GroupKeywords groupKeywords;
	private Integer groupId;//分组id
	private String keywords;//关键词
	private Long id;//id用于删除
	
	private File excelFile;


	/**
	 * 条件并分页查询分组关键词
	 * @return
	 */
	public String handle() {
		logger.info("进入查询所有分组关键词");
		groupTypeList = GroupType.getItems();
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询所有分组关键词");
		groupTypeList = GroupType.getItems();
		HttpServletRequest request = ServletActionContext.getRequest();
        Map<String, Object> map = null;
        if (groupId == null) {
        	logger.error("分组id不能为空");
			super.setErrorMessage("分组id为空");
			return "failure";
        }
        if (groupId <= 0) {
        	logger.error("分组id必须大于0");
			super.setErrorMessage("分组id必须大于0");
			return "failure";
        }
        try {
        	GroupKeywords groupKeywords = new GroupKeywords();
        	groupKeywords.setGroupType(GroupType.getItem(groupId));
        	groupKeywords.setKeywords(keywords);
            map = groupKeywordsService.queryGroupKeywordsList(groupKeywords, super.getPageBean());
        } catch (ApiRemoteCallFailedException e) {
            logger.error("查询分组关键词,api调用异常" + e.getMessage());
            super.setErrorMessage("查询分组关键词,api调用异常" + e.getMessage());
            return "failure";
        }
        if (map == null || map.size() == 0) {
            logger.error("API查询分组关键词为空");
            super.setErrorMessage("API查询分组关键词为空");
            return "failure";
        }
        groupKeywordsList = ((List<GroupKeywords>) map.get(Global.API_MAP_KEY_LIST));
        PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
        super.setPageString(PageUtil.getPageString(request, pageBean));
        super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
        logger.info("查询分组关键词结束");
		return "list";
	}
	
	public String input() {
		logger.info("进入添加分组关键词");
		groupTypeList = GroupType.getItems();
		return "inputForm";
	}
	
	public String delete() {
		logger.info("进入删除分组关键词");
		JSONObject rs = new JSONObject();
		rs.put("flag", "0");
		if(id == null) {
			logger.error("分组关键词id为空");
			rs.put("msg", "分组关键词id为空");
		}
		try {
			GroupKeywords groupKeywords = new GroupKeywords();
			groupKeywords.setId(id);
			groupKeywordsService.del(groupKeywords);
			rs.put("flag", "1");
		} catch (ApiRemoteCallFailedException e) {
			logger.error("删除分组关键词,api调用异常" + e.getMessage());
			rs.put("msg", "删除分组关键词,api调用异常" + e.getMessage());
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("删除分组关键词结束");
		return Action.NONE;
	}
	
	public String manage() {
		logger.info("进入保存分组关键词");
		if (groupId == null) {
			logger.error("分组id为空");
			super.setErrorMessage("分组id为空");
			return "failure";
		}
		if (GroupType.getItem(groupId) == null) {
			logger.error("根据分组id未能获取分组，请联系技术人员");
			super.setErrorMessage("根据分组id未能获取分组，请联系技术人员");
			return "failure";
		}
		if (StringUtils.isEmpty(keywords)) {
			logger.error("关键词为空");
			super.setErrorMessage("关键词为空");
			return "failure";
		}
		GroupKeywords groupKeywords = new GroupKeywords();
		groupKeywords.setGroupType(GroupType.getItem(groupId));
		groupKeywords.setKeywords(keywords);
		try {
			groupKeywordsService.save(groupKeywords);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("api调用异常" + e.getMessage());
            super.setErrorMessage("api调用异常" + e.getMessage());
            return "failure";
		} catch (Exception e) {
			logger.error("api调用出现运行时异常" + e.getMessage());
            super.setErrorMessage("api调用出现运行时异常" + e.getMessage());
            return "failure";
		}
		logger.info("保存分组关键词结束");
        super.setSuccessMessage("保存分组关键词成功");
        super.setForwardUrl("/business/groupKeywords.do?action=query&groupId=" + groupId);
        return "success";
	}
	
	/**
	 * 批量添加
	 * @return
	 */
	public String batchInput() {
		return "batchInput";
	}
	
	public String batchConfirm() {
		if (excelFile == null) {
			logger.error("上传文件为空");
			super.setErrorMessage("上传文件为空");
			return "failure";
		}
		Workbook workbook = ExcelUtil.createWorkbook(excelFile);
		if (workbook == null) {
			logger.error("请上传xls或者xlsx格式的文件");
			super.setErrorMessage("请上传xls或者xlsx格式的文件");
			return "failure";
		}
		Sheet sheet = workbook.getSheetAt(0);
		if (sheet == null) {
			logger.error("获取Excel表Sheet错误");
			super.setErrorMessage("获取Excel表Sheet错误");
			return "failure";
		}
		if (sheet.getPhysicalNumberOfRows() <= 1) {
			logger.error("上传文件格式不对");
			super.setErrorMessage("上传文件格式不对");
			return "failure";
		}
		if(groupKeywordsList == null){
			groupKeywordsList = new ArrayList<GroupKeywords>();
		}
		
		String keywords = "";
		int groupId = -1;
		List<GroupKeywords> groupKeywordsList = new ArrayList<GroupKeywords>();
		Map<String, GroupKeywords> exsitGroupKeywordsMap = new HashMap<String, GroupKeywords>();
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			try {
				GroupKeywords groupKeywords = new GroupKeywords();
				//分组id
				groupId = Integer.valueOf(ExcelUtil.getCellValue(sheet.getRow(i).getCell(0)));
				keywords = ExcelUtil.getCellValue(sheet.getRow(i).getCell(1));
				String key = groupId + "_" + keywords;
				if (GroupType.getItem(groupId) != null && !StringUtils.isEmpty(keywords) && exsitGroupKeywordsMap.get(key) == null) {
					groupKeywords.setGroupType(GroupType.getItem(groupId));
					groupKeywords.setKeywords(keywords);
					groupKeywordsList.add(groupKeywords);
					exsitGroupKeywordsMap.put(key, groupKeywords);
				}
			} catch (Exception e) {
				logger.error("获取敏感词错误，{}", e);
				continue;
			}
		}
		if (groupKeywordsList.size() == 0) {
			logger.error("上传文件中未能获取有效分组关键词");
			super.setErrorMessage("上传文件中未能获取有效分组关键词");
			return "failure";
		}
		this.groupKeywordsList = groupKeywordsList;
		return "batchInput";
	}
	
	public String batchManage() {
		
		List<String> successList = new ArrayList<String>();
		List<String> failureList = new ArrayList<String>();
		Map<String, String> insertIdMap = new HashMap<String, String>();
		
		if (groupKeywordsList != null && groupKeywordsList.size() > 0) {
			for (GroupKeywords groupKeywords : groupKeywordsList) {
				groupKeywords.setGroupType(GroupType.getItem(groupKeywords.getGroupId()));
			}
			try {
				groupKeywordsService.batchSave(groupKeywordsList, successList, failureList, insertIdMap);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("api调用出现异常，原因{}", e.getMessage());
				super.setErrorMessage("api调用出现异常，原因" + e.getMessage());
				return "failure";
			} catch (Exception e) {
				logger.error("api调用出现运行时异常,原因{}" + e.getMessage());
				super.setErrorMessage("api调用出现运行时异常，原因" + e.getMessage());
				return "failure";
			}
		}
		int sucNum = successList.size();
		int failNum = failureList.size();
		if (sucNum + failNum != groupKeywordsList.size()) {
			failNum = groupKeywordsList.size() - sucNum;
		}
		super.setForwardUrl("/business/groupKeywords.do");
		super.setSuccessMessage("本次共导入"+groupKeywordsList.size()+"个敏感词，其中成功导入" + sucNum + "个，失败" + failNum + "个。");
		return "success";
	}

	public void setGroupKeywordsService(GroupKeywordsService groupKeywordsService) {
		this.groupKeywordsService = groupKeywordsService;
	}

	public GroupKeywordsService getGroupKeywordsService() {
		return groupKeywordsService;
	}

	public void setGroupTypeList(List<GroupType> groupTypeList) {
		this.groupTypeList = groupTypeList;
	}

	public List<GroupType> getGroupTypeList() {
		return groupTypeList;
	}

	public void setGroupKeywordsList(List<GroupKeywords> groupKeywordsList) {
		this.groupKeywordsList = groupKeywordsList;
	}

	public List<GroupKeywords> getGroupKeywordsList() {
		return groupKeywordsList;
	}

	public void setGroupKeywords(GroupKeywords groupKeywords) {
		this.groupKeywords = groupKeywords;
	}

	public GroupKeywords getGroupKeywords() {
		return groupKeywords;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	
	public File getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}
}
