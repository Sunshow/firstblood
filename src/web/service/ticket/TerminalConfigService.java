package web.service.ticket;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.engine.entity.terminal.Terminal;
import com.lehecai.engine.entity.terminal.TerminalConfig;

public interface TerminalConfigService {
	void manage(TerminalConfig terminalConfig);
	List<TerminalConfig> list(TerminalConfig terminalConfig, PageBean pageBean);
	List<TerminalConfig> listByPlayType(TerminalConfig terminalConfig, PageBean pageBean);
	TerminalConfig get(Long ID);
	void del(TerminalConfig terminalConfig);
	PageBean getPageBean(TerminalConfig terminalConfig, PageBean pageBean);
	List<Terminal> listTerminal(TerminalConfig terminalConfig, PageBean pageBean);
	List<Terminal> listTerminalByPlayType(TerminalConfig terminalConfig, PageBean pageBean);
	public List<TerminalConfig> listByTerminalType(TerminalConfig terminalConfig, PageBean pageBean);
}