package web.service.ticket;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.finance.TerminalAccountCheckItem;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.engine.entity.ticket.Ticket;

public interface TicketService {

	/**
	 * 按条件查询票数据
	 * @param ticket
	 * @param pageBean
	 * @param orderStr TODO
	 * @param OrderView TODO
	 * @return
	 */
	public Map<String, Object> getResult(Ticket ticket, Date beginCreateTime, Date endCreateTime, Date beginPrintTime, Date endPrintTime,
                                         Date beginSendTime, Date endSendTime, PageBean pageBean, String orderStr, String OrderView);

	/**
	 * 根据id获取票数居
	 * @param id
	 * @return
	 */
	public Ticket get(Long id);
	/**
	 * 根据方案编码查询票信息
	 * @param planId
	 * @return
	 */
	public List<Ticket> getListByPlanId(String planId, PageBean pageBean);
	/**
	 * 根据方案编码查询票数量
	 * @param planId
	 * @return
	 */
	public int getCountByPlanId(String planId);
	
	/**
	 * 根据终端ID、彩种、彩期获取该终端的出票金额和中奖金额
	 * @author chirowong
	 * @param terminalId
	 * @param lotteryTypeId
	 * @param accountCheckType
	 * @param accountCheckDate
	 * @return
	 */
	public TerminalAccountCheckItem getTerminalAccountCheckItem(String terminalIds, Integer lotteryTypeId, Integer accountCheckType, String accountCheckDate);
	
	/**
	 * 根据planIds更新出票截止时间
	 * @param planIds
	 * @throws ApiRemoteCallFailedException
	 */
	public void updateTerminateTimeByPlanIds(List<String> planIds, List<String> changedList) throws ApiRemoteCallFailedException ;
}
