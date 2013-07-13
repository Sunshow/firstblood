package web.dao.impl.ticket;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.ticket.ActiveTicketDao;
import com.lehecai.core.api.ApiRequestOrder;
import com.lehecai.core.api.bean.query.IQueryProp;
import com.lehecai.core.api.bean.query.QueryOperator;
import com.lehecai.core.exception.PropDeserializeException;
import com.lehecai.engine.entity.serializer.TicketPropConstant;
import com.lehecai.engine.entity.ticket.Ticket;
import com.lehecai.engine.entity.ticket.TicketQuery;
import com.lehecai.engine.entity.ticket.TicketStatus;

public class ActiveTicketDaoImpl extends HibernateDaoSupport implements ActiveTicketDao {
	@SuppressWarnings("unchecked")
	@Override
	public List<Ticket> query(final TicketQuery ticketQueryProp, final PageBean pageBean) {
		return getHibernateTemplate().executeFind(new HibernateCallback<List<Ticket>>() {
			@Override
			public List<Ticket> doInHibernate(Session session)
					throws HibernateException, SQLException {
				StringBuffer sb = new StringBuffer("from Ticket t where 1=1");
				for (IQueryProp queryPropItem : ticketQueryProp.getQueryPropItemList()) {
					if (queryPropItem.getOperator().getValue() == QueryOperator.BETWEEN.getValue()) {
						if (queryPropItem.getValues()[0] != null) {
							sb.append(" and t.").append(queryPropItem.getName()).append(">=:").append(queryPropItem.getName()).append("_begin");
						}
						if (queryPropItem.getValues()[1] != null) {
							sb.append(" and t.").append(queryPropItem.getName()).append("<=:").append(queryPropItem.getName()).append("_end");
						}
					} else if (queryPropItem.getOperator().getValue() == QueryOperator.IN.getValue()) {
						sb.append(" and t.").append(queryPropItem.getName()).append(" in (:").append(queryPropItem.getName()).append(")");
					} else {
						sb.append(" and t.").append(queryPropItem.getName()).append(queryPropItem.getOperator().getName()).append(":").append(queryPropItem.getName());
					}
				}
				
				if (ticketQueryProp.getOrderList() != null && ticketQueryProp.getOrderList().size() > 0) {
					List<String> orderList = new ArrayList<String>();
					for (ApiRequestOrder order : ticketQueryProp.getOrderList()) {
						String orderStr = order.getField() + " " + order.getOrder();
						orderList.add(orderStr);
					}
					sb.append(" order by ").append(StringUtils.join(orderList, ","));
				}

				Query query = session.createQuery(sb.toString());
				
				TicketPropConstant ticketPropConstant = TicketPropConstant.getInstance();
				for (IQueryProp queryPropItem : ticketQueryProp.getQueryPropItemList()) {
					if (queryPropItem.getOperator().getValue() == QueryOperator.BETWEEN.getValue()) {
						if (queryPropItem.getValues()[0] != null) {
							Object obj = null;
							try {
								obj = ticketPropConstant.convertPropByAlias(queryPropItem.getName(), queryPropItem.getValues()[0]);
							} catch (PropDeserializeException e) {
								logger.error(e.getMessage(), e);
								throw new HibernateException("别名:" + queryPropItem.getName() + "，value:" + queryPropItem.getValues()[0] + "调用ticketPropConstant.convertPropByAlias时转换失败");
							}
							query.setParameter(queryPropItem.getName() + "_begin", obj);
						}
						if (queryPropItem.getValues()[1] != null) {
							Object obj = null;
							try {
								obj = ticketPropConstant.convertPropByAlias(queryPropItem.getName(), queryPropItem.getValues()[1]);
							} catch (PropDeserializeException e) {
								logger.error(e.getMessage(), e);
								throw new HibernateException("别名:" + queryPropItem.getName() + "，value:" + queryPropItem.getValues()[1] + "调用ticketPropConstant.convertPropByAlias时转换失败");
							}
							query.setParameter(queryPropItem.getName() + "_end", obj);
						}
					} else if (queryPropItem.getOperator().getValue() == QueryOperator.IN.getValue()) {
						List<Object> obj = null;
						try {
							obj = ticketPropConstant.convertPropByAlias(queryPropItem.getName(), queryPropItem.getValues());
						} catch (PropDeserializeException e) {
							logger.error(e.getMessage(), e);
							throw new HibernateException("别名:" + queryPropItem.getName() + "，value:" + StringUtils.join(queryPropItem.getValues(), ",") + "调用ticketPropConstant.convertPropByAlias时转换失败");
						}
						query.setParameterList(queryPropItem.getName(), obj);
					} else {
						Object obj = null;
						try {
							obj = ticketPropConstant.convertPropByAlias(queryPropItem.getName(), queryPropItem.getValues()[0]);
						} catch (PropDeserializeException e) {
							logger.error(e.getMessage(), e);
							throw new HibernateException("别名:" + queryPropItem.getName() + "，value:" + queryPropItem.getValues()[0] + "调用ticketPropConstant.convertPropByAlias时转换失败");
						}
						query.setParameter(queryPropItem.getName(), obj);
					}
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
	public int getCounts(final TicketQuery ticketQueryProp, final PageBean pageBean) {
		 return (Integer) getHibernateTemplate().execute(new HibernateCallback<Object>() {
				@Override
				public Integer doInHibernate(Session session)
						throws HibernateException, SQLException {
					StringBuffer sb = new StringBuffer("select count(t.id)from Ticket t where 1=1");
					for (IQueryProp queryPropItem : ticketQueryProp.getQueryPropItemList()) {
						if (queryPropItem.getOperator().getValue() == QueryOperator.BETWEEN.getValue()) {
							if (queryPropItem.getValues()[0] != null) {
								sb.append(" and t.").append(queryPropItem.getName()).append(">=:").append(queryPropItem.getName()).append("_begin");
							}
							if (queryPropItem.getValues()[1] != null) {
								sb.append(" and t.").append(queryPropItem.getName()).append("<=:").append(queryPropItem.getName()).append("_end");
							}
						} else if (queryPropItem.getOperator().getValue() == QueryOperator.IN.getValue()) {
							sb.append(" and t.").append(queryPropItem.getName()).append(" in (:").append(queryPropItem.getName()).append(")");
						} else {
							sb.append(" and t.").append(queryPropItem.getName()).append(queryPropItem.getOperator().getName()).append(":").append(queryPropItem.getName());
						}
					}

					Query query = session.createQuery(sb.toString());
					
					TicketPropConstant ticketPropConstant = TicketPropConstant.getInstance();
					for (IQueryProp queryPropItem : ticketQueryProp.getQueryPropItemList()) {
						if (queryPropItem.getOperator().getValue() == QueryOperator.BETWEEN.getValue()) {
							if (queryPropItem.getValues()[0] != null) {
								Object obj = null;
								try {
									obj = ticketPropConstant.convertPropByAlias(queryPropItem.getName(), queryPropItem.getValues()[0]);
								} catch (PropDeserializeException e) {
									logger.error(e.getMessage(), e);
									throw new HibernateException("别名:" + queryPropItem.getName() + "，value:" + queryPropItem.getValues()[0] + "调用ticketPropConstant.convertPropByAlias时转换失败");
								}
								query.setParameter(queryPropItem.getName() + "_begin", obj);
							}
							if (queryPropItem.getValues()[1] != null) {
								Object obj = null;
								try {
									obj = ticketPropConstant.convertPropByAlias(queryPropItem.getName(), queryPropItem.getValues()[1]);
								} catch (PropDeserializeException e) {
									logger.error(e.getMessage(), e);
									throw new HibernateException("别名:" + queryPropItem.getName() + "，value:" + queryPropItem.getValues()[1] + "调用ticketPropConstant.convertPropByAlias时转换失败");
								}
								query.setParameter(queryPropItem.getName() + "_end", obj);
							}
						} else if (queryPropItem.getOperator().getValue() == QueryOperator.IN.getValue()) {
							List<Object> obj = null;
							try {
								obj = ticketPropConstant.convertPropByAlias(queryPropItem.getName(), queryPropItem.getValues());
							} catch (PropDeserializeException e) {
								logger.error(e.getMessage(), e);
								throw new HibernateException("别名:" + queryPropItem.getName() + "，value:" + StringUtils.join(queryPropItem.getValues(), ",") + "调用ticketPropConstant.convertPropByAlias时转换失败");
							}
							query.setParameterList(queryPropItem.getName(), obj);
						} else {
							Object obj = null;
							try {
								obj = ticketPropConstant.convertPropByAlias(queryPropItem.getName(), queryPropItem.getValues()[0]);
							} catch (PropDeserializeException e) {
								logger.error(e.getMessage(), e);
								throw new HibernateException("别名:" + queryPropItem.getName() + "，value:" + queryPropItem.getValues()[0] + "调用ticketPropConstant.convertPropByAlias时转换失败");
							}
							query.setParameter(queryPropItem.getName(), obj);
						}
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
	public void updateStatus(final Long ticketId, final TicketStatus ticketStatus) {
		this.getHibernateTemplate().execute(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				StringBuffer sb = new StringBuffer("update Ticket t set status=:status where id=:id");
				Query query = session.createQuery(sb.toString());
				query.setParameter("status", ticketStatus);
				query.setParameter("id", ticketId);
				query.executeUpdate();
				return null;
			}
			
		});
	}
	
	@Override
	public void updateStatus(final Ticket ticket) {
		this.getHibernateTemplate().execute(new HibernateCallback<Object>() {
			@Override
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				StringBuffer sb = new StringBuffer("update Ticket t set status=:status, printTime =:printTime where id=:id");
				Query query = session.createQuery(sb.toString());
				query.setParameter("status", ticket.getStatus());
				query.setParameter("printTime", ticket.getPrintTime());
				query.setParameter("id", ticket.getId());
				query.executeUpdate();
				return null;
			}
		});
	}

}