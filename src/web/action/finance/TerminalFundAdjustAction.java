/**
 * 
 */
package web.action.finance;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.finance.PayType;
import com.lehecai.admin.web.domain.finance.TerminalAccountCheckItem;
import com.lehecai.admin.web.domain.finance.TerminalFundAdjust;
import com.lehecai.admin.web.service.finance.TerminalFundAdjustService;
import com.lehecai.admin.web.utils.PageUtil;

/**
 * @author chirowong
 * 款项调整
 */
public class TerminalFundAdjustAction extends BaseAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4260678425602382212L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private TerminalFundAdjustService terminalFundAdjustService;
	private TerminalFundAdjust terminalFundAdjust;
	private List<TerminalFundAdjust> terminalFundAdjusts;
	private TerminalAccountCheckItem terminalAccountCheckItem;
	private Long terminalAccountCheckItemId;
	private int payTypeId;
	
	public String handle(){
		logger.info("进入款项调整列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		terminalFundAdjusts = terminalFundAdjustService.list(terminalFundAdjust, super.getPageBean());
		PageBean pageBean = terminalFundAdjustService.getPageBean(terminalFundAdjust,super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		return "list";
	}
	
	public String input(){
		logger.info("添加款项调整");
		if(terminalFundAdjust != null && terminalFundAdjust.getId() != null){
			terminalFundAdjust = terminalFundAdjustService.get(terminalFundAdjust.getId());
		}else{
			if(terminalAccountCheckItemId != null){
				terminalFundAdjust = new TerminalFundAdjust();
				terminalFundAdjust.setTerminalAccountCheckItemId(terminalAccountCheckItemId);
				terminalFundAdjusts = terminalFundAdjustService.list(terminalFundAdjust, null);
				if(terminalFundAdjusts != null && terminalFundAdjusts.size() > 0){
					logger.info("已存在该对账单号");
					super.setErrorMessage("已存在该对账单号");
					return "failure";
				}
			}else{
				logger.info("添加款项调整错误，没有对账单序号");
				super.setErrorMessage("添加款项调整错误，没有对账单序号");
				return "failure";
			}
		}
		return "input";
	}
	
	public String view(){
		logger.info("查看款项调整信息");
		if(terminalFundAdjust != null && terminalFundAdjust.getId() != null){
			terminalFundAdjust = terminalFundAdjustService.get(terminalFundAdjust.getId());
		}else{
			logger.info("查看款项调整出错，款项调整编码为空");
			super.setErrorMessage("查看款项调整出错，款项调整编码为空");
			return "failure";
		}
		return "view";
	}
	
	public String del(){
		logger.info("删除款项调整信息");
		if (terminalFundAdjust != null && terminalFundAdjust.getId() != null) {
			terminalFundAdjust = terminalFundAdjustService.get(terminalFundAdjust.getId());
			terminalFundAdjustService.del(terminalFundAdjust);
		} else {
			logger.error("删除款项调整，编码为空");
			super.setErrorMessage("删除款项调整，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/finance/terminalFundAdjust.do");
		logger.info("删除款项调整结束");
		return "forward";
	}
	
	public String manage(){
		logger.info("进入更新款项调整信息");
		if (terminalFundAdjust != null) {
			if (terminalFundAdjust.getAmount() == null) {
				logger.error("添加款项调整，金额为空");
				super.setErrorMessage("金额不能为空");
				return "failure";
			}
			if (payTypeId == 0) {
				logger.error("添加款项调整，没有选择终端");
				super.setErrorMessage("没有选择终端");
				return "failure";
			}
			Date createTime = terminalFundAdjust.getCreateTime();
			if(createTime == null)
				terminalFundAdjust.setCreateTime(new Date());
			terminalFundAdjust.setPayType(PayType.getItem(payTypeId));
			Long userId = terminalFundAdjust.getUserId();
			if(userId == null) {
				HttpServletRequest request = ServletActionContext.getRequest();
				UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
				terminalFundAdjust.setUserId(userSessionBean.getUser().getId());
			}
			terminalFundAdjustService.manage(terminalFundAdjust);
			super.setForwardUrl("/finance/terminalFundAdjust.do");
			logger.info("更新款项调整信息结束");
			return "success";
		} else {
			logger.error("添加款项调整错误，提交表单为空");
			super.setErrorMessage("添加款项调整错误，提交表单不能为空");
			return "failure";
		}
	}
	
	public TerminalFundAdjustService getTerminalFundAdjustService() {
		return terminalFundAdjustService;
	}
	
	public void setTerminalFundAdjustService(
			TerminalFundAdjustService terminalFundAdjustService) {
		this.terminalFundAdjustService = terminalFundAdjustService;
	}

	public TerminalFundAdjust getTerminalFundAdjust() {
		return terminalFundAdjust;
	}

	public void setTerminalFundAdjust(TerminalFundAdjust terminalFundAdjust) {
		this.terminalFundAdjust = terminalFundAdjust;
	}

	public List<TerminalFundAdjust> getTerminalFundAdjusts() {
		return terminalFundAdjusts;
	}

	public void setTerminalFundAdjusts(List<TerminalFundAdjust> terminalFundAdjusts) {
		this.terminalFundAdjusts = terminalFundAdjusts;
	}

	public TerminalAccountCheckItem getTerminalAccountCheckItem() {
		return terminalAccountCheckItem;
	}

	public void setTerminalAccountCheckItem(
			TerminalAccountCheckItem terminalAccountCheckItem) {
		this.terminalAccountCheckItem = terminalAccountCheckItem;
	}

	public int getPayTypeId() {
		return payTypeId;
	}

	public void setPayTypeId(int payTypeId) {
		this.payTypeId = payTypeId;
	}

	public List<PayType> getPayTypes(){
		return PayType.getItems();
	}

	public Long getTerminalAccountCheckItemId() {
		return terminalAccountCheckItemId;
	}

	public void setTerminalAccountCheckItemId(Long terminalAccountCheckItemId) {
		this.terminalAccountCheckItemId = terminalAccountCheckItemId;
	}
}
