package web.action.include.statics.ticket;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.service.ticket.TicketService;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.engine.entity.ticket.Ticket;
import com.opensymphony.xwork2.Action;

public class TicketInfoAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private static final long serialVersionUID = 1L;
	
	private TicketService ticketService;
	
	private String plan_id;//方案编码
	private int page = 1;
	private int pagesize = 20;
	
	public String handle(){
		logger.info("开始执行查询票信息");
		
		Integer rc = 0;//0成功,1失败
		String message = "操作成功";
		
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject dataJson = new JSONObject();
		
		if (plan_id == null || "".equals(plan_id)) {
			rc = 1 ;
			message = "查看票信息:参数plan_id不能为空";
			logger.error("信息错误:查看票信息参数plan_id不能为空");
			return makeJson(response, rc, message, dataJson);
		}
		
		if (page < 1) {
			page = 1;
		}
		
		if (pagesize < 1) {
			pagesize = 20;
		}
		
		if (pagesize > 100) {
			pagesize = 100;
		}
		
		int cnt = page * pagesize;
		if (cnt - pagesize + 1 > 1000) {
			rc = 2 ;
			message = "查看票信息:只能查看最新的1000条记录";
			logger.error("信息错误:查看票信息只能查看最新的1000条记录");
			return makeJson(response, rc, message, dataJson);
		}
		
		PageBean pageBean = super.getPageBean();
		pageBean.setPage(page);
		if (cnt > 1000) {
			pageBean.setPageSize(1000 - (cnt - pagesize));
		} else {
			pageBean.setPageSize(pagesize);
		}
		JSONArray jsonArray = new JSONArray();
		
		List<Ticket> tickets = ticketService.getListByPlanId(plan_id, pageBean);
		for (Ticket t : tickets) {
			JSONObject d = new JSONObject();
			d.put("id", t.getId());
			d.put("lottery_type", t.getLotteryType().getValue());
			d.put("status", t.getStatus().getValue());
			d.put("content", t.getContent());
			d.put("amount", t.getAmount());
			d.put("create_time", t.getCreateTime() == null ? null : CoreDateUtils.formatDate(t.getCreateTime(), CoreDateUtils.DATETIME));
			d.put("print_time", t.getPrintTime() == null ? null : CoreDateUtils.formatDate(t.getPrintTime(), CoreDateUtils.DATETIME));
			d.put("send_time", t.getSendTime() == null ? null : CoreDateUtils.formatDate(t.getSendTime(), CoreDateUtils.DATETIME));
			d.put("multiple", t.getMultiple());
			d.put("play_type", t.getPlayType().getValue());
			d.put("dlt_addition", t.getDltAddition().getValue());
			d.put("is_winning", t.getIsWinning().getValue());
			d.put("winning_amount", t.getWinningAmount());
			d.put("winning_detail", t.getWinningDetail());
			d.put("ext", t.getExt());
			jsonArray.add(d);
		}
		dataJson.put("data", jsonArray);
		
		int count = ticketService.getCountByPlanId(plan_id);
		dataJson.put("count", count);
		
		logger.info("结束执行查询票信息");
		return makeJson(response, rc, message, dataJson);
	}
	
	protected String makeJson(HttpServletResponse response, Integer rc, String message, JSONObject dataJson) {
		JSONObject json = new JSONObject();
		json.put("code", rc);
		json.put("message", message);
		json.put("data", dataJson);
		
		super.writeRs(response, json);
		return Action.NONE;
	}

	public TicketService getTicketService() {
		return ticketService;
	}

	public void setTicketService(TicketService ticketService) {
		this.ticketService = ticketService;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String planId) {
		plan_id = planId;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
}
