/**
 * 
 */
package web.dao.finance;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.finance.TerminalFundAdjust;

/**
 * @author chirowong
 *
 */
public interface TerminalFundAdjustDao {
	public TerminalFundAdjust get(Long ID);
	public void save(TerminalFundAdjust terminalFundAdjust);
	public void del(TerminalFundAdjust terminalFundAdjust);
	public TerminalFundAdjust merge(TerminalFundAdjust terminalFundAdjust);
	public List<TerminalFundAdjust> list(TerminalFundAdjust terminalFundAdjust, PageBean pageBean);
	public PageBean getPageBean(TerminalFundAdjust terminalFundAdjust, PageBean pageBean);
}
