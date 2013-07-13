package web.action.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.constant.Global;

public class LogoutAction extends BaseAction {
	private static final long serialVersionUID = -8830679912602886965L;
	private Logger logger = LoggerFactory.getLogger(LogoutAction.class);

	public String handle() {
		logger.info("进入注销登录");
		if(super.getSession().get(Global.USER_SESSION) != null){		
			super.getSession().remove(Global.USER_SESSION);
		}
		super.setForwardUrl("/");
		logger.info("注销登录结束");
		return "forward";
	}
}
