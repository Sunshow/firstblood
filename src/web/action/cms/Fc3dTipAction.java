package web.action.cms;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.cms.Fc3dTip;
import com.lehecai.admin.web.utils.FileUtil;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.util.CoreDateUtils;

public class Fc3dTipAction extends BaseAction {
	private static final long serialVersionUID = 4603342995038571287L;
	private Logger logger = LoggerFactory.getLogger(Fc3dTipAction.class);
	
	private List<String> targets;		//指标
	private List<String> currentOmits;	//当前遗漏
	private List<String> historyValues;	//历史峰值
	private List<Fc3dTip> fc3dTips;
	
	private String staticDir;
	
	private String updateTime;
	
	/**
	 * 转向福彩3D小贴士录入
	 * @return
	 */
	public String handle() {
		logger.info("进入录入福彩3D小贴士数据");
		String webRoot = "";
		try {
			webRoot = WebUtils.getRealPath(ServletActionContext.getServletContext(), "");
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("webRoot：{}", webRoot);
		
		FileUtil.mkdir(webRoot + this.staticDir);
		
		String filePath = webRoot + this.staticDir + LotteryType.FC3D.getValue()+".json";
		filePath = filePath.replace('/', File.separatorChar);
		logger.info("filePath：{}", filePath);
		
		String jsonObjectString = "";
		File file = new File(filePath);
		if (!file.exists()) {
			logger.info("{}文件不存在", filePath);
			return "inputForm";
		}
		
		jsonObjectString = FileUtil.read(filePath);
		logger.info("jsonObjectString：{}", jsonObjectString);
		if (jsonObjectString == null || "".equals(jsonObjectString)) {
			logger.info("福彩3d小贴士对应的json字符串为空");
			return "inputForm";
		}
		
		fc3dTips = Fc3dTip.convertFromJsonObjectString(jsonObjectString);
		if (fc3dTips == null || fc3dTips.size() == 0) {
			logger.info("暂无福彩3d小贴士对应的数据");
			return "inputForm";
		}
		JSONObject jsonObject = JSONObject.fromObject(jsonObjectString);
		
		if (jsonObject.get(Global.KEY_UPDATE_TIME) != null) {
			updateTime = jsonObject.get(Global.KEY_UPDATE_TIME).toString();
		}
		logger.info("录入福彩3D小贴士数据结束");
		return "inputForm";
	}
	
	/**
	 * 添加福彩3D小贴士
	 * @return
	 */
	public String manage() {
		logger.info("进入更新福彩3D小贴士");
		if (targets == null || targets.size() == 0) {
			logger.error("指标为空");
			super.setErrorMessage("指标不能为空");
			return "failure";
		}
		if (currentOmits == null || currentOmits.size() == 0) {
			logger.error("当前遗漏为空");
			super.setErrorMessage("当前遗漏不能为空");
			return "failure";
		}
		if (historyValues == null || historyValues.size() == 0) {
			logger.error("历史峰值为空");
			super.setErrorMessage("历史峰值不能为空");
			return "failure";
		}
		
		String ja = Fc3dTip.toJsonArrayStr(targets, currentOmits, historyValues,CoreDateUtils.formatDateTime(new Date()));
		
		if(ja == null || ja.equals("")) {
			logger.info("转换后的json字符串为空");
			return "inputForm";
		}
		
		String webRoot = "";
		try {
			webRoot = WebUtils.getRealPath(ServletActionContext.getServletContext(), "");
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("webRoot：{}", webRoot);
		
		FileUtil.mkdir(webRoot + this.staticDir);
		
		String filePath = webRoot + this.staticDir + LotteryType.FC3D.getValue()+".json";
		filePath = filePath.replace('/', File.separatorChar);
		logger.info("filePath：{}"+filePath);
		
		FileUtil.write(filePath, ja.toString());
		
		super.setForwardUrl("/cms/fc3dTip.do");
		logger.info("添加福彩3D小贴士结束");
		return "success";
	}
	
	public List<String> getTargets() {
		return targets;
	}
	public void setTargets(List<String> targets) {
		this.targets = targets;
	}
	public List<String> getCurrentOmits() {
		return currentOmits;
	}
	public void setCurrentOmits(List<String> currentOmits) {
		this.currentOmits = currentOmits;
	}
	public List<String> getHistoryValues() {
		return historyValues;
	}
	public void setHistoryValues(List<String> historyValues) {
		this.historyValues = historyValues;
	}
	public void setFc3dTips(List<Fc3dTip> fc3dTips) {
		this.fc3dTips = fc3dTips;
	}
	public List<Fc3dTip> getFc3dTips() {
		return fc3dTips;
	}
	public void setStaticDir(String staticDir) {
		this.staticDir = staticDir;
	}
	public String getStaticDir() {
		return staticDir;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
}
