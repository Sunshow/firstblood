package web.action.lottery;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lehecai.core.lottery.cache.DcLottery;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.config.GlobalConfig;
import com.lehecai.admin.web.service.lottery.DcRaceService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.core.api.lottery.DcRace;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.DcRaceStatus;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseStatus;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.lock.LotteryDrawMemCacheLock;
import com.lehecai.core.lottery.status.LotteryDrawPlanStatus;
import com.lehecai.core.lottery.task.impl.LotteryDrawQueueTask;
import com.lehecai.core.queue.simple.SimpleQueueService;
import com.lehecai.core.service.memcached.MemcachedService;
import com.lehecai.core.util.lottery.LotteryUtil;
/**
 * 单场开奖及开奖状态显示
 * @author leiming
 *
 */
public class DcDrawStatusAction extends BaseAction {
	private static final long serialVersionUID = -367266851465374167L;
	private static final Logger logger = LoggerFactory.getLogger(DcDrawStatusAction.class);
	
	@SuppressWarnings("unused")
	private HttpServletRequest request;
	private HttpServletResponse response;
	private PhaseService phaseService;
	private SimpleQueueService simpleQueueService;
	private MemcachedService memcachedService;
	
	private Integer lotteryTypeValue;
	private String phaseNo;
	
	private Phase phase;
	private String isForAbort = "0";//开奖页面参数  是否只开流产
	private String lotteryConfigData;
	
	private String drawQueuePrefix;//开奖队列前缀
	
	private DcRaceService dcRaceService;
	private List<DcRace> dcRaces;
	private String lastMatchNum;
	
	@SuppressWarnings("unused")
	private List<LotteryType> lotteryTypeList;
	
	public String handle() {
		logger.info("进入单场开奖！");
		return "draw";
	}
	
	public String drawReady() {
		logger.info("进入准备开奖");
		if (phaseNo != null) {
			//dcRaces = dcRaceService.getDcRaceListByPhase(phaseNo);
			List<String> dcStatusList = new ArrayList<String>();
			dcStatusList.add(String.valueOf(DcRaceStatus.OPEN_BUY.getValue()));
			dcStatusList.add(String.valueOf(DcRaceStatus.RETURN_PRIZE.getValue()));
			dcRaces = dcRaceService.findDcRaceByStatus(dcStatusList, phaseNo);
			if (dcRaces == null || dcRaces.size() == 0) {
				logger.error("准备开奖，第{}期的单场比赛数据不存在",phaseNo);
				super.setErrorMessage("第"+phaseNo+"期的单场比赛数据不存在,无法开奖");
			}
		} else {
			logger.error("彩期号不存在,无法获取单场数据");
			super.setErrorMessage("彩期号不存在,无法获取单场数据");
		}
		logger.info("准备开奖结束");
		return "draw";
	}
	
	//开奖
	public String draw() {
		logger.info("进入开奖");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			rs.put("state","failed");
			rs.put("msg", "彩种类型数值不存在,null");
			writeRs(response,rs);
			return null;
		}
		String drawLockKey = null;
		LotteryDrawMemCacheLock lotteryDrawMemCacheLock = null;
		//校验开奖彩期的数据
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType phaseType = PhaseType.getItem(lotteryType);
		Phase phase = null;
		try {
			phase = phaseService.getPhaseByPhaseTypeAndPhaseNo(phaseType, phaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询彩期信息，api调用异常，{}",e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			rs.put("state","failed");
			rs.put("msg", e.getMessage());
			writeRs(response,rs);
			return null;
		}
		String msg = "";
		if (lotteryType==null) {
			rs.put("state","failed");
			msg = "要开奖的彩期类型值:"+lotteryTypeValue+"在系统中不存在,开奖失败";
			rs.put("msg", msg);
			logger.info(msg);
			writeRs(response,rs);
			return null;
		}
		if (phaseNo==null) {
			rs.put("state","failed");
			msg = "要开奖的彩票["+phaseType.getName()+"]的彩期号为null,开奖失败";
			rs.put("msg", msg);
			logger.info(msg);
			writeRs(response,rs);
			return null;
		}
		if (phase==null) {
			rs.put("state","failed");
			msg = "要开奖的彩票["+phaseType.getName()+"]的开奖彩期:"+phaseNo+"在系统中不存在,开奖失败";
			rs.put("msg", msg);
			logger.info(msg);
			writeRs(response,rs);
			return null;
		}
		drawLockKey = LotteryUtil.generateLotteryDrawLockKey(lotteryType, phaseNo);
		try {
			lotteryDrawMemCacheLock = (LotteryDrawMemCacheLock)memcachedService.get(drawLockKey, LotteryUtil.DRAW_LOCK_TIMEOUT);
		} catch (Exception e) {
			logger.error("从缓存中读取开奖锁出错", e);
			rs.put("state","failed");
			rs.put("msg", "从缓存中读取开奖锁出错");
			writeRs(response,rs);
			return null;
		}
		String msgHeader = "["+lotteryType.getName()+"]第<"+phaseNo+">期的开奖结果:";
		if (lotteryDrawMemCacheLock!=null) {
			rs.put("state","failed");
			msg = msgHeader+"已经有正在开奖的开奖任务,本次操作失败,需等本期开奖任务执行结束才可重新执行";
			rs.put("msg", msg);
			logger.info(msg);
			writeRs(response,rs);
			return null;
		}
		if (phase.getPhaseStatus().getValue() == PhaseStatus.REWARD.getValue() &&isForAbort.equals("1")) {
			rs.put("state","failed");
			msg = msgHeader+"已经派奖，不能重新开奖!";
			rs.put("msg", msg);
			logger.info(msg);
			writeRs(response,rs);
			return null;
		}
		
		//发送开奖任务
		LotteryDrawQueueTask lotteryDrawQueueTask = new LotteryDrawQueueTask();
		lotteryDrawQueueTask.setLotteryType(lotteryType);
		if ("1".equals(isForAbort)) {
			lotteryDrawQueueTask.setForAbort(true);
		}
		lotteryDrawQueueTask.setPhaseNo(phaseNo);
		lotteryDrawQueueTask.setLastMatchNum(lastMatchNum);
		//队列名称
		boolean resultFlag = false;
		try {
			resultFlag = simpleQueueService.putString(drawQueuePrefix+String.valueOf(lotteryTypeValue), LotteryDrawQueueTask.toJSONString(lotteryDrawQueueTask));
		} catch (Exception e) {
			logger.error("放入北单开奖任务到队列失败, {}, {}, lastMatchNum={}", new Object[] {
					lotteryType.getName(), 	phaseNo, lastMatchNum
			});
			logger.error(e.getMessage(), e);
		}
		if (resultFlag) {
			//加锁
			lotteryDrawMemCacheLock = new LotteryDrawMemCacheLock();
			lotteryDrawMemCacheLock.setLotteryTypeValue(lotteryTypeValue);
			lotteryDrawMemCacheLock.setPhase(phaseNo);
			boolean addLockFlag = false;
			try {
				addLockFlag = memcachedService.set(drawLockKey, lotteryDrawMemCacheLock, GlobalConfig.MEMCACHE_LOTTERY_DRAW_LOCK_ALIVE_TIME, LotteryUtil.DRAW_LOCK_TIMEOUT);
			} catch (Exception e) {
				logger.error("开奖任务加同步锁失败", e);
			}
			if (addLockFlag) {
				rs.put("state","success");
				msg = msgHeader+"发送开奖任务到开奖队列成功";
				rs.put("msg", msg);
				logger.info(msg);
			} else {
				rs.put("state","failed");
				msg = msgHeader+"发送开奖任务到开奖队列成功,但是开奖任务加同步锁失败";
				rs.put("msg", msg);
				logger.error(msg);
			}
		} else {
			rs.put("state","failed");
			msg = msgHeader+"发送开奖任务到开奖队列失败";
			rs.put("msg", msg);
			logger.error(msg);
		}
		writeRs(response,rs);
		logger.info("开奖结束");
		return null;
	}
	
	//开奖状态
	public String drawStatus() {
		logger.info("进入查看开奖状态");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			rs.put("state","failed");
			rs.put("msg", "彩种类型数值不存在,null");
			writeRs(response,rs);
			return null;
		}
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		String memCacheLotteryDrawKey = LotteryUtil.generateLotteryDrawKey(lotteryType, phaseNo);
		LotteryDrawPlanStatus lotteryDrawPlanStatus;
		try {
			lotteryDrawPlanStatus = (LotteryDrawPlanStatus)memcachedService.get(memCacheLotteryDrawKey);
		} catch (Exception e) {
			logger.error("从缓存中读取开奖锁出错", e);
			rs.put("state","failed");
			rs.put("msg", "从缓存中读取开奖锁出错");
			writeRs(response,rs);
			return null;
		}
		String msg = "";
		if (lotteryDrawPlanStatus==null) {
			rs.put("state", "failed");
			msg = "无正在执行的开奖状态";
			rs.put("msg", msg);
			rs.put("drawStatus", LotteryDrawPlanStatus.toJSON(lotteryDrawPlanStatus));
			logger.info(msg);
		} else {
			rs.put("state", "success");
			rs.put("drawStatus", LotteryDrawPlanStatus.toJSON(lotteryDrawPlanStatus));
		}
		writeRs(response,rs);
		logger.info("查看开奖状态结束");
		return null;
	}
	
	public PhaseService getPhaseService() {
		return phaseService;
	}
	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}
	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}
	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}
	public String getPhaseNo() {
		return phaseNo;
	}
	public void setPhaseNo(String phaseNo) {
		this.phaseNo = phaseNo;
	}
	public Phase getPhase() {
		return phase;
	}
	public void setPhase(Phase phase) {
		this.phase = phase;
	}
	public String getLotteryConfigData() {
		return lotteryConfigData;
	}
	public void setLotteryConfigData(String lotteryConfigData) {
		this.lotteryConfigData = lotteryConfigData;
	}
	public List<LotteryType> getLotteryTypeList() {
		return DcLottery.getList();
	}
	public void setLotteryTypeList(List<LotteryType> lotteryTypeList) {
		this.lotteryTypeList = lotteryTypeList;
	}
	public String getIsForAbort() {
		return isForAbort;
	}
	public void setIsForAbort(String isForAbort) {
		this.isForAbort = isForAbort;
	}
	public SimpleQueueService getSimpleQueueService() {
		return simpleQueueService;
	}
	public void setSimpleQueueService(SimpleQueueService simpleQueueService) {
		this.simpleQueueService = simpleQueueService;
	}
	public MemcachedService getMemcachedService() {
		return memcachedService;
	}
	public void setMemcachedService(MemcachedService memcachedService) {
		this.memcachedService = memcachedService;
	}
	public String getDrawQueuePrefix() {
		return drawQueuePrefix;
	}
	public void setDrawQueuePrefix(String drawQueuePrefix) {
		this.drawQueuePrefix = drawQueuePrefix;
	}
	public DcRaceService getDcRaceService() {
		return dcRaceService;
	}
	public void setDcRaceService(DcRaceService dcRaceService) {
		this.dcRaceService = dcRaceService;
	}
	public List<DcRace> getDcRaces() {
		return dcRaces;
	}
	public void setDcRaces(List<DcRace> dcRaces) {
		this.dcRaces = dcRaces;
	}
	public String getLastMatchNum() {
		return lastMatchNum;
	}
	public void setLastMatchNum(String lastMatchNum) {
		this.lastMatchNum = lastMatchNum;
	}
}
