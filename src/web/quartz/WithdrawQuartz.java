package web.quartz;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.WithdrawService;
import com.lehecai.core.api.user.WithdrawLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.WithdrawStatus;
import com.lehecai.core.util.CoreDateUtils;

public class WithdrawQuartz {
	private final Logger logger = LoggerFactory.getLogger(WithdrawQuartz.class);
	
	public final static String API_MAP_KEY_LOG = "log";
	public final static String API_MAP_KEY_TIME = "time";
	
	private WithdrawService withdrawService;
	
	private int pageSize = 1;//每页最大查询条数
	
	@SuppressWarnings("unchecked")
	public void run() {
		logger.info("开始定时处理推迟1天或15天的提款记录");
		List<WithdrawLog> allWithdrawLogList = new ArrayList<WithdrawLog>();
		
		List<String> withdrawStatus = new ArrayList<String>();
		withdrawStatus.add(String.valueOf(WithdrawStatus.DELAYEDFOR1DAY.getValue()));
		withdrawStatus.add(String.valueOf(WithdrawStatus.DELAYEDFOR15DAYS.getValue()));
		
		PageBean pageBean = new PageBean();
		pageBean.setPageSize(pageSize);
		
		int page = 1;
		while (pageSize > 0) {
			// 生成查询条件
			pageBean.setPage(page);
			// 根据条件查询
			Map<String, Object> map = null;
			try {
				logger.info("查询所有提款状态为推迟1天或15天的提款记录");
				map = withdrawService.getResult(null,null, null, withdrawStatus, null, null, null, null, null, null,
						null, null, null, null, null, null, pageBean);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
			}
			List<WithdrawLog> delayedWithdraws = null;
			if (map != null) {
				delayedWithdraws = (List<WithdrawLog>) map.get(Global.API_MAP_KEY_LIST);
			}
			
			if (delayedWithdraws != null && !delayedWithdraws.isEmpty()) {
				allWithdrawLogList.addAll(delayedWithdraws);
				if (delayedWithdraws.size() < pageSize) {
					break;
				}
			} else {
				break;
			}
			page ++;
		}

		logger.info("得到所有提款状态为推迟1天或15天的提款记录,size={}", allWithdrawLogList.size());
		if (allWithdrawLogList != null && allWithdrawLogList.size() > 0) {
			for (WithdrawLog delayedWithdrawLog : allWithdrawLogList) {
				logger.info("得到提款状态为推迟1天或15天的提款记录,withdraw_id={}", delayedWithdrawLog.getId());
				logger.info("得到提款状态为推迟1天或15天的提款记录,withdraw_user={}", delayedWithdrawLog.getUsername());
				logger.info("得到提款状态为推迟1天或15天的提款记录,withdraw_status={}", delayedWithdrawLog.getWithdrawStatus().getName());
				logger.info("得到提款状态为推迟1天或15天的提款记录,withdraw_ext={}", delayedWithdrawLog.getExtra());
				String extra = delayedWithdrawLog.getExtra();
				JSONObject logJson = null;
				try {
					logJson = JSONObject.fromObject(extra);
					if (logJson == null || logJson.isNullObject()) {
						continue;
					}
					logJson = logJson.getJSONObject(API_MAP_KEY_LOG);
					if (logJson == null || logJson.isNullObject()) {
						continue;
					}
					logJson = logJson.getJSONObject(String.valueOf(delayedWithdrawLog.getWithdrawStatus().getValue()));
					if (logJson == null || logJson.isNullObject()) {
						continue;
					}
				} catch (Exception e1) {
					logger.error("定时处理推迟提款extra字段json解析失败：logId={}, extra={}", delayedWithdrawLog.getId(), delayedWithdrawLog.getExtra());
					logger.error(e1.getMessage(), e1);
					continue;
				}
				String delayedTime = logJson.getString(API_MAP_KEY_TIME);
				logger.info("得到withdraw_id={}的delayedTime={}", delayedWithdrawLog.getId(), delayedTime);
				Calendar nowInstance = Calendar.getInstance();
				Date delayedDate = CoreDateUtils.parseDate(delayedTime, CoreDateUtils.DATETIME);
				if (delayedWithdrawLog.getWithdrawStatus().getValue() == WithdrawStatus.DELAYEDFOR1DAY.getValue()) {
					nowInstance.add(Calendar.DATE, -1);
				} else if (delayedWithdrawLog.getWithdrawStatus().getValue() == WithdrawStatus.DELAYEDFOR15DAYS.getValue()) {
					nowInstance.add(Calendar.DATE, -15);
				}
				logger.info("得到nowDate={}", CoreDateUtils.formatDateTime(nowInstance.getTime()));
				if (nowInstance.getTime().after(delayedDate)) {
					logger.info("到达推迟期限，自动更新为批准");
					boolean flag = false;
					try {
						flag = withdrawService.approve(delayedWithdrawLog.getId(), WithdrawStatus.APPROVE);
						logger.info("到达推迟期限，自动更新为批准,结果flag={}", flag);
						if(!flag){
							logger.error("变更提款状态为批准状态失败，请联系管理员!");
						}
					} catch (ApiRemoteCallFailedException e) {
						logger.error("变更提款状态为批准状态失败，请联系管理员!");
					}
				}
			}
		} else {
			logger.info("暂无推迟1天或15天的提款记录");
		}
	}

	public WithdrawService getWithdrawService() {
		return withdrawService;
	}

	public void setWithdrawService(WithdrawService withdrawService) {
		this.withdrawService = withdrawService;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
