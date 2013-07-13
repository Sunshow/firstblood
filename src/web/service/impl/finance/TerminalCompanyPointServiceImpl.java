package web.service.impl.finance;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.finance.TerminalCompanyPointDao;
import com.lehecai.admin.web.domain.finance.TerminalCompanyPoint;
import com.lehecai.admin.web.service.finance.TerminalCompanyPointService;

public class TerminalCompanyPointServiceImpl implements TerminalCompanyPointService {
	
	private TerminalCompanyPointDao terminalCompanyPointDao;
	
	@Override
	public void del(TerminalCompanyPoint terminalCompanyPoint) {
		terminalCompanyPointDao.del(terminalCompanyPoint);
	}

	@Override
	public TerminalCompanyPoint get(Long ID) {
		return terminalCompanyPointDao.get(ID);
	}

	@Override
	public void manage(TerminalCompanyPoint terminalCompanyPoint) {
		terminalCompanyPointDao.merge(terminalCompanyPoint);
	}
	
	@Override
	public TerminalCompanyPoint update(TerminalCompanyPoint terminalCompanyPoint) {
		return terminalCompanyPointDao.merge(terminalCompanyPoint);
	}
	
	@Override
	public List<TerminalCompanyPoint> list(TerminalCompanyPoint terminalCompanyPoint,PageBean pageBean){
		return terminalCompanyPointDao.list(terminalCompanyPoint, pageBean);
	}
	
	@Override
	public PageBean getPageBean(TerminalCompanyPoint terminalCompanyPoint,
			PageBean pageBean) {
		return terminalCompanyPointDao.getPageBean(terminalCompanyPoint, pageBean);
	}

	public TerminalCompanyPointDao getTerminalCompanyPointDao() {
		return terminalCompanyPointDao;
	}

	public void setTerminalCompanyPointDao(
			TerminalCompanyPointDao terminalCompanyPointDao) {
		this.terminalCompanyPointDao = terminalCompanyPointDao;
	}
}
