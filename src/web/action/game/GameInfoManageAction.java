package web.action.game;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.game.GameManageService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.game.GameInfo;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.game.GameFeature;
import com.lehecai.core.game.GameGroup;
import com.lehecai.core.game.GameSellStatus;
import com.lehecai.core.game.GameStatus;
import com.lehecai.core.game.GameType;

/**
 * @author jinsheng
 */
public class GameInfoManageAction extends BaseAction {

    private static final long serialVersionUID = 3078900224676188486L;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private GameInfo gameInfo;
	private List<GameInfo> gameInfoList;
    private GameManageService gameManageService;
    private Map<String, String> orderStrMap;
    private Map<String, String> orderViewMap;
    private List<GameFeature> gameFeatureList;

    private String orderStr;
    private String orderView;

    private Integer gameId;
    
    private int gameTypeValue = GameType.ALL.getValue();
	private int gameStatusValue = GameStatus.ALL.getValue();
    private int gameGroupValue = GameGroup.ALL.getValue();
    private int gameSellStatusValue = GameSellStatus.ALL.getValue();
    private int gameRecommendValue = YesNoStatus.ALL.getValue();
    private int gameTplValue = YesNoStatus.ALL.getValue();
    private int gameFeatureValue = GameFeature.ALL.getValue();
    private String gameRechargeCreditValue ;

	public String handle(){
        logger.info("进入游戏管理");
        return "list";
    }

    @SuppressWarnings("unchecked")
    public String query(){
        logger.info("进入查询游戏基本信息");
        HttpServletRequest request = ServletActionContext.getRequest();
        Map<String, Object> map = null;

        try {
            if (gameInfo == null) {
                gameInfo = new GameInfo();
            }
            GameType gameType = GameType.getItem(gameTypeValue);
            GameStatus gameStatus = GameStatus.getItem(gameStatusValue);
            GameGroup  gameGroup = GameGroup.getItem(gameGroupValue);
            GameSellStatus gameSellStatus = GameSellStatus.getItem(gameSellStatusValue);
            YesNoStatus gameRecommend = YesNoStatus.getItem(gameRecommendValue);
            YesNoStatus gameTpl = YesNoStatus.getItem(gameTplValue);

            gameInfo.setGameType(gameType);
            gameInfo.setGameStatus(gameStatus);
            gameInfo.setGameGroup(gameGroup);
            gameInfo.setGameSellStatus(gameSellStatus);
            gameInfo.setGameRecommend(gameRecommend);
            gameInfo.setGameTpl(gameTpl);

            map = gameManageService.queryGameInfoList(gameInfo, orderStr, orderView, super.getPageBean());
        } catch (ApiRemoteCallFailedException e) {
            logger.error("查询游戏基本信息,api调用异常" + e.getMessage());
            super.setErrorMessage("查询游戏基本信息,api调用异常" + e.getMessage());
            return "failure";
        }
        if (map == null || map.size() == 0) {
            logger.error("API查询游戏基本信息为空");
            super.setErrorMessage("API查询游戏基本信息为空");
            return "failure";
        }
        setGameInfoList((List<GameInfo>) map.get(Global.API_MAP_KEY_LIST));
        PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
        super.setPageString(PageUtil.getPageString(request, pageBean));
        super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
        logger.info("查询游戏基本信息结束");
        return "list";
    }

	public String input() {
    	logger.info("进入输入游戏基本信息");
    	gameFeatureList = GameFeature.getItems();
        if (gameId != null && gameId != 0) {
        	logger.info("进入更新游戏信息");
        	try {
				gameInfo = gameManageService.queryGameInfoById(gameId);
				if (gameInfo.getGameRechargeCredit() != null) {
					gameRechargeCreditValue = gameInfo.getGameRechargeCredit() + "";
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询游戏基本信息,api调用异常" + e.getMessage());
	            super.setErrorMessage("查询游戏基本信息,api调用异常" + e.getMessage());
	            return "failure";				
			}
        }
        return "input";
    }

    public String manageGameInfo() {
        logger.info("更新游戏基本信息开始");
        try {
            GameType gameType = GameType.getItem(gameTypeValue);
            GameStatus gameStatus = GameStatus.getItem(gameStatusValue);
            GameGroup gameGroup = GameGroup.getItem(gameGroupValue);
            GameSellStatus gameSellStatus = GameSellStatus.getItem(gameSellStatusValue);
            YesNoStatus gameRecommend = YesNoStatus.getItem(gameRecommendValue);
            YesNoStatus gameTpl = YesNoStatus.getItem(gameTplValue);

			gameInfo.setGameType(gameType);
			gameInfo.setGameStatus(gameStatus);
			gameInfo.setGameGroup(gameGroup);
			gameInfo.setGameSellStatus(gameSellStatus);
			gameInfo.setGameRecommend(gameRecommend);
			gameInfo.setGameTpl(gameTpl);
			gameInfo.setGameRechargeCredit(gameRechargeCreditValue);
			gameInfo.setGameFeature(GameFeature.getItem(gameFeatureValue));
            gameManageService.manageGameInfo(gameInfo);
        } catch (ApiRemoteCallFailedException e) {
            logger.error("修改游戏基本信息,api调用异常" + e.getMessage());
            super.setErrorMessage("修改游戏基本信息,api调用异常" + e.getMessage());
            return "failure";
        }
        logger.info("更新游戏基本信息结束");
        super.setSuccessMessage("更新游戏基本信息结束");
        super.setForwardUrl("/game/gameInfoManage.do?action=query" + "&orderStr=" + orderStr + "&orderView=" + orderView);
        return "success";
    }

    public List<GameType> getGameTypeList() {
        return GameType.getItems();
    }

    public List<YesNoStatus> getGameRecommendList() {
        return YesNoStatus.getItems();
    }

    public List<GameGroup> getGameGroupList() {
        return GameGroup.getItems();
    }

    public List<GameSellStatus> getGameSellStatusList() {
        return GameSellStatus.getItems();
    }

    public List<GameStatus> getGameStatusList() {
        return GameStatus.getItems();
    }
    
    public List<YesNoStatus> getGameTplList() {
    	return YesNoStatus.getItems();
    }
    
    public GameInfo getGameInfo() {
		return gameInfo;
	}

	public void setGameInfo(GameInfo gameInfo) {
		this.gameInfo = gameInfo;
	}

	public List<GameInfo> getGameInfoList() {
		return gameInfoList;
	}

	public void setGameInfoList(List<GameInfo> gameInfoList) {
		this.gameInfoList = gameInfoList;
	}

	public GameManageService getGameManageService() {
		return gameManageService;
	}

	public void setGameManageService(GameManageService gameManageService) {
		this.gameManageService = gameManageService;
	}

    public Map<String, String> getOrderStrMap() {
        orderStrMap = new HashMap<String, String>();
        orderStrMap.put(GameInfo.GAME_ID, "游戏id");
        orderStrMap.put(GameInfo.GAME_TYPE, "游戏类型");
        orderStrMap.put(GameInfo.GAME_SORT, "游戏排序");
        return orderStrMap;
    }

    public void setOrderStrMap(Map<String, String> orderStrMap) {
        this.orderStrMap = orderStrMap;
    }

    public Map<String, String> getOrderViewMap() {
        orderViewMap = new HashMap<String, String>();
        orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
        orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
        return orderViewMap;
    }
    
    public Map<String, String> getRechargeCreditMap() {
    	Map<String, String> rechargeCreditMap = new LinkedHashMap<String, String>();
    	rechargeCreditMap.put("y", "是");
    	rechargeCreditMap.put("n", "否");
        return rechargeCreditMap;
    }

    public void setOrderViewMap(Map<String, String> orderViewMap) {
        this.orderViewMap = orderViewMap;
    }
    
    public String getOrderStr() {
		return orderStr;
	}

	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}

	public String getOrderView() {
		return orderView;
	}

	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	public int getGameTypeValue() {
		return gameTypeValue;
	}

	public void setGameTypeValue(int gameTypeValue) {
		this.gameTypeValue = gameTypeValue;
	}

	public int getGameStatusValue() {
		return gameStatusValue;
	}

	public void setGameStatusValue(int gameStatusValue) {
		this.gameStatusValue = gameStatusValue;
	}

	public int getGameGroupValue() {
		return gameGroupValue;
	}

	public void setGameGroupValue(int gameGroupValue) {
		this.gameGroupValue = gameGroupValue;
	}

	public int getGameSellStatusValue() {
		return gameSellStatusValue;
	}

	public void setGameSellStatusValue(int gameSellStatusValue) {
		this.gameSellStatusValue = gameSellStatusValue;
	}

	public int getGameRecommendValue() {
		return gameRecommendValue;
	}

	public void setGameRecommendValue(int gameRecommendValue) {
		this.gameRecommendValue = gameRecommendValue;
	}
	
	public int getGameTplValue() {
		return gameTplValue;
	}

	public void setGameTplValue(int gameTplValue) {
		this.gameTplValue = gameTplValue;
	}

	public void setGameFeatureList(List<GameFeature> gameFeatureList) {
		this.gameFeatureList = gameFeatureList;
	}

	public List<GameFeature> getGameFeatureList() {
		return gameFeatureList;
	}

	public void setGameFeatureValue(int gameFeatureValue) {
		this.gameFeatureValue = gameFeatureValue;
	}

	public int getGameFeatureValue() {
		return gameFeatureValue;
	}

	public void setGameRechargeCreditValue(String gameRechargeCreditValue) {
		this.gameRechargeCreditValue = gameRechargeCreditValue;
	}

	public String getGameRechargeCreditValue() {
		return gameRechargeCreditValue;
	}
}