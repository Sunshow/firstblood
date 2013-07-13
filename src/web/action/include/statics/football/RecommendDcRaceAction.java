package web.action.include.statics.football;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.lottery.DcRaceService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.core.api.lottery.DcRace;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreDateUtils;
import com.lehecai.core.util.CoreFileUtils;

public class RecommendDcRaceAction extends BaseAction{

	private static final long serialVersionUID = -8188424058158571081L;
	private Logger logger = LoggerFactory.getLogger(this.getClass()); 

	private PhaseService phaseService;
	private DcRaceService dcRaceService;
	private Integer size;
	
	private String staticDir;
	private String rootDir;
	
	public String handle() {
		logger.info("开始查询单场推荐信息");
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject  jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		Phase phase = null;
		int onSaleCount = 0;
		List<DcRace> dcRaces = null;
		
		try {
			phase = phaseService.getCurrentPhase(PhaseType.getItem(LotteryType.DC_SFP.getValue()));
			logger.info("得到当前期:{}", phase.getPhase());
			//加载即时sp值
			
			if (phase != null) {
				dcRaces = dcRaceService.getRecommendDcRace(phase.getPhase(), size);
				logger.info("获得推荐比赛size:{}", dcRaces.size());
				logger.info("获得推荐比赛:{}", dcRaces);
				
				onSaleCount = dcRaceService.getDcRaceSaleCount(phase.getPhase());
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询推荐单场信息失败！{}", e.getMessage());
		}
		
		String sp = null;
		String spStr = rootDir + (staticDir.endsWith("/") ? staticDir : staticDir + "/") + "nocache/dcsp/" + phase.getPhase() + "_30.json";
		try {
			logger.info("文件路径:{}", spStr);
			sp = CoreFileUtils.readFile(spStr, CharsetConstant.CHARSET_UTF8);
			logger.info("读取即时sp:{}", sp);
			if (sp == null || "".equals(sp.trim())){
				logger.warn("未从文件中读取到SP:{}", spStr);
			}
		} catch (Exception e) {
			logger.error("未读取到文件:{}", spStr);
			logger.error(e.getMessage(), e);
		}
		JSONObject spJson = null;
		logger.info("将sp转换为json对象:{}", spStr);
		if (sp != null && !"".equals(sp.trim())) {
			spJson = JSONObject.fromObject(sp);
		}
		logger.info("转换成jsonObject:{}", spJson);
		if (phase != null) {
			jsonObject.put("phase_no", phase.getPhase());
		}
		jsonObject.put("onsale_count", onSaleCount);
		JSONObject obj = null;
		if (spJson != null && !spJson.isNullObject()) {
			obj = spJson.getJSONObject("sp");
		}
		jsonArray = toDcRaceJsonArray(dcRaces, obj);
		logger.info("得到最终jsonArray:{}", jsonArray);
			
		jsonObject.put("data", jsonArray);
		//控制台输出结果查看
		testData(jsonObject);
		writeRs(response, jsonObject);
		return null;
	}

	//测试输出
	private void testData(JSONObject jsonObject) {
		try {
			if (jsonObject != null) {
				logger.info("单场第[{}]期推荐赛程：", jsonObject.get("phase_no"));
				JSONArray jsonArray = jsonObject.getJSONArray("data");
				if (jsonArray != null && !jsonArray.isEmpty()) {
					for (Object object : jsonArray) {
						JSONObject tempObject = (JSONObject) object;
						logger.info("{} VS {}", new Object[]{tempObject.get("home_team"), tempObject.get("away_team")});
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	//将单场比赛转换问json格式
	private JSONArray toDcRaceJsonArray(List<DcRace> dcRaces, JSONObject spJson) {
		logger.info("toDcRaceJsonArray中参数spJson:{}", spJson);
		JSONArray array = new JSONArray();
		if (dcRaces != null && !dcRaces.isEmpty()) {
			for (DcRace dcRace : dcRaces) {
				JSONObject object = new JSONObject();
				object.put("phase", dcRace.getPhase());
				object.put("match_num", dcRace.getMatchNum());
				object.put("match_name", dcRace.getMatchName());
				object.put("home_team", dcRace.getHomeTeam());
				object.put("away_team", dcRace.getAwayTeam());
				try {
					object.put("match_date", CoreDateUtils.formatDateTime(dcRace.getMatchDate()));
				} catch (Exception e) {
					object.put("match_date", "");
					logger.error(e.getMessage(), e);
				}
				object.put("handicap", dcRace.getHandicap());
				object.put("fx_id", dcRace.getFxId());
				object.put("final_score", dcRace.getWholeScore());
				object.put("half_score", dcRace.getHalfScore());
				try {
					object.put("status", dcRace.getStatus().getName());
				} catch (Exception e) {
					object.put("status", "");
					logger.error(e.getMessage(), e);
				}
				if (spJson != null && !spJson.isNullObject()) {			
					object.put("sp_s", spJson.getString("m" + dcRace.getMatchNum() + "s") != null ? spJson.getString("m" + dcRace.getMatchNum() + "s") : "--");
					object.put("sp_p", spJson.getString("m" + dcRace.getMatchNum() + "p") != null ? spJson.getString("m" + dcRace.getMatchNum() + "p") : "--");
					object.put("sp_f", spJson.getString("m" + dcRace.getMatchNum() + "f") != null ? spJson.getString("m" + dcRace.getMatchNum() + "f") : "--");
				} else {
					object.put("sp_s", "--");
					object.put("sp_p", "--");
					object.put("sp_f", "--");
				}
						
				object.put("sp_sfp", dcRace.getSpSfp());
				object.put("sp_sxds", dcRace.getSpSxds());
				object.put("sp_jqs", dcRace.getSpJqs());
				object.put("sp_bf", dcRace.getSpBf());
				object.put("sp_bcsfp", dcRace.getSpBcsfp());
				
				array.add(object);
			}
		}
		
		return array;
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

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getStaticDir() {
		return staticDir;
	}

	public void setStaticDir(String staticDir) {
		this.staticDir = staticDir;
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}
	
}
