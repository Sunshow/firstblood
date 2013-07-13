package web.dao.impl.lottery;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.lottery.LotteryTicketConfigDAO;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.service.memcached.MemcachedService;
import com.lehecai.core.util.lottery.LotteryUtil;
import com.lehecai.engine.entity.lottery.LotteryTicketConfig;

public class LotteryTicketConfigDAOImpl extends HibernateDaoSupport implements
		LotteryTicketConfigDAO {
	
	protected transient final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private MemcachedService memcachedService;
	
	protected void deleteMC(LotteryType lotteryType) {
		try {
			memcachedService.delete(LotteryUtil.generateLotteryTicketConfigKey(lotteryType));
		} catch (Exception e) {
			logger.error("删除彩种{}的缓存失败", lotteryType.getName());
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 按彩种类型查找对应彩种类型的出票配置
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LotteryTicketConfig get(LotteryType lotteryType) {
		List<LotteryTicketConfig> list = this.getHibernateTemplate().find("from LotteryTicketConfig tc where tc.lotteryType=?",new Object[]{lotteryType});
		if (list != null && list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	@Override
	public void update(LotteryTicketConfig lotteryTicketConfig) {
		this.getHibernateTemplate().saveOrUpdate(lotteryTicketConfig);
		this.deleteMC(lotteryTicketConfig.getLotteryType());
	}

	@Override
	public void delete(LotteryTicketConfig lotteryTicketConfig) {
		this.getHibernateTemplate().delete(lotteryTicketConfig);
		this.deleteMC(lotteryTicketConfig.getLotteryType());
	}

	@Override
	public void delete(LotteryType lotteryType) {
		LotteryTicketConfig config = this.get(lotteryType);
		if(config != null && config.getId() != null ){
			this.delete(config);
		}
	}

	public void setMemcachedService(MemcachedService memcachedService) {
		this.memcachedService = memcachedService;
	}
	
}
