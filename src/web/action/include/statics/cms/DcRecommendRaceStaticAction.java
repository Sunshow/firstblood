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
import com.lehecai.admin.web.service.lottery.DcRaceService;
import com.lehecai.core.api.lottery.DcRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryConstant;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.util.CoreDateUtils;

public class DcRecommendRaceStaticAction extends AbstractRecommendRaceStaticAction<DcRace> {

	private static final long serialVersionUID = -3696553647404732797L;
	
	private DcRaceService dcRaceService;

    private PhaseService phaseService;
	
	private static Map<LotteryType, String[][]> spTypeMapping = new HashMap<LotteryType, String[][]>();
	private static Map<LotteryType, String> spParamMapping = new HashMap<LotteryType, String>();
	
	static {
		spParamMapping.put(LotteryType.DC_SFP, "sp_spf");
		spParamMapping.put(LotteryType.DC_SXDS, "sp_sxds");
		spParamMapping.put(LotteryType.DC_JQS, "sp_jqs");
		spParamMapping.put(LotteryType.DC_BF, "sp_bf");
		spParamMapping.put(LotteryType.DC_BCSFP, "sp_bqc");
		
		spTypeMapping.put(LotteryType.DC_SFP, new String[][]{
				{"spf_s", LotteryConstant.DC_SFP_S_VALUE},
				{"spf_p", LotteryConstant.DC_SFP_P_VALUE},
				{"spf_f", LotteryConstant.DC_SFP_F_VALUE},
		});
		spTypeMapping.put(LotteryType.DC_SXDS, new String[][]{
				{"sxds_sd", LotteryConstant.DC_SXDS_SD_VALUE},
				{"sxds_ss", LotteryConstant.DC_SXDS_SS_VALUE},
				{"sxds_xd", LotteryConstant.DC_SXDS_XD_VALUE},
				{"sxds_xs", LotteryConstant.DC_SXDS_XS_VALUE},
		});
		spTypeMapping.put(LotteryType.DC_BF, new String[][]{
				{"bf_zs_1_0", LotteryConstant.DC_BF_S_10_VALUE},
				{"bf_zs_2_0", LotteryConstant.DC_BF_S_20_VALUE},
				{"bf_zs_2_1", LotteryConstant.DC_BF_S_21_VALUE},
				{"bf_zs_3_0", LotteryConstant.DC_BF_S_30_VALUE},
				{"bf_zs_3_1", LotteryConstant.DC_BF_S_31_VALUE},
				{"bf_zs_3_2", LotteryConstant.DC_BF_S_32_VALUE},
				{"bf_zs_4_0", LotteryConstant.DC_BF_S_40_VALUE},
				{"bf_zs_4_1", LotteryConstant.DC_BF_S_41_VALUE},
				{"bf_zs_4_2", LotteryConstant.DC_BF_S_42_VALUE},
				{"bf_zs_qt", LotteryConstant.DC_BF_S_Ohter_VALUE},
				{"bf_zp_0_0", LotteryConstant.DC_BF_P_0_VALUE},
				{"bf_zp_1_1", LotteryConstant.DC_BF_P_1_VALUE},
				{"bf_zp_2_2", LotteryConstant.DC_BF_P_2_VALUE},
				{"bf_zp_3_3", LotteryConstant.DC_BF_P_3_VALUE},
				{"bf_zp_qt", LotteryConstant.DC_BF_P_Other_VALUE},
				{"bf_zf_0_1", LotteryConstant.DC_BF_F_01_VALUE},
				{"bf_zf_0_2", LotteryConstant.DC_BF_F_02_VALUE},
				{"bf_zf_1_2", LotteryConstant.DC_BF_F_12_VALUE},
				{"bf_zf_0_3", LotteryConstant.DC_BF_F_03_VALUE},
				{"bf_zf_1_3", LotteryConstant.DC_BF_F_13_VALUE},
				{"bf_zf_2_3", LotteryConstant.DC_BF_F_23_VALUE},
				{"bf_zf_0_4", LotteryConstant.DC_BF_F_04_VALUE},
				{"bf_zf_1_4", LotteryConstant.DC_BF_F_14_VALUE},
				{"bf_zf_2_4", LotteryConstant.DC_BF_F_24_VALUE},
				{"bf_zf_qt", LotteryConstant.DC_BF_F_Other_VALUE},
		});
		spTypeMapping.put(LotteryType.DC_JQS, new String[][]{
				{"jqs_0", LotteryConstant.DC_JQX_0_VALUE},                                                                                         
				{"jqs_1", LotteryConstant.DC_JQX_1_VALUE},
				{"jqs_2", LotteryConstant.DC_JQX_2_VALUE},
				{"jqs_3", LotteryConstant.DC_JQX_3_VALUE},
				{"jqs_4", LotteryConstant.DC_JQX_4_VALUE},
				{"jqs_5", LotteryConstant.DC_JQX_5_VALUE},
				{"jqs_6", LotteryConstant.DC_JQX_6_VALUE},
				{"jqs_7", LotteryConstant.DC_JQX_7_VALUE},
		});
		spTypeMapping.put(LotteryType.DC_BCSFP, new String[][]{
				{"bqc_ss", LotteryConstant.DC_BCSFP_SS_VALUE},                                                                                       
				{"bqc_sp", LotteryConstant.DC_BCSFP_SP_VALUE},
				{"bqc_sf", LotteryConstant.DC_BCSFP_SF_VALUE},
				{"bqc_ps", LotteryConstant.DC_BCSFP_PS_VALUE},
				{"bqc_pp", LotteryConstant.DC_BCSFP_PP_VALUE},
				{"bqc_pf", LotteryConstant.DC_BCSFP_PF_VALUE},
				{"bqc_fs", LotteryConstant.DC_BCSFP_FS_VALUE},
				{"bqc_fp", LotteryConstant.DC_BCSFP_FP_VALUE},
				{"bqc_ff", LotteryConstant.DC_BCSFP_FF_VALUE},
		});
	}

	@Override
	protected DcRace getMatch(RecommendRace recommendRace) {
		try {
			DcRace dcRace = dcRaceService.getDcRaceByMatchNum(recommendRace.getPhase(), recommendRace.getMatchNum());
			return dcRace;
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询单场对阵信息异常，{}", e.getMessage());
			return null;
		}
	}

	@Override
	protected Date getMatchDate(DcRace match) {
		return match.getMatchDate();
	}

    @Override
    protected String getMatchName(DcRace match) {
        if (match == null) {
            return null;
        }
        return match.getMatchName();
    }

    @Override
    protected String getMatchNum(DcRace match) {
        if (match == null) {
            return null;
        }
        return String.valueOf(match.getMatchNum());
    }

    @Override
    protected int getPriority(DcRace match) {
        return match.getPriority();
    }

    @Override
    protected List<DcRace> getOnsaleMatchList() throws Exception {
        Phase phase = phaseService.getCurrentPhase(PhaseType.getItem(LotteryType.DC_SFP));

        if (phase != null) {
            List<String> statusList = new ArrayList<String>();
            statusList.add(String.valueOf(DcRaceStatus.CAN_BUY.getValue()));
            return dcRaceService.findDcRaceByStatus(statusList, phase.getPhase());
        }

        return null;
    }

    @Override
	protected void outputMatchData(DcRace match, RecommendRace recommendRace, JSONObject json) {
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
			matchSpMap = dcRaceService.getDcCurrentInstantSP(matchIdList, lotteryType);
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

	public DcRaceService getDcRaceService() {
		return dcRaceService;
	}

	public void setDcRaceService(DcRaceService dcRaceService) {
		this.dcRaceService = dcRaceService;
	}

    public PhaseService getPhaseService() {
        return phaseService;
    }

    public void setPhaseService(PhaseService phaseService) {
        this.phaseService = phaseService;
    }
}
