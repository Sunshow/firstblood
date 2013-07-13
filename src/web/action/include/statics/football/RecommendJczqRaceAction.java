package web.action.include.statics.football;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.lottery.JczqRaceService;
import com.lehecai.core.api.lottery.JczqRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryConstant;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.util.CoreDateUtils;

public class RecommendJczqRaceAction extends BaseAction{

	private static final long serialVersionUID = -863661807480561662L;
	private Logger logger = LoggerFactory.getLogger(this.getClass()); 

	private JczqRaceService jczqRaceService;

	private Integer size;
	private Integer lotteryTypeValue;
	
	public String handle() {
		logger.info("开始查询推荐竞猜足球信息");
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject  jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		List<JczqRace> jczqRaces = null;
		int onSaleCount = 0;
		
		if (lotteryTypeValue == null) {
			lotteryTypeValue = LotteryType.JCZQ_SPF.getValue();
		}
		
		try {
			jczqRaces = jczqRaceService.recommendJczqRace(size);
			logger.info("获得推荐比赛size:{}", jczqRaces.size());
			onSaleCount = jczqRaceService.getJczqRaceSaleCount();
		} catch (Exception e) {
			logger.error("API查询推荐竞猜足球信息失败！{}", e.getMessage());
		}
		
		if (jczqRaces == null || jczqRaces.size() == 0) {
			jsonObject.put("data", jsonArray);
			writeRs(response, jsonObject);
			return null;
		}
		List<String> matchNums = new ArrayList<String>();
		
		for (JczqRace jczqRace : jczqRaces) {
			matchNums.add(jczqRace.getMatchNum());
		}
		
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		Map<String, Map<String, String>> map = null;
		try {
			map = jczqRaceService.getJczqCurrentStaticSp(matchNums, lotteryType);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		jsonArray = toJczqRaceJsonArray(jczqRaces, map, lotteryType);
		logger.info("得到最终jsonArray:{}", jsonArray);
			
		jsonObject.put("data", jsonArray);
		jsonObject.put("onsale_count", onSaleCount);
		//控制台输出结果查看
		//testData(jsonObject);
		writeRs(response, jsonObject);
		return null;
	}
	
	//将单场比赛转换问json格式
	private JSONArray toJczqRaceJsonArray(List<JczqRace> jczqRaces, Map<String, Map<String, String>> map, LotteryType lotteryType) {
		JSONArray array = new JSONArray();
		if (jczqRaces != null && !jczqRaces.isEmpty()) {
			for (JczqRace jczqRace : jczqRaces) {
				JSONObject object = new JSONObject();
				object.put("match_num", jczqRace.getMatchNum());
				object.put("official_num", jczqRace.getOfficialNum());
				object.put("official_week_day", jczqRace.getOfficialWeekDay());
				object.put("match_name", jczqRace.getMatchName());
				object.put("home_team", jczqRace.getHomeTeam());
				object.put("away_team", jczqRace.getAwayTeam());
				try {
					object.put("end_sale_time", CoreDateUtils.formatDateTime(jczqRace.getEndSaleTime()));
				} catch (Exception e) {
					object.put("end_sale_time", "");
					logger.error(e.getMessage(), e);
				}
				try {
					object.put("match_date", CoreDateUtils.formatDateTime(jczqRace.getMatchDate()));
				} catch (Exception e) {
					object.put("match_date", "");
					logger.error(e.getMessage(), e);
				}
				try {
					object.put("official_date", CoreDateUtils.formatDateTime(jczqRace.getOfficialDate()));
				} catch (Exception e) {
					object.put("official_date", "");
					logger.error(e.getMessage(), e);
				}
				object.put("handicap", jczqRace.getHandicap());
//				object.put("static_handicap", jczqRace.getStaticHandicap());
//				object.put("dynamic_preset_score", jczqRace.getDynamicPresetScore());
//				object.put("static_preset_score", jczqRace.getStaticPresetScore());
				object.put("first_half", jczqRace.getFirstHalf());
				
				object.put("final_score", jczqRace.getFinalScore());
				object.put("fx_id", jczqRace.getFxId());
				object.put("priority", jczqRace.getPriority());
				
				Map<String, String> spMap = null;
				if (map != null) {
					spMap = map.get(jczqRace.getMatchNum());
				}
				String sp = "--";
				List<String> _list = LotteryConstant.getLotteryConstantList(lotteryType);
				for (String s : _list) {
					if (spMap != null) {
						sp = (String)spMap.get(s);
					}
					object.put("sp_" + s, sp);
				}
				array.add(object);
			}
		}
		
		return array;
	}

	//测试输出
	@SuppressWarnings("unused")
	private void testData(JSONObject jsonObject) {
		try {
			if (jsonObject != null) {
				logger.info("竞猜足球推荐赛程：");
				JSONArray jsonArray = jsonObject.getJSONArray("data");
				if (jsonArray != null && !jsonArray.isEmpty()) {
					for (Object object : jsonArray) {
						JSONObject tempObject = (JSONObject) object;
						logger.info("matchDate:{}，priority:{}，match_num:{}，{} VS {}", new Object[]{tempObject.getString("match_date"), tempObject.get("priority"), tempObject.get("match_num"), tempObject.get("home_team"), tempObject.get("away_team")});
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}

	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}
	
	public JczqRaceService getJczqRaceService() {
		return jczqRaceService;
	}

	public void setJczqRaceService(JczqRaceService jczqRaceService) {
		this.jczqRaceService = jczqRaceService;
	}
}
