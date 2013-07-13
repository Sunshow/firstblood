package web.action.ticket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.service.ticket.TerminalConfigService;
import com.lehecai.admin.web.service.ticket.TerminalService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.EnabledStatus;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.bean.common.TimeRegion;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.engine.entity.terminal.Terminal;
import com.lehecai.engine.entity.terminal.TerminalConfig;
import com.lehecai.engine.entity.terminal.TerminalType;

public class TerminalAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private Logger logger = LoggerFactory.getLogger(TerminalAction.class);
	
	private TerminalService terminalService;
	
	private Terminal terminal;
	
	private List<Terminal> terminalList;
	
	private Integer terminalTypeId;
	private Integer enabledStatusId;
	private Integer pausedStatusId;
	
	private String idString;
	private List<EnabledStatus> enabledStatusList;
	private List<TerminalType> terminalTypeList;
	private List<TerminalConfig> terminalConfigList;
	private List<LotteryType> lotteryTypeList;
	private TerminalConfig terminalConfig;
	private TerminalConfigService terminalConfigService;

	public String handle() {
		logger.info("进入查询终端列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		PageBean page = super.getPageBean();
		page.setPageSize(100);
		if (terminal == null) {
			terminal = new Terminal();
		}
		terminalTypeList = TerminalType.getItems();
		enabledStatusList = EnabledStatus.getItemsForQuery();
		if (enabledStatusId == null) {
			enabledStatusId = EnabledStatus.ENABLED.getValue();
		}
		terminal.setIsEnabled(EnabledStatus.getItem(enabledStatusId));
		if (terminalTypeId != null && terminalTypeId != TerminalType.ALL.getValue()) {
			terminal.setTerminalType(TerminalType.getItem(terminalTypeId));
		}
		if (pausedStatusId != null && pausedStatusId != YesNoStatus.ALL.getValue()) {
			terminal.setIsPaused(YesNoStatus.getItem(pausedStatusId));
		}
		terminalList = terminalService.list(terminal, page);
		PageBean pageBean = terminalService.getPageBean(terminal, page);
		super.setPageString(PageUtil.getPageString(request, pageBean));
		return "list";
	}

	protected void preprocessTerminal(Terminal terminal) {
		if (terminal == null) {
			return;
		}
		if (terminal.getAllotForbidPeriod() != null) {
			TimeRegion timeRegion = TimeRegion.parse(terminal.getAllotForbidPeriod());
			terminal.setAllotForbidPeriod(timeRegion == null ? null : timeRegion.toString());
		}
		if (terminal.getSendForbidPeriod() != null) {
			TimeRegion timeRegion = TimeRegion.parse(terminal.getSendForbidPeriod());
			terminal.setSendForbidPeriod(timeRegion == null ? null : timeRegion.toString());
		}
	}
	
	public String manage() {
		logger.info("进入更新终端信息");
		if (terminal != null) {
			if (terminal.getName() == null || "".equals(terminal.getName())) {
				logger.error("终端名称为空");
				super.setErrorMessage("终端名称不能为空");
				return "failure";
			}		
			terminal.setTerminalType(TerminalType.getItem(terminalTypeId));
			terminal.setIsEnabled(EnabledStatus.getItem(enabledStatusId));
			terminal.setIsPaused(YesNoStatus.getItem(pausedStatusId));
			
			this.preprocessTerminal(terminal);
			
			terminalService.manage(terminal);
		} else {
			logger.error("添加终端错误，提交表单为空");
			super.setErrorMessage("添加终端错误，提交表单不能为空");
			return "failure";
		}
		super.setForwardUrl("/ticket/terminal.do");
		logger.info("更新终端信息结束");
		return "success";
	}
	
	public String input() {
		logger.info("进入输入终端信息");
		if (terminal != null) {
			if (terminal.getId() != null) {			
				terminal = terminalService.get(terminal.getId());
				terminalTypeId = terminal.getTerminalType().getValue();
				enabledStatusId = terminal.getIsEnabled().getValue();
				pausedStatusId = terminal.getIsPaused().getValue();
			}
		} else {
			terminal = new Terminal();
		}
		return "inputForm";
	}
	
	public String view() {
		logger.info("进入查看终端详情");
		if (terminal != null && terminal.getId() != null) {
			terminal = terminalService.get(terminal.getId());
		} else {
			logger.error("查看终端详情，编码为空");
			super.setErrorMessage("查看终端详情，编码不能为空");
			return "failure";
		}
		logger.info("查看终端详情结束");
		return "view";
	}
	
	public String edit(){
		logger.info("进入终端编辑彩种！");
		HttpServletRequest request = ServletActionContext.getRequest();
		if (terminalConfig == null) {
			terminalConfig = new TerminalConfig();
		}
		if (terminal.getId() != null) {
			terminalConfig.setTerminalId(terminal.getId());
			terminalConfigList = terminalConfigService.listByTerminalType(terminalConfig, super.getPageBean());
			PageBean pageBean = terminalConfigService.getPageBean(terminalConfig, super.getPageBean());
			super.setPageString(PageUtil.getPageString(request, pageBean));
			List<LotteryType> originalList = OnSaleLotteryList.get();
			List<LotteryType> targetList = new ArrayList<LotteryType>();
			for (int i=0; i<originalList.size(); i++) {
				targetList.add(originalList.get(i));
			}
			for (TerminalConfig tempConfig : terminalConfigList) {
				for (int i=0; i<targetList.size(); i++) {
					LotteryType type = targetList.get(i);
					if (type.getValue() == tempConfig.getLotteryType().getValue()) {
						targetList.remove(i);
						break;
					}
				}
			}  
			lotteryTypeList = targetList;
		} else {
			logger.error("没有查找到对应的终端");
			super.setErrorMessage("没有查找到对应的终端");
			super.setForwardUrl("/ticket/terminal.do");
			return "failure";
		}
		return "edit";
	}
	
	public String batchDelete(){
		logger.info("进入批量删除彩种！");
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject obj = new JSONObject();
		obj.put("code", -1);
		obj.put("msg", "删除失败");
		if(idString == null || idString == ""){
			logger.error("删除的彩种为空");
			obj.put("code", -1);
			obj.put("msg", "删除的彩种为空！");
			writeRs(response, obj);
			return null;
		}
		List<String> deleteLotteryType =  Arrays.asList(idString.split(","));
		try {
			ResultBean resultBean = terminalService.deleteLotteryType(deleteLotteryType);
			obj.put("code", resultBean.getCode());
			obj.put("msg", resultBean.getMessage());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("删除彩期，api调用异常，{}", e.getMessage());
			obj.put("code", -1);
			obj.put("msg", "api调用异常，请联系技术人员!原因：" + e.getMessage());
			writeRs(response, obj);
		}
		writeRs(response, obj);
		return null;
	}
	
	public String update() {
		logger.info("进入设置终端状态");
		if (terminal != null && terminal.getId() != null) {
			terminal = terminalService.get(terminal.getId());
			terminal.setIsEnabled(terminal.getIsEnabled() == EnabledStatus.ENABLED?EnabledStatus.DISABLED:EnabledStatus.ENABLED);
			this.preprocessTerminal(terminal);
			terminalService.manage(terminal);
		} else {
			logger.error("设置终端状态，编码为空");
			super.setErrorMessage("设置终端状态，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/ticket/terminal.do");
		logger.info("设置终端状态结束");
		return "forward";
	}
	
	public String del() {
		logger.info("进入删除终端");
		if (terminal != null && terminal.getId() != null) {
			terminal = terminalService.get(terminal.getId());
			terminalService.del(terminal);
		} else {
			logger.error("删除终端，编码为空");
			super.setErrorMessage("删除终端，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/ticket/terminal.do");
		logger.info("删除终端结束");
		return "forward";
	}
	
	public TerminalConfigService getTerminalConfigService() {
		return terminalConfigService;
	}

	public void setTerminalConfigService(TerminalConfigService terminalConfigService) {
		this.terminalConfigService = terminalConfigService;
	}
	
	public TerminalService getTerminalService() {
		return terminalService;
	}

	public void setTerminalService(TerminalService terminalService) {
		this.terminalService = terminalService;
	}

	public Terminal getTerminal() {
		return terminal;
	}

	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}

	public List<Terminal> getTerminalList() {
		return terminalList;
	}

	public void setTerminalList(List<Terminal> terminalList) {
		this.terminalList = terminalList;
	}

	public List<TerminalType> getTerminalTypes() {
		return TerminalType.getItems();
	}
	
	public List<EnabledStatus> getEnabledStatuses() {
		return EnabledStatus.getItems();
	}

	public Integer getTerminalTypeId() {
		return terminalTypeId;
	}

	public void setTerminalTypeId(Integer terminalTypeId) {
		this.terminalTypeId = terminalTypeId;
	}

	public Integer getEnabledStatusId() {
		return enabledStatusId;
	}

	public void setEnabledStatusId(Integer enabledStatusId) {
		this.enabledStatusId = enabledStatusId;
	}

	public Integer getPausedStatusId() {
		return pausedStatusId;
	}

	public void setPausedStatusId(Integer pausedStatusId) {
		this.pausedStatusId = pausedStatusId;
	}

	public void setEnabledStatusList(List<EnabledStatus> enabledStatusList) {
		this.enabledStatusList = enabledStatusList;
	}

	public List<EnabledStatus> getEnabledStatusList() {
		return enabledStatusList;
	}

	public void setTerminalTypeList(List<TerminalType> terminalTypeList) {
		this.terminalTypeList = terminalTypeList;
	}

	public List<TerminalType> getTerminalTypeList() {
		return terminalTypeList;
	}

	public void setTerminalConfig(TerminalConfig terminalConfig) {
		this.terminalConfig = terminalConfig;
	}

	public TerminalConfig getTerminalConfig() {
		return terminalConfig;
	}

	public void setIdString(String idString) {
		this.idString = idString;
	}

	public String getIdString() {
		return idString;
	}

	public void setTerminalConfigList(List<TerminalConfig> terminalConfigList) {
		this.terminalConfigList = terminalConfigList;
	}

	public List<TerminalConfig> getTerminalConfigList() {
		return terminalConfigList;
	}

	public void setLotteryTypeList(List<LotteryType> lotteryTypeList) {
		this.lotteryTypeList = lotteryTypeList;
	}

	public List<LotteryType> getLotteryTypeList() {
		return lotteryTypeList;
	}
}
