package web.action.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.business.ComboOrderCancelService;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class ComboOrderCancelAction extends BaseAction {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;

	private Long comboOrderId;
	private ComboOrderCancelService comboOrderCancelService;
	
	public String handle() {
		return "list";
	}

	public String comboOrderCancel() {
		logger.info("进入取消套餐订单");
		boolean flag = false;
		try {
			flag = comboOrderCancelService.comboOrderCancel(comboOrderId);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("取消套餐订单，api调用异常，{}", e.getMessage());
			setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
			return "failure";
		}
		if (!flag) {
			logger.error("取消套餐订单失败");
			setErrorMessage("取消套餐订单失败");
			return "failure";
		}
		logger.info("结束取消套餐订单");
		return "success";
	}

	public Long getComboOrderId() {
		return comboOrderId;
	}

	public void setComboOrderId(Long comboOrderId) {
		this.comboOrderId = comboOrderId;
	}

	public ComboOrderCancelService getComboOrderCancelService() {
		return comboOrderCancelService;
	}

	public void setComboOrderCancelService(
			ComboOrderCancelService comboOrderCancelService) {
		this.comboOrderCancelService = comboOrderCancelService;
	}
}