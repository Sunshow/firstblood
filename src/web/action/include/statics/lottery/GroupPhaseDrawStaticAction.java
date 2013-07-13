package web.action.include.statics.lottery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.utils.StringUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.lottery.SfpArrange;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;

public class GroupPhaseDrawStaticAction extends BaseAction {
	
	private static final long serialVersionUID = 1L;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private List<Integer> phaseTypeList;
	
	private String lotteryTypeId;//格式为1,2,3,4,5
	
	private String staticDir; 
	
	private String webRoot;
	
	private PhaseService phaseService;
	

	@SuppressWarnings("unchecked")
	public void handle() {
		logger.info("开始查询14场赛程列表");
		try {
			webRoot = WebUtils.getRealPath(ServletActionContext.getServletContext(), "");
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		
		logger.info("webRoot........................"+webRoot);
		
		if(lotteryTypeId != null && !"".equals(lotteryTypeId)){
			String[] lotteryTypeStr = StringUtil.split(lotteryTypeId, ',');
			if(lotteryTypeStr != null && lotteryTypeStr.length > 0){
				phaseTypeList = new ArrayList<Integer>();
				for(String s : lotteryTypeStr){
					phaseTypeList.add(Integer.valueOf(s));
				}
			}
		}
		JSONObject nationDraw = new JSONObject();
		
		if(phaseTypeList != null && phaseTypeList.size() > 0){		
			for(int phaseType : phaseTypeList){
				PhaseType type = PhaseType.getItem(phaseType);
				if(type == null){
					logger.info("没有彩种: {}", phaseType);
					continue;
				}
				
				String filePath = webRoot + this.staticDir + phaseType + ".json";
				filePath = filePath.replace('/', File.separatorChar);
				String jsonStr = null;
				synchronized (LatestPhaseDrawStaticAction.getLock()) {
					jsonStr = this.read(filePath);
				}
				if(jsonStr == null || jsonStr.trim().isEmpty()){
					logger.error("没有找到彩种" + type.getName() + "_" + type.getValue() + "的最新开奖结果抓取缓存");
					continue;
				}
				
				JSONObject obj = JSONObject.fromObject(jsonStr);
				if(phaseType == PhaseType.getItem(LotteryType.SFC).getValue()){
					Map<String, Object> mapMatches = null;
					try{
						Map<String,Object> conditionMatch = new HashMap<String, Object>();
						conditionMatch.put(SfpArrange.QUERY_PHASE, obj.get("phase"));
						
						//按照彩期降序排列
						conditionMatch.put("orderStr", SfpArrange.ORDER_MATCH_NUM);
						conditionMatch.put("orderView", ApiConstant.API_REQUEST_ORDER_ASC);
						
						PageBean pageBeanMatch = new PageBean();
						pageBeanMatch.setPageSize(14);
						
						mapMatches = phaseService.getSfpMatches(conditionMatch, pageBeanMatch);
						JSONArray jsonArrayMatch = new JSONArray();
						
						if(mapMatches == null){
							logger.error("查询phase={}时返回的map结果为空", obj.get("phase"));
						}else{
							List<SfpArrange> matches = (List<SfpArrange>)mapMatches.get(Global.API_MAP_KEY_LIST);
							//处理开奖结果显示
							if(matches == null || matches.size() <= 0){
								logger.error("查询phase={}时返回的map结果为空", obj.get("phase"));
							}else{
								for(SfpArrange sfpArrange : matches){
									JSONObject jsonObjectMatch = new JSONObject();
									jsonObjectMatch.put("home_team", sfpArrange.getHomeTeam());
									jsonObjectMatch.put("away_team", sfpArrange.getGuestTeam());
									jsonArrayMatch.add(jsonObjectMatch);
								}
							}
						}
						obj.put("match", jsonArrayMatch);
						
					}catch(ApiRemoteCallFailedException e){
						logger.error(e.getMessage(), e);
					}
				}
				
				nationDraw.put("lottery_type_" + obj.get("lottery_type"), obj.toString());
				
			}
		}
		try {
			PrintWriter writer = ServletActionContext.getResponse().getWriter();
			
			logger.info("nationDraw............"+nationDraw.toString());
			
			writer.println(nationDraw.toString());
		} catch (IOException e) {
			logger.error("生成json时出现异常",e);
			return;
		}
		logger.info("结束查询14场赛程列表");
		return;
	}

	public List<Integer> getPhaseTypeList() {
		return phaseTypeList;
	}

	public void setPhaseTypeList(List<Integer> phaseTypeList) {
		this.phaseTypeList = phaseTypeList;
	}

	public String getStaticDir() {
		return staticDir;
	}

	public void setStaticDir(String staticDir) {
		this.staticDir = staticDir;
	}
	
	private String read(String pathname) {
		File file = new File(pathname);
		if( !file.exists() ){
			return null;
		}
		String s = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] bytes = new byte[fis.available()];
			fis.read(bytes);
			s = new String(bytes);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}

	public String getLotteryTypeId() {
		return lotteryTypeId;
	}

	public void setLotteryTypeId(String lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}
	
	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}
}
