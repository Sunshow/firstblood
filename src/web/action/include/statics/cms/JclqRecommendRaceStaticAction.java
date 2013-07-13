package web.action.include.statics.cms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.lehecai.admin.web.domain.cms.BasketballAnalysisData;
import com.lehecai.admin.web.domain.cms.RecommendRace;
import com.lehecai.admin.web.service.lottery.JclqRaceService;
import com.lehecai.core.api.lottery.JclqRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.JclqRaceStatus;
import com.lehecai.core.lottery.LotteryConstant;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.util.CoreDateUtils;

public class JclqRecommendRaceStaticAction extends AbstractRecommendRaceStaticAction<JclqRace> {

	private static final long serialVersionUID = 7312351506740374722L;

	private JclqRaceService jclqRaceService;


	private static Map<LotteryType, String[][]> spTypeMapping = new HashMap<LotteryType, String[][]>();
	private static Map<LotteryType, String> spParamMapping = new HashMap<LotteryType, String>();
	
	static {
		spParamMapping.put(LotteryType.JCLQ_SF, "sp_sf");
		spParamMapping.put(LotteryType.JCLQ_RFSF, "sp_rfsf");
		spParamMapping.put(LotteryType.JCLQ_SFC, "sp_sfc");
		spParamMapping.put(LotteryType.JCLQ_DXF, "sp_dxf");
		
		spTypeMapping.put(LotteryType.JCLQ_SF, new String[][]{
				{"sf_s", LotteryConstant.JCLQ_SF_S_VALUE},                                                                                           
				{"sf_f", LotteryConstant.JCLQ_SF_F_VALUE},
		});
		spTypeMapping.put(LotteryType.JCLQ_RFSF, new String[][]{
				{"rfsf_s", LotteryConstant.JCLQ_RFSF_S_VALUE},                                                                                       
				{"rfsf_f", LotteryConstant.JCLQ_RFSF_F_VALUE},
				{"rfsf_handicap", LotteryConstant.JCLQ_RFSF_HANDICAP},
		});
		spTypeMapping.put(LotteryType.JCLQ_SFC, new String[][]{
				{"sfc_h_1_5", LotteryConstant.JCLQ_SFC_H_1_5_VALUE},                                                                                 
				{"sfc_h_6_10", LotteryConstant.JCLQ_SFC_H_6_10_VALUE},
				{"sfc_h_11_15", LotteryConstant.JCLQ_SFC_H_11_15_VALUE},
				{"sfc_h_16_20", LotteryConstant.JCLQ_SFC_H_16_20_VALUE},
				{"sfc_h_21_25", LotteryConstant.JCLQ_SFC_H_21_25_VALUE},
				{"sfc_h_26_plus", LotteryConstant.JCLQ_SFC_H_26_PLUS_VALUE},
				{"sfc_a_1_5", LotteryConstant.JCLQ_SFC_A_1_5_VALUE},
				{"sfc_a_6_10", LotteryConstant.JCLQ_SFC_A_6_10_VALUE},
				{"sfc_a_11_15", LotteryConstant.JCLQ_SFC_A_11_15_VALUE},
				{"sfc_a_16_20", LotteryConstant.JCLQ_SFC_A_16_20_VALUE},
				{"sfc_a_21_25", LotteryConstant.JCLQ_SFC_A_21_25_VALUE},
				{"sfc_a_26_plus", LotteryConstant.JCLQ_SFC_A_26_PLUS_VALUE},
		});
		spTypeMapping.put(LotteryType.JCLQ_DXF, new String[][]{
				{"dxf_SMALL", LotteryConstant.JCLQ_DXF_SMALL},                                                                                       
				{"dxf_LARGE", LotteryConstant.JCLQ_DXF_LARGE},
				{"dxf_PRESETSCORE", LotteryConstant.JCLQ_DXF_PRESETSCORE},
		});
	}
	

	@Override
	protected JclqRace getMatch(RecommendRace recommendRace) {
		try {
			JclqRace jclqRace = jclqRaceService.getRaceByMatchNum(recommendRace.getMatchNum().toString());
			return jclqRace;
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询竞彩篮球对阵信息异常，{}", e.getMessage());
			return null;
		}
	}

	@Override
	protected Date getMatchDate(JclqRace match) {
		return match.getMatchDate();
	}

    @Override
    protected String getMatchName(JclqRace match) {
        if (match == null) {
            return null;
        }
        return match.getMatchName();
    }

    @Override
    protected String getMatchNum(JclqRace match) {
        if (match == null) {
            return null;
        }
        return match.getMatchNum();
    }

    @Override
    protected int getPriority(JclqRace match) {
        return match.getPriority();
    }

    @Override
    protected List<JclqRace> getOnsaleMatchList() throws Exception {
        List<JclqRaceStatus> statusList = new ArrayList<JclqRaceStatus>();
        statusList.add(JclqRaceStatus.OPEN);
        return jclqRaceService.getRaceListByDateAndStatus(null, statusList, false);
    }

    @Override
	protected void outputMatchData(JclqRace match, RecommendRace recommendRace, JSONObject json) {
		LotteryType lotteryType = LotteryType.getItem(this.getLotteryTypeValue());

        if (recommendRace == null) {
            json.put("home_team", match.getHomeTeam());
            json.put("away_team", match.getAwayTeam());
        }
		
		json.put("official_weekday", match.getOfficialWeekDay());
		json.put("official_num", match.getOfficialNum());
		json.put("match_date", CoreDateUtils.formatDate(match.getMatchDate(),CoreDateUtils.DATETIME));
		json.put("static_draw_status", match.getStaticDrawStatus().getValue());
		json.put("dynamic_draw_status", match.getDynamicDrawStatus().getValue());
		json.put("fx_id", match.getFxId());
		
		if (lotteryType.getValue() == LotteryType.JCLQ_SF.getValue()) {
			json.put("static_sale_sf_status", match.getStaticSaleSfStatus().getValue());
			json.put("dynamic_sale_sf_status", match.getDynamicSaleSfStatus().getValue());
		} else if (lotteryType.getValue() == LotteryType.JCLQ_RFSF.getValue()) {
			json.put("static_sale_rfsf_status", match.getStaticSaleRfsfStatus().getValue());
			json.put("dynamic_sale_rfsf_status", match.getDynamicSaleRfsfStatus().getValue());
		} else if (lotteryType.getValue() == LotteryType.JCLQ_SFC.getValue()) {
			json.put("static_sale_sfc_status", match.getStaticSaleSfcStatus().getValue());
			json.put("dynamic_sale_sfc_status", match.getDynamicSaleSfcStatus().getValue());
		} else if (lotteryType.getValue() == LotteryType.JCLQ_DXF.getValue()) {
			json.put("static_sale_dxf_status", match.getStaticSaleDxfStatus().getValue());
			json.put("dynamic_sale_dxf_status", match.getDynamicSaleDxfStatus().getValue());
		}

		List<String> matchNums = new ArrayList<String>();
		matchNums.add(match.getMatchNum());
		
		Map<String, Map<String, String>> matchSpMap = null;
		try {
			matchSpMap = jclqRaceService.getJclqCurrentStaticSp(matchNums, lotteryType);
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
	
	public JclqRaceService getJclqRaceService() {
		return jclqRaceService;
	}

	public void setJclqRaceService(JclqRaceService jclqRaceService) {
		this.jclqRaceService = jclqRaceService;
	}

	@Override
	protected void getExtraMatchInfo(JSONArray jsonArray) {
		if (jsonArray != null && jsonArray.size() > 0) {
			String fixIds = "";
			int num = 0;
			for (int i=0;i<jsonArray.size();i++) {
				JSONObject jsonObject = (JSONObject)jsonArray.get(i);
				String fixId = jsonObject.getString("fx_id");
				if (!StringUtils.isEmpty(fixId) && Integer.valueOf(fixId) > 0) {
					if (num > 0) {
						fixIds += ",";
					}
					fixIds += fixId;
					num++;
				}
			}
			Map<Long, BasketballAnalysisData> dataMap = null;
			if (!StringUtils.isEmpty(fixIds)) {
				try {
					dataMap = this.getRecommendRaceService().getBasketballAnalysisData(fixIds);
					for (Object obj : jsonArray) {
						JSONObject jsonObject = (JSONObject)obj;
						String fixId = jsonObject.getString("fx_id");

						JSONObject aeObj = new JSONObject();
						aeObj.put("ae_hl", "-");
						aeObj.put("ae_al", "-");
					
						if (dataMap != null) {
							BasketballAnalysisData data = dataMap.get(Long.valueOf(fixId));
							if (data != null) {
								if (data.getAeHl() != null && !data.getAeHl().equals("null")) {
									aeObj.put("ae_hl", data.getAeHl());
								}
								if (data.getAeAl() != null && !data.getAeAl().equals("null")) {
									aeObj.put("ae_al", data.getAeAl());
								}
							}
						}
						jsonObject.put("ae", aeObj.toString());
					}
				} catch (ApiRemoteCallFailedException e) {
					logger.error("批量获取平均欧赔异常，批量分析id={}，原因{}", fixIds, e.getMessage());
				}
			} else {
				for (Object obj : jsonArray) {
					JSONObject jsonObject = (JSONObject)obj;
					JSONObject aeObj = new JSONObject();
					aeObj.put("ae_hl", "-");
					aeObj.put("ae_al", "-");
					jsonObject.put("ae", aeObj.toString());
				}
			}
			
		}
	}


}
