/**
 * 
 */
package web.dao.finance;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.finance.TerminalCompany;
/**
 * @author chirowong
 *
 */
public interface TerminalCompanyDao {
	public TerminalCompany get(Long ID);
	public void save(TerminalCompany terminalCompany);
	public void del(TerminalCompany terminalCompany);
	public TerminalCompany merge(TerminalCompany terminalCompany);
	public List<TerminalCompany> list(TerminalCompany terminalCompany, PageBean pageBean);
	public PageBean getPageBean(TerminalCompany terminalCompany, PageBean pageBean);
}
