package web.action.config;

import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.config.impl.lotterychase.ChaseCompleteSMSConfig;
import com.lehecai.core.lottery.ChaseStatus;

/**
 * 追号完成自动发短信配置
 * @author qatang
 *
 */
public class ChaseCompleteSMSConfigAction extends AbstractChaseStoppedSMSConfigAction<ChaseCompleteSMSConfig> {
	private static final long serialVersionUID = -3571696659887505154L;

	@Override
	public ChaseStatus getChaseStoppedStatus() {
		return ChaseStatus.COMPLETED;
	}

	@Override
	protected String getChaseStoppedSMSConfigGroup() {
		return SettingConstant.GROUP_CHASE_COMPLETE_SMS_CONFIG;
	}
}
