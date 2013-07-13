package web.action.game;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.game.GameManageService;
import com.lehecai.core.api.game.GameItem;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.game.ItemStatus;
import com.lehecai.core.game.ItemType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author jinsheng
 */
public class GameItemManageAction extends BaseAction {

    private static final long serialVersionUID = -4966625326883124957L;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Integer gameId;
    private GameItem gameItem;
    private List<Integer> itemTypeValues;
	private List<Integer> itemStatusValues;
	private List<GameItem> gameItemList;
	private GameManageService gameManageService;
	
	private String orderStr;
	private String orderView;

    public String handle(){
        logger.info("进入游戏物品管理");	
        return "list";
    }
    
    public String input() {
        logger.info("进入输入游戏物品信息");
        
        if (gameId != null && gameId > 0) {
            logger.info("进入更新游戏物品");

            try {
                gameItemList = gameManageService.queryGameItemByGameId(gameId);

                if (gameItemList == null) {
                    logger.info("没有查询到游戏物品信息");                   
                }
            } catch (ApiRemoteCallFailedException e) {
                logger.error("查询游戏物品,api调用异常" + e.getMessage());
                super.setErrorMessage("查询游戏物品,api调用异常" + e.getMessage());
                return "failure";
            }
        }
        return "input";
    }
    
    public String manageGameItem() {
        logger.info("更新游戏物品开始");

        if (gameItemList != null && gameItemList.size() > 0) {
            for (int i = 0; i < gameItemList.size(); i++) {
            	GameItem gameItemUpdate = gameItemList.get(i);
            	
            	if (gameItemUpdate != null) {
            		try {
                    	ItemType itemType = itemTypeValues.get(i) == null ? null : ItemType.getItem(itemTypeValues.get(i));
                    	ItemStatus itemStatus = itemStatusValues.get(i) == null ? null : ItemStatus.getItem(itemStatusValues.get(i));
                    	gameItemUpdate.setItemType(itemType);
                    	gameItemUpdate.setItemStatus(itemStatus);
        				
                        gameManageService.manageGameItem(gameItemUpdate);
                    } catch (ApiRemoteCallFailedException e) {
                       logger.error("修改游戏物品,api调用异常" + e.getMessage());
                       super.setErrorMessage("修改游戏物品,api调用异常" + e.getMessage());
                       return "failure";
                    }           
            	}              
            }
        }
        logger.info("更新游戏物品结束");
        super.setSuccessMessage("更新游戏物品结束");
        super.setForwardUrl("/game/gameInfoManage.do?action=query" + "&orderStr=" + orderStr + "&orderView=" + orderView);
        return "success";
    }
    
    public List<ItemType> getGameItemTypeList() {
    	return ItemType.getItems();
    }
    
    public List<ItemStatus> getGameItemStatusList() {
    	return ItemStatus.getItems();
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }
    
    public GameItem getGameItem() {
		return gameItem;
	}

	public void setGameItem(GameItem gameItem) {
		this.gameItem = gameItem;
	}
	
	public List<Integer> getItemTypeValues() {
		return itemTypeValues;
	}

	public void setItemTypeValues(List<Integer> itemTypeValues) {
		this.itemTypeValues = itemTypeValues;
	}

	public List<Integer> getItemStatusValues() {
		return itemStatusValues;
	}

	public void setItemStatusValues(List<Integer> itemStatusValues) {
		this.itemStatusValues = itemStatusValues;
	}
    
    public List<GameItem> getGameItemList() {
		return gameItemList;
	}

	public void setGameItemList(List<GameItem> gameItemList) {
		this.gameItemList = gameItemList;
	}

    public GameManageService getGameManageService() {
        return gameManageService;
    }

    public void setGameManageService(GameManageService gameManageService) {
        this.gameManageService = gameManageService;
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
}