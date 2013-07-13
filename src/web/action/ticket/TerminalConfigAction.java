package web.action.ticket;

import java.util.ArrayList;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.service.ticket.TerminalConfigService;
import com.lehecai.admin.web.service.ticket.TerminalService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.EnabledStatus;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.bean.common.TimeRegion;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PlayType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.engine.entity.terminal.Terminal;
import com.lehecai.engine.entity.terminal.TerminalConfig;
import com.lehecai.engine.entity.terminal.TerminalType;

public class TerminalConfigAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private Logger logger = LoggerFactory.getLogger(TerminalConfigAction.class);
	
	private TerminalConfigService terminalConfigService;
	private TerminalService terminalService;
	
	private TerminalConfig terminalConfig;
	
	private List<TerminalConfig> terminalConfigList;
	private List<Terminal> terminalList;
	private List<LotteryType> lotteryTypeList;
	
	private Integer lotteryTypeId;
	private Integer terminalTypeId;
	private Integer enabledStatusId;
	private Integer isPaused;
	private Integer playTypeId;
	private Long terminalId;
	
	private String command;
	private String idString;
	private  int flag ;

	private final static String PAUSE = "pause";
	private final static String START = "start";
	private final static String ENABLED = "enabled";
	private final static String DISABLED = "disabled";

	private LotteryType lotteryType;
	private PlayType playType;
	
	private List<PlayType> playTypeList;
	
	public String handle(){
		logger.info("进入查询终端配置 ");
		lotteryTypeList = OnSaleLotteryList.get();
		return "lotteryTypes";
	}
	
	public String list(){
		logger.info("进入查询出票终端");
		HttpServletRequest request = ServletActionContext.getRequest();
		if (terminalConfig == null) {
			terminalConfig = new TerminalConfig();
		}
		terminalConfig.setLotteryType(LotteryType.getItem(lotteryTypeId));
		terminalConfigList = terminalConfigService.list(terminalConfig, super.getPageBean());
		PageBean pageBean = terminalConfigService.getPageBean(terminalConfig, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		terminalList = terminalConfigService.listTerminal(terminalConfig, pageBean);
		
		logger.info("查询出票终端结束");
		return "list";
	}
	public String listByPlayType(){
		logger.info("进入查询出票终端");
		HttpServletRequest request = ServletActionContext.getRequest();
		if (terminalConfig == null) {
			terminalConfig = new TerminalConfig();
		}
		terminalConfig.setLotteryType(LotteryType.getItem(lotteryTypeId));
		terminalConfigList = terminalConfigService.listByPlayType(terminalConfig, super.getPageBean());
		PageBean pageBean = terminalConfigService.getPageBean(terminalConfig, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		
		logger.info("查询出票终端结束");
		return "listByPlayType";
	}

	protected void preprocessTerminalConfig(TerminalConfig terminalConfig) {
		if (terminalConfig == null) {
			return;
		}
		if (terminalConfig.getAllotForbidPeriod() != null) {
			TimeRegion timeRegion = TimeRegion.parse(terminalConfig.getAllotForbidPeriod());
			terminalConfig.setAllotForbidPeriod(timeRegion == null ? null : timeRegion.toString());
		}
		if (terminalConfig.getSendForbidPeriod() != null) {
			TimeRegion timeRegion = TimeRegion.parse(terminalConfig.getSendForbidPeriod());
			terminalConfig.setSendForbidPeriod(timeRegion == null ? null : timeRegion.toString());
		}
	}
	
	public String manage() {
		logger.info("进入更新终端配置");
		if (terminalConfig != null) {
			terminalConfig.setLotteryType(LotteryType.getItem(lotteryTypeId));
			terminalConfig.setPlayType(PlayType.DEFAULT);
			terminalConfig.setTerminalType(terminalService.get(terminalConfig.getTerminalId()).getTerminalType());
			if (enabledStatusId != null) {				
				terminalConfig.setIsEnabled(EnabledStatus.getItem(enabledStatusId));
			}
			if (isPaused != null) {
				terminalConfig.setIsPaused(YesNoStatus.getItem(isPaused));
			}
			this.preprocessTerminalConfig(terminalConfig);
			terminalConfigService.manage(terminalConfig);
		} else {
			logger.error("添加彩种终端出票配置错误，提交的表单为空");
			super.setErrorMessage("添加彩种终端出票配置错误，提交表单不能为空");
			return "failure";
		}
		if (flag == 1) {
			super.setForwardUrl("/ticket/terminalConfig.do?action=list&lotteryTypeId=" + lotteryTypeId);
		} 
		if (flag == 2){
			super.setForwardUrl("/ticket/terminal.do?action=edit&terminal.id=" + terminalConfig.getTerminalId());
		}
		logger.info("更新终端配置结束");
		return "forward";
	}
	public String manageByPlayType() {
		logger.info("进入更新终端配置");
		if (terminalConfig != null) {
			if (playTypeId != null && playTypeId < 0) {
				super.setErrorMessage("按玩法添加彩种终端出票配置错误：必须指定一个玩法");
				return "failure";
			}
			terminalConfig.setLotteryType(LotteryType.getItem(lotteryTypeId));
			terminalConfig.setTerminalType(terminalService.get(terminalId).getTerminalType());
			terminalConfig.setPlayType(PlayType.getItem(playTypeId));
			terminalConfig.setTerminalId(terminalId);
			if (enabledStatusId != null) {				
				terminalConfig.setIsEnabled(EnabledStatus.getItem(enabledStatusId));
			}
			if (isPaused != null) {
				terminalConfig.setIsPaused(YesNoStatus.getItem(isPaused));
			}
			if (terminalConfig.getId() == null) {
				List<TerminalConfig> configList = terminalConfigService.listByPlayType(terminalConfig, super.getPageBean());
				if (configList != null && configList.size() > 0) {
					for (TerminalConfig config : configList) {
						if (config.getTerminalId().longValue() == terminalId.longValue()) {
							super.setErrorMessage("按玩法添加彩种终端出票配置错误：lotteryType:" + terminalConfig.getLotteryType() + ", playType:" + terminalConfig.getPlayType() + ", terminalId:" + terminalId + "，数据已存在，不能重复添加");
							return "failure";
						}
					}
				}
			}
			this.preprocessTerminalConfig(terminalConfig);
			terminalConfigService.manage(terminalConfig);
		} else {
			logger.error("添加彩种终端出票配置错误，提交的表单为空");
			super.setErrorMessage("添加彩种终端出票配置错误，提交表单不能为空");
			return "failure";
		}
		super.setForwardUrl("/ticket/terminalConfig.do?action=listByPlayType&lotteryTypeId=" + lotteryTypeId);
		logger.info("更新终端配置结束");
		return "forward";
	}
	
	public String input() {
		logger.info("进入输入终端配置信息");
		if (terminalConfig != null) {
			if (terminalConfig.getId() != null) {			
				terminalConfig = terminalConfigService.get(terminalConfig.getId());
				lotteryTypeId = terminalConfig.getLotteryType().getValue();
				terminalTypeId = terminalConfig.getTerminalType().getValue();
				enabledStatusId = terminalConfig.getIsEnabled().getValue();
				isPaused = terminalConfig.getIsPaused().getValue();
			} else {			
				terminalConfig.setWeight(0);
				terminalConfig.setTerminateForward(0L);
			}
		}
		return "inputForm";
	}
	public String inputByPlayType() {
		logger.info("进入输入终端配置信息");
		if (terminalConfig != null && terminalConfig.getId() != null) {			
			terminalConfig = terminalConfigService.get(terminalConfig.getId());
			lotteryType = terminalConfig.getLotteryType();
			terminalId = terminalConfig.getTerminalId();
			enabledStatusId = terminalConfig.getIsEnabled().getValue();
			playType = terminalConfig.getPlayType();
			isPaused = terminalConfig.getIsPaused().getValue();
		} else {
			terminalConfig = new TerminalConfig();
			terminalConfig.setWeight(0);
			terminalConfig.setTerminateForward(0L);
			lotteryType = LotteryType.getItem(lotteryTypeId);
			playTypeList = PlayType.getItemsByLotteryType(lotteryType);
			terminalList = terminalConfigService.listTerminalByPlayType(terminalConfig, super.getPageBean());
		}
		return "inputFormByPlayType";
	}
	
	public String del() {
		logger.info("进入删除终端配置");
		if (terminalConfig != null && terminalConfig.getId() != null) {
			terminalConfig = terminalConfigService.get(terminalConfig.getId());
			terminalConfigService.del(terminalConfig);
		} else {
			logger.error("删除终端配置，编码为空");
			super.setErrorMessage("删除终端配置，编码不能为空");
			return "failure";
		}
		if (flag == 1) {
			super.setForwardUrl("/ticket/terminalConfig.do?action=list&lotteryTypeId=" + lotteryTypeId);
			logger.info("删除终端配置结束");
			return "forward";
		}
		if (flag == 2) {
			if (terminalId == null) {
				logger.error("删除终端配置，编码为空");
				super.setErrorMessage("删除终端配置，编码不能为空");
				return "failure";
			} else {
				super.setForwardUrl("/ticket/terminal.do?action=edit&terminal.id=" + terminalId);
				logger.info("删除终端配置结束");
				return "forward";
			}
		}
		return "forward";
	}
	public String delByPlayType() {
		logger.info("进入删除终端配置");
		if (terminalConfig != null && terminalConfig.getId() != null) {
			terminalConfig = terminalConfigService.get(terminalConfig.getId());
			terminalConfigService.del(terminalConfig);
		} else {
			logger.error("删除终端配置，编码为空");
			super.setErrorMessage("删除终端配置，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/ticket/terminalConfig.do?action=listByPlayType&lotteryTypeId=" + lotteryTypeId);
		logger.info("删除终端配置结束");
		return "forward";
	}

	public String batchUpdate() {
		logger.info("进入批量修改终端配置状态");

		StringBuffer info = new StringBuffer();
		JSONObject json = new JSONObject();

		if ((command == null || "".equals(command)) || (idString == null || idString.length() == 0)) {
			info.append("修改终端配置参数为空!");
			logger.error(info.toString());

			json.put("result", "failed");
			json.put("info", info.toString());
			super.writeRs(ServletActionContext.getResponse(), json);
			return null;
		} else {
			// 解析ID字符串
			List<Long> idList = new ArrayList<Long>();
			try {
				String[] idArr = idString.split(",");
				for (String id : idArr) {
					idList.add(Long.parseLong(id));
				}
			} catch (Exception e) {
				info.append("解析ID字符串出现异常!");
				logger.error(info.toString(), e);

				json.put("result", "failed");
				json.put("info", info.toString());
				super.writeRs(ServletActionContext.getResponse(), json);
			}

			// 通过传入ID获取有效的终端配置对象
			List<TerminalConfig> configList = new ArrayList<TerminalConfig>();

			JSONArray handleArray = new JSONArray();// 最终进行操作的终端配置ID列表

			List<Long> inexistenceTerminalConfigIdList = new ArrayList<Long>();// 不存在的终端配置ID列表

			for (Long id : idList) {
				TerminalConfig config = terminalConfigService.get(id);
				if (config == null) {
					inexistenceTerminalConfigIdList.add(id);
				}
				configList.add(config);
			}

			if (!inexistenceTerminalConfigIdList.isEmpty()) {
				info.append(String.format("ID=%s的终端配置并不存在!", inexistenceTerminalConfigIdList));
				if (configList.isEmpty()) {
					info.append("无可处理终端!处理失败!");
					json.put("result", "failed");
					json.put("info", info.toString());
				}

			}

			// 批量暂停送票
			if (PAUSE.equals(command)) {
				for (TerminalConfig config : configList) {
					if (config.getIsPaused().equals(YesNoStatus.NO)) {
						config.setIsPaused(YesNoStatus.YES);
						terminalConfigService.manage(config);
					}
					handleArray.add(config.getId());
				}
				info.append(String.format("成功更新编码为: %s 的终端配置状态为暂停送票!", handleArray));
				logger.info(info.toString());
			}

			// 批量启动送票
			else if (START.equals(command)) {
				for (TerminalConfig config : configList) {
					if (config.getIsPaused().equals(YesNoStatus.YES)) {
						config.setIsPaused(YesNoStatus.NO);
						terminalConfigService.manage(config);
					}
					handleArray.add(config.getId());
				}
				info.append(String.format("成功更新编码为: %s 的终端配置状态为启动送票!", handleArray));
				logger.info(info.toString());
			}

			// 批量启用终端
			else if (ENABLED.equals(command)) {
				for (TerminalConfig config : configList) {
					if (config.getIsEnabled().equals(EnabledStatus.DISABLED)) {
						config.setIsEnabled(EnabledStatus.ENABLED);
						terminalConfigService.manage(config);
					}
					handleArray.add(config.getId());
				}
				info.append(String.format("成功更新编码为: %s 的终端配置状态为启用!", handleArray));
				logger.info(info.toString());
			}

			// 批量禁用终端
			else if (DISABLED.equals(command)) {
				for (TerminalConfig config : configList) {
					if (config.getIsEnabled().equals(EnabledStatus.ENABLED)) {
						config.setIsEnabled(EnabledStatus.DISABLED);
						terminalConfigService.manage(config);
					}
					handleArray.add(config.getId());
				}
				info.append(String.format("成功更新编码为: %s 的终端配置状态为禁用!", handleArray));
				logger.info(info.toString());
			}

			// 错误命令
			else {
				info.append("错误命令!");
				logger.error(info.toString());
			}

			json.put("result", "succeed");
			json.put("info", info.toString());
			json.put("idList", handleArray);
			super.writeRs(ServletActionContext.getResponse(), json);
		}
		return null;
	}

	public TerminalConfigService getTerminalConfigService() {
		return terminalConfigService;
	}

	public void setTerminalConfigService(TerminalConfigService terminalConfigService) {
		this.terminalConfigService = terminalConfigService;
	}

	public TerminalConfig getTerminalConfig() {
		return terminalConfig;
	}

	public void setTerminalConfig(TerminalConfig terminalConfig) {
		this.terminalConfig = terminalConfig;
	}

	public List<TerminalConfig> getTerminalConfigList() {
		return terminalConfigList;
	}

	public void setTerminalConfigList(List<TerminalConfig> terminalConfigList) {
		this.terminalConfigList = terminalConfigList;
	}

	public List<TerminalType> getTerminalTypes() {
		return TerminalType.getItems();
	}

	public List<LotteryType> getLotteryTypeList() {
		return lotteryTypeList;
	}

	public void setLotteryTypeList(List<LotteryType> lotteryTypeList) {
		this.lotteryTypeList = lotteryTypeList;
	}

	public Integer getTerminalTypeId() {
		return terminalTypeId;
	}

	public void setTerminalTypeId(Integer terminalTypeId) {
		this.terminalTypeId = terminalTypeId;
	}

	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}

	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}

	public LotteryType getLotteryType() {
		lotteryType = LotteryType.getItem(lotteryTypeId);
		return lotteryType;
	}

	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}

	public List<Terminal> getTerminalList() {
		return terminalList;
	}

	public void setTerminalList(List<Terminal> terminalList) {
		this.terminalList = terminalList;
	}
	public List<EnabledStatus> getEnabledStatuses() {
		return EnabledStatus.getItems();
	}

	public Integer getEnabledStatusId() {
		return enabledStatusId;
	}

	public void setEnabledStatusId(Integer enabledStatusId) {
		this.enabledStatusId = enabledStatusId;
	}

	public Integer getIsPaused() {
		return isPaused;
	}

	public void setIsPaused(Integer isPaused) {
		this.isPaused = isPaused;
	}

	public TerminalService getTerminalService() {
		return terminalService;
	}

	public void setTerminalService(TerminalService terminalService) {
		this.terminalService = terminalService;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getIdString() {
		return idString;
	}

	public void setIdString(String idString) {
		this.idString = idString;
	}

	public List<PlayType> getPlayTypeList() {
		return playTypeList;
	}

	public void setPlayTypeList(List<PlayType> playTypeList) {
		this.playTypeList = playTypeList;
	}

	public PlayType getPlayType() {
		return playType;
	}

	public void setPlayType(PlayType playType) {
		this.playType = playType;
	}

	public Integer getPlayTypeId() {
		return playTypeId;
	}

	public void setPlayTypeId(Integer playTypeId) {
		this.playTypeId = playTypeId;
	}

	public Long getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(Long terminalId) {
		this.terminalId = terminalId;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}

}
