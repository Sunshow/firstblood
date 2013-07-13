package web.action.schedule;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.service.lottery.JczqRaceService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.core.api.lottery.JczqChampionSecondRace;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.JczqDynamicSaleStatus;
import com.lehecai.core.lottery.JczqRaceStatus;
import com.lehecai.core.lottery.JczqStaticSaleStatus;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.cache.JczqLottery;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.lottery.fetcher.jczq.IJczqChampionSecondScheduleFetcher;
import com.lehecai.core.lottery.fetcher.jczq.JczqChampionSecondScheduleItem;
import com.lehecai.core.lottery.fetcher.jczq.impl.CommonJczqChampionSecondScheduleFetcher;
import com.opensymphony.xwork2.Action;

public class JczqChampionSecondRaceAction extends BaseAction {
	private static final long serialVersionUID = 5495014512794680107L;

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private JczqRaceService jczqRaceService;
	
	private PhaseService phaseService;
	
	private LotteryType lotteryType = LotteryType.JCZQ_GYJ;
	
	private String phase;
	
	private int fetcherType;
	
	private int statusValue;
	
	private JczqChampionSecondRace race;
	
	private List<JczqChampionSecondRace> races;
	
	private List<Integer> statusValues;
	
	public String handle() {
		logger.info("进入查询竞彩足球猜冠亚军对阵信息");
		if (StringUtils.isEmpty(phase)) {
			Phase currentPhase = null;
			try {
				currentPhase = phaseService.getCurrentPhase(PhaseType.getItem(LotteryType.JCZQ_GYJ.getValue()));
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询竞彩足球当前期时，api调用异常，{}", e);
			}
			if (currentPhase != null) {
				phase = currentPhase.getPhase();
			}
		}
		try {
			races = jczqRaceService.getChampionSecondRaceList(phase, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("查询竞彩足球猜冠亚军对阵信息结束");
		return "list";
	}
	
	public String fetchRaceList() {
		logger.info("进入获取竞彩足球猜冠亚军比赛数据");
		try {
			if (!StringUtils.isEmpty(phase)) {
				IJczqChampionSecondScheduleFetcher fetcher = new CommonJczqChampionSecondScheduleFetcher();
				List<JczqChampionSecondScheduleItem> scheduleItems = fetcher.fetch(phase, FetcherType.getItem(fetcherType));
				
				if (scheduleItems == null || scheduleItems.isEmpty()) {
					logger.error("从官方抓取猜冠亚军赛程数据结果为空, phase={}", phase);
					throw new Exception("抓取猜冠亚军赛程数据结果为空,fetcherType=" + FetcherType.getItem(fetcherType).toString() + "");
				}
				
				races = new ArrayList<JczqChampionSecondRace>();
				for (JczqChampionSecondScheduleItem item : scheduleItems) {
					JczqChampionSecondRace jczqChampionSecondRace = new JczqChampionSecondRace();
					jczqChampionSecondRace.setMatchNum(item.getMatchNum());
					jczqChampionSecondRace.setPhase(item.getPhase());
					jczqChampionSecondRace.setStatus(item.getStatus());
					jczqChampionSecondRace.setHomeTeam(item.getHomeTeam());
					jczqChampionSecondRace.setAwayTeam(item.getAwayTeam());
					races.add(jczqChampionSecondRace);
				}
			} else {
				logger.error("获取竞彩足球猜冠亚军比赛数据彩期参数为空");
				super.setErrorMessage("获取竞彩足球猜冠亚军比赛数据彩期参数为空");
			}
		} catch (Exception e) {
			logger.error("抓取赛程出错", e);
			super.setErrorMessage("抓取赛程出错：" + e.getMessage());
		}
		logger.info("获取竞彩足球猜冠亚军比赛数据结束");
		return "fetchList";
	}
	
	public String createRace() {
		logger.info("进入创建赛程");
		
		if (StringUtils.isEmpty(phase)) {
			Phase currentPhase = null;
			try {
				currentPhase = phaseService.getCurrentPhase(PhaseType.getItem(LotteryType.JCZQ_GYJ.getValue()));
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询竞彩足球当前期时，api调用异常，{}", e);
			}
			if (currentPhase != null) {
				phase = currentPhase.getPhase();
			}
		}
		race.setPhase(phase);
		race.setStatus(JczqRaceStatus.UNOPEN);
		
		if (jczqRaceService.saveChampionSecondRace(race)) {
			logger.info("创建赛程成功");
			super.setSuccessMessage("创建赛程成功");
		} else {
			logger.error("创建赛程失败");
			super.setSuccessMessage("创建赛程失败");
		}
		try {
			races = jczqRaceService.getChampionSecondRaceList(phase, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("创建赛程结束");
		return "list";
	}
	
	public String updateRace() {
		logger.info("进入修改赛程");
		boolean b = jczqRaceService.updateChampionSecondRace(race);

		JSONObject rs = new JSONObject();
		if (b) {
			rs.put("msg", "更新成功");
			logger.error("更新竞彩足球猜冠亚军数据成功");
		} else {
			rs.put("msg", "更新失败，请重试");
			logger.error("更新竞彩足球猜冠亚军数据失败");
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("修改赛程结束");
		return null;
	}
	
	public String updateRaces() {
		logger.info("进入更新赛程数据");
		ResultBean resultBean = null;
		
		if (races == null || races.size() == 0) {
			logger.error("更新赛程为空");
			super.setErrorMessage("更新赛程不能为空");
			try {
				races = jczqRaceService.getChampionSecondRaceList(phase, null);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("竞彩足球猜冠亚军查询数据库中赛程数据时，api调用异常，{}", e);
			}
			return "list";
		}
		try {
			if (races != null && races.size() > 0) {
				for (int i = 0 ; i < races.size(); i ++) {
					if (statusValues != null && statusValues.size() > 0) {
						races.get(i).setStatus(JczqRaceStatus.getItem(statusValues.get(i)));
					}
				}
			}
			
			resultBean = jczqRaceService.batchCreateChampionSecond(races);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("批量更新猜冠亚军赛程数据时，api调用异常，{}", e);
		}
		if (resultBean == null) {
			logger.error("更新竞彩足球猜冠亚军赛程，更新失败");
			super.setErrorMessage("更新竞彩足球猜冠亚军赛程失败，请联系管理员");
			try {
				races = jczqRaceService.getChampionSecondRaceList(phase, null);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("竞彩足球猜冠亚军查询数据库中赛程数据时，api调用异常，{}", e);
			}
			return "list";
		}
		if (!resultBean.isResult()) {
			super.setErrorMessage(resultBean.getMessage());
			try {
				races = jczqRaceService.getChampionSecondRaceList(phase, null);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("竞彩足球猜冠亚军查询数据库中赛程数据时，api调用异常，{}", e);
			}
			return "list";
		}
		try {
			races = jczqRaceService.getChampionSecondRaceList(phase, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("竞彩足球猜冠亚军查询数据库中赛程数据时，api调用异常，{}", e);
		}
		super.setSuccessMessage(resultBean.getMessage());
		logger.info("更新赛程数据结束");
		return "list";
	}
	
	public String updateStatus() {
		logger.info("进入更新赛程状态");
		HttpServletResponse response = ServletActionContext.getResponse();
		Integer rc = 0;//0成功,1失败
		String message = "更新" + race.getId() + "成功";
		
		JczqRaceStatus status = JczqRaceStatus.getItem(statusValue);
		race.setStatus(status);
		boolean flag = jczqRaceService.updateChampionSecondRaceStatus(race);
		try {
			races = jczqRaceService.getChampionSecondRaceList(phase, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("竞彩足球猜冠亚军查询数据库中赛程数据时，api调用异常，{}", e);
		}
		if (!flag){
			super.setErrorMessage("更新" + race.getId() + "失败");
			rc = 1;//0成功,1失败
			message = "更新"  + race.getId() + "失败";
			logger.info("更新失败");
		} else {
			super.setSuccessMessage("更新" + race.getId() + "成功");
			logger.info("更新成功");
		}
		for (JczqChampionSecondRace r : races) {
			if (race.getId() != null && !"".equals(race.getId())) {
				if (race.getId().equals(r.getId())) {
					race = r;
				}
			}
		}
		JSONObject json = new JSONObject();
		json.put("code", rc);
		json.put("message", message);
		json.put("data", JczqChampionSecondRace.toJSON(race));
		super.writeRs(response, json);
		logger.info("更新赛程状态结束");
		return Action.NONE;
	}
	
	public List<FetcherType> getFetcherTypeList() {
		List<FetcherType> list = new ArrayList<FetcherType>();
		list.add(FetcherType.T_PENGINEAPI);
		list.add(FetcherType.T_OFFICIAL);
		return list;
	}
	
	public int getFetcherType() {
		return fetcherType;
	}

	public void setFetcherType(int fetcherType) {
		this.fetcherType = fetcherType;
	}

	public JczqRaceService getJczqRaceService() {
		return jczqRaceService;
	}

	public void setJczqRaceService(JczqRaceService jczqRaceService) {
		this.jczqRaceService = jczqRaceService;
	}

	public List<FetcherType> getFetchers() {
		return FetcherType.getItems();
	}
	
	public List<LotteryType> getJczqLotteryList(){
		return JczqLottery.getList();
	}
	
	public JczqStaticSaleStatus getOpenJczqStaticSaleStatus() {
		return JczqStaticSaleStatus.SALE_OPEN;
	}
	
	public JczqRaceStatus getUnopenStatus() {
		return JczqRaceStatus.UNOPEN;
	}
	
	public JczqRaceStatus getOpenStatus() {
		return JczqRaceStatus.OPEN;
	}
	
	public JczqRaceStatus getCloseStatus() {
		return JczqRaceStatus.CLOSE;
	}
	
	public List<JczqStaticSaleStatus> getJczqStaticSaleStatus() {
		return JczqStaticSaleStatus.getItems();
	}
	
	public List<JczqDynamicSaleStatus> getJczqDynamicSaleStatus() {
		return JczqDynamicSaleStatus.getItems();
	}

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}
	public List<Integer> getStatusValues() {
		return statusValues;
	}
	public void setStatusValues(List<Integer> statusValues) {
		this.statusValues = statusValues;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public int getStatusValue() {
		return statusValue;
	}

	public void setStatusValue(int statusValue) {
		this.statusValue = statusValue;
	}

	public JczqChampionSecondRace getRace() {
		return race;
	}

	public void setRace(JczqChampionSecondRace race) {
		this.race = race;
	}

	public List<JczqChampionSecondRace> getRaces() {
		return races;
	}

	public void setRaces(List<JczqChampionSecondRace> races) {
		this.races = races;
	}

	public LotteryType getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}
}
