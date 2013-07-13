package web.service.impl.cms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.dao.cms.RecommendRaceDao;
import com.lehecai.admin.web.domain.cms.BasketballAnalysisData;
import com.lehecai.admin.web.domain.cms.RecommendRace;
import com.lehecai.admin.web.service.cms.RecommendRaceService;
import com.lehecai.core.api.DataApiUrlConstant;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.type.cooperator.Cooperator;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreHttpUtils;

/**
 * 推荐赛程业务逻辑层实现类
 * @author yanweijie
 *
 */
public class RecommendRaceServiceImpl implements RecommendRaceService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final static String BASKETBALL_URL = DataApiUrlConstant.BASKETBALL_DATA;
	private final static int DTYPE_OE = 10;//平均欧赔
	
	private RecommendRaceDao recommendRaceDao;
	private String analysisUrl;

	/**
	 * 更新推荐赛程
	 */
	public void merge(RecommendRace recommendRace) {
		recommendRaceDao.merge(recommendRace);
	}
	
	/**
	 * 删除推荐赛程
	 */
	public void delete(Long recommendRaceId) {
		recommendRaceDao.delete(recommendRaceDao.getById(recommendRaceId));
	}
	
	/**
	 * 查询所有赛程
	 */
	public List<RecommendRace> findList(Cooperator cooperator, LotteryType lotteryType) {
		return recommendRaceDao.findList(cooperator, lotteryType);
	}
	
	@Override
	public Map<Long, BasketballAnalysisData> getBasketballAnalysisData(String matchIds)
			throws ApiRemoteCallFailedException {
		Map<Long, BasketballAnalysisData> map = new HashMap<Long, BasketballAnalysisData>();
		try {
			String responseStr = null;
			List<String> result = CoreHttpUtils.getUrl(analysisUrl + BASKETBALL_URL, "mid=" + matchIds + "&dtype=" + DTYPE_OE, CharsetConstant.CHARSET_UTF8, 10000);
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
			if (array != null && !array.isEmpty()) {
				for (Object obj : array) {
					JSONObject tempObj = (JSONObject)obj;
					for (Iterator<?> i = tempObj.keySet().iterator(); i.hasNext();) {  
						String key = (String)i.next();  
						JSONObject jsonData = tempObj.getJSONObject(key) == null ? null : tempObj.getJSONObject(key).getJSONObject("ae");
						if (jsonData != null) {
							BasketballAnalysisData analysisData = new BasketballAnalysisData();
							analysisData.setAeAf(String.valueOf(jsonData.get("af")));
							analysisData.setAeAl(String.valueOf(jsonData.get("al")));
							analysisData.setAeHf(String.valueOf(jsonData.get("hf")));
							analysisData.setAeHl(String.valueOf(jsonData.get("hl")));
							analysisData.setMatchId(Long.valueOf(key));
							map.put(Long.valueOf(key), analysisData);
							break;
						}
					}
					
				}
			}
			
		} catch (Exception e) {
			logger.error("API请求错误，请联系技术人员。" + e.getMessage());
			return null;
		}
		logger.info(map.toString());
		return map;
	}
	
	public String getAnalysisUrl() {
		return analysisUrl;
	}

	public void setAnalysisUrl(String analysisUrl) {
		this.analysisUrl = analysisUrl;
	}

	public RecommendRaceDao getRecommendRaceDao() {
		return recommendRaceDao;
	}

	public void setRecommendRaceDao(RecommendRaceDao recommendRaceDao) {
		this.recommendRaceDao = recommendRaceDao;
	}


}
