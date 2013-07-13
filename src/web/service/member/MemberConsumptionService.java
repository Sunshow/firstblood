package web.service.member;

import java.util.Date;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;

public interface MemberConsumptionService {
	public Map<String, Object> getResult(LotteryType lotteryType, Long userid,
                                         String username, Date beginDate, Date endDate, String orderStr,
                                         String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;
}