/**
 * 
 */
package web.action.customconfig;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.customconfig.PayType;
import com.lehecai.admin.web.service.customconfig.PayTypeService;
import com.lehecai.admin.web.utils.PageUtil;

/**
 * @author chirowong
 * 支付类型管理
 */
public class PayTypeAction extends BaseAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4260678425602382212L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private PayTypeService payTypeService;
	private PayType payType;
	private List<PayType> payTypes;
	
	public String handle(){
		logger.info("进入支付类型列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		if(payType == null) payType = new PayType();
		payTypes = payTypeService.list(payType, super.getPageBean());
		PageBean pageBean = payTypeService.getPageBean(payType,super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		return "list";
	}
	
	public String input(){
		logger.info("添加支付类型");
		if(payType != null && payType.getId() != null){
			payType = payTypeService.get(payType.getId());
		}else{
			payType = new PayType();
		}
		return "input";
	}
	
	public String view(){
		logger.info("查看支付类型信息");
		if(payType != null && payType.getId() != null){
			payType = payTypeService.get(payType.getId());
		}else{
			logger.info("查看支付类型出错，支付类型编码为空");
			super.setErrorMessage("查看支付类型出错，支付类型编码为空");
			return "failure";
		}
		return "view";
	}
	
	public String del(){
		logger.info("删除支付类型信息");
		if (payType != null && payType.getId() != null) {
			payType = payTypeService.get(payType.getId());
			payTypeService.del(payType);
		} else {
			logger.error("删除支付类型，编码为空");
			super.setErrorMessage("删除支付类型，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/customConfig/payType.do");
		logger.info("删除支付类型结束");
		return "success";
	}
	
	public String manage(){
		logger.info("进入更新支付类型信息");
		if (payType != null) {
			payType.setValid(true);
			payTypeService.manage(payType);
			super.setForwardUrl("/customConfig/payType.do");
			logger.info("更新支付类型信息结束");
			return "success";
		} else {
			logger.error("添加支付类型错误，提交表单为空");
			super.setErrorMessage("添加支付类型错误，提交表单不能为空");
			return "failure";
		}
	}

	public PayTypeService getPayTypeService() {
		return payTypeService;
	}
	
	public void setPayTypeService(
			PayTypeService payTypeService) {
		this.payTypeService = payTypeService;
	}

	public PayType getPayType() {
		return payType;
	}

	public void setPayType(PayType payType) {
		this.payType = payType;
	}

	public List<PayType> getPayTypes() {
		return payTypes;
	}

	public void setPayTypes(List<PayType> payTypes) {
		this.payTypes = payTypes;
	}
}
