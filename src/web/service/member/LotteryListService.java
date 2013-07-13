package web.service.member;

import java.util.Date;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface LotteryListService {
	
	//模糊查询
	public Map<String, Object> fuzzyQueryResult(String lotteryType, Date ticketPrintTimeStart, Date ticketPrintTimeEnd, PageBean pageBean)
			throws ApiRemoteCallFailedException;
}
