package web.service.lottery;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.core.api.lottery.Chase;
import com.lehecai.core.api.lottery.ChaseDetail;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ChaseStatus;
import com.lehecai.core.lottery.ChaseType;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.StopChaseType;

/**
 * 追号业务逻辑层接口
 * @author yanweijie
 *
 */
public interface ChaseService {
	
	public Map<String, Object> getResult(String chaseId, String planId, String username,
                                         LotteryType lotteryType, ChaseStatus chaseStatus, StopChaseType stopChaseType, ChaseType chaseType, Date beginCreateTime, Date endCreateTime,
                                         String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;
	public boolean cancel(String chaseId) throws ApiRemoteCallFailedException;
	public ResultBean batchCancel(String refundStr) throws ApiRemoteCallFailedException;
	public List<ChaseDetail> listDetail(String chaseId) throws ApiRemoteCallFailedException;
	public Chase get(String chaseId) throws ApiRemoteCallFailedException;
	public String parseContent(LotteryType lotteryType, String content) throws ApiRemoteCallFailedException;	
}