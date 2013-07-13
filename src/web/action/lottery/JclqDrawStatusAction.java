package web.action.lottery;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.config.GlobalConfig;
import com.lehecai.admin.web.service.lottery.JclqRaceService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.core.api.lottery.JclqRace;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.BetType;
import com.lehecai.core.lottery.JclqDynamicDrawStatus;
import com.lehecai.core.lottery.JclqRaceStatus;
import com.lehecai.core.lottery.JclqStaticDrawStatus;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.cache.JclqLottery;
import com.lehecai.core.lottery.lock.LotteryDrawMemCacheLock;
import com.lehecai.core.lottery.status.LotteryDrawPlanStatus;
import com.lehecai.core.lottery.task.impl.LotteryDrawQueueTask;
import com.lehecai.core.queue.simple.SimpleQueueService;
import com.lehecai.core.service.memcached.MemcachedService;
import com.lehecai.core.util.lottery.LotteryUtil;
/**
 * 竞彩篮球开奖及开奖状态显示
 * @author leiming
 *
 */
public class JclqDrawStatusAction extends BaseAction {
	private static final long serialVersionUID = -367266851465374167L;
	private static final Logger logger = LoggerFactory.getLogger(JclqDrawStatusAction.class);
	
	@SuppressWarnings("unused")
	private HttpServletRequest request;
	private HttpServletResponse response;
	private PhaseService phaseService;
	private SimpleQueueService simpleQueueService;
	private MemcachedService memcachedService;
	
	


	private int maxJclqRaceNum = 1000;//一次最多获取的竞彩篮球场次数 ioc
	private String isForAbort = "0";//开奖页面参数  是否只开流产
	private String lotteryConfigData;
	
	private String drawQueuePrefix;//开奖队列前缀
	
	private JclqRaceService jclqRaceService;
	private List<JclqRace> jclqRaces;
	private String lastMatchNum;
	private String phaseNo;
	@SuppressWarnings("unused")
	private List<BetType> betTypes;//投注类型列表
	private Integer lotteryTypeValue;
	private Integer betTypeValue;//页面选择的要开奖的投注类型
	
	private LotteryType lotteryType;
	private BetType betType;
	
	@SuppressWarnings("unused")
	private List<LotteryType> lotteryTypeList;
	
	public String handle() {
		logger.info("进入篮彩开奖");
		return "drawReady";
	}
	
	public String drawReady() {
		logger.info("进入篮彩开奖准备");
		if (!StringUtils.isEmpty(phaseNo) && !phaseNo.equals("-1")) {
			
			lotteryType = LotteryType.getItem(lotteryTypeValue);
			
			List<JclqRaceStatus> jclqStatusList = new ArrayList<JclqRaceStatus>();
			jclqStatusList.add(JclqRaceStatus.CLOSE);
			jclqStatusList.add(JclqRaceStatus.DRAW);
			jclqStatusList.add(JclqRaceStatus.REWARD);
			jclqStatusList.add(JclqRaceStatus.RESULT_SET);
			
			JclqStaticDrawStatus staticDrawStatus = null;
			if (betTypeValue != null && betTypeValue == BetType.JCLQ_STATIC_BET.getValue()) {
				staticDrawStatus = JclqStaticDrawStatus.OPEN;
				betType = BetType.getItem(betTypeValue);
			}
			
			JclqDynamicDrawStatus dynamicDrawStatus = null;
			if (betTypeValue != null && betTypeValue == BetType.JCLQ_DYNAMIC_BET.getValue()) {
				if (lotteryType.getValue() == LotteryType.JCLQ_SFC.getValue()) {
					staticDrawStatus = JclqStaticDrawStatus.OPEN;
					betType = BetType.getItem(betTypeValue);
				} else {
					dynamicDrawStatus = JclqDynamicDrawStatus.OPEN;
					betType = BetType.getItem(betTypeValue);
				}
			}
			
			try {
				jclqRaces = jclqRaceService.getJclqRaceDrawReadyList(phaseNo, jclqStatusList, staticDrawStatus, dynamicDrawStatus, maxJclqRaceNum);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
			}
			if (jclqRaces == null || jclqRaces.size() == 0) {
				logger.error("获取彩期<"+phaseNo+">竞彩足球比赛数据不存在,无法开奖");
				super.setErrorMessage("获取彩期<"+phaseNo+">竞彩足球比赛数据不存在,无法开奖");
				return "drawReady";
			}
		} else {
			logger.error("开奖时间不存在,无法获取竞彩篮球比赛数据");
			super.setErrorMessage("开奖时间不存在,无法获取竞彩篮球比赛数据");
			return "drawReady";
		}
		logger.info("篮彩开奖准备结束");
		return "draw";
	}
	
	//开奖
	public String draw() {
		logger.info("进入篮彩开奖");
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
		
		
		
		String msg = "";
		if (lotteryType==null) {
			rs.put("state","failed");
			msg = "要开奖的彩期类型值:"+lotteryTypeValue+"在系统中不存在,开奖失败";
			rs.put("msg", msg);
			logger.info(msg);
			writeRs(response,rs);
			return null;
		}
		if (StringUtils.isEmpty(phaseNo)) {
			rs.put("state","failed");
			msg = "要开奖的彩票["+phaseType.getName()+"]的彩期为空,开奖失败";
			rs.put("msg", msg);
			logger.info(msg);
			writeRs(response,rs);
			return null;
		}
		
		drawLockKey = LotteryUtil.generateLotteryDrawLockKeyByBetType(lotteryType, phaseNo ,String.valueOf(betTypeValue));
		try {
			lotteryDrawMemCacheLock = (LotteryDrawMemCacheLock)memcachedService.get(drawLockKey, LotteryUtil.DRAW_LOCK_TIMEOUT);
		} catch (Exception e) {
			logger.error("从缓存中读取开奖锁出错", e);
			rs.put("state","failed");
			rs.put("msg", "从缓存中读取开奖锁出错");
			writeRs(response,rs);
			return null;
		}
		String msgHeader = "["+lotteryType.getName()+"]彩期为<"+phaseNo+">的"+(lotteryDrawMemCacheLock==null?"":(lotteryDrawMemCacheLock.getBetType()==null?"":lotteryDrawMemCacheLock.getBetType().getName()))+"开奖结果:";
		if (lotteryDrawMemCacheLock!=null) {
			rs.put("state","failed");
			msg = msgHeader+"已经有正在开奖的开奖任务,本次操作失败,需等本期开奖任务执行结束才可重新执行";
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
		lotteryDrawQueueTask.setBetType(BetType.getItem(betTypeValue));
		//队列名称
		boolean resultFlag = false;
		try {
			resultFlag = simpleQueueService.putString(drawQueuePrefix+String.valueOf(lotteryTypeValue), LotteryDrawQueueTask.toJSONString(lotteryDrawQueueTask));
		} catch (Exception e) {
			logger.error("放入竞彩篮球开奖任务到队列失败, {}, lastMatchNum={}", new Object[] {
					lotteryType.getName(), 	lastMatchNum
			});
			logger.error(e.getMessage(), e);
		}
		if (resultFlag) {
			//加锁
			lotteryDrawMemCacheLock = new LotteryDrawMemCacheLock();
			lotteryDrawMemCacheLock.setLotteryTypeValue(lotteryTypeValue);
			lotteryDrawMemCacheLock.setPhase(phaseNo);
			BetType lockBetType = BetType.getItem(betTypeValue);
			lotteryDrawMemCacheLock.setBetType(lockBetType);
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
		logger.info("篮彩开奖结束");
		return null;
	}
	
	//开奖状态
	public String drawStatus() {
		logger.info("进入查询篮彩开奖状态");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			rs.put("state","failed");
			rs.put("msg", "彩种类型数值不存在,null");
			writeRs(response,rs);
			return null;
		}
		String msg = "";
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType phaseType = PhaseType.getItem(lotteryType);
		if (StringUtils.isEmpty(phaseNo)) {
			rs.put("state","failed");
			msg = "要开奖的彩票["+phaseType.getName()+"]的彩期为null,开奖失败";
			rs.put("msg", msg);
			logger.info(msg);
			writeRs(response,rs);
			return null;
		}
		String memCacheLotteryDrawKey = LotteryUtil.generateLotteryDrawKeyByBetType(lotteryType, phaseNo , String.valueOf(betTypeValue));
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
		logger.info("查询篮彩开奖状态结束");
		return null;
	}
	
	public String getJclqOnSalePhaseList() {
		logger.info("获取竞彩足球在售彩期列表开始");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			lotteryTypeValue = LotteryType.ALL.getValue();
		}
		LotteryType lastLotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType lastPhaseType = PhaseType.getItem(lastLotteryType);
		List<Phase> phases = null;
		try {
			phases = phaseService.findOnSalePhases(lastPhaseType,super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取前后N期彩期列表，api调用异常，{}", e.getMessage());
			rs.put("state", "failed");
			rs.put("msg", e.getMessage());
			rs.put("phases", "[]");
			rs.put("assignPhaseNo", "");
			writeRs(response, rs);
			return null;
		}
		rs.put("state", "success");
		rs.put("phases", Phase.toJSONArray(phases));
		writeRs(response, rs);
		logger.info("获取竞彩足球在售彩期列表结束");
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
	public String getLotteryConfigData() {
		return lotteryConfigData;
	}
	public void setLotteryConfigData(String lotteryConfigData) {
		this.lotteryConfigData = lotteryConfigData;
	}
	public List<LotteryType> getLotteryTypeList() {
		return JclqLottery.getList();
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
	
	public String getLastMatchNum() {
		return lastMatchNum;
	}
	public void setLastMatchNum(String lastMatchNum) {
		this.lastMatchNum = lastMatchNum;
	}
	public JclqRaceService getJclqRaceService() {
		return jclqRaceService;
	}
	public void setJclqRaceService(JclqRaceService jclqRaceService) {
		this.jclqRaceService = jclqRaceService;
	}
	public List<JclqRace> getJclqRaces() {
		return jclqRaces;
	}
	public void setJclqRaces(List<JclqRace> jclqRaces) {
		this.jclqRaces = jclqRaces;
	}
	public List<BetType> getBetTypes() {
		List<BetType> items = new ArrayList<BetType>();
		items.add(BetType.JCLQ_STATIC_BET);
		items.add(BetType.JCLQ_DYNAMIC_BET);
		return items;
	}
	public void setBetTypes(List<BetType> betTypes) {
		this.betTypes = betTypes;
	}
	public Integer getBetTypeValue() {
		return betTypeValue;
	}
	public void setBetTypeValue(Integer betTypeValue) {
		this.betTypeValue = betTypeValue;
	}
	
	
	public LotteryType getLotteryType() {
		return lotteryType;
	}
	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}
	public BetType getBetType() {
		return betType;
	}
	public void setBetType(BetType betType) {
		this.betType = betType;
	}

	public String getPhaseNo() {
		return phaseNo;
	}

	public void setPhaseNo(String phaseNo) {
		this.phaseNo = phaseNo;
	}
}
