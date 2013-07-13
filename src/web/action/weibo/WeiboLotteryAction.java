/**
 * 
 */
package web.action.weibo;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.weibo.TokenInfo;
import com.lehecai.admin.web.domain.weibo.WeiboLottery;
import com.lehecai.admin.web.service.weibo.WeiboLotteryService;
import com.lehecai.admin.web.service.weibo.WeiboService;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.WeiboType;

/**
 * @author yanweijie
 *
 */
public class WeiboLotteryAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(WeiboLotteryAction.class);
	
	private WeiboLotteryService weiboLotteryService;
	
	private String uid;
	private Integer weiboTypeId = WeiboType.SINA.getValue();
	private List<Integer> lotteryTypeIds;
	
	private String weiboLotteryArraryStr;
	
	private String content;
	
	private List<Integer> weiboTypeIdList;
	private List<TokenInfo> tokenInfoList;
	
	private Map<Integer, WeiboService> weiboServiceMap;
	
	public String handle() {
		logger.info("进入微博彩种管理输入微博编号");
		
		tokenInfoList = weiboLotteryService.findTokenInfoList();
		
		return "inputWeibo";
	}
	
	/**
	 * 转向选择彩种
	 * @return
	 */
	public String input() {
		logger.info("进入微博彩种管理选择彩种");
		if (uid == null || uid.equals("")) {
			logger.error("微博编号为空");
			super.setErrorMessage("微博编号不能为空");
			return "failure";
		}
		
		WeiboService weiboService = weiboServiceMap.get(weiboTypeId);
		TokenInfo tokenInfo = weiboService.getToken(uid, WeiboType.getItem(weiboTypeId));//根据微博类型及用户编号查询认证消息
		if (tokenInfo == null) {
			logger.error("{}编号的用户未在{}微博认证", uid, WeiboType.getItem(weiboTypeId).getName());
			super.setErrorMessage(uid + "编号的用户未在" + WeiboType.getItem(weiboTypeId).getName() + "认证");
			return "failure";
		}
		
		List<WeiboLottery> weiboLotteryList = weiboLotteryService.findWeiboLotteryList(uid, WeiboType.getItem(weiboTypeId)); //根据微博编号查询微博彩种分享记录
		
		if (weiboLotteryList == null || weiboLotteryList.size() == 0) {
			logger.info("{}的{}编号暂无彩种配置", WeiboType.getItem(weiboTypeId).getName(), uid);
		} else {
			weiboLotteryArraryStr = JSONArray.fromObject(weiboLotteryList).toString();
			logger.info(weiboLotteryArraryStr);
		}
		
		return "inputLottery";
	}
	
	/**
	 * 更新微博彩种
	 * @return
	 */
	public String manage() {
		logger.info("进入更新微博彩种");
		if (uid == null || uid.equals("")) {
			logger.error("微博编号为空");
			super.setErrorMessage("微博编号不能为空");
			return "failure";
		}
		
		if (lotteryTypeIds == null || lotteryTypeIds.size() == 0) {
			logger.error("彩种为空");
			super.setErrorMessage("彩种不能为空");
			return "failure";
		}
		logger.info("lotteryTypeIds：{}", lotteryTypeIds);
		
		WeiboType weiboType = WeiboType.getItem(weiboTypeId);
		
		List<WeiboLottery> weiboLotteryList = weiboLotteryService.findWeiboLotteryList(uid, weiboType);	//查询微博分享彩种记录列表
		if (weiboLotteryList != null && weiboLotteryList.size() != 0) {
			for (WeiboLottery weiboLottery : weiboLotteryList) {	//循环微博分享已经添加的彩种记录列表
				weiboLotteryService.deleteWeiboLottery(weiboLottery);//删除微博分享已经添加的彩种记录
			}
		}
		
		for (Integer tempId : lotteryTypeIds) {
			WeiboLottery weiboLottery = new WeiboLottery();
			weiboLottery.setUid(uid);									//设置微博编号
			weiboLottery.setWeiboType(weiboType);						//设置微博类型
			weiboLottery.setLotteryType(LotteryType.getItem(tempId));	//设置彩种
			
			weiboLotteryService.addWeiboLottery(weiboLottery); 		//添加微博分享彩种记录
		}
		
		logger.info("更新成功");
		super.setForwardUrl("/weibo/weiboLottery.do?action=input&uid=" + uid + "&weiboTypeId=" + weiboTypeId);
		return "success";
	}
	
	public List<LotteryType> getLotteryTypeList() {
		return LotteryType.getItems();
	}

	public WeiboLotteryService getWeiboLotteryService() {
		return weiboLotteryService;
	}

	public void setWeiboLotteryService(WeiboLotteryService weiboLotteryService) {
		this.weiboLotteryService = weiboLotteryService;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public Integer getWeiboTypeId() {
		return weiboTypeId;
	}

	public void setWeiboTypeId(Integer weiboTypeId) {
		this.weiboTypeId = weiboTypeId;
	}
	
	public List<Integer> getLotteryTypeIds() {
		return lotteryTypeIds;
	}

	public void setLotteryTypeIds(List<Integer> lotteryTypeIds) {
		this.lotteryTypeIds = lotteryTypeIds;
	}

	public String getWeiboLotteryArraryStr() {
		return weiboLotteryArraryStr;
	}

	public void setWeiboLotteryArraryStr(String weiboLotteryArraryStr) {
		this.weiboLotteryArraryStr = weiboLotteryArraryStr;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Integer> getWeiboTypeIdList() {
		return weiboTypeIdList;
	}

	public void setWeiboTypeIdList(List<Integer> weiboTypeIdList) {
		this.weiboTypeIdList = weiboTypeIdList;
	}

	public List<TokenInfo> getTokenInfoList() {
		return tokenInfoList;
	}

	public void setTokenInfoList(List<TokenInfo> tokenInfoList) {
		this.tokenInfoList = tokenInfoList;
	}

	public Map<Integer, WeiboService> getWeiboServiceMap() {
		return weiboServiceMap;
	}

	public void setWeiboServiceMap(Map<Integer, WeiboService> weiboServiceMap) {
		this.weiboServiceMap = weiboServiceMap;
	}
}
