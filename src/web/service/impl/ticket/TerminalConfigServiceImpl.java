package web.service.impl.ticket;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.ticket.TerminalConfigDao;
import com.lehecai.admin.web.dao.ticket.TerminalDao;
import com.lehecai.admin.web.service.ticket.TerminalConfigService;
import com.lehecai.core.EnabledStatus;
import com.lehecai.engine.entity.terminal.Terminal;
import com.lehecai.engine.entity.terminal.TerminalConfig;

public class TerminalConfigServiceImpl implements TerminalConfigService {
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.TerminalConfigService#add()
	 */
	private TerminalConfigDao terminalConfigDao;
	private TerminalDao terminalDao;
	
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.TerminalConfigService#manage(com.lehecai.admin.web.domain.business.TerminalConfig)
	 */
	public void manage(TerminalConfig terminalConfig){
		terminalConfigDao.merge(terminalConfig);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.TerminalConfigService#getTerminalConfigDao()
	 */
	public TerminalConfigDao getTerminalConfigDao() {
		return terminalConfigDao;
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.TerminalConfigService#setTerminalConfigDao(com.lehecai.admin.web.dao.business.TerminalConfigDao)
	 */
	public void setTerminalConfigDao(TerminalConfigDao terminalConfigDao) {
		this.terminalConfigDao = terminalConfigDao;
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.TerminalConfigService#list(com.lehecai.admin.web.domain.business.TerminalConfig, com.lehecai.admin.web.bean.PageBean)
	 */
	@Override
	public List<TerminalConfig> list(TerminalConfig terminalConfig, PageBean pageBean) {
		// TODO Auto-generated method stub
		return terminalConfigDao.list(terminalConfig, pageBean);
	}
	
	@Override
	public List<TerminalConfig> listByPlayType(TerminalConfig terminalConfig, PageBean pageBean) {
		// TODO Auto-generated method stub
		return terminalConfigDao.listByPlayType(terminalConfig, pageBean);
	}
    
	public List<TerminalConfig> listByTerminalType(TerminalConfig terminalConfig, PageBean pageBean){
		return terminalConfigDao.listByTerminalType(terminalConfig, pageBean);
	}
	
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.TerminalConfigService#get(java.lang.Long)
	 */
	@Override
	public TerminalConfig get(Long ID) {
		// TODO Auto-generated method stub
		return terminalConfigDao.get(ID);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.TerminalConfigService#del(com.lehecai.admin.web.domain.business.TerminalConfig)
	 */
	@Override
	public void del(TerminalConfig terminalConfig) {
		// TODO Auto-generated method stub
		terminalConfigDao.del(terminalConfig);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.TerminalConfigService#getPageBean(com.lehecai.admin.web.domain.business.TerminalConfig, com.lehecai.admin.web.bean.PageBean)
	 */
	@Override
	public PageBean getPageBean(TerminalConfig terminalConfig, PageBean pageBean) {
		// TODO Auto-generated method stub
		return terminalConfigDao.getPageBean(terminalConfig, pageBean);
	}

	@Override
	public List<Terminal> listTerminal(TerminalConfig terminalConfig,
			PageBean pageBean) {
		// TODO Auto-generated method stub
		pageBean.setPageFlag(false);
		Terminal terminal = new Terminal();
		terminal.setIsEnabled(EnabledStatus.ENABLED);
		List<Terminal> terminals = terminalDao.list(terminal, pageBean);
		List<TerminalConfig> terminalConfigs = terminalConfigDao.list(terminalConfig, pageBean);
		
		for(TerminalConfig tc : terminalConfigs){
			for(Terminal t :terminals){
				if(tc.getTerminalId().longValue() == t.getId().longValue()){
					terminals.remove(t);
					break;
				}
			}
		}
		return terminals;
	}
	@Override
	public List<Terminal> listTerminalByPlayType(TerminalConfig terminalConfig,
			PageBean pageBean) {
		// TODO Auto-generated method stub
		pageBean.setPageFlag(false);
		Terminal terminal = new Terminal();
		terminal.setIsEnabled(EnabledStatus.ENABLED);
		List<Terminal> terminals = terminalDao.list(terminal, pageBean);
//		List<TerminalConfig> terminalConfigs = terminalConfigDao.listByPlayType(terminalConfig, pageBean);
//		
//		for(TerminalConfig tc : terminalConfigs){
//			for(Terminal t :terminals){
//				if(tc.getTerminalId().longValue() == t.getId().longValue()){
//					terminals.remove(t);
//					break;
//				}
//			}
//		}
		return terminals;
	}

	public TerminalDao getTerminalDao() {
		return terminalDao;
	}

	public void setTerminalDao(TerminalDao terminalDao) {
		this.terminalDao = terminalDao;
	}
}
