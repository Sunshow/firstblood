/**
 * 
 */
package web.action.finance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.finance.TerminalCompany;
import com.lehecai.admin.web.service.finance.TerminalCompanyService;
import com.lehecai.admin.web.service.ticket.TerminalService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.engine.entity.terminal.Terminal;

/**
 * @author chirowong
 * 出票商管理
 */
public class TerminalCompanyAction extends BaseAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4260678425602382212L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private TerminalService terminalService;
	private TerminalCompanyService terminalCompanyService;
	private List<Terminal> terminalList;
	private TerminalCompany terminalCompany;
	private List<TerminalCompany> terminalCompanys;
	private List<Terminal> terminalListForView;
	
	public String handle(){
		logger.info("进入出票商列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		terminalCompanys = terminalCompanyService.list(terminalCompany, super.getPageBean());
		PageBean pageBean = terminalCompanyService.getPageBean(terminalCompany,super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		return "list";
	}
	
	public String input(){
		logger.info("添加出票商");
		Terminal terminal = new Terminal();
		PageBean nopageBean = new PageBean();
		nopageBean.setPageFlag(false);
		terminalList = terminalService.list(terminal, nopageBean);
		if(terminalCompany != null && terminalCompany.getId() != null){
			terminalCompany = terminalCompanyService.get(terminalCompany.getId());
		}else{
			terminalCompany = new TerminalCompany();
		}
		return "input";
	}
	
	public String view(){
		logger.info("查看出票商信息");
		if(terminalCompany != null && terminalCompany.getId() != null){
			terminalCompany = terminalCompanyService.get(terminalCompany.getId());
			String terminals = terminalCompany.getTerminalTypes();
			String[] arrTerminals = terminals.split(",");
			terminalListForView = new ArrayList<Terminal>();
			for(int i = 0; i < arrTerminals.length; i++){
				terminalListForView.add(terminalService.get(Long.valueOf(arrTerminals[i].trim())));
			}
		}else{
			logger.info("查看出票商出错，出票商编码为空");
			super.setErrorMessage("查看出票商出错，出票商编码为空");
			return "failure";
		}
		return "view";
	}
	
	public String del(){
		logger.info("删除出票商信息");
		if (terminalCompany != null && terminalCompany.getId() != null) {
			terminalCompany = terminalCompanyService.get(terminalCompany.getId());
			terminalCompanyService.del(terminalCompany);
		} else {
			logger.error("删除出票商，编码为空");
			super.setErrorMessage("删除出票商，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/finance/terminalCompany.do");
		logger.info("删除出票商结束");
		return "forward";
	}
	
	public String manage(){
		logger.info("进入更新出票商信息");
		if (terminalCompany != null) {
			if (terminalCompany.getName() == null || "".equals(terminalCompany.getName())) {
				logger.error("添加出票商，出票商名为空");
				super.setErrorMessage("出票商名不能为空");
				return "failure";
			}
			if (terminalCompany.getTerminalTypes() == null || "".equals(terminalCompany.getTerminalTypes())) {
				logger.error("添加出票商，没有选择终端");
				super.setErrorMessage("没有选择终端");
				return "failure";
			}
			if(terminalCompany.getId() == null){
				List<TerminalCompany> terminalCompanyList = terminalCompanyService.list(terminalCompany, null);
				if(terminalCompanyList != null && terminalCompanyList.size() > 0){
					logger.error("出票商名重复");
					super.setErrorMessage("出票商名重复");
					return "failure";
				}
			}else{
				TerminalCompany tc = terminalCompanyService.get(terminalCompany.getId());
				if(!tc.getName().equals(terminalCompany.getName())){
					List<TerminalCompany> terminalCompanyList = terminalCompanyService.list(terminalCompany, null);
					if(terminalCompanyList != null && terminalCompanyList.size() > 0){
						logger.error("出票商名重复");
						super.setErrorMessage("出票商名重复");
						return "failure";
					}
				}
			}
			terminalCompany.setCreateTime(new Date());
			terminalCompanyService.manage(terminalCompany);
			super.setForwardUrl("/finance/terminalCompany.do");
			logger.info("更新出票商信息结束");
			return "success";
		} else {
			logger.error("添加出票商错误，提交表单为空");
			super.setErrorMessage("添加出票商错误，提交表单不能为空");
			return "failure";
		}
	}
	
	public TerminalService getTerminalService() {
		return terminalService;
	}

	public void setTerminalService(TerminalService terminalService) {
		this.terminalService = terminalService;
	}

	public List<Terminal> getTerminalList() {
		return terminalList;
	}

	public void setTerminalList(List<Terminal> terminalList) {
		this.terminalList = terminalList;
	}

	public List<LotteryType> getLotteryTypeList() {
		return OnSaleLotteryList.get();
	}

	public TerminalCompanyService getTerminalCompanyService() {
		return terminalCompanyService;
	}
	
	public void setTerminalCompanyService(
			TerminalCompanyService terminalCompanyService) {
		this.terminalCompanyService = terminalCompanyService;
	}

	public TerminalCompany getTerminalCompany() {
		return terminalCompany;
	}

	public void setTerminalCompany(TerminalCompany terminalCompany) {
		this.terminalCompany = terminalCompany;
	}

	public List<TerminalCompany> getTerminalCompanys() {
		return terminalCompanys;
	}

	public void setTerminalCompanys(List<TerminalCompany> terminalCompanys) {
		this.terminalCompanys = terminalCompanys;
	}

	public List<Terminal> getTerminalListForView() {
		return terminalListForView;
	}

	public void setTerminalListForView(List<Terminal> terminalListForView) {
		this.terminalListForView = terminalListForView;
	}
}
