/**
 * 
 */
package web.action.weibo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.weibo.TokenInfo;
import com.lehecai.admin.web.domain.weibo.WeiboLottery;
import com.lehecai.admin.web.service.weibo.WeiboLotteryService;
import com.lehecai.admin.web.service.weibo.WeiboService;
import com.lehecai.core.api.bean.ApiResultBean;
import com.lehecai.core.lottery.WeiboType;

/**
 * @author qatang
 *
 */
public class WeiboAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(WeiboAction.class);
	
	private List<Integer> weiboTypeIdList;
	private String content;
	private Integer lotteryTypeId;
	
	private Map<Integer, WeiboService> weiboServiceMap;
	private WeiboLotteryService weiboLotteryService;
	
	private List<String> successMsgList;			//微薄发表成功结果
	private List<String> failMsgList;				//微薄发表失败结果
	
	public String handle() {
		return "authorizedPage";
	}
	
	public String edit() {
		logger.info("进入编辑分享内容");
		logger.info("content.........." + content);
		return "editContent";
	}
	
	public String publish() {
		if (weiboTypeIdList == null || weiboTypeIdList.size() == 0) {
			logger.error("微博类型列表为空");
			super.setErrorMessage("微博类型列表为空，请先选择微博类型");
			return "failure";
		}
		if (content == null || content.equals("")) {
			logger.error("微博内容为空");
			super.setErrorMessage("微博内容不能为空");
			return "failure";
		}
		
		successMsgList = new ArrayList<String>();
		failMsgList = new ArrayList<String>();
		for (Integer weiboTypeId : weiboTypeIdList) {
			WeiboService weiboService = weiboServiceMap.get(weiboTypeId);
			if (weiboService == null) {
				logger.error("未找到对应微博类型为{}的service实现类", weiboTypeId);
				super.setErrorMessage("微博类型为" + weiboTypeId + "的service实现类为空");
				return "failure";
			}
			
			List<TokenInfo> newTokenInfoList = new ArrayList<TokenInfo>();
			List<TokenInfo> tokenInfoList = weiboService.getTokenList(WeiboType.getItem(weiboTypeId));
			for (TokenInfo tokenInfo : tokenInfoList) {
				List<WeiboLottery> weiboLotteryList = weiboLotteryService.findWeiboLotteryList(tokenInfo.getUid(), WeiboType.getItem(weiboTypeId));
				for (WeiboLottery weiboLottery : weiboLotteryList) {
					if (weiboLottery.getLotteryType().getValue() == lotteryTypeId) {
						newTokenInfoList.add(tokenInfo);
						break;
					}
				}
			}
			
			if (newTokenInfoList != null && newTokenInfoList.size() != 0) {
				List<ApiResultBean> tempList = weiboService.publish(content, newTokenInfoList);
				for(ApiResultBean bean : tempList) {
					if (bean.getResult()) {
						successMsgList.add(bean.getMessage());
					} else {
						failMsgList.add(bean.getMessage());
					}
				}
			} else {
				logger.warn("{}微博没有绑定任何彩种", WeiboType.getItem(weiboTypeId));
				failMsgList.add(WeiboType.getItem(weiboTypeId).getName() + "微博没有绑定任何彩种");
			}
		}
		
		return "list_result";
	}
	
	public List<WeiboType> getWeiboTypeList() {
		return WeiboType.getItems();
	}


	public List<Integer> getWeiboTypeIdList() {
		return weiboTypeIdList;
	}

	public void setWeiboTypeIdList(List<Integer> weiboTypeIdList) {
		this.weiboTypeIdList = weiboTypeIdList;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}

	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}

	public Map<Integer, WeiboService> getWeiboServiceMap() {
		return weiboServiceMap;
	}

	public void setWeiboServiceMap(Map<Integer, WeiboService> weiboServiceMap) {
		this.weiboServiceMap = weiboServiceMap;
	}

	public WeiboLotteryService getWeiboLotteryService() {
		return weiboLotteryService;
	}

	public void setWeiboLotteryService(WeiboLotteryService weiboLotteryService) {
		this.weiboLotteryService = weiboLotteryService;
	}

	public List<String> getSuccessMsgList() {
		return successMsgList;
	}

	public void setSuccessMsgList(List<String> successMsgList) {
		this.successMsgList = successMsgList;
	}

	public List<String> getFailMsgList() {
		return failMsgList;
	}

	public void setFailMsgList(List<String> failMsgList) {
		this.failMsgList = failMsgList;
	}
}
