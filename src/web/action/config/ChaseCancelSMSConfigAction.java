package web.action.config;

import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.config.impl.lotterychase.ChaseCancelSMSConfig;
import com.lehecai.core.lottery.ChaseStatus;

/**
 * 追号撤销自动发短信配置
 * @author qatang
 *
 */
public class ChaseCancelSMSConfigAction extends AbstractChaseStoppedSMSConfigAction<ChaseCancelSMSConfig> {
	private static final long serialVersionUID = 967234565688782643L;

	@Override
	public ChaseStatus getChaseStoppedStatus() {
		return ChaseStatus.CANCELLED;
	}

	@Override
	protected String getChaseStoppedSMSConfigGroup() {
		return SettingConstant.GROUP_CHASE_CANCEL_SMS_CONFIG;
	}
}
