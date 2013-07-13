package web.action.include.statics.lottery;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.lehecai.core.lottery.PhaseStatus;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.util.CoreDateUtils;
import com.opensymphony.xwork2.Action;

public class LotteryDrawStaticAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private final String ORDER_STR = "orderStr";//排序字段
	private final String ORDER_VIEW = "orderView";//排序方式
	
	private PhaseService phaseService;
	private Integer lottery_id;//彩种编码
	private Integer count = 20;//开奖结果条数,默认20条
	
	@SuppressWarnings("unchecked")
	public String handle(){
		logger.info("开始获取历史开奖结果json数据");
		Integer rc = 0;//0成功,1失败
		String message = "操作成功";
		
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONArray jsonArray = new JSONArray();
		
		if(lottery_id == null || lottery_id == 0L){
			rc = 1;
			message = "彩种编码不能为空";
			logger.error("彩种编码不能为空");
		}else{
			logger.info("彩种编码lottery_id={}", lottery_id);
			
			Map<String,Object> condition = new HashMap<String,Object>();
			condition.put(Phase.QUERY_PHASETYPE, PhaseType.getItem(lottery_id).getValue() + "");
			//彩期状态为结果已公布或者已开奖或者已派奖
			List<String> list = new ArrayList<String>();
			list.add(PhaseStatus.RESULT_SET.getValue() + "");
			list.add(PhaseStatus.DRAW.getValue() + "");
			list.add(PhaseStatus.REWARD.getValue() + "");
			condition.put(Phase.QUERY_STATUS, list);
			//按照彩期降序排列
			condition.put(ORDER_STR, Phase.ORDER_PHASE);
			condition.put(ORDER_VIEW, ApiConstant.API_REQUEST_ORDER_DESC);
			
			Map<String, Object> map = null;
			
			PageBean pageBean = super.getPageBean();
			pageBean.setPageSize(count);
			
			try {
				map = phaseService.getPhases(condition, pageBean);
				if(map == null){
					rc = 1;
					message = "查询lottery_id=" + lottery_id + "时返回的map结果为空";
					logger.error("查询lottery_id={}时返回的map结果为空", lottery_id);
				}else{
					List<Phase> phases = (List<Phase>)map.get(Global.API_MAP_KEY_LIST);
					//处理开奖结果显示
					if(phases == null || phases.size() <= 0){
						rc = 1;
						message = "查询lottery_id=" + lottery_id + "时返回的phases结果为空";
						logger.error("查询lottery_id={}时返回的phases结果为空", lottery_id);
					}else{
						for(Phase phase : phases){
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("phase", phase.getPhase());
							jsonObject.put("time_draw", CoreDateUtils.formatDate(phase.getDrawTime(), CoreDateUtils.DATETIME));
							jsonObject.put("time_draw_yyyymmdd", CoreDateUtils.formatDate(phase.getDrawTime(), CoreDateUtils.DATE));
							jsonObject.put("time_draw_hhmm", CoreDateUtils.formatDate(phase.getDrawTime(), "HH:mm"));
							jsonObject.put("result", phase.getResult());
							jsonArray.add(jsonObject);
						}
					}
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				rc = 1;
				message = "查询lottery_id=" + lottery_id + "时出错,错误信息为:" + e.getMessage();
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

	public Integer getLottery_id() {
		return lottery_id;
	}

	public void setLottery_id(Integer lotteryId) {
		lottery_id = lotteryId;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}
}
