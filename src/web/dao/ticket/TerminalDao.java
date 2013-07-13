package web.dao.ticket;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.engine.entity.terminal.Terminal;

public interface TerminalDao {
	void merge(Terminal terminal);
	List<Terminal> list(Terminal terminal, PageBean pageBean);
	Terminal get(Long ID);
	void del(Terminal terminal);
	PageBean getPageBean(Terminal terminal, PageBean pageBean);
}