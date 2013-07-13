/**
 * 
 */
package web.dao.finance;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.finance.TerminalCompanyPoint;
/**
 * @author chirowong
 *
 */
public interface TerminalCompanyPointDao {
	public TerminalCompanyPoint get(Long ID);
	public void save(TerminalCompanyPoint terminalCompanyPoint);
	public void del(TerminalCompanyPoint terminalCompanyPoint);
	public TerminalCompanyPoint merge(TerminalCompanyPoint terminalCompanyPoint);
	public List<TerminalCompanyPoint> list(TerminalCompanyPoint terminalCompanyPoint, PageBean pageBean);
	public PageBean getPageBean(TerminalCompanyPoint terminalCompanyPoint, PageBean pageBean);
}
