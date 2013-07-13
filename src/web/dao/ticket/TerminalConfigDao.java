package web.dao.ticket;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.engine.entity.terminal.TerminalConfig;

public interface TerminalConfigDao {
	void merge(TerminalConfig terminalConfig);
	List<TerminalConfig> list(TerminalConfig terminalConfig, PageBean pageBean);
	List<TerminalConfig> listByPlayType(TerminalConfig terminalConfig, PageBean pageBean);
	TerminalConfig get(Long ID);
	void del(TerminalConfig terminalConfig);
	PageBean getPageBean(TerminalConfig terminalConfig, PageBean pageBean);
	void delByTerminalId(Long id);	
	public List<TerminalConfig> listByTerminalType(TerminalConfig terminalConfig, PageBean pageBean);
}