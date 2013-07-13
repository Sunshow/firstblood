package web.action.game;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.game.GameManageService;
import com.lehecai.core.api.game.GameServer;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.game.ServerRecharge;
import com.lehecai.core.game.ServerStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author jinsheng
 */
public class GameServerManageAction extends BaseAction {

    private static final long serialVersionUID = 7261456842092636090L;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Integer gameId;
	private GameServer gameServer;
	private List<Integer> serverRechargeValues;
	private List<Integer> serverStatusValues;
    private List<GameServer> gameServerList;
    private GameManageService gameManageService;
    
    private String orderStr;
    private String orderView;

    public String handle(){
        logger.info("进入游戏服务器管理");
        return "list";
    }

    public String input() {
        logger.info("进入输入游戏服务器信息");

        if (gameId != null && gameId > 0) {
            logger.info("进入更新游戏服务器");

            try {
                gameServerList = gameManageService.queryGameServerByGameId(gameId);

                if (gameServerList == null) {
                    logger.info("没有查询到游戏服务器信息");
                }
            } catch (ApiRemoteCallFailedException e) {
                logger.error("查询游戏服务器,api调用异常" + e.getMessage());
                super.setErrorMessage("查询游戏服务器,api调用异常" + e.getMessage());
                return "failure";
            }
        }
        return "input";
    }

    public String manageGameServer() {
        logger.info("更新游戏服务器开始");

        if (gameServerList != null && gameServerList.size() > 0) {
            for (int i = 0; i < gameServerList.size(); i++) {
            	GameServer gameServerUpdate = gameServerList.get(i);
            	
            	if (gameServerUpdate != null) {
            		try {	
                    	ServerRecharge serverRecharge = serverRechargeValues.get(i) == null ? null : ServerRecharge.getItem(serverRechargeValues.get(i));
                    	ServerStatus serverStatus = serverStatusValues.get(i) == null ? null : ServerStatus.getItem(serverStatusValues.get(i));
        				gameServerUpdate.setServerRecharge(serverRecharge);
        				gameServerUpdate.setServerStatus(serverStatus);
        				
                        gameManageService.manageGameServer(gameServerUpdate);
                    } catch (ApiRemoteCallFailedException e) {
                        logger.error("修改游戏服务器,api调用异常" + e.getMessage());
                        super.setErrorMessage("修改游戏物品,api调用异常" + e.getMessage());
                        return "failure";
                    } 
            	}                    
            }
        }
        logger.info("更新游戏服务器结束");
        super.setSuccessMessage("更新游戏服务器结束");
        super.setForwardUrl("/game/gameInfoManage.do?action=query" + "&orderStr=" + orderStr + "&orderView=" + orderView);
        return "success";
    }

    public List<ServerStatus> getGameServerStatusList() {
        return ServerStatus.getItems();
    }

    public List<ServerRecharge> getGameServerRechargeList() {
        return ServerRecharge.getItems();
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public GameServer getGameServer() {
        return gameServer;
    }

    public void setGameServer(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public List<Integer> getServerRechargeValues() {
		return serverRechargeValues;
	}

	public void setServerRechargeValues(List<Integer> serverRechargeValues) {
		this.serverRechargeValues = serverRechargeValues;
	}

	public List<Integer> getServerStatusValues() {
		return serverStatusValues;
	}

	public void setServerStatusValues(List<Integer> serverStatusValues) {
		this.serverStatusValues = serverStatusValues;
	}

    public List<GameServer> getGameServerList() {
		return gameServerList;
	}

	public void setGameServerList(List<GameServer> gameServerList) {
		this.gameServerList = gameServerList;
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