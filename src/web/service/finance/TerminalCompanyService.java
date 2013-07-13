package web.service.finance;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.finance.TerminalCompany;

public interface TerminalCompanyService {
	public TerminalCompany get(Long ID);
	public void manage(TerminalCompany terminalCompany);
	public void del(TerminalCompany terminalCompany);
	public TerminalCompany update(TerminalCompany terminalCompany);
	public List<TerminalCompany> list(TerminalCompany terminalCompany, PageBean pageBean);
	public PageBean getPageBean(TerminalCompany terminalCompany, PageBean pageBean);
}
