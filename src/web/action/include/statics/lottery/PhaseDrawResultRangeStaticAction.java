package web.action.include.statics.lottery;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseDrawResultRangeMap;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.util.CoreDateUtils;
import com.opensymphony.xwork2.Action;

public class PhaseDrawResultRangeStaticAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private final String ORDER_STR = "orderStr";//排序字段
	private final String ORDER_VIEW = "orderView";//排序方式
	
	public static final String QUERY_DRAWTIME_START = "draw_start";//
	public static final String QUERY_DRAWTIME_END = "draw_end";//
	
	private PhaseService phaseService;
	private Integer lottery_id;//彩种编码
	private String lottery_time;//查询时间
	
	@SuppressWarnings("unchecked")
	public String handle(){
		logger.info("开始获取历史开奖结果json数据");
		Integer rc = 0;//0成功,1失败
		String message = "操作成功";
		
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONArray jsonArray = new JSONArray();
		Date date = Calendar.getInstance().getTime();
		if (lottery_time != null) {
			date = CoreDateUtils.parseDate(lottery_time);
		}
		if (lottery_id == null || lottery_id == 0L) {
			rc = 1;
			message = "彩种编码不能为空";
			logger.error("彩种编码不能为空");
		} else if (date == null) {
			rc = 1;
			message = "查询时间参数错误";
			logger.error("查询时间参数错误");
		} else {
			LotteryType lotteryType = LotteryType.getItem(lottery_id);
			logger.info("彩种编码lottery_id={}", lottery_id);
			String rangeTime = PhaseDrawResultRangeMap.getPhaseDrawResultRangeByLotteryType(lotteryType);
			if (rangeTime != null) {
				
				String[] rangeArray = rangeTime.split(",");
				Calendar startCal = convertCalendarFromDate(date, rangeArray[0].split(":"));
				Calendar endCal = convertCalendarFromDate(date, rangeArray[1].split(":"));
				//如果开始时间大于结束时间,结束时间加1天
				if (startCal.after(endCal)) {
					endCal.add(Calendar.DATE, 1);
				}
				Map<String,Object> condition = new HashMap<String,Object>();
				condition.put(Phase.QUERY_PHASETYPE, PhaseType.getItem(lottery_id).getValue() + "");
				condition.put(QUERY_DRAWTIME_START, startCal.getTime());
				condition.put(QUERY_DRAWTIME_END, endCal.getTime());
				//按照彩期降序排列
				condition.put(ORDER_STR, Phase.ORDER_PHASE);
				condition.put(ORDER_VIEW, ApiConstant.API_REQUEST_ORDER_ASC);
				
				Map<String, Object> map = null;
				
				PageBean pageBean = super.getPageBean();
				pageBean.setPageSize(288);
				try {
					map = phaseService.getPhases(condition, pageBean);
					if(map == null){
						rc = 1;
						message = "查询lottery_id=" + lottery_id + "时返回的map结果为空";
						logger.error("查询lottery_id={}时返回的map结果为空", lottery_id);
					}else{
						List<Phase> phases = (List<Phase>)map.get(Global.API_MAP_KEY_LIST);
						//logger.info(phases.size()+"----------------------------------");
						//处理开奖结果显示
						if(phases == null || phases.size() <= 0){
							rc = 1;
							message = "查询lottery_id=" + lottery_id + "时返回的phases结果为空";
							logger.error("查询lottery_id={}时返回的phases结果为空", lottery_id);
						}else{
							Map<Integer, LotteryType> k3Map = getK3Map();
							for(Phase phase : phases){
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("phase", phase.getPhase());
								jsonObject.put("time_draw", CoreDateUtils.formatDate(phase.getDrawTime(), CoreDateUtils.DATETIME));
								jsonObject.put("time_draw_yyyymmdd", CoreDateUtils.formatDate(phase.getDrawTime(), CoreDateUtils.DATE));
								jsonObject.put("time_draw_hhmm", CoreDateUtils.formatDate(phase.getDrawTime(), "HH:mm"));
								jsonObject.put("result", phase.getResult());
								//各地快3处理方式相同
								if (k3Map.get(lotteryType.getValue()) != null) {
									jsonObject.put("result_shape", this.getJLK3ResultShape(phase.getResult()));
								}
								jsonArray.add(jsonObject);
							}
						}
					}
				} catch (ApiRemoteCallFailedException e) {
					logger.error(e.getMessage(), e);
					rc = 1;
					message = "查询lottery_id=" + lottery_id + "时出错,错误信息为:" + e.getMessage();
				}
			} else {
				rc = 1;
				message = "彩种编码错误";
				logger.error("彩种编码错误");
			}
		}
		
		JSONObject json = new JSONObject();
		json.put("code", rc);
		json.put("message", message);
		json.put("data", jsonArray);
		
		super.writeRs(response, json);
		
		logger.info("结束获取历史开奖结果json数据");
		return Action.NONE;
	}
	
	private Calendar convertCalendarFromDate (Date date, String[] timeArray) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(timeArray[0]));
		cal.set(Calendar.MINUTE, Integer.valueOf(timeArray[1]));
		cal.set(Calendar.SECOND, Integer.valueOf(timeArray[2]));
		return cal;
	}
	
	private String getJLK3ResultShape (String result) {
		String shapeStr = "";
		if (result == null) {
			return shapeStr;
		}
		JSONObject jsonObject = JSONObject.fromObject(result);
		JSONArray resultArray = jsonObject.getJSONArray("result");
		for (Iterator<?> iterator = resultArray.iterator(); iterator.hasNext();) {
			Map<String, Integer> tempMap = new HashMap<String, Integer>();
			JSONObject object = (JSONObject) iterator.next();
			JSONArray dataArray = object.getJSONArray("data");
			for (Iterator<?> iterator2 = dataArray.iterator(); iterator2.hasNext();) {
				String data = (String) iterator2.next();
				Integer num = tempMap.get(data);
				if (num != null) {
					if (num == 1) {
						shapeStr = "二同号";
						tempMap.put(data, 2);
					} else if (num == 2) {
						shapeStr = "三同号";
					}
				} else {
					tempMap.put(data, 1);
				}
			}
			if (shapeStr.equals("")) {
				shapeStr = "三不同号";
			}
		}
		return shapeStr;
	}
	
	private Map<Integer, LotteryType> getK3Map() {
		Map<Integer, LotteryType> map = new HashMap<Integer, LotteryType>();
		map.put(LotteryType.JLK3.getValue(), LotteryType.JLK3);
		map.put(LotteryType.GXK3.getValue(), LotteryType.GXK3);
		map.put(LotteryType.JSK3.getValue(), LotteryType.JSK3);
		map.put(LotteryType.AHK3.getValue(), LotteryType.AHK3);
		return map;
	}

	public String getLottery_time() {
		return lottery_time;
	}

	public void setLottery_time(String lottery_time) {
		this.lottery_time = lottery_time;
	}

	public Integer getLottery_id() {
		return lottery_id;
	}

	public void setLottery_id(Integer lotteryId) {
		lottery_id = lotteryId;
	}

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}
}
