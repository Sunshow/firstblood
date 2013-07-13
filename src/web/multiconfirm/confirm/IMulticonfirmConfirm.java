package web.multiconfirm.confirm;

import com.lehecai.admin.web.multiconfirm.MulticonfirmTask;

public interface IMulticonfirmConfirm {
	/**
	 * 进行验证
	 * @param task
	 * @param sb
	 * @return
	 */
	public boolean comfirm(MulticonfirmTask task, StringBuffer sb);
	/**
	 * 验证任务是否达到验证条件
	 * @param task
	 * @param message
	 * @return
	 */
	public boolean auditConfirm(MulticonfirmTask task, StringBuffer message);
}
