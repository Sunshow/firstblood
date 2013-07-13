package web.action.include.statics.cms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lehecai.core.lottery.JczqRaceStatus;
import net.sf.json.JSONObject;

import com.lehecai.admin.web.domain.cms.RecommendRace;
import com.lehecai.admin.web.service.lottery.JczqRaceService;
import com.lehecai.core.api.lottery.JczqRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryConstant;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.util.CoreDateUtils;

public class JczqRecommendRaceStaticAction extends AbstractRecommendRaceStaticAction<JczqRace> {

	private static final long serialVersionUID = -6632417854417033812L;

	private JczqRaceService jczqRaceService;
	
	private static Map<LotteryType, String[][]> spTypeMapping = new HashMap<LotteryType, String[][]>();
	private static Map<LotteryType, String> spParamMapping = new HashMap<LotteryType, String>();
	
	static {
		spParamMapping.put(LotteryType.JCZQ_SPF, "sp_spf");
		spParamMapping.put(LotteryType.JCZQ_BF, "sp_bf");
		spParamMapping.put(LotteryType.JCZQ_JQS, "sp_jqs");
		spParamMapping.put(LotteryType.JCZQ_BQC, "sp_bqc");
		
		spTypeMapping.put(LotteryType.JCZQ_SPF, new String[][]{
				{"sf_s", LotteryConstant.JCZQ_SPF_S_VALUE},
				{"sf_p", LotteryConstant.JCZQ_SPF_P_VALUE},
				{"sf_f", LotteryConstant.JCZQ_SPF_F_VALUE},
		});
		spTypeMapping.put(LotteryType.JCZQ_BF, new String[][]{
				{"bf_zs_1_0", LotteryConstant.JCZQ_BF_ZS_1_0_VALUE},
				{"bf_zs_2_0", LotteryConstant.JCZQ_BF_ZS_2_0_VALUE},
				{"bf_zs_2_1", LotteryConstant.JCZQ_BF_ZS_2_1_VALUE},
				{"bf_zs_3_0", LotteryConstant.JCZQ_BF_ZS_3_0_VALUE},
				{"bf_zs_3_1", LotteryConstant.JCZQ_BF_ZS_3_1_VALUE},
				{"bf_zs_3_2", LotteryConstant.JCZQ_BF_ZS_3_2_VALUE},
				{"bf_zs_4_0", LotteryConstant.JCZQ_BF_ZS_4_0_VALUE},
				{"bf_zs_4_1", LotteryConstant.JCZQ_BF_ZS_4_1_VALUE},
				{"bf_zs_4_2", LotteryConstant.JCZQ_BF_ZS_4_2_VALUE},
				{"bf_zs_5_0", LotteryConstant.JCZQ_BF_ZS_5_0_VALUE},
				{"bf_zs_5_1", LotteryConstant.JCZQ_BF_ZS_5_1_VALUE},
				{"bf_zs_5_2", LotteryConstant.JCZQ_BF_ZS_5_2_VALUE},
				{"bf_zs_qt", LotteryConstant.JCZQ_BF_ZS_QT_VALUE},
				{"bf_zp_0_0", LotteryConstant.JCZQ_BF_ZP_0_0_VALUE},
				{"bf_zp_1_1", LotteryConstant.JCZQ_BF_ZP_1_1_VALUE},
				{"bf_zp_2_2", LotteryConstant.JCZQ_BF_ZP_2_2_VALUE},
				{"bf_zp_3_3", LotteryConstant.JCZQ_BF_ZP_3_3_VALUE},
				{"bf_zp_qt", LotteryConstant.JCZQ_BF_ZP_QT_VALUE},
				{"bf_zf_0_1", LotteryConstant.JCZQ_BF_ZF_0_1_VALUE},
				{"bf_zf_0_2", LotteryConstant.JCZQ_BF_ZF_0_2_VALUE},
				{"bf_zf_1_2", LotteryConstant.JCZQ_BF_ZF_1_2_VALUE},
				{"bf_zf_0_3", LotteryConstant.JCZQ_BF_ZF_0_3_VALUE},
				{"bf_zf_1_3", LotteryConstant.JCZQ_BF_ZF_1_3_VALUE},
				{"bf_zf_2_3", LotteryConstant.JCZQ_BF_ZF_2_3_VALUE},
				{"bf_zf_0_4", LotteryConstant.JCZQ_BF_ZF_0_4_VALUE},
				{"bf_zf_1_4", LotteryConstant.JCZQ_BF_ZF_1_4_VALUE},
				{"bf_zf_2_4", LotteryConstant.JCZQ_BF_ZF_2_4_VALUE},
				{"bf_zf_0_5", LotteryConstant.JCZQ_BF_ZF_0_5_VALUE},
				{"bf_zf_1_5", LotteryConstant.JCZQ_BF_ZF_1_5_VALUE},
				{"bf_zf_2_5", LotteryConstant.JCZQ_BF_ZF_2_5_VALUE},
				{"bf_zf_qt", LotteryConstant.JCZQ_BF_ZF_QT_VALUE},
		});
		spTypeMapping.put(LotteryType.JCZQ_JQS, new String[][]{
				{"jqs_0", LotteryConstant.JCZQ_JQS_0_VALUE},                                                                                         
				{"jqs_1", LotteryConstant.JCZQ_JQS_1_VALUE},
				{"jqs_2", LotteryConstant.JCZQ_JQS_2_VALUE},
				{"jqs_3", LotteryConstant.JCZQ_JQS_3_VALUE},
				{"jqs_4", LotteryConstant.JCZQ_JQS_4_VALUE},
				{"jqs_5", LotteryConstant.JCZQ_JQS_5_VALUE},
				{"jqs_6", LotteryConstant.JCZQ_JQS_6_VALUE},
				{"jqs_7", LotteryConstant.JCZQ_JQS_7_VALUE},
		});
		spTypeMapping.put(LotteryType.JCZQ_BQC, new String[][]{
				{"bqc_ss", LotteryConstant.JCZQ_BQC_SS_VALUE},                                                                                       
				{"bqc_sp", LotteryConstant.JCZQ_BQC_SP_VALUE},
				{"bqc_sf", LotteryConstant.JCZQ_BQC_SF_VALUE},
				{"bqc_ps", LotteryConstant.JCZQ_BQC_PS_VALUE},
				{"bqc_pp", LotteryConstant.JCZQ_BQC_PP_VALUE},
				{"bqc_pf", LotteryConstant.JCZQ_BQC_PF_VALUE},
				{"bqc_fs", LotteryConstant.JCZQ_BQC_FS_VALUE},
				{"bqc_fp", LotteryConstant.JCZQ_BQC_FP_VALUE},
				{"bqc_ff", LotteryConstant.JCZQ_BQC_FF_VALUE},
		});
	}
	
	@Override
	protected JczqRace getMatch(RecommendRace recommendRace) {
		try {
			JczqRace jczqRace = jczqRaceService.getRaceByMatchNum(recommendRace.getMatchNum().toString());
			return jczqRace;
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询竞彩足球对阵信息异常，{}", e.getMessage());
			return null;
		}
	}

	@Override
	protected Date getMatchDate(JczqRace match) {
		return match.getMatchDate();
	}

    @Override
    protected String getMatchName(JczqRace match) {
        if (match == null) {
            return null;
        }
        return match.getMatchName();
    }

    @Override
    protected String getMatchNum(JczqRace match) {
        if (match == null) {
            return null;
        }
        return match.getMatchNum();
    }

    @Override
    protected int getPriority(JczqRace match) {
        return match.getPriority();
    }

    @Override
    protected List<JczqRace> getOnsaleMatchList() throws Exception {
        List<JczqRaceStatus> statusList = new ArrayList<JczqRaceStatus>();
        statusList.add(JczqRaceStatus.OPEN);
        return jczqRaceService.getRaceListByDateAndStatus(null, statusList, false);
    }

    @Override
	protected void outputMatchData(JczqRace match, RecommendRace recommendRace, JSONObject json) {
		LotteryType lotteryType = LotteryType.getItem(this.getLotteryTypeValue());

        if (recommendRace != null) {
            if (recommendRace.getDcId() == null) {
                json.put("dc_id", "0");
            } else {
                json.put("dc_id", recommendRace.getDcId().toString());
            }
        } else {
            json.put("home_team", match.getHomeTeam());
            json.put("away_team", match.getAwayTeam());
        }
		
		json.put("match_date", CoreDateUtils.formatDate(match.getMatchDate(),CoreDateUtils.DATETIME));
		json.put("handicap", match.getHandicap());
		json.put("static_draw_status", match.getStaticDrawStatus().getValue());
		json.put("dynamic_draw_status", match.getDynamicDrawStatus().getValue());
		
		if (lotteryType.getValue() == LotteryType.JCZQ_SPF.getValue()) {
			json.put("static_sale_spf_status", match.getStaticSaleSpfStatus().getValue());
			json.put("dynamic_sale_spf_status", match.getDynamicSaleSpfStatus().getValue());
			
		} else if (lotteryType.getValue() == LotteryType.JCZQ_BF.getValue()) {
			json.put("static_sale_bf_status", match.getStaticSaleBfStatus().getValue());
			json.put("dynamic_sale_bf_status", match.getDynamicSaleBfStatus().getValue());
		} else if (lotteryType.getValue() == LotteryType.JCZQ_JQS.getValue()) {
			json.put("static_sale_jqs_status", match.getStaticSaleJqsStatus().getValue());
			json.put("dynamic_sale_jqs_status", match.getDynamicSaleJqsStatus().getValue());
		} else if (lotteryType.getValue() == LotteryType.JCZQ_BQC.getValue()) {
			json.put("static_sale_bqc_status", match.getStaticSaleBqcStatus().getValue());
			json.put("dynamic_sale_bqc_status", match.getDynamicSaleBqcStatus().getValue());
		}

		List<String> matchNums = new ArrayList<String>();
		matchNums.add(match.getMatchNum());
		
		Map<String, Map<String, String>> matchSpMap = null;
		try {
			matchSpMap = jczqRaceService.getJczqCurrentStaticSp(matchNums, lotteryType);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取固定奖金胜负sp异常，{}", e.getMessage());
		}

		String[][] spTypeDefs = spTypeMapping.get(lotteryType);
		
		Map<String, String> spMap = matchSpMap == null ? null : matchSpMap.get(match.getMatchNum());
		
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

	public JczqRaceService getJczqRaceService() {
		return jczqRaceService;
	}

	public void setJczqRaceService(JczqRaceService jczqRaceService) {
		this.jczqRaceService = jczqRaceService;
	}

}
