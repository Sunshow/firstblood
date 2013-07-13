package web.service.impl.alias;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.AliasMatchBean;
import com.lehecai.admin.web.enums.AliasDataProvider;
import com.lehecai.admin.web.service.alias.AliasService;
import com.lehecai.core.api.DataApiUrlConstant;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.CoreHttpUtils;

public class AliasServiceImpl implements AliasService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private String baseUrl;
	
	@Override
	public List<AliasMatchBean> getAliasFromMatchInfo(AliasDataProvider aliasDataProvider,
			List<AliasMatchBean> aliasMatchs) {
		String aliasUrl = baseUrl + DataApiUrlConstant.FOOTBALL_ALIAS;
		//Elements
		JSONObject jsonArrayElements = new JSONObject();
		int i = 0;
		for (AliasMatchBean match : aliasMatchs) {
			JSONObject jsonObjectElements = new JSONObject();
			jsonObjectElements.put("leagueName", match.getLeagueLongName());
			jsonObjectElements.put("homeTeamName", match.getHomeTeamLongName());
			jsonObjectElements.put("awayTeamName", match.getAwayTeamLongName());
			jsonObjectElements.put("matchTime", match.getMatchTime());
			jsonObjectElements.put("matchId", match.getMatchId());
				
			jsonArrayElements.put(String.valueOf(i++), jsonObjectElements);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("dataProviderId", aliasDataProvider.getValue());
		jsonObject.put("match", jsonArrayElements);
		
		try {
			String responseStr = null;
			Map<String, String> params = new HashMap<String, String>();
			params.put("json", jsonObject.toString());
			List<String> result = CoreHttpUtils.postUrl(aliasUrl, params, CharsetConstant.CHARSET_UTF8, 10000);
			
			responseStr = result.get(0);
			
			JSONObject json = null;
			try {
				json = JSONObject.fromObject(responseStr);
			} catch (Exception e) {
				logger.error("API调用返回结果格式不正确", e);
				logger.error("responseStr: {}", responseStr);
			}
			
			if (json == null) {
				return null;
			}
			
			try {
				if(json.getInt("code") != 0) {
					logger.error("API调用返回码不正确");
					return null;
				}
			} catch (Exception e) {
				logger.error("API调用返回码不正确", e);
				return null;
			}
			
			JSONArray array = JSONArray.fromObject(json.getString("data"));
			JSONObject jo = JSONObject.fromObject(array.get(0));
			
			for (int j = 0 ; j < i; j++) {
				try {
					JSONObject jsonTmp = JSONObject.fromObject(jo.getString(j + ""));
					aliasMatchs.get(j).setAwayTeamShortName(JSONObject.fromObject(jsonTmp.getString("awayTeamName")).getString("shortName"));
					aliasMatchs.get(j).setHomeTeamShortName(JSONObject.fromObject(jsonTmp.getString("homeTeamName")).getString("shortName"));
					aliasMatchs.get(j).setLeagueShortName(JSONObject.fromObject(jsonTmp.getString("leagueName")).getString("shortName"));
					aliasMatchs.get(j).setMatchId(jsonTmp.getInt("matchId"));
					
				} catch(Exception e) {
					logger.error("未匹配到第" + j + "场数据");
					continue;
				}
			}
		} catch (Exception e) {
			logger.error("API请求错误，请联系技术人员。" + e.getMessage());
			return null;
		}
		return aliasMatchs;
	}
	
	@Override
	public List<AliasMatchBean> getAliasFromBasketballScheduleInfo(
			AliasDataProvider aliasDataProvider,
			List<AliasMatchBean> aliasMatchs) {
		String basketballUrl = baseUrl + DataApiUrlConstant.BASKETBALL_ALIAS;
		//Elements
		JSONObject jsonArrayElements = new JSONObject();
		int i = 0;
		for (AliasMatchBean match : aliasMatchs) {
			JSONObject jsonObjectElements = new JSONObject();
			jsonObjectElements.put("leagueName", match.getLeagueLongName());
			jsonObjectElements.put("homeTeamName", match.getHomeTeamLongName());
			jsonObjectElements.put("awayTeamName", match.getAwayTeamLongName());
			jsonObjectElements.put("matchTime", match.getMatchTime());
			jsonObjectElements.put("matchId", match.getMatchId());
				
			jsonArrayElements.put(String.valueOf(i++), jsonObjectElements);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("dataProviderId", aliasDataProvider.getValue());
		jsonObject.put("match", jsonArrayElements);
		
		try {
			String responseStr = null;
			Map<String, String> params = new HashMap<String, String>();
			params.put("json", jsonObject.toString());
			List<String> result = CoreHttpUtils.postUrl(basketballUrl, params, CharsetConstant.CHARSET_UTF8, 10000);
			
			responseStr = result.get(0);
			
			JSONObject json = null;
			try {
				json = JSONObject.fromObject(responseStr);
			} catch (Exception e) {
				logger.error("API调用返回结果格式不正确", e);
				logger.error("responseStr: {}", responseStr);
			}
			
			if (json == null) {
				return null;
			}
			
			try {
				if(json.getInt("code") != 0) {
					logger.error("API调用返回码不正确");
					return null;
				}
			} catch (Exception e) {
				logger.error("API调用返回码不正确", e);
				return null;
			}
			
			JSONArray array = JSONArray.fromObject(json.getString("data"));
			JSONObject jo = JSONObject.fromObject(array.get(0));
			for (int j = 0 ; j < i; j++) {
				try {
					if (jo.containsKey(j+"")) {
						JSONObject jsonTmp = JSONObject.fromObject(jo.getString(j + ""));
						aliasMatchs.get(j).setAwayTeamShortName(JSONObject.fromObject(jsonTmp.getString("awayTeamName")).getString("shortName"));
						aliasMatchs.get(j).setHomeTeamShortName(JSONObject.fromObject(jsonTmp.getString("homeTeamName")).getString("shortName"));
						aliasMatchs.get(j).setLeagueShortName(JSONObject.fromObject(jsonTmp.getString("leagueName")).getString("shortName"));
						aliasMatchs.get(j).setMatchId(jsonTmp.getInt("matchId"));
					} else {
						aliasMatchs.get(j).setMatchId(null);
						logger.warn("未匹配到第" + j + "场数据别名数据");
					}
				} catch(Exception e) {
					logger.error("匹配第" + j + "场数据别名时失败");
					continue;
				}
			}
		} catch (Exception e) {
			logger.error("API请求错误，请联系技术人员。" + e.getMessage());
			return null;
		}
		return aliasMatchs;
	}

	@Override
	public List<AliasMatchBean> getFootballMatchTimeByIds(String[] matchIds) {
		String dataUrl = baseUrl + DataApiUrlConstant.FOOTBALL_ANALYSIS;
		//Elements
		String ids = "";
		for (int i=0; i<matchIds.length; i++) {
			if (i > 0) {
				ids += ",";
			}
			ids += matchIds[i];
		}
		dataUrl += "?dtype=8&mid=" + ids;
		List<AliasMatchBean> aliasMatchs = new ArrayList<AliasMatchBean>();
		try {
			String responseStr = null;
			List<String> result = CoreHttpUtils.getUrl(dataUrl, "", CharsetConstant.CHARSET_UTF8, 10000);
			responseStr = result.get(0);
			
			JSONObject json = null;
			try {
				json = JSONObject.fromObject(responseStr);
			} catch (Exception e) {
				logger.error("API调用返回结果格式不正确", e);
				logger.error("responseStr: {}", responseStr);
			}
			if (json == null) {
				return null;
			}
			try {
				if(json.getInt("code") != 0) {
					logger.error("API调用返回码不正确");
					return null;
				}
			} catch (Exception e) {
				logger.error("API调用返回码不正确", e);
				return null;
			}
			JSONArray array = JSONArray.fromObject(json.getString("data"));
			
			for (int i = 0 ; i<array.size(); i++) {
				JSONObject jo = JSONObject.fromObject(array.get(i));
				try {
					AliasMatchBean aliasMatchBean = new AliasMatchBean();
					String matchId = "";
					for (Iterator<?> iterator = jo.keys(); iterator.hasNext();) {
						matchId = iterator.next() + "";
					}
					JSONObject jsonInfo = jo.getJSONObject(matchId);
					JSONObject jsonTmp = jsonInfo.getJSONObject("info");
					aliasMatchBean.setMatchId(Integer.valueOf(matchId));
					aliasMatchBean.setMatchTime(CoreDateUtils.formatDate(CoreDateUtils.parseDate(jsonTmp.getString("mt"), CoreDateUtils.DATETIME), CoreDateUtils.DATETIME));
					aliasMatchs.add(aliasMatchBean);
				} catch(Exception e) {
					logger.error(e.getMessage(), e);
					continue;
				}
			}
		} catch (Exception e) {
			logger.error("API请求错误，请联系技术人员。" + e.getMessage());
			return null;
		}
		return aliasMatchs;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}


}
