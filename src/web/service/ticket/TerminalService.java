package web.service.ticket;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.engine.entity.terminal.Terminal;

public interface TerminalService {
	void manage(Terminal terminal);
	List<Terminal> list(Terminal terminal, PageBean pageBean);
	Terminal get(Long ID);
	void del(Terminal terminal);
	PageBean getPageBean(Terminal terminal, PageBean pageBean);
	public ResultBean deleteLotteryType(List<String> deleteLotteryType) throws ApiRemoteCallFailedException;
}