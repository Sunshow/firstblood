package web.action.cms;

import com.lehecai.admin.web.domain.cms.RecommendRace;
import com.lehecai.admin.web.service.lottery.DcRaceService;
import com.lehecai.admin.web.service.lottery.JczqRaceService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.core.api.lottery.DcRace;
import com.lehecai.core.api.lottery.JczqRace;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.util.CoreDateUtils;
import com.opensymphony.xwork2.Action;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.ServletActionContext;

import java.util.*;

/**
 * 竞彩足球推荐赛程管理
 * @author yanweijie
 *
 */
public class JczqRecommendRaceAction extends AbstractRecommendRaceAction {

	private static final long serialVersionUID = -5352145275369387787L;
	private static final String PHASE_FORMAT_DATE = "yyyyMMdd";
	
	private JczqRaceService jczqRaceService;

	private DcRaceService dcRaceService;
	private PhaseService phaseService;
	
	private List<JczqRace> races;
	private List<DcRace> dcRaces;
	
	private String phaseNo;
	
	private Long dcId;
	private String matchNum;
	private Date matchDate;
	
	static {
		lotteryActionMap.put(LotteryType.JCZQ_SPF, "jczqSpf%sRecommendRace");
		lotteryActionMap.put(LotteryType.JCZQ_JQS, "jczqJqs%sRecommendRace");
		lotteryActionMap.put(LotteryType.JCZQ_BF, "jczqBf%sRecommendRace");
		lotteryActionMap.put(LotteryType.JCZQ_BQC, "jczqBqc%sRecommendRace");
	}

	@Override
	protected void initAvailableRaceList() throws Exception {
        /*
		List<JczqRaceStatus> raceStatusList = new ArrayList<JczqRaceStatus>();
		raceStatusList.add(JczqRaceStatus.OPEN);
		*/

        if (this.getPhaseNo() == null) {
            List<Phase> onsalePhaseList = phaseService.findOnSalePhases(PhaseType.getItem(LotteryType.JCZQ_SPF), null);
            if (onsalePhaseList != null && !onsalePhaseList.isEmpty()) {
                this.setPhaseNo(onsalePhaseList.get(0).getPhase());
            } else {
                //throw new RuntimeException("未找到可销售彩期");
            }
        }

        if (this.getPhaseNo() != null) {
            List<JczqRace> allJczqRaces = jczqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null,
                    this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date(), PHASE_FORMAT_DATE)));

            if (allJczqRaces != null && !allJczqRaces.isEmpty()) {	//去除已推荐赛程
                if (this.getRecommendRaces() != null) {
                    // 已推荐的场次
                    Set<String> matchNumSet = new HashSet<String>();
                    for (RecommendRace recommendRace : this.getRecommendRaces()) {
                        matchNumSet.add(recommendRace.getMatchNum().toString());
                    }

                    races = new ArrayList<JczqRace>();
                    for (JczqRace jczqRace : allJczqRaces) {
                        if (matchNumSet.contains(jczqRace.getMatchNum())) {
                            continue;
                        }
                        races.add(jczqRace);
                    }
                } else {
                    races = allJczqRaces;
                }
            } else {
                logger.info("彩期<{}>没有竞彩足球对阵信息", this.getPhaseNo());
            }
        }
	}

	/**
	 * 查询匹配比赛日期的足球单场对阵
	 * @return
	 */
	public String findMatchDcRaces () {
		logger.info("进入查询匹配比赛日期的足球单场对阵");
		if (matchDate == null) {
			logger.error("比赛日期为空");
			super.setErrorMessage("比赛日期不能为空");
			return "failure";
		}
		
		dcRaces = dcRaceService.findDcRacesByMatchDate(matchDate);
		JSONArray dcRacesArray = DcRace.toJSONArray(dcRaces);
		
		logger.info(dcRacesArray.toString());
		
		JSONObject rs = new JSONObject();
		rs.put("data", dcRacesArray);
		super.writeRs(ServletActionContext.getResponse(), rs);
		
		return Action.NONE;
	}
	
	@Override
	protected void processRecommendRace(RecommendRace recommendRace) {
		recommendRace.setDcId(dcId);
	}

	@Override
	protected String getRecommendForwardUrl() {
		return super.getRecommendForwardUrl() + "?lotteryTypeValue=" + this.getLotteryTypeValue() + "&phaseNo=" + this.getPhaseNo();
	}
	
	public JczqRaceService getJczqRaceService() {
		return jczqRaceService;
	}

	public void setJczqRaceService(JczqRaceService jczqRaceService) {
		this.jczqRaceService = jczqRaceService;
	}
	
	public int getPhaseSelectLotteryTypeValue(){
		return LotteryType.JCZQ_SPF.getValue();
	}
	
	public List<JczqRace> getRaces() {
		return races;
	}

	public void setRaces(List<JczqRace> races) {
		this.races = races;
	}

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}
	
	public DcRaceService getDcRaceService() {
		return dcRaceService;
	}

	public void setDcRaceService(DcRaceService dcRaceService) {
		this.dcRaceService = dcRaceService;
	}

	public String getPhaseNo() {
		return phaseNo;
	}

	public void setPhaseNo(String phaseNo) {
		this.phaseNo = phaseNo;
	}

	public List<DcRace> getDcRaces() {
		return dcRaces;
	}

	public void setDcRaces(List<DcRace> dcRaces) {
		this.dcRaces = dcRaces;
	}
	
	public Long getDcId() {
		return dcId;
	}

	public void setDcId(Long dcId) {
		this.dcId = dcId;
	}

	public String getMatchNum() {
		return matchNum;
	}

	public void setMatchNum(String matchNum) {
		this.matchNum = matchNum;
	}

	public Date getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(Date matchDate) {
		this.matchDate = matchDate;
	}

	public PhaseType getPhaseType() {
		return PhaseType.getItem(LotteryType.DC_SFP);
	}
}
