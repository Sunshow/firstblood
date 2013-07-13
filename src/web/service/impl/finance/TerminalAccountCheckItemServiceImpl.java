package web.service.impl.finance;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.finance.TerminalAccountCheckItemDao;
import com.lehecai.admin.web.domain.finance.TerminalAccountCheckItem;
import com.lehecai.admin.web.service.finance.TerminalAccountCheckItemService;

public class TerminalAccountCheckItemServiceImpl implements TerminalAccountCheckItemService {
	
	private TerminalAccountCheckItemDao terminalAccountCheckItemDao;
	
	@Override
	public void del(TerminalAccountCheckItem terminalAccountCheckItem) {
		terminalAccountCheckItemDao.del(terminalAccountCheckItem);
	}

	@Override
	public TerminalAccountCheckItem get(Long ID) {
		return terminalAccountCheckItemDao.get(ID);
	}

	@Override
	public void manage(TerminalAccountCheckItem terminalAccountCheckItem) {
		terminalAccountCheckItemDao.merge(terminalAccountCheckItem);
	}

	@Override
	public List<TerminalAccountCheckItem> list(TerminalAccountCheckItem terminalAccountCheckItem,Date beginDate,Date endDate,PageBean pageBean){
		return terminalAccountCheckItemDao.list(terminalAccountCheckItem, beginDate, endDate, pageBean);
	}
	
	@Override
	public PageBean getPageBean(TerminalAccountCheckItem terminalAccountCheckItem,Date beginDate,Date endDate, PageBean pageBean){
		return terminalAccountCheckItemDao.getPageBean(terminalAccountCheckItem, beginDate, endDate, pageBean);
	}
	
	@Override
	public TerminalAccountCheckItem getTotal(TerminalAccountCheckItem terminalAccountCheckItem,Date beginDate,Date endDate, PageBean pageBean){
		return terminalAccountCheckItemDao.getTotal(terminalAccountCheckItem, beginDate, endDate, pageBean);
	}

	public TerminalAccountCheckItemDao getTerminalAccountCheckItemDao() {
		return terminalAccountCheckItemDao;
	}

	public void setTerminalAccountCheckItemDao(
			TerminalAccountCheckItemDao terminalAccountCheckItemDao) {
		this.terminalAccountCheckItemDao = terminalAccountCheckItemDao;
	}
}
