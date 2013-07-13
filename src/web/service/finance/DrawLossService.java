package web.service.finance;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.finance.DrawLoss;
import com.lehecai.core.lottery.LotteryType;

public interface DrawLossService {
	public DrawLoss get(Long ID);
	public boolean getByLotteryTypeAmountCheckDate(LotteryType lotteryType, String amountCheckDate);
	public void manage(DrawLoss item);
	public void del(DrawLoss item);
	public List<DrawLoss> list(DrawLoss drawLoss, Date beginDate, Date endDate, PageBean pageBean);
	public PageBean getPageBean(DrawLoss drawLoss, Date beginDate, Date endDate, PageBean pageBean);
	public DrawLoss getTotal(DrawLoss drawLoss, Date beginDate, Date endDate, PageBean pageBean);
}
