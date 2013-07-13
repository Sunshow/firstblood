package web.action.business;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.action.lottery.ChaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.WaitChaseService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.lottery.ChaseWait;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.StopChaseType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;

public class WaitChaseAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(ChaseAction.class);
	
	private WaitChaseService waitChaseService;
	private PhaseService phaseService;
	
	private List<ChaseWait> waitChases;
	
	private String id;					//等待追号单一编号
	private String ids;					//等待追号多个编号，以','连接
	private String chaseId;				//追号id
	private Integer lotteryTypeId;		//彩种编号
	private String phase;				//彩期
	private Integer stopChaseType;		//追号停止类型
	
	/**
	 * 查询等待追号
	 * @return
	 */
	public String handle(){
		logger.info("进入查询等待追号列表");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query(){
		logger.info("进入查询等待追号列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		
		lotteryTypeId = lotteryTypeId == null ? LotteryType.ALL.getValue() : lotteryTypeId;	//彩种编号(默认全部编号)
		LotteryType lt = LotteryType.getItem(lotteryTypeId);
		StopChaseType sct = stopChaseType == null ? null : StopChaseType.getItem(stopChaseType);//追号停止类型
		
		Map<String, Object> map = null;
		try {
			map = waitChaseService.getWaitResult(chaseId, lt, phase, 
					sct, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询等待追号列表，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if(map != null){			
			waitChases = (List<ChaseWait>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("查询等待追号列表结束");
		return "list";
	}
	
	/**
	 * 执行追号
	 * @return
	 */
	public String executeWaitChase() {
		logger.info("进入执行追号");
		if ((id == null || "".equals(id)) && (ids == null || "".equals(ids))) {
			logger.error("执行追号，编码为空");
			super.setErrorMessage("执行追号，编码为空");
			return "failure";
		}
		boolean executeChaseResult = false;
		if (id != null && !"".equals(id)) {//单个追号
			try {
				executeChaseResult = waitChaseService.executeWaitChase(id);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("执行追号，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
		}
		if (ids != null && !"".equals(ids)) {//批量追号
			String[] idStrs = ids.split(",");//以','号拆分所有的追号编码
			for (int i = 0;i < idStrs.length;i++) {
				try {
					executeChaseResult = waitChaseService.executeWaitChase(idStrs[i]);
				} catch (ApiRemoteCallFailedException e) {
					logger.error("执行追号，api调用异常，{}", e.getMessage());
					super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
					return "failure";
				}
			}
		}
		if (executeChaseResult) {
			logger.info("执行追号成功");
		} else {
			logger.error("执行追号失败");
			super.setErrorMessage("执行追号失败");
			return "failure";
		}
		
		super.setForwardUrl("/business/waitChase.do");
		logger.info("执行追号结束");
		return "success";
	}
	
	public WaitChaseService getWaitChaseService() {
		return waitChaseService;
	}
	public void setWaitChaseService(WaitChaseService waitChaseService) {
		this.waitChaseService = waitChaseService;
	}
	public PhaseService getPhaseService() {
		return phaseService;
	}
	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getChaseId() {
		return chaseId;
	}
	public void setChaseId(String chaseId) {
		this.chaseId = chaseId;
	}
	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}
	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	public List<ChaseWait> getWaitChases() {
		return waitChases;
	}
	public void setWaitChases(List<ChaseWait> waitChases) {
		this.waitChases = waitChases;
	}
	public Integer getStopChaseType() {
		return stopChaseType;
	}
	public void setStopChaseType(Integer stopChaseType) {
		this.stopChaseType = stopChaseType;
	}

	/**
	 * 获取所有彩票种类
	 * @return
	 */
	public List<LotteryType> getLotteryTypes(){
		return OnSaleLotteryList.getForQuery();
	}
	/**
	 * 获取所有停止追号类型
	 * @return
	 */
	public List<StopChaseType> getStopChaseTypes(){
		return StopChaseType.getItems();
	}

}
