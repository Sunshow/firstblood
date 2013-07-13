package web.multiconfirm.confirm;

import java.util.List;

import com.lehecai.admin.web.multiconfirm.MulticonfirmRecord;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTask;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTaskStatus;
import com.lehecai.admin.web.service.multiconfirm.MulticonfirmService;
import com.lehecai.admin.web.service.user.PermissionService;
import com.lehecai.core.util.CoreDateUtils;

public abstract class  AbstractMulticonfirmConfirm implements IMulticonfirmConfirm{
	
	private PermissionService permissionService;
	private MulticonfirmService multiconfirmService;
	
	/**
	 * 具体执行输入记录比对
	 * @param recordList
	 * @param sb
	 * @return
	 */
	protected abstract boolean doConfirm(List<MulticonfirmRecord> recordList, StringBuffer sb);
	
	@Override
	public boolean comfirm(MulticonfirmTask task, StringBuffer sb) {
		if (sb == null) {
			sb = new StringBuffer();
		}
		List<MulticonfirmRecord> recordList = multiconfirmService.getRecordList(task, null);
		
		if (recordList == null || recordList.size() == 0) {
			return false;
		}

		if (doConfirm(recordList, sb)) {
			task.setResult(recordList.get(0).getResult());
			multiconfirmService.manageStatus(task, MulticonfirmTaskStatus.PASSED);
			sb.append("<br />验证通过");
			return true;
		} else {
			multiconfirmService.manageStatus(task, MulticonfirmTaskStatus.FAILED);
			sb.append("<br />验证未通过");
			return false;
		}
	}

	@Override
	public boolean auditConfirm(MulticonfirmTask task, StringBuffer sb) {
		List<MulticonfirmRecord> recordList = multiconfirmService.getRecordList(task, null);
		sb.append("输入记录:<br />");
		if (recordList != null && recordList.size() > 0) {
			sb.append("<table border='1' class='querytab' cellspacing='0' cellpadding='0' border='0'>");
			sb.append("<tr><td>用户名</td><td>输入值</td><td>最后修改时间</td></tr>").append("").append("");
			for (MulticonfirmRecord record : recordList) {
				sb.append("<tr><td>").append(permissionService.getUser(record.getUserId()).getName()).append("</td>");
				sb.append("<td>").append(record.getResult()).append("</td>");
				sb.append("<td>").append(CoreDateUtils.formatDate(record.getUpdateTime(), CoreDateUtils.DATETIME)).append("</td></tr>");
			}
			sb.append("</table>");
		}
		return multiconfirmService.auditConfirm(task, sb);
	};

	public MulticonfirmService getMulticonfirmService() {
		return multiconfirmService;
	}

	public void setMulticonfirmService(MulticonfirmService multiconfirmService) {
		this.multiconfirmService = multiconfirmService;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
}
