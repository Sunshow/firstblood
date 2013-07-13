package web.dao.ticket;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.finance.TerminalAccountCheckItem;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketStatus;

public interface TicketDao {
	
	/**
	 * 按条件查询票数据
	 * @param ticket
	 * @param beginCreateTime 拆票起始时间
	 * @param endCreateTime 拆票截止时间
	 * @param beginPrintTime 出票起始时间
	 * @param endPrintTime 出票截止时间
	 * @param beginSendTime 送票起始时间
	 * @param endSendTime 送票截止时间
	 * @param pageBean		
	 * @param orderStr
	 * @param orderView
	 * @return
	 */
	public List<Ticket> getResult(Ticket ticket, Date beginCreateTime, Date endCreateTime, Date beginPrintTime, Date endPrintTime,
                                  Date beginSendTime, Date endSendTime, PageBean pageBean, String orderStr, String orderView);
	
	/**
	 * 按条件查询票数量
	 * @param ticket
	 * @param beginCreateTime 拆票起始时间
	 * @param endCreateTime 拆票截止时间
	 * @param beginPrintTime 出票起始时间
	 * @param endPrintTime 出票截止时间
	 * @param beginSendTime 送票起始时间
	 * @param endSendTime 送票截止时间
	 * @param isWinning 是否中奖
	 * @param pageBean
	 * @return
	 */
	public int getCounts(Ticket ticket, Date beginCreateTime, Date endCreateTime, Date beginPrintTime, Date endPrintTime,
                         Date beginSendTime, Date endSendTime);
	/**
	 * 根据方案编码、pageBean查询票信息
	 * @param planId
	 * @param pageBean
	 * @return
	 */
	public List<Ticket> getResultByPlanId(String planId, PageBean pageBean);
	
	/**
	 * 根据方案编码查询票数量
	 * @param planId
	 * @return
	 */
	public int getCountsByPlanId(String planId);

	/**
	 * 根据ID获取票数居
	 * @param id
	 * @return
	 */
	public Ticket get(Long id);
	
	/**
	 * 根据终端ID列表、彩种、彩期获取该终端的出票金额和中奖金额
	 * @author chirowong
	 * @param terminalIds
	 * @param lotteryTypeId
	 * @param accountCheckType
	 * @param accountCheckDate
	 * @return
	 */
	public TerminalAccountCheckItem getTerminalAccountCheckItem(String terminalIds, Integer lotteryTypeId, Integer accountCheckType, String accountCheckDate);
	
	/**
	 * 根据planId更新出票截止时间,通过planId查询到改方案的截止时间，然后更新该方案对应的票的截止时间
	 * @param planId
	 * @param terminateTime
	 */
	public void updateTerminateTimeByPlanId(String planId, Date terminateTime, List<TicketStatus> ticketStatusList);
}
