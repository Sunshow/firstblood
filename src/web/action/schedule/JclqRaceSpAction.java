package web.action.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.service.lottery.JclqRaceService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.service.ticket.TerminalService;
import com.lehecai.core.EnabledStatus;
import com.lehecai.core.api.lottery.JclqRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.exception.UnsupportedFetcherTypeException;
import com.lehecai.core.lottery.JclqDynamicDrawStatus;
import com.lehecai.core.lottery.JclqRaceStatus;
import com.lehecai.core.lottery.JclqStaticDrawStatus;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.lottery.fetcher.jclq.IJclqSpFetcher;
import com.lehecai.core.lottery.fetcher.jclq.JclqSpItem;
import com.lehecai.core.lottery.fetcher.jclq.impl.CommonJclqSpFetcher;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.engine.entity.terminal.Terminal;

public class JclqRaceSpAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final String PHASE_FORMAT_DATE = "yyyyMMdd";

	private JclqRaceService jclqRaceService;
	private PhaseService phaseService;
	private TerminalService terminalService;

	private int fetcherType;
	private JclqRace race;
	private List<JclqRace> races;
	private String phaseNo;
	private int tag;
	private String terminalId;
	private List<String> drawStatusList;
	private List<Terminal> terminalList;
	
	public String handle() {
		logger.info("进入查询竞彩篮球对阵信息");
		List<JclqRaceStatus> statuses = new ArrayList<JclqRaceStatus>();
		statuses.add(JclqRaceStatus.CLOSE);
		statuses.add(JclqRaceStatus.DRAW);
		statuses.add(JclqRaceStatus.RESULT_SET);
		statuses.add(JclqRaceStatus.REWARD);
		try {
			races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), statuses, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date())));
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("查询竞彩篮球对阵信息结束");
		return "list";
	}
	
	public String fetchSpList() {
		logger.info("进入获取sp数据");
		List<JclqRace> dbRaces = null;
		List<JclqRaceStatus> statuses = new ArrayList<JclqRaceStatus>();
		statuses.add(JclqRaceStatus.CLOSE);
		statuses.add(JclqRaceStatus.DRAW);
		statuses.add(JclqRaceStatus.RESULT_SET);
		statuses.add(JclqRaceStatus.REWARD);
		try {
			dbRaces = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), statuses, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date())));
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		List<JclqRace> fetchRaces = null;
		try {
			//sp
			IJclqSpFetcher spFetcher = new CommonJclqSpFetcher();
			if (StringUtils.isEmpty(terminalId)) {
				terminalId = null;
			}
			List<JclqSpItem> spItems = spFetcher.fetch(this.getPhaseNo(), FetcherType.getItem(fetcherType), terminalId);
			
			fetchRaces = new ArrayList<JclqRace>();
			if (spItems != null && spItems.size() > 0) {
				for (JclqSpItem spItem : spItems) {
					JclqRace race  = new JclqRace();
					race.setMatchNum(spItem.getMatchNum());
					race.setOfficialNum(spItem.getOfficialNum());
					race.setOfficialDate(spItem.getOfficialDate());
					race.setMatchDate(spItem.getMatchDate());
					race.setMatchName(spItem.getMatchName());
					race.setHomeTeam(spItem.getHomeTeam());
					race.setAwayTeam(spItem.getAwayTeam());
					race.setPrizeSf(spItem.getPrizeSf());
					race.setPrizeRfsf(spItem.getPrizeRfsf());
					race.setPrizeSfc(spItem.getPrizeSfc());
					race.setPrizeDxf(spItem.getPrizeDxf());
					race.setFirstQuarter(spItem.getFirstQuarter());
					race.setSecondQuarter(spItem.getSecondQuarter());
					race.setThirdQuarter(spItem.getThirdQuarter());
					race.setFourthQuarter(spItem.getFourthQuarter());
					race.setFinalScore(spItem.getFinalScore());
					fetchRaces.add(race);
				}
			}
		} catch (UnsupportedFetcherTypeException e) {
			logger.error(e.getMessage(), e);
		}
		
		if (dbRaces == null || dbRaces.size() == 0) {
			logger.error("状态为关闭，结果已公布，已开奖或已派奖的赛程数据为空");
			super.setErrorMessage(this.getPhaseNo() + "数据库中未查询到状态为关闭，结果已公布，已开奖或已派奖的赛程");
			return "list";
		}
		if (fetchRaces == null || fetchRaces.size() == 0) {
			logger.error("赛程数据为空");
			super.setErrorMessage(this.getPhaseNo() + "未抓取到赛程：抓取器：" + FetcherType.getItem(fetcherType).getName());
			races = dbRaces;
			return "list";
		}
		
		for (JclqRace dbRace : dbRaces) {
			if (dbRace.getStatus() != null && dbRace.getStatus().getValue() == JclqRaceStatus.REWARD.getValue()
					&& dbRace.getDynamicDrawStatus().getValue() == JclqDynamicDrawStatus.OPEN.getValue()
					&& dbRace.getStaticDrawStatus().getValue() == JclqStaticDrawStatus.OPEN.getValue()) {
				// 已派奖的比赛不重新修改比分
				continue;
			}
			for (JclqRace fetchRace : fetchRaces) {
				if (dbRace.getMatchNum().equals(fetchRace.getMatchNum())) {
					dbRace.setFirstQuarter(fetchRace.getFirstQuarter());
					dbRace.setSecondQuarter(fetchRace.getSecondQuarter());
					dbRace.setThirdQuarter(fetchRace.getThirdQuarter());
					dbRace.setFourthQuarter(fetchRace.getFourthQuarter());
					dbRace.setFinalScore(fetchRace.getFinalScore());
					dbRace.setPrizeSf(fetchRace.getPrizeSf());
					dbRace.setPrizeRfsf(fetchRace.getPrizeRfsf());
					dbRace.setPrizeSfc(fetchRace.getPrizeSfc());
					dbRace.setPrizeDxf(fetchRace.getPrizeDxf());
				}
			}
		}
		races = dbRaces;
		logger.info("获取sp数据结束");
		return "list";
	}
	
	public String updateRace() {
		logger.info("进入更新竞彩篮球结果sp数据");
		boolean b = jclqRaceService.updateRaceSp(race);

		JSONObject rs = new JSONObject();
		if (b) {
			logger.info("更新竞彩篮球结果sp数据成功");
			rs.put("msg", "更新成功");
		} else {
			logger.error("更新竞彩篮球结果sp数据失败");
			rs.put("msg", "更新失败请重试");
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("更新竞彩篮球结果sp数据结束");
		return null;
	}
	
	public String updateRaces() {
		logger.info("进入更新结果sp数据");
		ResultBean resultBean = null;
		if (races == null || races.size() == 0) {
			logger.error("更新结果spsp为空");
			super.setErrorMessage("更新结果sp不能为空");
			return "failure";
		}
		try {
			resultBean = jclqRaceService.batchCreateSp(races);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("批量更新结果sp数据时，，api调用异常，{}", e);
		}
		if (resultBean == null) {
			logger.error("更新竞彩篮球结果sp失败");
			super.setErrorMessage("更新竞彩篮球结果sp失败，请联系管理员");
			return "failure";
		}
		if (!resultBean.isResult()) {
			logger.error("更新竞彩篮球结果sp失败");
			super.setErrorMessage(resultBean.getMessage());
			return "failure";
		}
		List<JclqRaceStatus> statuses = new ArrayList<JclqRaceStatus>();
		statuses.add(JclqRaceStatus.CLOSE);
		statuses.add(JclqRaceStatus.DRAW);
		statuses.add(JclqRaceStatus.RESULT_SET);
		statuses.add(JclqRaceStatus.REWARD);
		try {
			races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), statuses, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date())));
		} catch (ApiRemoteCallFailedException e) {
			logger.error("竞彩篮球查询数据库中结果sp数据时，api调用异常，{}", e);
		}
		super.setSuccessMessage(resultBean.getMessage());
		logger.info("更新结果sp数据结束");
		return "list";
	}
	
	public String updateDynamicDraw() {
		logger.info("进入更新单关可开奖状态");
		StringBuffer sb = new StringBuffer();
		if (drawStatusList != null && drawStatusList.size() > 0) {
			for (String matchNum : drawStatusList) {
				JclqRace r = new JclqRace();
				r.setMatchNum(matchNum);
				r.setDynamicDrawStatus(JclqDynamicDrawStatus.OPEN);
				
				if (!jclqRaceService.updateRaceDynamicDrawStatus(r)) {
					sb.append(matchNum + "更新单关可开奖状态失败");
				}
			}
		}
		List<JclqRaceStatus> statuses = new ArrayList<JclqRaceStatus>();
		statuses.add(JclqRaceStatus.CLOSE);
		statuses.add(JclqRaceStatus.DRAW);
		statuses.add(JclqRaceStatus.RESULT_SET);
		statuses.add(JclqRaceStatus.REWARD);
		try {
			races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), statuses, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date())));
		} catch (ApiRemoteCallFailedException e) {
			logger.error("竞彩篮球查询数据库中赛程数据时，api调用异常，{}", e);
		}
		if (sb.length() > 0) {
			logger.error("更新失败");
			super.setErrorMessage("更新" + sb.toString() + "失败");
		} else {
			logger.info("更新成功");
			super.setSuccessMessage("更新成功");
		}
		logger.info("更新单关可开奖状态结束");
		return "list";
	}
	
	public String updateStaticDraw() {
		logger.info("进入更新过关可开奖状态");
		StringBuffer sb = new StringBuffer();
		if (drawStatusList != null && drawStatusList.size() > 0) {
			for (String matchNum : drawStatusList) {
				JclqRace r = new JclqRace();
				r.setMatchNum(matchNum);
				r.setStaticDrawStatus(JclqStaticDrawStatus.OPEN);
				
				if (!jclqRaceService.updateRaceStaticDrawStatus(r)) {
					sb.append(matchNum + "更新过关可开奖状态失败");
				}
			}
		}
		List<JclqRaceStatus> statuses = new ArrayList<JclqRaceStatus>();
		statuses.add(JclqRaceStatus.CLOSE);
		statuses.add(JclqRaceStatus.DRAW);
		statuses.add(JclqRaceStatus.RESULT_SET);
		statuses.add(JclqRaceStatus.REWARD);
		try {
			races = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), statuses, this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date())));
		} catch (ApiRemoteCallFailedException e) {
			logger.error("竞彩篮球查询数据库中赛程数据时，api调用异常，{}", e);
		}
		if (sb.length() > 0) {
			logger.error("更新失败");
			super.setErrorMessage("更新" + sb.toString() + "失败");
		} else {
			logger.info("更新成功");
			super.setSuccessMessage("更新成功");
		}
		logger.info("更新过关可开奖状态结束");
		return "list";
	}
	
	public int getLotteryTypeValue(){
		return LotteryType.JCLQ_SF.getValue();
	}
	
	public int getFetcherType() {
		return fetcherType;
	}

	public void setFetcherType(int fetcherType) {
		this.fetcherType = fetcherType;
	}

	public JclqRaceService getJclqRaceService() {
		return jclqRaceService;
	}

	public void setJclqRaceService(JclqRaceService jclqRaceService) {
		this.jclqRaceService = jclqRaceService;
	}

	public List<JclqRace> getRaces() {
		return races;
	}

	public void setRaces(List<JclqRace> races) {
		this.races = races;
	}


	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public JclqRace getRace() {
		return race;
	}

	public void setRace(JclqRace race) {
		this.race = race;
	}

	public List<FetcherType> getFetchers() {
		return FetcherType.getItems();
	}

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}

	public List<String> getDrawStatusList() {
		return drawStatusList;
	}

	public void setDrawStatusList(List<String> drawStatusList) {
		this.drawStatusList = drawStatusList;
	}
	
	public String getPhaseNo() {
		if(StringUtils.isEmpty(phaseNo)){
			phaseNo = CoreDateUtils.formatDate(new Date(),PHASE_FORMAT_DATE);
		}
		return phaseNo;
	}

	public void setPhaseNo(String phaseNo) {
		this.phaseNo = phaseNo;
	}
	
	public JclqRaceStatus getCloseStatus() {
		return JclqRaceStatus.CLOSE;
	}
	
	public JclqRaceStatus getResultSetStatus() {
		return JclqRaceStatus.RESULT_SET;
	}
	public JclqRaceStatus getRewardStatus() {
		return JclqRaceStatus.REWARD;
	}
	public JclqDynamicDrawStatus getOpenDynamicDrawStatus() {
		return JclqDynamicDrawStatus.OPEN;
	}
	public JclqStaticDrawStatus getOpenStaticDrawStatus() {
		return JclqStaticDrawStatus.OPEN;
	}

	public void setTerminalService(TerminalService terminalService) {
		this.terminalService = terminalService;
	}

	public TerminalService getTerminalService() {
		return terminalService;
	}

	public void setTerminalList(List<Terminal> terminalList) {
		this.terminalList = terminalList;
	}

	public List<Terminal> getTerminalList() {
		Terminal terminal = new Terminal();
		terminal.setIsEnabled(EnabledStatus.ENABLED);
		PageBean page = super.getPageBean();
		page.setPageFlag(false);
		try{
			terminalList = terminalService.list(terminal, page);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return terminalList;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getTerminalId() {
		return terminalId;
	}

}
