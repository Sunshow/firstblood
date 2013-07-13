package web.action.config;

import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.config.impl.lotterychase.ChaseStoppedSMSConfig;
import com.lehecai.core.lottery.ChaseStatus;

/**
 * 追号中奖后停止自动发短信配置
 * @author qatang
 *
 */
public class ChaseStoppedSMSConfigAction extends AbstractChaseStoppedSMSConfigAction<ChaseStoppedSMSConfig> {
	private static final long serialVersionUID = -2225123800707608839L;

	@Override
	protected String getChaseStoppedSMSConfigGroup() {
		return SettingConstant.GROUP_CHASE_STOPPED_SMS_CONFIG;
	}

	@Override
	public ChaseStatus getChaseStoppedStatus() {
		return ChaseStatus.STOPPED;
	}
	
}
