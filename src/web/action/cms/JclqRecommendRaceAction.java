package web.action.cms;

import com.lehecai.admin.web.domain.cms.RecommendRace;
import com.lehecai.admin.web.service.lottery.JclqRaceService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.core.api.lottery.JclqRace;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.util.CoreDateUtils;

import java.util.*;

/**
 * 竞彩篮球推荐赛程管理
 * @author yanweijie
 *
 */
public class JclqRecommendRaceAction extends AbstractRecommendRaceAction {

	private static final long serialVersionUID = 3544458996563614450L;
	private static final String PHASE_FORMAT_DATE = "yyyyMMdd";
	
	private JclqRaceService jclqRaceService;
	private PhaseService phaseService;
	
	private List<JclqRace> races;
	
	private String phaseNo;
	
	static {
		lotteryActionMap.put(LotteryType.JCLQ_SF, "jclqSf%sRecommendRace");
		lotteryActionMap.put(LotteryType.JCLQ_RFSF, "jclqRfsf%sRecommendRace");
		lotteryActionMap.put(LotteryType.JCLQ_SFC, "jclqSfc%sRecommendRace");
		lotteryActionMap.put(LotteryType.JCLQ_DXF, "jclqDxf%sRecommendRace");
	}

	@Override
	protected void initAvailableRaceList() throws Exception {
        /*
		List<JclqRaceStatus> raceStatusList = new ArrayList<JclqRaceStatus>();
		raceStatusList.add(JclqRaceStatus.OPEN);
		*/
		
        if (this.getPhaseNo() == null) {
            List<Phase> onsalePhaseList = phaseService.findOnSalePhases(PhaseType.getItem(LotteryType.JCLQ_SF), null);
            if (onsalePhaseList != null && !onsalePhaseList.isEmpty()) {
                this.setPhaseNo(onsalePhaseList.get(0).getPhase());
            } else {
                //throw new RuntimeException("未找到可销售彩期");
            }
        }

        if (this.getPhaseNo() != null) {
            List<JclqRace> allJclqRaces = jclqRaceService.getRaceListByDateAndStatus(this.getPhaseNo(), null,
                    this.getPhaseNo().equals(CoreDateUtils.formatDate(new Date(), PHASE_FORMAT_DATE)));

            if (allJclqRaces != null && !allJclqRaces.isEmpty()) {	//去除已推荐赛程
                if (this.getRecommendRaces() != null) {
                    // 已推荐的场次
                    Set<String> matchNumSet = new HashSet<String>();
                    for (RecommendRace recommendRace : this.getRecommendRaces()) {
                        matchNumSet.add(recommendRace.getMatchNum().toString());
                    }

                    races = new ArrayList<JclqRace>();
                    for (JclqRace jclqRace : allJclqRaces) {
                        if (matchNumSet.contains(jclqRace.getMatchNum())) {
                            continue;
                        }
                        races.add(jclqRace);
                    }
                } else {
                    races = allJclqRaces;
                }
            } else {
                logger.info("{}没有竞彩足球对阵信息", this.getPhaseNo());
            }
        }
	}
	
	@Override
	protected String getRecommendForwardUrl() {
		return super.getRecommendForwardUrl() + "?lotteryTypeValue=" + this.getLotteryTypeValue() + "&phaseNo=" + this.getPhaseNo();
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
	public String getPhaseNo() {
		return phaseNo;
	}

	public void setPhaseNo(String phaseNo) {
		this.phaseNo = phaseNo;
	}

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}

    public int getPhaseSelectLotteryTypeValue(){
        return LotteryType.JCLQ_SF.getValue();
    }
}
