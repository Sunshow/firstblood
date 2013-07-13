package web.service.impl.finance;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.finance.TerminalCompanyDao;
import com.lehecai.admin.web.domain.finance.TerminalCompany;
import com.lehecai.admin.web.service.finance.TerminalCompanyService;

public class TerminalCompanyServiceImpl implements TerminalCompanyService {
	
	private TerminalCompanyDao terminalCompanyDao;
	
	@Override
	public void del(TerminalCompany terminalCompany) {
		terminalCompanyDao.del(terminalCompany);
	}

	@Override
	public TerminalCompany get(Long ID) {
		return terminalCompanyDao.get(ID);
	}

	@Override
	public void manage(TerminalCompany terminalCompany) {
		terminalCompanyDao.merge(terminalCompany);
	}
	
	@Override
	public TerminalCompany update(TerminalCompany terminalCompany) {
		return terminalCompanyDao.merge(terminalCompany);
	}
	
	@Override
	public List<TerminalCompany> list(TerminalCompany terminalCompany,PageBean pageBean){
		return terminalCompanyDao.list(terminalCompany, pageBean);
	}
	
	@Override
	public PageBean getPageBean(TerminalCompany terminalCompany,
			PageBean pageBean) {
		return terminalCompanyDao.getPageBean(terminalCompany, pageBean);
	}

	public TerminalCompanyDao getTerminalCompanyDao() {
		return terminalCompanyDao;
	}

	public void setTerminalCompanyDao(
			TerminalCompanyDao terminalCompanyDao) {
		this.terminalCompanyDao = terminalCompanyDao;
	}
}
