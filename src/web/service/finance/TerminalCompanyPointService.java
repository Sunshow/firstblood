package web.service.finance;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.finance.TerminalCompanyPoint;

public interface TerminalCompanyPointService {
	public TerminalCompanyPoint get(Long ID);
	public void manage(TerminalCompanyPoint terminalCompanyPoint);
	public void del(TerminalCompanyPoint terminalCompanyPoint);
	public TerminalCompanyPoint update(TerminalCompanyPoint terminalCompanyPoint);
	public List<TerminalCompanyPoint> list(TerminalCompanyPoint terminalCompanyPoint, PageBean pageBean);
	public PageBean getPageBean(TerminalCompanyPoint terminalCompanyPoint, PageBean pageBean);
}
