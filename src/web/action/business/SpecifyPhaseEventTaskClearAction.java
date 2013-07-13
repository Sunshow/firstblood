package web.action.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.BIService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;

public class SpecifyPhaseEventTaskClearAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private PhaseService phaseService;
	private BIService bIService;

	private Integer lotteryTypeValue;// 彩种列表选中值
	private Integer phaseEventTypeValue;// 彩期事件列表选中值


	public String handle() {
		return "inputForm";
	}

	public String executeRun() {
		HttpServletResponse response = ServletActionContext.getResponse();

		StringBuffer log = new StringBuffer();
		log.append("目的 : 手工清除指定彩期守护事件; ");

		// 基本参数校验
		if (lotteryTypeValue == null || phaseEventTypeValue == null ) {
			log.append(String.format("lotteryTypeValue=[%s], ", lotteryTypeValue));
			log.append(String.format("phaseEventTypeValue=[%s], ", phaseEventTypeValue));
			log.append(" --> 基本参数为null!");
			logger.error(log.toString());
			writeRs(response, "执行失败 : 程序错误,请联系技术人员!");
			return null;
		}

		// 校验彩种列表选中值
		LotteryType lotteryType = null;
		{
			lotteryType = LotteryType.getItem(lotteryTypeValue);
			if (lotteryType == null) {
				log.append(String.format(" --> 获取彩种id=[%s]为空!", lotteryTypeValue));
				logger.error(log.toString());
				writeRs(response, String.format("执行失败 : 无此彩种id=[%s]!", lotteryTypeValue));
				return null;
			}
			log.append(String.format("彩种 : %s ; ", lotteryType.getName()));
		}

		// 彩期守护事件类型校验
		{
			log.append(String.format("彩期事件类型值 : %s ; ", phaseEventTypeValue));
			// TODO 增加彩期事件时需要修改判断条件
			if (phaseEventTypeValue < 1 || phaseEventTypeValue > 8) {
				log.append(" --> 输入彩期事件类型值错误!");
				logger.error(log.toString());
				writeRs(response, "执行失败 : 请正确选择列出的彩期事件类型!");
				return null;
			}
		}
		
		// 执行对engine请求
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put(BIService.PROCESS_CODE, BIService.Specify_PhaseEventTaskClear_Process);
		requestMap.put("lotteryTypeValue", String.valueOf(lotteryTypeValue));
		requestMap.put("phaseEventTypeValue", String.valueOf(phaseEventTypeValue));

		Map<String, String> responseMap = bIService.request(requestMap, lotteryType);

		if (responseMap == null || responseMap.size() == 0) {
			log.append(" --> engine返回为空!");
			logger.error(log.toString());
			writeRs(response, "执行失败 : 调用程序返回为空!");
			return null;
		}

		if (responseMap.get(BIService.RESP_CODE).equals("0000")) {
			String info = responseMap.get(BIService.RESP_MESG);
			log.append(" --> 事件执行成功!");
			logger.info(log.toString());
			writeRs(response, "执行成功 : ".concat(info));
		} else {
			String info = responseMap.get(BIService.RESP_MESG);
			log.append(String.format(" --> 事件执行失败 : [%s] ; ", info));
			logger.error(log.toString());
			writeRs(response, "执行失败 : ".concat(info));
		}
		return null;
	}

	public List<LotteryType> getLotteryTypeList() {
		// 获取在售彩种列表
		return OnSaleLotteryList.get();
	}

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}

	public BIService getbIService() {
		return bIService;
	}

	public void setbIService(BIService bIService) {
		this.bIService = bIService;
	}

	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}


	public void setPhaseEventTypeValue(Integer phaseEventTypeValue) {
		this.phaseEventTypeValue = phaseEventTypeValue;
	}

	
}