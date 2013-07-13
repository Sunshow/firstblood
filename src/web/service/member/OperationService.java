package web.service.member;

import java.util.Date;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.OperationStatus;
import com.lehecai.core.lottery.OperationType;

public interface OperationService {

	public Map<String, Object> getResult(OperationType operationType,
                                         OperationStatus operationStatus, String username, Long uid,
                                         Date beginDate, Date endDate, Long sourceId,
                                         boolean distinctMember, String orderStr, String orderView,
                                         PageBean pageBean) throws ApiRemoteCallFailedException;
}