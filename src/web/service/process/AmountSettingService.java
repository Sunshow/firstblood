
package web.service.process;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.process.AmountSetting;

public interface AmountSettingService {
	
	public List<AmountSetting> updateAndQueryList(String processId, String taskId, Long operateId, PageBean pageBean);
	public PageBean getPageBean(String processId, String taskId, Long operateId, PageBean pageBean);
	public AmountSetting update(Long id);
	public Double auditAmount(String processId, String taskId, Long operateId, Long roleId);
	public void del(AmountSetting amountSetting);
	public void manage(AmountSetting amountSetting);
	public void manageAmountAdd(String processId, String taskId, Long operateId, Double amount);
	public void manageAmountMinus(String processId, String taskId, Long operateId, Double amount);
	
}
