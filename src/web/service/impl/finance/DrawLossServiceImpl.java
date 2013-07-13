package web.service.impl.finance;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.finance.DrawLossDao;
import com.lehecai.admin.web.domain.finance.DrawLoss;
import com.lehecai.admin.web.service.finance.DrawLossService;
import com.lehecai.core.lottery.LotteryType;

public class DrawLossServiceImpl implements DrawLossService {
	
	private DrawLossDao drawLossDao;
	
	@Override
	public void del(DrawLoss drawLoss) {
		drawLossDao.del(drawLoss);
	}

	@Override
	public DrawLoss get(Long ID) {
		return drawLossDao.get(ID);
	}

	@Override
	public void manage(DrawLoss drawLoss) {
		drawLossDao.merge(drawLoss);
	}

	@Override
	public List<DrawLoss> list(DrawLoss drawLoss,Date beginDate,Date endDate,PageBean pageBean){
		return drawLossDao.list(drawLoss, beginDate, endDate, pageBean);
	}
	
	@Override
	public PageBean getPageBean(DrawLoss drawLoss,Date beginDate,Date endDate, PageBean pageBean){
		return drawLossDao.getPageBean(drawLoss, beginDate, endDate, pageBean);
	}
	
	@Override
	public DrawLoss getTotal(DrawLoss drawLoss,Date beginDate,Date endDate, PageBean pageBean){
		return drawLossDao.getTotal(drawLoss, beginDate, endDate, pageBean);
	}

	public DrawLossDao getDrawLossDao() {
		return drawLossDao;
	}

	public void setDrawLossDao(
			DrawLossDao drawLossDao) {
		this.drawLossDao = drawLossDao;
	}

	@Override
	public boolean getByLotteryTypeAmountCheckDate(LotteryType lotteryType,
			String amountCheckDate) {
		return this.drawLossDao.getByLotteryTypeAmountCheckDate(lotteryType, amountCheckDate);
	}
}
