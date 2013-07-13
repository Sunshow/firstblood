package web.dao.finance;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.finance.TerminalAccountCheckItem;

public interface TerminalAccountCheckItemDao {
	public TerminalAccountCheckItem get(Long ID);
	public void save(TerminalAccountCheckItem item);
	public void del(TerminalAccountCheckItem item);
	public TerminalAccountCheckItem merge(TerminalAccountCheckItem item);
	public List<TerminalAccountCheckItem> list(TerminalAccountCheckItem terminalAccountCheckItem, Date beginDate, Date endDate, PageBean pageBean);
	public PageBean getPageBean(TerminalAccountCheckItem terminalAccountCheckItem, Date beginDate, Date endDate, PageBean pageBean);
	public TerminalAccountCheckItem getTotal(TerminalAccountCheckItem terminalAccountCheckItem, Date beginDate, Date endDate, PageBean pageBean);
}
