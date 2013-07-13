package web.action.business;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.business.Swc;
import com.lehecai.admin.web.enums.SwcFilterItem;
import com.lehecai.admin.web.service.business.SwcService;
import com.lehecai.admin.web.utils.ExcelUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.EnabledStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.util.CoreDateUtils;

/**
 * 后台敏感词Action
 * @author yanweijie
 *
 */
public class SwcAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(SwcAction.class);
	
	private SwcService swcService;
	
	private List<Swc> swcList;
	
	private Swc swc;											//敏感词对象
	
	private String name;										//敏感词
	private String oldName;										//修改之前的敏感词
	private int status = EnabledStatus.ALL.getValue();			//敏感词状态(默认全部用于查询)
	
	private String str;			//被检测字符串
	private String filterStr;	//过滤后字符串
	private String word;		//敏感词
	private int[] flag;
	
	private File excelFile;

	/**
	 * 条件并分页查询敏感词
	 * @return
	 */
	public String handle() {
		logger.info("进入查询所有敏感词");
		return "list";
	}
	
	/**
	 * 条件并分页查询敏感词
	 * @return
	 */
	public String query() {
		logger.info("进入查询所有敏感词");
		swcList = swcService.findSwcList(name, status, super.getPageBean());//多条件并分页查询所有敏感词
		PageBean pageBean = swcService.getPageBean(super.getPageBean(), name,status);//封装分页对象
		
		HttpServletRequest request = ServletActionContext.getRequest();
		super.setPageString(PageUtil.getPageString(request, pageBean));
		
		return "list";
	}
	
	/**
	 * 转向添加/修改
	 */
	public String input() {
		logger.info("进入输入敏感词");
		if (swc != null && (swc.getId() != null && swc.getId() != 0L)) {//转向修改
			swc = swcService.getById(swc.getId());
			oldName = swc.getName();
		}
		
		return "inputForm";
	}
	
	/**
	 * 转向添加/修改
	 */
	public String batchInput() {
		return "batchInput";
	}
	
	public String batchManage() {
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
		if(swcList == null){
			swcList = new ArrayList<Swc>();
		}
		String name = "";
		long total = 0,success = 0,repeat = 0;
		for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
			try {
				name = ExcelUtil.getCellValue(sheet.getRow(i).getCell(0));		//敏感词
				name = name.replaceAll("\\；", ";");//替换中文分号
				name = name.replaceAll("\\，", ";");//替换中文逗号
				name = name.replaceAll("\\,", ";");//替换英文逗号
			} catch (Exception e) {
				logger.error("获取敏感词错误，{}", e);
				continue;
			}
			if(!StringUtils.isEmpty(name)){
				String[] nameArray = name.split(";");
				for(String str : nameArray){
					total++;
					Swc tempswc = swcService.getByName(str);//查询敏感词是否存在
					if(tempswc != null){
						repeat++;
					}else{
						Swc swcTmp = new Swc();
						swcTmp.setName(str);
						swcTmp.setStatus(EnabledStatus.ENABLED);//设置敏感词状态(默认为禁用)
						swcTmp.setCreateTime(new Date());//设置添加时间
						swcService.merge(swcTmp);
						success++;
					}
				}
			}
		}
		super.setForwardUrl("/business/swc.do");
		super.setSuccessMessage("本次共导入"+total+"个敏感词，其中成功导入"+success+"个，重复"+repeat+"个。");
		return "success";
	}

	/**
	 * 添加/修改敏感词
	 * @return
	 */
	public String manage() {
		logger.info("进入更新敏感词");
		if (swc != null) {
			if (swc.getId() != null && swc.getId() != 0L) { //修改
				if (!swc.getName().equals(oldName)) {//如果敏感词有修改
					Swc tempswc = swcService.getByName(swc.getName());//查询敏感词是否存在
					if (tempswc != null) {
						logger.error("{}已经存在", swc.getName());
						super.setErrorMessage(swc.getName() + "已经存在");
						return "failure";
					}
				}
				swc.setUpdateTime(new Date());//设置修改时间
				
				swc.setStatus(EnabledStatus.getItem(status));//设置敏感词状态
				swcService.merge(swc);//修改敏感词
			} else {//添加
				Swc tempswc = swcService.getByName(swc.getName());//查询敏感词是否存在
				if (tempswc != null) {
					logger.error("{}已经存在！", swc.getName());
					super.setErrorMessage(swc.getName() + "已经存在");
					return "failure";
				}
				
				swc.setCreateTime(new Date());//设置添加时间
				swc.setStatus(EnabledStatus.DISABLED);//设置敏感词状态(默认为禁用)
				
				swcService.merge(swc);//添加到数据库
			}
		} else {
			logger.error("更新敏感词，提交表单为空");
			super.setErrorMessage("更新敏感词，提交表单为空");
			return "failure";
		}
		
		super.setForwardUrl("/business/swc.do");
		logger.info("更新敏感词结束");
		return "success";
	}
	
	/**
	 * 调用接口添加、删除敏感词
	 * @return
	 */
	public String manageStatus() {
		logger.info("进入修改敏感词状态");
		int code = ApiConstant.RC_SUCCESS;
		if (swc != null && (swc.getId() != null && swc.getId() != 0L)) {
			swc = swcService.getById(swc.getId());//根据敏感词编号查询敏感词对象
			swc.setUpdateTime(new Date());	//设置修改时间
			
			if (status == EnabledStatus.DISABLED.getValue()) {//当前状态为禁用
				swc.setStatus(EnabledStatus.ENABLED);	//设置状态为启用
				try {
					swcService.save(swc);//调用接口添加敏感词，并修改敏感词状态为启用
				} catch (ApiRemoteCallFailedException e) {
					code = ApiConstant.RC_FAILURE;
					logger.error("添加敏感词，api调用异常，{}", e.getMessage());
					super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
					return "failure";
				}
			} else if (status == EnabledStatus.ENABLED.getValue()) {//当前状态为启用
				swc.setStatus(EnabledStatus.DISABLED);		//设置状态为禁用
				try {
					swcService.del(swc);//调用接口删除敏感词，并修改敏感词状态为禁用
				} catch (ApiRemoteCallFailedException e) {
					code = ApiConstant.RC_FAILURE;
					logger.error("删除敏感词，api调用异常，{}", e.getMessage());
					super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
					return "failure";
				}
			}
		}
		
		JSONObject rs = new JSONObject();
		rs.put("code", code);
		rs.put("status", swc.getStatus());
		rs.put("updateTime", CoreDateUtils.formatDate(swc.getUpdateTime(), CoreDateUtils.DATETIME));
		
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("修改敏感词状态结束");
		return null;
	}
	
	
	/**
	 * 转向检测敏感词
	 */
	public String inputCheck() {
		logger.info("进入输入检测字符串");
		return "check";
	}
	
	/**
	 * 检测敏感词
	 * @return
	 */
	public String check() {
		logger.info("进入检测字符串");
		if ((str != null && !str.equals("")) && (flag.length != 0)) {
			int checkFlag = 0;
			for(int tempFlag : flag) {	//遍历所有过滤选项
				if (tempFlag == SwcFilterItem.PMF_FILTER_ALL.getValue() || tempFlag == SwcFilterItem.PMF_FILTER_NONE.getValue()) {
					checkFlag = tempFlag;
					break;
				} else {
					checkFlag += tempFlag;
				}
			}
			
			Map<String, String> map = null;
			try {
				map = swcService.check(str, checkFlag);//检测输入的字符串是否有敏感词，返回包含的第一个敏感词
			} catch (ApiRemoteCallFailedException e) {
				logger.error("检测敏感词，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
			if (map != null) {
				word = map.get(Global.API_MAP_KEY_WORD);
				filterStr = map.get(Global.API_MAP_KEY_FILTER_STRING);
				if (word != null && !word.equals("")) {
					filterStr = filterStr.replace(word, "<font class='red spanmargin'>" + word + "</font>");//替换检测字符串中的敏感词以红色字体显示
				}
			} else {
				filterStr = str;
			}
		} else {
			logger.error("检测敏感词，必要参数为空！");
			super.setErrorMessage("检测敏感词，必要参数为空！");
			return "failure";
		}
		logger.info("检测字符串结束");
		return "check";
	}
	
	public List<EnabledStatus> getEnabledStatusItemsForQuery(){
		return EnabledStatus.getItemsForQuery();
	}
	
	public List<EnabledStatus> getEnabledStatusItems(){
		return EnabledStatus.getItems();
	}
	
	public List<SwcFilterItem> getSwcFilterItems(){
		return SwcFilterItem.list;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public SwcService getSwcService() {
		return swcService;
	}

	public void setSwcService(SwcService swcService) {
		this.swcService = swcService;
	}

	public List<Swc> getSwcList() {
		return swcList;
	}

	public void setSwcList(List<Swc> swcList) {
		this.swcList = swcList;
	}

	public Swc getSwc() {
		return swc;
	}

	public void setSwc(Swc swc) {
		this.swc = swc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public String getFilterStr() {
		return filterStr;
	}

	public void setFilterStr(String filterStr) {
		this.filterStr = filterStr;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int[] getFlag() {
		return flag;
	}

	public void setFlag(int[] flag) {
		this.flag = flag;
	}

	public File getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}
}
