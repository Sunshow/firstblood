package web.action.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.core.service.setting.SettingService;

/**
 * 同步配置
 * @author qatang
 *
 */
public class SyncSettingsAction extends BaseAction {
	private static final long serialVersionUID = -2225123800707608839L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private SettingService settingService;
	
	private String result;
	
	public String handle() {
		logger.info("进入同步配置页面");
		return "list";
	}
	

	public String sync() {
		logger.info("同步配置");
		try {
			result = settingService.sync();
		} catch (Exception e) {
			logger.error("同步配置异常，{}", e);
			super.setErrorMessage("同步配置异常，" + e);
			return "failure";
		}
		return "result";
	}

	public SettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(SettingService settingService) {
		this.settingService = settingService;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
