package web.action.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.BIService;
import com.lehecai.admin.web.service.ticket.TerminalService;
import com.lehecai.engine.entity.terminal.Terminal;

public class SyncTicketJcSpAction extends BaseAction {

	private static final long serialVersionUID = -625750626570317436L;
	private Logger logger = LoggerFactory.getLogger(SyncTicketJcSpAction.class);

	private TerminalService terminalService;
	private List<Terminal> terminalList;
	private BIService bIService;
	private Long terminalId;
	private String ticketString;

	private final static String TERMINAL_ID = "terminalId";
	private final static String TICKET_ID = "ticketId";
	private final static String TERMINAL_TYPE_ID = "terminalTypeId";

	public String handle() {
		logger.info("列出所有终端");
		terminalList = terminalService.list(null, super.getPageBean());
		return "inputForm";
	}

	public String syncTicketJcSp() {

		HttpServletResponse response = ServletActionContext.getResponse();

		StringBuffer log = new StringBuffer();

		// 必要参数判断
		if (ticketString == null) {
			log.append("票ID不能为空!");
			logger.error(log.toString());
			writeRs(response, log.toString());
			return null;
		}

		if (this.terminalId == null) {
			log.append("请指定查询终端!");
			logger.error(log.toString());
			writeRs(response, log.toString());
			return null;
		}

		// 将票字符串转为List
		List<Long> ticketIdList = new ArrayList<Long>();
		
		try {
			String[] ticketIdArray = StringUtils.split(ticketString, ",;\n ");

			for (String id : ticketIdArray) {
				ticketIdList.add(Long.parseLong(id));
			}

		} catch (Exception e) {
			log.append("请将票ID用\",\"隔开!ID只能为数字!");
			writeRs(response, log.toString());
			logger.error(log.toString());
			return null;
		}

		// 获取终端类型
		Terminal terminal = terminalService.get(this.terminalId);

		if (terminal == null) {
			log.append(" --> 未知终端!");
			logger.error(log.toString());
			writeRs(response, String.format("同步失败 : 未知终端ID=%s!", this.terminalId));
			return null;
		}

		log.append("目的 : 同步票SP值 ; ");
		log.append(String.format("票ID : [%s] ; ", ticketString));
		log.append(String.format("终端ID : %s ; ", this.terminalId));
		log.append(String.format("终端类型 : %s ; ", terminal.getName()));

		StringBuffer spInfo = new StringBuffer();// 记录每张票的同步结果
		boolean errorFlag = false;// 是否需要打日志

		// 开始依次同步SP值
		for (Long ticketId : ticketIdList) {

			spInfo.append(String.format("票ID=[%s]SP值 : ", ticketId.toString()));

			Map<String, String> requestMap = generatorSyncJcSpString(ticketId.toString(), terminalId.toString(), String.valueOf(terminal.getTerminalType().getValue()));

			// 执行请求
			Map<String, String> responseMap = bIService.request(requestMap);

			if (responseMap == null || responseMap.size() == 0) {
				spInfo.append("失败! --> 程序返回为空!<br/>");
				errorFlag = true;
				continue;
			}

			if (responseMap.get(BIService.RESP_CODE).equals("0000")) {
				String sp = responseMap.get(BIService.RESP_MESG);
				spInfo.append(String.format("成功! --> %s!<br/>", sp));
				continue;
			} else {
				String info = responseMap.get(BIService.RESP_MESG);
				spInfo.append(String.format("失败! --> %s!<br/>", info));
				logger.error(log.toString());
				errorFlag = true;
			}
		}

		log.append(spInfo.toString());

		if (errorFlag) {
			logger.error(log.toString());
		} else {
			logger.info(log.toString());
		}

		writeRs(response, spInfo.toString());
		return null;
	}

	/** 生成请求MAP */
	private Map<String, String> generatorSyncJcSpString(String ticketId, String terminalId, String terminalType) {
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put(BIService.PROCESS_CODE, BIService.Ticket_Jc_Sp_Synchronous);
		requestMap.put(TICKET_ID, ticketId);
		requestMap.put(TERMINAL_ID, terminalId);
		requestMap.put(TERMINAL_TYPE_ID, terminalType);
		return requestMap;
	}

	public TerminalService getTerminalService() {
		return terminalService;
	}

	public void setTerminalService(TerminalService terminalService) {
		this.terminalService = terminalService;
	}

	public List<Terminal> getTerminalList() {
		return terminalList;
	}

	public Long getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(Long terminalId) {
		this.terminalId = terminalId;
	}

	public BIService getbIService() {
		return bIService;
	}

	public void setbIService(BIService bIService) {
		this.bIService = bIService;
	}

	public String getTicketString() {
		return ticketString;
	}

	public void setTicketString(String ticketString) {
		this.ticketString = ticketString;
	}

}
