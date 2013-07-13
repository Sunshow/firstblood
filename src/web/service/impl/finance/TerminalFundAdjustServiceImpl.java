package web.service.impl.finance;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.finance.TerminalFundAdjustDao;
import com.lehecai.admin.web.domain.finance.TerminalFundAdjust;
import com.lehecai.admin.web.service.finance.TerminalFundAdjustService;

public class TerminalFundAdjustServiceImpl implements TerminalFundAdjustService {
	
	private TerminalFundAdjustDao terminalFundAdjustDao;
	
	@Override
	public void del(TerminalFundAdjust terminalFundAdjust) {
		terminalFundAdjustDao.del(terminalFundAdjust);
	}

	@Override
	public TerminalFundAdjust get(Long ID) {
		return terminalFundAdjustDao.get(ID);
	}

	@Override
	public void manage(TerminalFundAdjust terminalFundAdjust) {
		terminalFundAdjustDao.merge(terminalFundAdjust);
	}

	@Override
	public List<TerminalFundAdjust> list(TerminalFundAdjust terminalFundAdjust,PageBean pageBean){
		return terminalFundAdjustDao.list(terminalFundAdjust, pageBean);
	}
	
	@Override
	public PageBean getPageBean(TerminalFundAdjust terminalFundAdjust,
			PageBean pageBean) {
		return terminalFundAdjustDao.getPageBean(terminalFundAdjust, pageBean);
	}

	public TerminalFundAdjustDao getTerminalFundAdjustDao() {
		return terminalFundAdjustDao;
	}

	public void setTerminalFundAdjustDao(
			TerminalFundAdjustDao terminalFundAdjustDao) {
		this.terminalFundAdjustDao = terminalFundAdjustDao;
	}
}
