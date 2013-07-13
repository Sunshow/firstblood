package web.service.finance;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.finance.TerminalFundAdjust;

public interface TerminalFundAdjustService {
	public TerminalFundAdjust get(Long ID);
	public void manage(TerminalFundAdjust terminalFundAdjust);
	public void del(TerminalFundAdjust terminalFundAdjust);
	public List<TerminalFundAdjust> list(TerminalFundAdjust terminalFundAdjust, PageBean pageBean);
	public PageBean getPageBean(TerminalFundAdjust terminalFundAdjust, PageBean pageBean);
}
