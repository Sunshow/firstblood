package web.action.include.statics.cms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.lottery.DcRaceStatus;
import com.lehecai.core.lottery.PhaseType;
import net.sf.json.JSONObject;

import com.lehecai.admin.web.domain.cms.RecommendRace;
import com.lehecai.admin.web.service.lottery.SfggRaceService;
import com.lehecai.core.api.lottery.SfggRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryConstant;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.util.CoreDateUtils;

public class SfggRecommendRaceStaticAction extends AbstractRecommendRaceStaticAction<SfggRace> {

	private static final long serialVersionUID = 649432108837747060L;

	private SfggRaceService sfggRaceService;

    private PhaseService phaseService;
	
	private static Map<LotteryType, String[][]> spTypeMapping = new HashMap<LotteryType, String[][]>();
	private static Map<LotteryType, String> spParamMapping = new HashMap<LotteryType, String>();
	
	static {
		spParamMapping.put(LotteryType.DC_SFGG, "sp_sfgg");
		
		spTypeMapping.put(LotteryType.DC_SFGG, new String[][]{
				{"sfgg_s", LotteryConstant.DC_SFGG_S_VALUE},
				{"sfgg_f", LotteryConstant.DC_SFGG_F_VALUE},
		});
	}

    @Override
    protected String getMatchName(SfggRace match) {
        if (match == null) {
            return null;
        }
        return match.getMatchName();
    }

    @Override
    protected String getMatchNum(SfggRace match) {
        if (match == null) {
            return null;
        }
        return String.valueOf(match.getMatchNum());
    }

    @Override
    protected int getPriority(SfggRace match) {
        return match.getPriority();
    }

    @Override
    protected List<SfggRace> getOnsaleMatchList() throws Exception {
        Phase phase = phaseService.getCurrentPhase(PhaseType.getItem(LotteryType.DC_SFGG));

        if (phase != null) {
            List<String> statusList = new ArrayList<String>();
            statusList.add(String.valueOf(DcRaceStatus.CAN_BUY.getValue()));
            return sfggRaceService.findSfggRaceByStatus(statusList, phase.getPhase());
        }

        return null;
    }

    @Override
	protected SfggRace getMatch(RecommendRace recommendRace) {
		try {
			SfggRace race = sfggRaceService.getSfggRaceByMatchNum(recommendRace.getPhase(), recommendRace.getMatchNum());
			return race;
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询胜负过关对阵信息异常，{}", e.getMessage());
			return null;
		}
	}

	@Override
	protected Date getMatchDate(SfggRace match) {
		return match.getMatchDate();
	}

	@Override
	protected void outputMatchData(SfggRace match, RecommendRace recommendRace, JSONObject json) {
		LotteryType lotteryType = LotteryType.getItem(this.getLotteryTypeValue());

        if (recommendRace == null) {
            json.put("home_team", match.getHomeTeam());
            json.put("away_team", match.getAwayTeam());
        }
		
		json.put("phase", match.getPhase());
		json.put("match_date", CoreDateUtils.formatDate(match.getMatchDate(), CoreDateUtils.DATETIME));
		json.put("handicap", match.getHandicap());
		
		List<String> matchIdList = new ArrayList<String>();
		matchIdList.add(match.getId());
		
		Map<String, Map<String, String>> matchSpMap = null;
		try {
			matchSpMap = sfggRaceService.getCurrentInstantSP(matchIdList, lotteryType);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取固定奖金胜负sp异常，{}", e.getMessage());
		}

		String[][] spTypeDefs = spTypeMapping.get(lotteryType);
		
		Map<String, String> spMap = matchSpMap == null ? null : matchSpMap.get(String.valueOf(match.getMatchNum()));
		
		JSONObject spJsonObject = new JSONObject();
		for (String[] def : spTypeDefs) {
			if (spMap == null || !spMap.containsKey(def[1])) {
				spJsonObject.put(def[0], "-");
			} else {
				spJsonObject.put(def[0], spMap.get(def[1]));
			}
		}
		json.put(spParamMapping.get(lotteryType), spJsonObject);
	}

	public SfggRaceService getSfggRaceService() {
		return sfggRaceService;
	}

	public void setSfggRaceService(SfggRaceService sfggRaceService) {
		this.sfggRaceService = sfggRaceService;
	}

    public PhaseService getPhaseService() {
        return phaseService;
    }

    public void setPhaseService(PhaseService phaseService) {
        this.phaseService = phaseService;
    }
}
