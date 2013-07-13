package web.dao.impl.ticket;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.ticket.TicketDao;
import com.lehecai.admin.web.domain.finance.TerminalAccountCheckItem;
import com.lehecai.core.YesNoStatus;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketBatch;
import com.lehecai.engine.entity.ticket.TicketStatus;

public class TicketDaoImpl extends HibernateDaoSupport implements TicketDao {
	private final Logger logger = LoggerFactory.getLogger(TicketDaoImpl.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Ticket> getResult(final Ticket ticket, final Date beginCreateTime, final Date endCreateTime, final Date beginPrintTime, final Date endPrintTime, 
			final Date beginSendTime, final Date endSendTime, final PageBean pageBean, final String orderStr, final String orderView) {
		return getHibernateTemplate().executeFind(new HibernateCallback<List<TicketBatch>>() {
					@Override
					public List<TicketBatch> doInHibernate(Session session)
							throws HibernateException, SQLException {
						StringBuffer sb = new StringBuffer("from Ticket m where 1=1");

						if (ticket != null) {
							if (ticket.getPhase() != null && !"".equals(ticket.getPhase())) {
								sb.append(" and m.phase=:phase");
							}
							if (ticket.getLotteryType() != null && ticket.getLotteryType().getValue() > 0) {
								sb.append(" and m.lotteryType=:lotteryType");
							}
							if (ticket.getPlanNo() != null && !"".equals(ticket.getPlanNo())) {
								sb.append(" and m.planNo=:planNo");
							}
							if (ticket.getBatchId() != null && ticket.getBatchId() > 0) {
								sb.append(" and m.batchId=:batchId");
							}
							if (ticket.getId() != null && ticket.getId() > 0) {
								sb.append(" and m.id=:id");
							}
							if (ticket.getUsername() != null && !"".equals(ticket.getUsername())) {
								sb.append(" and m.username=:username");
							}
							if (ticket.getTerminalId() != null && ticket.getTerminalId() > 0) {
								sb.append(" and m.terminalId=:terminalId");
							}
							if (ticket.getStatus() != null && ticket.getStatus().getValue() > 0) {
								sb.append(" and m.status=:status");
							}
							if (ticket.getIsWinning() != null && ticket.getIsWinning().getValue() != YesNoStatus.ALL.getValue()) {
								sb.append(" and m.isWinning=:isWinning");
							}
							if (!StringUtils.isEmpty(ticket.getSerialId())) {
								sb.append(" and m.serialId=:serialId");
							}
							if (!StringUtils.isEmpty(ticket.getExternalId())) {
								sb.append(" and m.externalId=:externalId");
							}
						}
						if (beginCreateTime != null) {
							sb.append(" and m.createTime >= :beginCreateTime");
						}
						if (endCreateTime != null) {
							sb.append(" and m.createTime <= :endCreateTime");
						}
						if (beginPrintTime != null) {
							sb.append(" and m.printTime >= :beginPrintTime");
						}
						if (endPrintTime != null) {
							sb.append(" and m.printTime <= :endPrintTime");
						}
						if (beginSendTime != null) {
							sb.append(" and m.sendTime >= :beginSendTime");
						}
						if (endSendTime != null) {
							sb.append(" and m.sendTime <= :endSendTime");
						}
						
						if (orderStr != null && !orderStr.isEmpty()) {
							sb.append(" order by ").append(orderStr).append(" ").append(orderView);
						}

						logger.info("按条件查询票数据HQL:{}", sb.toString());
						Query query = session.createQuery(sb.toString());

						if (ticket != null) {
							if (ticket.getPhase() != null && !"".equals(ticket.getPhase())) {
								query.setParameter("phase", ticket.getPhase());
							}
							if (ticket.getLotteryType() != null && ticket.getLotteryType().getValue() > 0) {
								query.setParameter("lotteryType", ticket.getLotteryType());
							}
							if (ticket.getPlanNo() != null && !"".equals(ticket.getPlanNo())) {
								query.setParameter("planNo", ticket.getPlanNo());
							}
							if (ticket.getBatchId() != null && ticket.getBatchId() > 0) {
								query.setParameter("batchId", ticket.getBatchId());
							}
							if (ticket.getId() != null && ticket.getId() > 0) {
								query.setParameter("id", ticket.getId());
							}
							if (ticket.getUsername() != null && !"".equals(ticket.getUsername())) {
								query.setParameter("username", ticket.getUsername());
							}
							if (ticket.getTerminalId() != null && ticket.getTerminalId() > 0) {
								query.setParameter("terminalId", ticket.getTerminalId());
							}
							if (ticket.getStatus() != null && ticket.getStatus().getValue() > 0) {
								query.setParameter("status", ticket.getStatus());
							}
							if (ticket.getIsWinning() != null && ticket.getIsWinning().getValue() != YesNoStatus.ALL.getValue()) {
								query.setParameter("isWinning", ticket.getIsWinning());
							}
							if (!StringUtils.isEmpty(ticket.getSerialId())) {
								query.setParameter("serialId", ticket.getSerialId());
							}
							if (!StringUtils.isEmpty(ticket.getExternalId())) {
								query.setParameter("externalId", ticket.getExternalId());
							}
						}
						if (beginCreateTime != null) {
							query.setParameter("beginCreateTime", beginCreateTime);
						}
						if (endCreateTime != null) {
							query.setParameter("endCreateTime", endCreateTime);
						}
						if (beginPrintTime != null) {
							query.setParameter("beginPrintTime", beginPrintTime);
						}
						if (endPrintTime != null) {
							query.setParameter("endPrintTime", endPrintTime);
						}
						if (beginSendTime != null) {
							query.setParameter("beginSendTime", beginSendTime);
						}
						if (endSendTime != null) {
							query.setParameter("endSendTime", endSendTime);
						}

						if (pageBean != null && pageBean.isPageFlag()) {
							if (pageBean.getPage() > 0) {
								query.setFirstResult((pageBean.getPage() - 1)
										* pageBean.getPageSize());
							}
							if (pageBean.getPageSize() > 0) {
								query.setMaxResults(pageBean.getPageSize());
							}
						}

						return query.list();
					}
				});
	}

	@Override
	public int getCounts(final Ticket ticket, final Date beginCreateTime, final Date endCreateTime, final Date beginPrintTime, final Date endPrintTime, 
			final Date beginSendTime, final Date endSendTime) {
		return (Integer) getHibernateTemplate().execute(new HibernateCallback<Object>() {
					@Override
					public Integer doInHibernate(Session session)
							throws HibernateException, SQLException {
						StringBuffer sb = new StringBuffer("select count(m.id) from Ticket m where 1=1");

						if (ticket != null) {
							if (ticket.getLotteryType() != null && ticket.getLotteryType().getValue() > 0) {
								sb.append(" and m.lotteryType=:lotteryType");
							}
							if (ticket.getPhase() != null && !"".equals(ticket.getPhase())) {
								sb.append(" and m.phase=:phase");
							}
							if (ticket.getPlanNo() != null && !"".equals(ticket.getPlanNo())) {
								sb.append(" and m.planNo=:planNo");
							}
							if (ticket.getBatchId() != null && ticket.getBatchId() > 0) {
								sb.append(" and m.batchId=:batchId");
							}
							if (ticket.getId() != null && ticket.getId() > 0) {
								sb.append(" and m.id=:id");
							}
							if (ticket.getUsername() != null && !"".equals(ticket.getUsername())) {
								sb.append(" and m.username=:username");
							}
							if (ticket.getTerminalId() != null && ticket.getTerminalId() > 0) {
								sb.append(" and m.terminalId=:terminalId");
							}
							if (ticket.getStatus() != null && ticket.getStatus().getValue() > 0) {
								sb.append(" and m.status=:status");
							}
							if (ticket.getIsWinning() != null && ticket.getIsWinning().getValue() != YesNoStatus.ALL.getValue()) {
								sb.append(" and m.isWinning=:isWinning");
							}
							if (!StringUtils.isEmpty(ticket.getSerialId())) {
								sb.append(" and m.serialId=:serialId");
							}
							if (!StringUtils.isEmpty(ticket.getExternalId())) {
								sb.append(" and m.externalId=:externalId");
							}
						}
						if (beginCreateTime != null) {
							sb.append(" and m.createTime >= :beginCreateTime");
						}
						if (endCreateTime != null) {
							sb.append(" and m.createTime <= :endCreateTime");
						}
						if (beginPrintTime != null) {
							sb.append(" and m.printTime >= :beginPrintTime");
						}
						if (endPrintTime != null) {
							sb.append(" and m.printTime <= :endPrintTime");
						}
						if (beginSendTime != null) {
							sb.append(" and m.sendTime >= :beginSendTime");
						}
						if (endSendTime != null) {
							sb.append(" and m.sendTime <= :endSendTime");
						}

						Query query = session.createQuery(sb.toString());

						if (ticket != null) {
							if (ticket.getLotteryType() != null && ticket.getLotteryType().getValue() > 0) {
								query.setParameter("lotteryType", ticket.getLotteryType());
							}
							if (ticket.getPhase() != null && !"".equals(ticket.getPhase())) {
								query.setParameter("phase", ticket.getPhase());
							}
							if (ticket.getPlanNo() != null && !"".equals(ticket.getPlanNo())) {
								query.setParameter("planNo", ticket.getPlanNo());
							}
							if (ticket.getBatchId() != null && ticket.getBatchId() > 0) {
								query.setParameter("batchId", ticket.getBatchId());
							}
							if (ticket.getId() != null && ticket.getId() > 0) {
								query.setParameter("id", ticket.getId());
							}
							if (ticket.getUsername() != null && !"".equals(ticket.getUsername())) {
								query.setParameter("username", ticket.getUsername());
							}
							if (ticket.getTerminalId() != null && ticket.getTerminalId() > 0) {
								query.setParameter("terminalId", ticket.getTerminalId());
							}
							if (ticket.getStatus() != null && ticket.getStatus().getValue() > 0) {
								query.setParameter("status", ticket.getStatus());
							}
							if (ticket.getIsWinning() != null && ticket.getIsWinning().getValue() != YesNoStatus.ALL.getValue()) {
								query.setParameter("isWinning", ticket.getIsWinning());
							}
							if (!StringUtils.isEmpty(ticket.getSerialId())) {
								query.setParameter("serialId", ticket.getSerialId());
							}
							if (!StringUtils.isEmpty(ticket.getExternalId())) {
								query.setParameter("externalId", ticket.getExternalId());
							}
						}
						if (beginCreateTime != null) {
							query.setParameter("beginCreateTime", beginCreateTime);
						}
						if (endCreateTime != null) {
							query.setParameter("endCreateTime", endCreateTime);
						}
						if (beginPrintTime != null) {
							query.setParameter("beginPrintTime", beginPrintTime);
						}
						if (endPrintTime != null) {
							query.setParameter("endPrintTime", endPrintTime);
						}
						if (beginSendTime != null) {
							query.setParameter("beginSendTime", beginSendTime);
						}
						if (endSendTime != null) {
							query.setParameter("endSendTime", endSendTime);
						}
						Object object = query.uniqueResult();
						if (object != null) {
							Integer i = ((Long)object).intValue();
							return i;
						}

						return null;
					}
				});
	}

	@Override
	public int getCountsByPlanId(final String planId) {
		return (Integer) getHibernateTemplate().execute(
				new HibernateCallback<Integer>() {
					@Override
					public Integer doInHibernate(Session session)
							throws HibernateException, SQLException {
						StringBuffer sb = new StringBuffer(
								"select count(m) from Ticket m where 1=1");
						
						if (planId != null && !"".equals(planId)) {
							sb.append(" and m.planNo = :planNo");
						}
						logger.info("按条件查询票数据HQL:{}", sb.toString());
						Query query = session.createQuery(sb.toString());

						if (planId != null && !"".equals(planId)) {
							query.setParameter("planNo", planId);
						}
						return ((Long)query.iterate().next()).intValue();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Ticket> getResultByPlanId(final String planId, final PageBean pageBean) {
		return getHibernateTemplate().executeFind(
				new HibernateCallback<List<Ticket>>() {

					@Override
					public List<Ticket> doInHibernate(Session session)
							throws HibernateException, SQLException {
						StringBuffer sb = new StringBuffer(
								"from Ticket m where 1=1");
						
						if (planId != null && !"".equals(planId)) {
							sb.append(" and m.planNo = :planNo");
						}
						sb.append(" order by m.id");
						logger.info("按条件查询票数据HQL:{}", sb.toString());
						Query query = session.createQuery(sb.toString());

						if (planId != null && !"".equals(planId)) {
							query.setParameter("planNo", planId);
						}
							
						if (pageBean != null && pageBean.isPageFlag()) {
							if (pageBean.getPage() > 0) {
								query.setFirstResult((pageBean.getPage() - 1)
										* pageBean.getPageSize());
							}
							if (pageBean.getPageSize() > 0) {
								query.setMaxResults(pageBean.getPageSize());
							}
						}

						return query.list();
					}
				});
	}
	@Override
	public Ticket get(final Long id) {
		return (Ticket) getHibernateTemplate().execute(
				new HibernateCallback<Object>() {
					@Override
					public Ticket doInHibernate(Session session)
							throws HibernateException, SQLException {
						
						StringBuffer sb = new StringBuffer("from Ticket m where 1=1 and m.id=:id");
						Query query = session.createQuery(sb.toString());
						query.setParameter("id", id);
						
						Object object = query.uniqueResult();
						
						if (object != null) {
							return (Ticket) object;
						}

						return null;
					}
				});
	}
	
	@Override
	public TerminalAccountCheckItem getTerminalAccountCheckItem(final String terminalId,final Integer lotteryTypeId,final Integer accountCheckType,final String accountCheckDate){
		return (TerminalAccountCheckItem)getHibernateTemplate().execute(new HibernateCallback<Object>(){
			@SuppressWarnings("unchecked")
			@Override
			public TerminalAccountCheckItem doInHibernate(Session session) throws HibernateException,SQLException{
				StringBuffer sb = new StringBuffer("select sum(amount) as lehecaiDrawMoney,sum(winning_amount) as lehecaiPrizeMoney from ticket ");
				sb.append("where lottery_type = ").append(lotteryTypeId.intValue()).append(" ");
				sb.append("and status = ").append(TicketStatus.PRINT_SUCCESS.getValue()).append(" ");//只统计出票成功的
				if(terminalId != null && !"".equals(terminalId)){
					sb.append("and terminal_id in (").append(terminalId).append(") ");
				}
				if(accountCheckType != null && accountCheckType.intValue() != 1 ){//按期
					sb.append("and phase = ").append(accountCheckDate).append(" ");
				}else{//按天
					sb.append("and print_time like '").append(accountCheckDate).append("%'");
				}
				Query query = session.createSQLQuery(sb.toString());
				List list = query.list();
				TerminalAccountCheckItem item = null;
				if(list != null && list.size() > 0){
					item = new TerminalAccountCheckItem();
					Object[] objectArray = (Object[])list.get(0);
					Double lehecaiDrawMoney = (Double)objectArray[0];
					lehecaiDrawMoney = lehecaiDrawMoney == null ? 0 : lehecaiDrawMoney;
					item.setLehecaiDrawMoney(lehecaiDrawMoney);
					Double lehecaiPrizeMoney = (Double)objectArray[1];
					lehecaiPrizeMoney = lehecaiPrizeMoney == null ? 0 : lehecaiPrizeMoney;
					item.setLehecaiPrizeMoney(lehecaiPrizeMoney);
				}
				return item;
			}
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public void updateTerminateTimeByPlanId(final String planId,final Date terminateTime, final List<TicketStatus> ticketStatusList) {
		getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException {
					StringBuffer hql = new StringBuffer("update Ticket m set terminateTime = :terminateTime ");
					hql.append(" where m.planNo = :planId and status in (:statusList)");
					Query query = session.createQuery(hql.toString());
					query.setParameterList("statusList", ticketStatusList);
					query.setParameter("terminateTime", terminateTime);
					query.setParameter("planId", planId);
					query.executeUpdate();
					return null;
				}
		});
	}

}
