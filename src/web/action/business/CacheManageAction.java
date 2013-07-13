package web.action.business;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.business.CacheManageService;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.opensymphony.xwork2.Action;

/**
 * 缓存Action
 * @author yanweijie
 *
 */
public class CacheManageAction extends BaseAction {
	private static final long serialVersionUID = 7614709909581614903L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private CacheManageService cacheManageService;
	
	private Long walletUid;		//钱包编码
	private Long dcId;			//单场编码
	private Long sfggId;	    //胜负过关编码
	private Long jclqMatchNum;	//竞彩篮球编码
	private Long jczqMatchNum;	//竞彩足球编码
	private Long planId;		//方案编码
	private Long orderId;		//订单编码
	private String counterUid;	//计数器编码

	public String handle() {
		logger.info("进入缓存删除");
		return "input";
	}
	
	/**
	 * 删除钱包缓存
	 */
	public String deleteWalletCache() {
		logger.info("进入删除钱包缓存");
		
		JSONObject rs = new JSONObject();
		
		if (walletUid == null || walletUid == 0L) {
			rs.put("msg", "钱包编码为空");
		} else {
			boolean deleteResult = false;
			try {
				deleteResult = cacheManageService.deleteWalletCache(walletUid);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("删除钱包缓存，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
			if (deleteResult) {
				logger.info("删除钱包缓存数据成功");
				rs.put("msg", "删除钱包缓存数据成功");
			} else {
				logger.error("删除钱包缓存数据失败");
				rs.put("msg", "删除钱包缓存数据失败");
			}
		}
		
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("删除钱包缓存结束");
		return Action.NONE;
	}
	
	/**
	 * 删除单场缓存
	 */
	public String deleteDCCache() {
		logger.info("进入删除单场缓存");
		
		JSONObject rs = new JSONObject();
		
		if (dcId == null || dcId == 0L) {
			rs.put("msg", "单场编码为空");
		} else {
			boolean deleteResult = false;
			try {
				deleteResult = cacheManageService.deleteDCCache(dcId);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("删除单场缓存，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
			if (deleteResult) {
				logger.info("删除单场缓存数据成功");
				rs.put("msg", "删除单场缓存数据成功");
			} else {
				logger.error("删除单场缓存数据失败");
				rs.put("msg", "删除单场缓存数据失败");
			}
		}
		
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("删除单场缓存结束");
		return Action.NONE;
	}
	
	/**
	 * 删除胜负过关缓存
	 */
	public String deleteSFGGCache() {
		logger.info("进入删除胜负过关缓存");
		
		JSONObject rs = new JSONObject();
		
		if (sfggId == null || sfggId == 0L) {
			rs.put("msg", "胜负过关编码为空");
		} else {
			boolean deleteResult = false;
			try {
				deleteResult = cacheManageService.deleteSFGGCache(sfggId);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("删除胜负过关缓存，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
			if (deleteResult) {
				logger.info("删除胜负过关缓存数据成功");
				rs.put("msg", "删除胜负过关缓存数据成功");
			} else {
				logger.error("删除胜负过关缓存数据失败");
				rs.put("msg", "删除胜负过关缓存数据失败");
			}
		}
		
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("删除胜负过关缓存结束");
		return Action.NONE;
	}

	/**
	 * 删除竞彩篮球缓存
	 */
	public String deleteJCLQCache() {
		logger.info("进入删除竞彩篮球缓存");
		
		JSONObject rs = new JSONObject();
		
		if (jclqMatchNum == null || jclqMatchNum == 0L) {
			rs.put("msg","竞彩篮球编码为空");
		} else {
			boolean deleteResult = false;
			try {
				deleteResult = cacheManageService.deleteJCLQCache(jclqMatchNum);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("删除竞彩篮球缓存，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
			if (deleteResult) {
				logger.info("删除竞彩篮球缓存数据成功");
				rs.put("msg", "删除竞彩篮球缓存数据成功");
			} else {
				logger.error("删除竞彩篮球缓存数据失败");
				rs.put("msg", "删除竞彩篮球缓存数据失败");
			}
		}
		
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("删除竞彩篮球缓存结束");
		return Action.NONE;
	}
	
	/**
	 * 删除竞彩足球缓存
	 */
	public String deleteJCZQCache() {
		logger.info("进入删除竞彩足球缓存");
		
		JSONObject rs = new JSONObject();
		
		if (jczqMatchNum == null || jczqMatchNum == 0L) {
			rs.put("msg","竞彩足球编码为空");
		} else {
			boolean deleteResult = false;
			try {
				deleteResult = cacheManageService.deleteJCZQCache(jczqMatchNum);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("删除竞彩足球缓存，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
			if (deleteResult) {
				logger.info("删除竞彩足球缓存数据成功");
				rs.put("msg", "删除竞彩足球缓存数据成功");
			} else {
				logger.error("删除竞彩足球缓存数据失败");
				rs.put("msg", "删除竞彩足球缓存数据失败");
			}
		}
		
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("删除竞彩足球缓存结束");
		return Action.NONE;
	}
	
	/**
	 * 删除方案缓存
	 */
	public String deletePlanCache() {
		logger.info("进入删除方案缓存");
		
		JSONObject rs = new JSONObject();
		
		if (planId == null || planId == 0L) {
			rs.put("msg", "方案编码为空");
		} else {
			boolean deleteResult = false;
			try {
				deleteResult = cacheManageService.deletePlanCache(planId);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("删除方案缓存，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
			if (deleteResult) {
				logger.info("删除方案缓存数据成功");
				rs.put("msg", "删除方案缓存数据成功");
			} else {
				logger.error("删除方案缓存数据失败");
				rs.put("msg", "删除方案缓存数据失败");
			}
		}
		
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("删除方案缓存结束");
		return Action.NONE;
	}
		
	/**
	 * 删除订单缓存
	 */
	public String deleteOrderCache() {
		logger.info("进入删除订单缓存");
		
		JSONObject rs = new JSONObject();
		
		if (orderId == null || orderId == 0L) {
			logger.error("订单编码为空");
			rs.put("msg", "订单编码为空");
		} else {
			boolean deleteResult = false;
			try {
				deleteResult = cacheManageService.deleteOrderCache(orderId);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("删除订单缓存，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
			
			if (deleteResult) {
				logger.info("删除订单缓存数据成功");
				rs.put("msg", "删除订单缓存数据成功");
			} else {
				logger.error("删除订单缓存数据失败");
				rs.put("msg", "删除订单缓存数据失败");
			}
		}
		
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("删除订单缓存结束");
		return Action.NONE;
	}
	
	/**
	 * 删除计数器缓存
	 */
	public String deleteCounterCache() {
		logger.info("进入删除计数器缓存");
		JSONObject rs = new JSONObject();
		
		if (StringUtils.isEmpty(counterUid)) {
			rs.put("flag", false);
			rs.put("msg", "计数器编码为空");
		} else {
			boolean deleteResult = false;
			String[] counterUids = counterUid.split(",");
			List<Long> uids = new ArrayList<Long>();
			
			for (String uid : counterUids) {
				try {
					uids.add(Long.parseLong(uid));
				} catch (NumberFormatException e) {
					logger.error("类型转换错误！编码为" + uid + "的用户计数器缓存删除失败");
					rs.put("flag", false);
					rs.put("msg", "类型转换错误！编码为" + uid + "的用户计数器缓存删除失败");					
				}
			}
			try {
				deleteResult = cacheManageService.deleteCounterCache(uids);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("删除计数器缓存，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
			if (deleteResult) {
				logger.info("删除计数器缓存数据成功");
				rs.put("flag", true);
				rs.put("msg", "删除计数器缓存数据成功");
			} else {
				logger.error("删除计数器缓存数据失败");
				rs.put("flag", false);
				rs.put("msg", "删除计数器缓存数据失败");
			}
		}	
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("删除计数器缓存结束");
		return Action.NONE;
	}
	
	/**
	 * 删除用户登录失败计数缓存
	 */
	public String deleteUserLoginFailureCountCache() {
		logger.info("开始删除用户登录失败计数缓存");
		JSONObject rs = new JSONObject();
		boolean deleteResult = false;
		
		try {
			deleteResult = cacheManageService.deleteUserLoginFailureCountCache();
		} catch (ApiRemoteCallFailedException e) {
			logger.error("删除用户登录失败计数缓存，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
			return "failure";
		}
		if (deleteResult) {
			logger.info("删除用户登录失败计数缓存成功");
			rs.put("flag", true);
			rs.put("msg", "删除用户登录失败计数缓存成功");
		} else {
			logger.error("删除用户登录失败计数缓存失败");
			rs.put("flag", false);
			rs.put("msg", "删除用户登录失败计数缓存失败");
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("删除用户登录失败计数缓存结束");
		return Action.NONE;
	}
	
	public CacheManageService getCacheManageService() {
		return cacheManageService;
	}
	public void setCacheManageService(CacheManageService cacheManageService) {
		this.cacheManageService = cacheManageService;
	}
	public Long getWalletUid() {
		return walletUid;
	}
	public void setWalletUid(Long walletUid) {
		this.walletUid = walletUid;
	}
	public Long getDcId() {
		return dcId;
	}
	public void setDcId(Long dcId) {
		this.dcId = dcId;
	}
	public Long getJclqMatchNum() {
		return jclqMatchNum;
	}
	public void setJclqMatchNum(Long jclqMatchNum) {
		this.jclqMatchNum = jclqMatchNum;
	}
	public Long getJczqMatchNum() {
		return jczqMatchNum;
	}
	public void setJczqMatchNum(Long jczqMatchNum) {
		this.jczqMatchNum = jczqMatchNum;
	}
	public Long getPlanId() {
		return planId;
	}
	public void setPlanId(Long planId) {
		this.planId = planId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Long getSfggId() {
		return sfggId;
	}
	public void setSfggId(Long sfggId) {
		this.sfggId = sfggId;
	}	
	public String getCounterUid() {
		return counterUid;
	}
	public void setCounterUid(String counterUid) {
		this.counterUid = counterUid;
	}
}
