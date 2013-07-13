package web.service.impl.ticket;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.dao.ticket.TerminalConfigDao;
import com.lehecai.admin.web.dao.ticket.TerminalDao;
import com.lehecai.admin.web.service.ticket.TerminalService;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.engine.entity.terminal.Terminal;
import com.lehecai.engine.entity.terminal.TerminalConfig;

public class TerminalServiceImpl implements TerminalService {
	
	private final Logger logger = LoggerFactory.getLogger(TerminalServiceImpl.class);
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.TerminalService#add()
	 */
	private TerminalDao terminalDao;
	private TerminalConfigDao terminalConfigDao;
	
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.TerminalService#manage(com.lehecai.admin.web.domain.business.Terminal)
	 */
	public void manage(Terminal terminal){
		terminalDao.merge(terminal);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.TerminalService#getTerminalDao()
	 */
	public TerminalDao getTerminalDao() {
		return terminalDao;
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.TerminalService#setTerminalDao(com.lehecai.admin.web.dao.business.TerminalDao)
	 */
	public void setTerminalDao(TerminalDao terminalDao) {
		this.terminalDao = terminalDao;
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.TerminalService#list(com.lehecai.admin.web.domain.business.Terminal, com.lehecai.admin.web.bean.PageBean)
	 */
	@Override
	public List<Terminal> list(Terminal terminal, PageBean pageBean) {
		// TODO Auto-generated method stub
		return terminalDao.list(terminal, pageBean);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.TerminalService#get(java.lang.Long)
	 */
	@Override
	public Terminal get(Long ID) {
		return terminalDao.get(ID);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.TerminalService#del(com.lehecai.admin.web.domain.business.Terminal)
	 */
	@Override
	public void del(Terminal terminal) {
		terminalDao.del(terminal);
		terminalConfigDao.delByTerminalId(terminal.getId());
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.business.TerminalService#getPageBean(com.lehecai.admin.web.domain.business.Terminal, com.lehecai.admin.web.bean.PageBean)
	 */
	@Override
	public PageBean getPageBean(Terminal terminal, PageBean pageBean) {
		return terminalDao.getPageBean(terminal, pageBean);
	}

	public ResultBean deleteLotteryType(List<String> deleteLotteryType) throws ApiRemoteCallFailedException{
		logger.info("进入调用删除彩票的实现");
		ResultBean resultBean = new ResultBean();
		for (Iterator<String> i = deleteLotteryType.iterator(); i.hasNext();){  
			   String  terminalConfigId = i.next();
			   Long id = Long.parseLong(terminalConfigId);
			   if (id != null) {
				   TerminalConfig terminalConfig = new TerminalConfig();
				   terminalConfig = terminalConfigDao.get(id);
				   terminalConfigDao.del(terminalConfig);
					resultBean.setCode(0);
					String msg = "删除彩期,成功";
					resultBean.setMessage(msg);
					resultBean.setResult(true);
			   } 
		} 

		return resultBean;
	}
	
	public TerminalConfigDao getTerminalConfigDao() {
		return terminalConfigDao;
	}

	public void setTerminalConfigDao(TerminalConfigDao terminalConfigDao) {
		this.terminalConfigDao = terminalConfigDao;
	}
}
