/**
 * 
 */
package web.action.event;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.event.EuroCupService;
import com.lehecai.core.api.event.EuroCupDraw;
import com.lehecai.core.api.event.EuroCupTeam;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.util.CoreNumberUtil;

/**
 * @author chirowong
 *
 */
public class EuroCupPrizeAction extends BaseAction {

	private static final long serialVersionUID = 8625207108649371005L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private HttpServletResponse response;
	
	private static final Integer TYPE_TOP1_DRAW = 1;
	private static final Integer TYPE_TOP4_DRAW = 2;
	private static final Integer TYPE_TOP8_DRAW = 3;
	
	private EuroCupService euroCupService;
	private List<EuroCupTeam> euroCupTeams;
	private String teamIds;
	private Integer type;//开奖类型 1-冠军 2-四强 3-八强
	private Integer time;//超时时间 单位秒
	
	@SuppressWarnings("unchecked")
	public String handle(){
		logger.info("进入获取欧洲杯开奖信息开始");
		Map<String, Object> map = null;
		try{
			map = euroCupService.findTeamList(super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取欧洲杯球队信息，api调用异常，{}", e.getMessage());
			super.setErrorMessage("获取欧洲杯球队信息，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}		
		if (map != null) {
			euroCupTeams = (List<EuroCupTeam>)map.get(Global.API_MAP_KEY_LIST);
		}
		return "prize";
	}
	
	public String prize(){
		logger.info("进入获取欧洲杯开奖信息开始");
		ResultBean resultBean = null;
		JSONObject object = new JSONObject();
		if(type == null){
			object.put("result", false);
			object.put("message","开奖类型不能为空");
			writeRs(ServletActionContext.getResponse(), object);
			logger.info("进入获取欧洲杯开奖信息结束");
			return null;
		}
		
		if(teamIds == null || teamIds.equals("")){
			object.put("result", false);
			object.put("message","开奖球队不能为空");
			writeRs(ServletActionContext.getResponse(), object);
			logger.info("进入获取欧洲杯开奖信息结束");
			return null;
		}
		
		if(type.intValue() == TYPE_TOP1_DRAW.intValue()){
			String[] arrTeamIds = teamIds.split(",");
			if(arrTeamIds.length != 1){
				object.put("result", false);
				object.put("message","冠军球队只能有1个");
				writeRs(ServletActionContext.getResponse(), object);
				logger.info("进入获取欧洲杯开奖信息结束");
				return null;
			}
		}
		
		if(type.intValue() == TYPE_TOP4_DRAW.intValue()){
			String[] arrTeamIds = teamIds.split(",");
			if(arrTeamIds.length != 4){
				object.put("result", false);
				object.put("message","四强球队只能为4个");
				writeRs(ServletActionContext.getResponse(), object);
				logger.info("进入获取欧洲杯开奖信息结束");
				return null;
			}
		}
		
		if(type.intValue() == TYPE_TOP8_DRAW.intValue()){
			String[] arrTeamIds = teamIds.split(",");
			if(arrTeamIds.length != 8){
				object.put("result", false);
				object.put("message","八强球队只能为8个");
				writeRs(ServletActionContext.getResponse(), object);
				logger.info("进入获取欧洲杯开奖信息结束");
				return null;
			}
		}
		
		try{
			resultBean = euroCupService.prize(teamIds,type);
			object.put("result", resultBean.isResult());
			object.put("msg", resultBean.getMessage());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取欧洲杯开奖信息，api调用异常，{}", e.getMessage());
			object.put("result", false);
			object.put("msg", e.getMessage());
		}		
		writeRs(ServletActionContext.getResponse(), object);
		logger.info("进入获取欧洲杯开奖信息结束");
		return null;
	}
	
	public String getPrizeResult(){
		logger.info("进入获取开奖状态");
		int i = 0;
		JSONObject js = new JSONObject();
		EuroCupDraw ecd = new EuroCupDraw();
		response = ServletActionContext.getResponse();
		while(true){
			i++;
			try {
				ecd = euroCupService.getPrizeResult();
			} catch (ApiRemoteCallFailedException e) {
			}
			int euroCupDrawStatus = ecd.getStatus().intValue();
			js.put("status", euroCupDrawStatus);
			if(euroCupDrawStatus == 3){
				StringBuffer sb = new StringBuffer();
				sb.append("大约需要开奖的订单数：");
				sb.append(ecd.getTotalAbout()+"，");
				sb.append("精确需要开奖的订单数：");
				sb.append(ecd.getTotalExact()+"，");
				sb.append("开奖成功的订单数：");
				sb.append(ecd.getSuccessedNum()+"，");
				sb.append("开奖失败的订单数：");
				sb.append(ecd.getFailedNum()+"，");
				sb.append("耗时：");
				sb.append(CoreNumberUtil.formatNumber(ecd.getTimeConsuming(),false,3,0));
				js.put("msg", sb.toString());
				break;
			}else{
				if(i > time){
					break;
				}
				try {
					Thread.sleep(1000);//暂停1s
				} catch (InterruptedException e1) {
					break;
				}
			}
		}
		writeRs(response,js);
		logger.info("获取开奖状态结束");
		return null;
	}
	
	public String flushPrize(){
		logger.info("进入清空开奖状态开始");
		ResultBean resultBean = null;
		JSONObject object = new JSONObject();
		try{
			resultBean = euroCupService.flushPrize();
			object.put("result", resultBean.isResult());
			object.put("msg", resultBean.getMessage());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("清空开奖状态，api调用异常，{}", e.getMessage());
			object.put("result", false);
			object.put("msg", e.getMessage());
		}		
		writeRs(ServletActionContext.getResponse(), object);
		logger.info("进入清空开奖状态结束");
		return null;
	}
	
	public EuroCupService getEuroCupService() {
		return euroCupService;
	}

	public void setEuroCupService(EuroCupService euroCupService) {
		this.euroCupService = euroCupService;
	}

	public List<EuroCupTeam> getEuroCupTeams() {
		return euroCupTeams;
	}

	public void setEuroCupTeams(List<EuroCupTeam> euroCupTeams) {
		this.euroCupTeams = euroCupTeams;
	}

	public String getTeamIds() {
		return teamIds;
	}

	public void setTeamIds(String teamIds) {
		this.teamIds = teamIds;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}
}
