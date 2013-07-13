package web.dao.impl.ticket;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.ticket.TerminalConfigDao;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PlayType;
import com.lehecai.core.service.memcached.MemcachedService;
import com.lehecai.engine.entity.terminal.TerminalConfig;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class TerminalConfigDaoImpl extends HibernateDaoSupport implements TerminalConfigDao {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private static String MC_KEY_PREFIX_TERMINAL_CONFIG = "admin_terminal_config_";
    private static String MC_KEY_PREFIX_TERMINAL_CONFIG_IDS_LOTTERYTYPE = "admin_terminal_config_ids_lotterytype_";
    private static String MC_KEY_PREFIX_TERMINAL_CONFIG_IDS_PLAYTYPE = "admin_terminal_config_ids_playtype_";

    private static String MC_KEY_TERMINAL_CONFIG_IDS_PLAYTYPE_ALL = "admin_terminal_config_ids_playtype_all";

	private MemcachedService memcachedService;

	protected String generateTerminalConfigKey(Long id) {
		if (id != null) {
			return MC_KEY_PREFIX_TERMINAL_CONFIG + id.toString();
		}
		return null;
	}

    protected String generateTerminalConfigIdsKey(LotteryType lotteryType) {
        if (lotteryType != null && lotteryType.getValue() != LotteryType.ALL.getValue()) {
            return MC_KEY_PREFIX_TERMINAL_CONFIG_IDS_LOTTERYTYPE + lotteryType.getValue();
        }
        return null;
    }

    protected String generateTerminalConfigIdsKey(PlayType playType) {
        if (playType != null && playType.getValue() != PlayType.ALL.getValue() && playType.getValue() != PlayType.DEFAULT.getValue()) {
            return MC_KEY_PREFIX_TERMINAL_CONFIG_IDS_PLAYTYPE + playType.getValue();
        }
        return null;
    }

	protected void deleteMC(Long id) {
		if (id == null) {
			return;
		}
		try {
			memcachedService.delete(generateTerminalConfigKey(id));
		} catch (Exception e) {
			logger.error("删除终端配置(id={}, key={})的缓存失败", id.toString(), generateTerminalConfigKey(id));
			logger.error(e.getMessage(), e);
		}
	}

	protected void deleteMC(LotteryType lotteryType) {
		if (lotteryType == null) {
			return;
		}
        String key = generateTerminalConfigIdsKey(lotteryType);
		try {
			memcachedService.delete(key);
		} catch (Exception e) {
			logger.error("删除终端配置(彩种={},key={})的缓存失败", lotteryType, key);
			logger.error(e.getMessage(), e);
		}
	}

    protected void deleteMC(PlayType playType) {
        if (playType == null) {
            return;
        }
        String key = generateTerminalConfigIdsKey(playType);
        if (key != null) {
            try {
                memcachedService.delete(key);
            } catch (Exception e) {
                logger.error("删除终端配置(玩法={},key={})的缓存失败", playType, key);
                logger.error(e.getMessage(), e);
            }
        }
        try {
        	 memcachedService.delete(MC_KEY_TERMINAL_CONFIG_IDS_PLAYTYPE_ALL);
        } catch (Exception e) {
            logger.error("删除终端配置(玩法=all)的缓存失败");
            logger.error(e.getMessage(), e);
        }
    }

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.TerminalConfigDao#merge(com.lehecai.admin.web.domain.business.TerminalConfig)
	 */
	@Override
	public void merge(TerminalConfig terminalConfig) {
		getHibernateTemplate().merge(terminalConfig);
		this.deleteMC(terminalConfig.getId());
		this.deleteMC(terminalConfig.getLotteryType());
        if (terminalConfig.getPlayType() != null) {
            this.deleteMC(terminalConfig.getPlayType());
        }
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.TerminalConfigDao#list(com.lehecai.admin.web.domain.business.TerminalConfig, com.lehecai.admin.web.bean.PageBean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TerminalConfig> list(final TerminalConfig terminalConfig, final PageBean pageBean) {
		return (List<TerminalConfig>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from TerminalConfig u where 1 = 1");

						if(terminalConfig.getLotteryType() != null){
							hql.append(" and u.lotteryType = :lotteryType");
						}
						
						hql.append(" and u.playType = :playType");
						hql.append(" order by u.weight desc,u.id");
						Query query = session.createQuery(hql.toString());
						if(terminalConfig.getLotteryType() != null){
							query.setParameter("lotteryType", terminalConfig.getLotteryType());
						}
						query.setParameter("playType", PlayType.DEFAULT);
						
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
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TerminalConfig> listByTerminalType(final TerminalConfig terminalConfig, final PageBean pageBean) {
		return (List<TerminalConfig>) getHibernateTemplate().execute(
				new HibernateCallback<List<TerminalConfig>>() {
					public List<TerminalConfig> doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from TerminalConfig u where 1 = 1");
						
						if (terminalConfig.getTerminalId() != null) {
							hql.append(" and u.terminalId = :terminalId");
						}
						hql.append(" order by u.weight desc,u.id");
						Query query = session.createQuery(hql.toString());
						if(terminalConfig.getTerminalId() != null){
							query.setParameter("terminalId", terminalConfig.getTerminalId());
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
		
	@SuppressWarnings("unchecked")
	@Override
	public List<TerminalConfig> listByPlayType(final TerminalConfig terminalConfig, final PageBean pageBean) {
		return (List<TerminalConfig>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
					throws HibernateException {
						StringBuffer hql = new StringBuffer("from TerminalConfig u where 1 = 1");
						
						if(terminalConfig.getLotteryType() != null){
							hql.append(" and u.lotteryType = :lotteryType");
						}
						
						if (terminalConfig.getPlayType() != null && terminalConfig.getPlayType().getValue() != PlayType.DEFAULT.getValue()) {
							hql.append(" and u.playType = :playType");
						} else {
							hql.append(" and u.playType <> :playType");
						}
						hql.append(" order by u.weight desc,u.id");
						Query query = session.createQuery(hql.toString());
						if(terminalConfig.getLotteryType() != null){
							query.setParameter("lotteryType", terminalConfig.getLotteryType());
						}
						
						if (terminalConfig.getPlayType() != null && terminalConfig.getPlayType().getValue() != PlayType.DEFAULT.getValue()) {
							query.setParameter("playType", terminalConfig.getPlayType());
						} else {
							query.setParameter("playType", PlayType.DEFAULT);
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
	 * @see com.lehecai.admin.web.dao.impl.business.TerminalConfigDao#get(java.lang.Long)
	 */
	@Override
	public TerminalConfig get(Long ID) {
		return getHibernateTemplate().get(TerminalConfig.class, ID);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.TerminalConfigDao#del(com.lehecai.admin.web.domain.business.TerminalConfig)
	 */
	@Override
	public void del(TerminalConfig terminalConfig) {
		getHibernateTemplate().delete(terminalConfig);
		this.deleteMC(terminalConfig.getId());
        this.deleteMC(terminalConfig.getLotteryType());
        if (terminalConfig.getPlayType() != null) {
            this.deleteMC(terminalConfig.getPlayType());
        }
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.impl.business.TerminalConfigDao#getPageBean(com.lehecai.admin.web.domain.business.TerminalConfig, com.lehecai.admin.web.bean.PageBean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final TerminalConfig terminalConfig, final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from TerminalConfig u where 1 = 1");
						if(terminalConfig.getLotteryType() != null){
							hql.append(" and u.lotteryType = :lotteryType");
						}
						if (terminalConfig.getTerminalId() != null) {
							hql.append(" and u.terminalId = :terminalId");
						}
						
						Query query = session.createQuery(hql.toString());
						if(terminalConfig.getLotteryType() != null){
							query.setParameter("lotteryType", terminalConfig.getLotteryType());
						}
						if(terminalConfig.getTerminalId() != null){
							query.setParameter("terminalId", terminalConfig.getTerminalId());
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

	@Override
	public void delByTerminalId(Long id) {
		TerminalConfig config = this.get(id);
		this.del(config);
	}

	public MemcachedService getMemcachedService() {
		return memcachedService;
	}

	public void setMemcachedService(MemcachedService memcachedService) {
		this.memcachedService = memcachedService;
	}
}
