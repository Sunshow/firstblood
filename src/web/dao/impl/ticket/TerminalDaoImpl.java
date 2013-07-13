package web.dao.impl.ticket;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.ticket.TerminalDao;
import com.lehecai.core.EnabledStatus;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.service.memcached.MemcachedService;
import com.lehecai.engine.entity.terminal.Terminal;
import com.lehecai.engine.entity.terminal.TerminalType;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class TerminalDaoImpl extends HibernateDaoSupport implements TerminalDao {
	
	protected transient final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private MemcachedService memcachedService;

	private static String MC_KEY_PREFIX_TERMINAL = "admin_terminal_";

	/** 生成终端缓存key */
	protected String generateTerminalKey(Long id) {
		return MC_KEY_PREFIX_TERMINAL + id.toString();
	}
	
	protected void deleteMC(Long id) {
		if (id == null) {
			return;
		}
		try {
			memcachedService.delete(generateTerminalKey(id));
		} catch (Exception e) {
			logger.error("删除终端(id={})的缓存失败", id);
			logger.error(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.TerminalDao#merge(com.lehecai.admin.web.domain.business.Terminal)
	 */
	@Override
	public void merge(Terminal terminal) {
		getHibernateTemplate().merge(terminal);
		
		this.deleteMC(terminal.getId());
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.TerminalDao#list(com.lehecai.admin.web.domain.business.Terminal, com.lehecai.admin.web.bean.PageBean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Terminal> list(final Terminal terminal, final PageBean pageBean) {
		return (List<Terminal>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from Terminal u where 1 = 1");
						if(terminal != null) {
							if (terminal.getId() != null) {
								hql.append(" and u.id = :id");
							}
							if(terminal.getIsEnabled() != null && terminal.getIsEnabled().getValue() != EnabledStatus.ALL.getValue()){
								hql.append(" and u.isEnabled = :isEnabled");
							}
							if (terminal.getTerminalType() != null && terminal.getTerminalType().getValue() != TerminalType.ALL.getValue()) {
								hql.append(" and u.terminalType = :terminalType");
							}
							if (terminal.getIsPaused() != null && terminal.getIsPaused().getValue() != YesNoStatus.ALL.getValue()) {
								hql.append(" and u.isPaused = :isPaused");
							}
						}
						Query query = session.createQuery(hql.toString());
						if(terminal != null) {
							if (terminal.getId() != null) {
								query.setParameter("id", terminal.getId());
							}
							if(terminal.getIsEnabled() != null && terminal.getIsEnabled().getValue() != EnabledStatus.ALL.getValue()){
								query.setParameter("isEnabled", terminal.getIsEnabled());
							}
							if (terminal.getTerminalType() != null && terminal.getTerminalType().getValue() != TerminalType.ALL.getValue()) {
								query.setParameter("terminalType", terminal.getTerminalType());
							}
							if (terminal.getIsPaused() != null && terminal.getIsPaused().getValue() != YesNoStatus.ALL.getValue()) {
								query.setParameter("isPaused", terminal.getIsPaused());
							}
						}
						
						if(pageBean.isPageFlag()){
							if(pageBean.getPageSize() != 0){
								query.setFirstResult((pageBean.getPage() - 1) * pageBean.getPageSize());
								query.setMaxResults(pageBean.getPageSize());
							}
						}
						return query.list();
					}
				});
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.TerminalDao#get(java.lang.Long)
	 */
	@Override
	public Terminal get(Long ID) {
		return getHibernateTemplate().get(Terminal.class, ID);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.TerminalDao#del(com.lehecai.admin.web.domain.business.Terminal)
	 */
	@Override
	public void del(Terminal terminal) {
		getHibernateTemplate().delete(terminal);
		this.deleteMC(terminal.getId());
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.TerminalDao#getPageBean(com.lehecai.admin.web.domain.business.Terminal, com.lehecai.admin.web.bean.PageBean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final Terminal terminal, final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from Terminal u where 1 = 1");
						
						if(terminal != null) {
							if (terminal.getId() != null) {
								hql.append(" and u.id = :id");
							}
							if(terminal.getIsEnabled() != null && terminal.getIsEnabled().getValue() != EnabledStatus.ALL.getValue()){
								hql.append(" and u.isEnabled = :isEnabled");
							}
							if (terminal.getTerminalType() != null && terminal.getTerminalType().getValue() != TerminalType.ALL.getValue()) {
								hql.append(" and u.terminalType = :terminalType");
							}
							if (terminal.getIsPaused() != null && terminal.getIsPaused().getValue() != YesNoStatus.ALL.getValue()) {
								hql.append(" and u.isPaused = :isPaused");
							}
						}
						Query query = session.createQuery(hql.toString());
						if(terminal != null) {
							if (terminal.getId() != null) {
								query.setParameter("id", terminal.getId());
							}
							if(terminal.getIsEnabled() != null && terminal.getIsEnabled().getValue() != EnabledStatus.ALL.getValue()){
								query.setParameter("isEnabled", terminal.getIsEnabled());
							}
							if (terminal.getTerminalType() != null && terminal.getTerminalType().getValue() != TerminalType.ALL.getValue()) {
								query.setParameter("terminalType", terminal.getTerminalType());
							}
							if (terminal.getIsPaused() != null && terminal.getIsPaused().getValue() != YesNoStatus.ALL.getValue()) {
								query.setParameter("isPaused", terminal.getIsPaused());
							}
						}
						
						if(pageBean.isPageFlag()){
							int totalCount = ((Long)query.iterate().next()).intValue();
							pageBean.setCount(totalCount);
							int pageCount = 0;//页数
							if(pageBean.getPageSize() != 0) {
					            pageCount = totalCount / pageBean.getPageSize();
					            if(totalCount % pageBean.getPageSize() != 0) {
					                pageCount ++;
					            }
					        }
							pageBean.setPageCount(pageCount);
						}
						return pageBean;
					}
				});
	}

	public MemcachedService getMemcachedService() {
		return memcachedService;
	}

	public void setMemcachedService(MemcachedService memcachedService) {
		this.memcachedService = memcachedService;
	}
}
