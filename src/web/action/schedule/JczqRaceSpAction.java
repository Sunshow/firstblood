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
import com.lehecai.admin.web.service.lottery.JczqRaceService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.service.ticket.TerminalService;
import com.lehecai.core.EnabledStatus;
import com.lehecai.core.api.lottery.JczqRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.exception.UnsupportedFetcherTypeException;
import com.lehecai.core.lottery.JczqDynamicDrawStatus;
import com.lehecai.core.lottery.JczqRaceStatus;
import com.lehecai.core.lottery.JczqStaticDrawStatus;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.lottery.fetcher.jczq.IJczqSpFetcher;
import com.lehecai.core.lottery.fetcher.jczq.JczqSpItem;
import com.lehecai.core.lottery.fetcher.jczq.impl.CommonJczqSpFetcher;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.engine.entity.terminal.Terminal;

public class JczqRaceSpAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final String PHASE_FORMAT_DATE = "yyyyMMdd";

	private JczqRaceService jczqRaceService;
	private PhaseService phaseService;
	private TerminalService terminalService;

	private int fetcherType;
	private JczqRace race;
	private List<JczqRace> races;
	private String phaseNo;
	private int tag;
	private String terminalId;
	private List<String> drawStatusList;
	private List<Terminal> terminalList;
	
	public String handle() {
		logger.info("进入查询竞彩足球对阵信息");
		List<JczqRaceStatus> statuses = new ArrayList<JczqRaceStatus>();
		statuses.add(JczqRaceStatus.CLOSE);
		statuses.add(JczqRaceStatus.DRAW);
		statuses.add(JczqRaceStatus.RESULT_SET);
		statuses.add(JczqRaceStatus.REWARD);
		try {
			Date phaseDate = convertPhaseNoToDate(this.getPhaseNo(), PHASE_FORMAT_DATE);
			races = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), statuses, CoreDateUtils.formatDate(phaseDate).equals(CoreDateUtils.formatDate(new Date())));
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		return "list";
	}
	
	public String fetchSpList() {
		logger.info("进入获取竞彩足球结果sp数据");
		List<JczqRace> dbRaces = null;
		List<JczqRaceStatus> statuses = new ArrayList<JczqRaceStatus>();
		statuses.add(JczqRaceStatus.CLOSE);
		statuses.add(JczqRaceStatus.DRAW);
		statuses.add(JczqRaceStatus.RESULT_SET);
		statuses.add(JczqRaceStatus.REWARD);
		try {
			Date phaseDate = convertPhaseNoToDate(phaseNo, PHASE_FORMAT_DATE);
			dbRaces = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), statuses, CoreDateUtils.formatDate(phaseDate).equals(CoreDateUtils.formatDate(new Date())));
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
		}
		List<JczqRace> fetchRaces = null;
		try {
			//sp
			IJczqSpFetcher spFetcher = new CommonJczqSpFetcher();
			if (StringUtils.isEmpty(terminalId)) {
				terminalId = null;
			}
			List<JczqSpItem> spItems = spFetcher.fetch(this.getPhaseNo(), FetcherType.getItem(fetcherType), terminalId);
			
			fetchRaces = new ArrayList<JczqRace>();
			if (spItems != null && spItems.size() > 0) {
				for (JczqSpItem spItem : spItems) {
					JczqRace race  = new JczqRace();
					race.setMatchNum(spItem.getMatchNum());
					race.setOfficialNum(spItem.getOfficialNum());
					race.setOfficialDate(spItem.getOfficialDate());
					race.setMatchDate(spItem.getMatchDate());
					race.setMatchName(spItem.getMatchName());
					race.setHomeTeam(spItem.getHomeTeam());
					race.setAwayTeam(spItem.getAwayTeam());
					race.setPrizeSpf(spItem.getPrizeSpf());
					race.setPrizeBf(spItem.getPrizeBf());
					race.setPrizeJqs(spItem.getPrizeJqs());
					race.setPrizeBqc(spItem.getPrizeBqc());
					race.setPrizeSpfWrq(spItem.getPrizeSpfWrq());
					race.setFirstHalf(spItem.getFirstHalf());
					race.setSecondHalf(spItem.getSecondHalf());
					race.setFinalScore(spItem.getFinalScore());
					fetchRaces.add(race);
				}
			}
		} catch (UnsupportedFetcherTypeException e) {
			logger.error(e.getMessage(), fetcherType, e);
		}
		
		if (dbRaces == null || dbRaces.size() == 0) {
			logger.error("状态为关闭，结果已公布，已开奖或已派奖的赛程为空");
			super.setErrorMessage("彩期<" + this.getPhaseNo() + ">数据库中未查询到状态为关闭，结果已公布，已开奖或已派奖的赛程");
			return "list";
		}
		if (fetchRaces == null || fetchRaces.size() == 0) {
			logger.error("抓取到赛程为空");
			super.setErrorMessage("彩期<" + this.getPhaseNo() + ">未抓取到赛程：抓取器：" + FetcherType.getItem(fetcherType).getName());
			races = dbRaces;
			return "list";
		}
		
		for (JczqRace dbRace : dbRaces) {
			if (dbRace.getStatus() != null && dbRace.getStatus().getValue() == JczqRaceStatus.REWARD.getValue()
					&& dbRace.getDynamicDrawStatus().getValue() == JczqDynamicDrawStatus.OPEN.getValue()
					&& dbRace.getStaticDrawStatus().getValue() == JczqStaticDrawStatus.OPEN.getValue()) {
				// 已派奖的比赛不重新修改比分
				continue;
			}
			for (JczqRace fetchRace : fetchRaces) {
				if (dbRace.getMatchNum().equals(fetchRace.getMatchNum())) {
					dbRace.setFirstHalf(fetchRace.getFirstHalf());
					dbRace.setSecondHalf(fetchRace.getSecondHalf());
					dbRace.setFinalScore(fetchRace.getFinalScore());
					dbRace.setPrizeSpf(fetchRace.getPrizeSpf());
					dbRace.setPrizeBf(fetchRace.getPrizeBf());
					dbRace.setPrizeJqs(fetchRace.getPrizeJqs());
					dbRace.setPrizeBqc(fetchRace.getPrizeBqc());
                    dbRace.setPrizeSpfWrq(fetchRace.getPrizeSpfWrq());
				}
			}
		}
		races = dbRaces;
		logger.info("获取竞彩足球结果sp数据结束");
		return "list";
	}
	
	public String updateRace() {
		logger.info("进入更新竞彩足球结果sp数据");
		boolean b = jczqRaceService.updateRaceSp(race);

		JSONObject rs = new JSONObject();
		if (b) {
			rs.put("msg", "更新成功");
			logger.info("更新竞彩足球结果sp数据成功!");
		} else {
			rs.put("msg", "更新失败请重试");
			logger.error("更新竞彩足球结果sp数据失败!");
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("更新竞彩足球结果sp数据结束");
		return null;
	}
	
	public String updateRaces() {
		logger.info("进入批量更新竞彩足球结果sp数据");
		ResultBean resultBean = null;
		if (races == null || races.size() == 0) {
			logger.error("结果sp为空");
			super.setErrorMessage("更新结果sp不能为空");
			return "failure";
		}
		try {
			resultBean = jczqRaceService.batchCreateSp(races);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("批量更新结果sp数据时，api调用异常，{}", e);
		}
		if (resultBean == null) {
			logger.error("更新竞彩足球结果sp失败");
			super.setErrorMessage("更新竞彩足球结果sp失败，请联系管理员");
			return "failure";
		}
		if (!resultBean.isResult()) {
			super.setErrorMessage(resultBean.getMessage());
			return "failure";
		}
		List<JczqRaceStatus> statuses = new ArrayList<JczqRaceStatus>();
		statuses.add(JczqRaceStatus.CLOSE);
		statuses.add(JczqRaceStatus.DRAW);
		statuses.add(JczqRaceStatus.RESULT_SET);
		statuses.add(JczqRaceStatus.REWARD);
		try {
			Date phaseDate = convertPhaseNoToDate(phaseNo, PHASE_FORMAT_DATE);
			races = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), statuses, CoreDateUtils.formatDate(phaseDate).equals(CoreDateUtils.formatDate(new Date())));
		} catch (ApiRemoteCallFailedException e) {
			logger.error("竞彩足球查询数据库中结果sp数据时，api调用异常，{}", e);
		}
		super.setSuccessMessage(resultBean.getMessage());
		logger.info("批量更新竞彩足球结果sp数据结束");
		return "list";
	}
	
	public String updateDynamicDraw() {
		logger.info("进入更新单关可开奖状态");
		StringBuffer sb = new StringBuffer();
		if (drawStatusList != null && drawStatusList.size() > 0) {
			for (String matchNum : drawStatusList) {
				JczqRace jczqRace = null;				
				try {
					jczqRace = jczqRaceService.getRaceByMatchNum(matchNum);
				} catch (ApiRemoteCallFailedException e) {
					logger.error("竞彩足球查询数据库中赛程数据时，api调用异常");
				}		
				if (StringUtils.isEmpty(jczqRace.getFinalScore())) {
					sb.append("竞彩足球浮动奖金开奖状态失败,终场比分为空！");
					logger.error("更新竞彩足球浮动奖金开奖状态失败,终场比分为空！");
				} else {
					jczqRace.setMatchNum(matchNum);
					jczqRace.setDynamicDrawStatus(JczqDynamicDrawStatus.OPEN);
					
					if (!jczqRaceService.updateRaceDynamicDrawStatus(jczqRace)) {
						sb.append(matchNum + "更新单关可开奖状态失败");
					}
				}
			}
		}
		List<JczqRaceStatus> statuses = new ArrayList<JczqRaceStatus>();
		statuses.add(JczqRaceStatus.CLOSE);
		statuses.add(JczqRaceStatus.DRAW);
		statuses.add(JczqRaceStatus.RESULT_SET);
		statuses.add(JczqRaceStatus.REWARD);
		try {
			Date phaseDate = convertPhaseNoToDate(phaseNo, PHASE_FORMAT_DATE);
			races = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), statuses, CoreDateUtils.formatDate(phaseDate).equals(CoreDateUtils.formatDate(new Date())));
		} catch (ApiRemoteCallFailedException e) {
			logger.error("竞彩足球查询数据库中赛程数据时，api调用异常，{}", e);
		}
		if (sb.length() > 0) {
			super.setErrorMessage("更新" + sb.toString() + "失败");
			logger.info("更新失败");
		} else {
			super.setSuccessMessage("更新成功");
			logger.info("更新成功");
		}
		logger.info("更新单关可开奖状态结束");
		return "list";
	}
	
	public String updateStaticDraw() {
		logger.info("进入更新过关可开奖");
		StringBuffer sb = new StringBuffer();
		if (drawStatusList != null && drawStatusList.size() > 0) {
			for (String matchNum : drawStatusList) {
				JczqRace jczqRace = null;			
				try {
					jczqRace = jczqRaceService.getRaceByMatchNum(matchNum);
				} catch (ApiRemoteCallFailedException e) {
					logger.error("竞彩足球查询数据库中赛程数据时，api调用异常");
				}
				if (StringUtils.isEmpty(jczqRace.getFinalScore())) {
					sb.append("竞彩足球固定奖金开奖状态失败,终场比分为空！");
					logger.error("更新竞彩足球固定奖金开奖状态失败,终场比分为空！");
				} else {
					jczqRace.setMatchNum(matchNum);
					jczqRace.setStaticDrawStatus(JczqStaticDrawStatus.OPEN);
					
					if (!jczqRaceService.updateRaceStaticDrawStatus(jczqRace)) {
						sb.append(matchNum + "更新过关可开奖状态失败");
					}
				}
			}
		}
		List<JczqRaceStatus> statuses = new ArrayList<JczqRaceStatus>();
		statuses.add(JczqRaceStatus.CLOSE);
		statuses.add(JczqRaceStatus.DRAW);
		statuses.add(JczqRaceStatus.RESULT_SET);
		statuses.add(JczqRaceStatus.REWARD);
		try {
			Date phaseDate = convertPhaseNoToDate(phaseNo, PHASE_FORMAT_DATE);
			races = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), statuses, CoreDateUtils.formatDate(phaseDate).equals(CoreDateUtils.formatDate(new Date())));
		} catch (ApiRemoteCallFailedException e) {
			logger.error("竞彩足球查询数据库中赛程数据时，api调用异常，{}", e);
		}
		if (sb.length() > 0) {
			super.setErrorMessage("更新" + sb.toString() + "失败");
			logger.info("更新失败");
		} else {
			super.setSuccessMessage("更新成功");
			logger.info("更新成功");
		}
		logger.info("更新过关可开奖结束");
		return "list";
	}
	
	private Date convertPhaseNoToDate(String phaseNo,String pattern){
		Date date =  CoreDateUtils.parseDate(phaseNo,pattern);
		return date;
	}
	
	public int getLotteryTypeValue(){
		return LotteryType.JCZQ_SPF.getValue();
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

	public List<JczqRace> getRaces() {
		return races;
	}

	public void setRaces(List<JczqRace> races) {
		this.races = races;
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

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public JczqRace getRace() {
		return race;
	}

	public void setRace(JczqRace race) {
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
	
	public JczqRaceStatus getCloseStatus() {
		return JczqRaceStatus.CLOSE;
	}
	
	public JczqRaceStatus getResultSetStatus() {
		return JczqRaceStatus.RESULT_SET;
	}
	public JczqRaceStatus getRewardStatus() {
		return JczqRaceStatus.REWARD;
	}
	public JczqDynamicDrawStatus getOpenDynamicDrawStatus() {
		return JczqDynamicDrawStatus.OPEN;
	}
	public JczqStaticDrawStatus getOpenStaticDrawStatus() {
		return JczqStaticDrawStatus.OPEN;
	}

	public void setTerminalService(TerminalService terminalService) {
		this.terminalService = terminalService;
	}

	public TerminalService getTerminalService() {
		return terminalService;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getTerminalId() {
		return terminalId;
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
}
