package web.action.ticket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.service.ticket.TerminalService;
import com.lehecai.admin.web.service.ticket.TicketBatchService;
import com.lehecai.engine.entity.terminal.Terminal;
import com.lehecai.engine.entity.ticket.TicketBatch;
import com.lehecai.engine.entity.ticket.TicketBatchStatus;

public class TerminalMonitorAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private Logger logger = LoggerFactory.getLogger(TerminalMonitorAction.class);
	
	private TicketBatchService ticketBatchService;
	private TerminalService terminalService;
	
	private List<Terminal> terminals;
	private Map<Long, List<TicketBatch>> ticketBatchs = new HashMap<Long, List<TicketBatch>>();
	private TicketBatch ticketBatch = null;
	
	public String handle() {
		logger.info("进入查询终端监控列表");
		PageBean pageBean = new PageBean();
		pageBean.setPageFlag(false);
		terminals = terminalService.list(null, pageBean);
		for (Terminal terminal : terminals) {
			List<TicketBatch> ticketBatch = ticketBatchService.findByStatusAndTerminalId(TicketBatchStatus.SEND_WAITING, terminal.getId(), 0);
			ticketBatchs.put(terminal.getId(), ticketBatch);
		}
		return "index";
	}

	public String changeTerminal() {
		logger.info("进入修改终端");
		if (ticketBatch != null && (ticketBatch.getId() != null && ticketBatch.getId() != 0L) 
				&& (ticketBatch.getTerminalId() != null && ticketBatch.getTerminalId() != 0L)) {
			ticketBatchService.updateTerminalId(ticketBatch.getId(), ticketBatch.getTerminalId());
			
			return handle();
		} else {
			logger.error("修改出票终端，所需参数为空");
			super.setErrorMessage("修改出票终端，所需的参数为空");
			return "failure";
		}
	}
	
	
	public List<Terminal> getTerminals() {
		return terminals;
	}

	public void setTerminals(List<Terminal> terminals) {
		this.terminals = terminals;
	}

	public Map<Long, List<TicketBatch>> getTicketBatchs() {
		return ticketBatchs;
	}

	public void setTicketBatchs(Map<Long, List<TicketBatch>> ticketBatchs) {
		this.ticketBatchs = ticketBatchs;
	}

	public void setTicketBatchService(TicketBatchService ticketBatchService) {
		this.ticketBatchService = ticketBatchService;
	}

	public void setTerminalService(TerminalService terminalService) {
		this.terminalService = terminalService;
	}

	public TicketBatch getTicketBatch() {
		return ticketBatch;
	}

	public void setTicketBatch(TicketBatch ticketBatch) {
		this.ticketBatch = ticketBatch;
	}
}
