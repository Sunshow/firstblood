package web.service.member;

import java.util.Date;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface CreditLogService {

	public Map<String, Object> getResult(long uid, String username,
                                         Date beginDate, Date endDate, String type, String orderStr,
                                         String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;
}
