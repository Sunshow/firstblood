package web.dao.process;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.process.AmountSetting;
import com.lehecai.admin.web.enums.ProcessUserType;


public interface AmountSettingDao {
	public List<AmountSetting> list(String processId, String taskId, Long operateId, PageBean pageBean);
	public PageBean getPageBean(String processId, String taskId, Long operateId, PageBean pageBean);
	public AmountSetting get(Long id);
	public void del(AmountSetting amountSetting);
	public void merge(AmountSetting amountSetting);
	public Double getOneTimeAmount(String processId, String taskId, Long operateId, ProcessUserType userType);
		
}