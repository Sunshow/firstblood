package web.action.include.statics.basketball;

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
import com.lehecai.admin.web.service.lottery.JclqRaceService;
import com.lehecai.core.api.lottery.JclqRace;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryConstant;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.util.CoreDateUtils;

public class RecommendJclqRaceAction extends BaseAction{

	private static final long serialVersionUID = -8188424058158571081L;
	private Logger logger = LoggerFactory.getLogger(this.getClass()); 

	private JclqRaceService jclqRaceService;
	private Integer size;
	private Integer lotteryTypeValue;
	
	public String handle() {
		logger.info("进入查询推荐竞猜篮球信息");
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject  jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		List<JclqRace> jclqRaces = null;
		int onSaleCount = 0;
		
		if (lotteryTypeValue == null) {
			lotteryTypeValue = LotteryType.JCLQ_SF.getValue();
		}
		
		try {
			jclqRaces = jclqRaceService.recommendJclqRace(size);
			logger.info("获得推荐比赛size:{}", jclqRaces.size());
			onSaleCount = jclqRaceService.getJclqRaceSaleCount();
		} catch (Exception e) {
			logger.error("API查询推荐竞猜篮球信息失败！{}", e.getMessage());
		}
		
		if (jclqRaces == null || jclqRaces.size() == 0) {
			jsonObject.put("data", jsonArray);
			writeRs(response, jsonObject);
			return null;
		}
		List<String> matchNums = new ArrayList<String>();
		
		for (JclqRace jclqRace : jclqRaces) {
			matchNums.add(jclqRace.getMatchNum());
		}
		
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		Map<String, Map<String, String>> map = null;
		try {
			map = jclqRaceService.getJclqCurrentStaticSp(matchNums, lotteryType);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
		}
		jsonArray = toJclqRaceJsonArray(jclqRaces, map, lotteryType);
		logger.info("得到最终jsonArray:{}", jsonArray);
			
		jsonObject.put("data", jsonArray);
		jsonObject.put("onsale_count", onSaleCount);
		//控制台输出结果查看
		//testData(jsonObject);
		writeRs(response, jsonObject);
		return null;
	}
	
	//将单场比赛转换问json格式
	private JSONArray toJclqRaceJsonArray(List<JclqRace> jclqRaces, Map<String, Map<String, String>> map, LotteryType lotteryType) {
		JSONArray array = new JSONArray();
		if (jclqRaces != null && !jclqRaces.isEmpty()) {
			for (JclqRace jclqRace : jclqRaces) {
				JSONObject object = new JSONObject();
				object.put("match_num", jclqRace.getMatchNum());
				object.put("official_num", jclqRace.getOfficialNum());
				object.put("official_week_day", jclqRace.getOfficialWeekDay());
				object.put("match_name", jclqRace.getMatchName());
				object.put("home_team", jclqRace.getHomeTeam());
				object.put("away_team", jclqRace.getAwayTeam());
				try {
					object.put("end_sale_time", CoreDateUtils.formatDateTime(jclqRace.getEndSaleTime()));
				} catch (Exception e) {
					object.put("end_sale_time", "");
					logger.error(e.getMessage(), e);
				}
				try {
					object.put("match_date", CoreDateUtils.formatDateTime(jclqRace.getMatchDate()));
				} catch (Exception e) {
					object.put("match_date", "");
					logger.error(e.getMessage(), e);
				}
				try {
					object.put("official_date", CoreDateUtils.formatDateTime(jclqRace.getOfficialDate()));
				} catch (Exception e) {
					object.put("official_date", "");
					logger.error(e.getMessage(), e);
				}
				object.put("dynamic_handicap", jclqRace.getDynamicHandicap());
				object.put("static_handicap", jclqRace.getStaticHandicap());
				object.put("dynamic_preset_score", jclqRace.getDynamicPresetScore());
				object.put("static_preset_score", jclqRace.getStaticPresetScore());
				object.put("final_score", jclqRace.getFinalScore());
				object.put("fx_id", jclqRace.getFxId());
				object.put("priority", jclqRace.getPriority());
				
				Map<String, String> spMap = null;
				if (map != null) {
					spMap = map.get(jclqRace.getMatchNum());
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
				logger.info("竞猜篮球推荐赛程：");
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

	public JclqRaceService getJclqRaceService() {
		return jclqRaceService;
	}

	public void setJclqRaceService(JclqRaceService jclqRaceService) {
		this.jclqRaceService = jclqRaceService;
	}

	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}

	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}
}
