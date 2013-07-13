package web.action.config;

import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.config.impl.lotterychase.ChaseDrawFailedSMSConfig;
import com.lehecai.core.lottery.ChaseStatus;

/**
 * 追号开奖失败撤销自动发短信配置
 * @author qatang
 *
 */
public class ChaseDrawFailedSMSConfigAction extends AbstractChaseStoppedSMSConfigAction<ChaseDrawFailedSMSConfig> {

	private static final long serialVersionUID = 5198424333169248938L;

	@Override
	public ChaseStatus getChaseStoppedStatus() {
		return ChaseStatus.DRAWFAILED;
	}

	@Override
	protected String getChaseStoppedSMSConfigGroup() {
		return SettingConstant.GROUP_CHASE_DRAW_FAILED_SMS_CONFIG;
	}
}
