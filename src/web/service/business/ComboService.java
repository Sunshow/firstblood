package web.service.business;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.lottery.Combo;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface ComboService {
	
	/**
	 * 查询套餐信息
	 * @return
	 */
	public Map<String, Object> queryComboList(Long comboId, Long comborevId, YesNoStatus yesNoStatus, PageBean pageBean) throws ApiRemoteCallFailedException;

	/**
	 * 更新套餐信息
	 * @return
	 */
	public void updateCombo(Combo combo) throws ApiRemoteCallFailedException;

	/**
	 * 更新套餐信息
	 * @return
	 */
	public void addCombo(Combo combo) throws ApiRemoteCallFailedException;
	
}
