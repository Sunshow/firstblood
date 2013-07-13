package web.service.game;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.game.GameInfo;
import com.lehecai.core.api.game.GameItem;
import com.lehecai.core.api.game.GameServer;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

import java.util.List;
import java.util.Map;

/**
 * @author He Wang
 */
public interface GameManageService {

    /**
     * 查询游戏信息
     * @param gameInfo
     * @param pageBean
     * @return
     * @throws com.lehecai.core.exception.ApiRemoteCallFailedException
     */
    public Map<String, Object> queryGameInfoList(GameInfo gameInfo, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;

    /**
     * 更改游戏信息
     * @param gameInfo
     * @throws com.lehecai.core.exception.ApiRemoteCallFailedException
     */
    public void manageGameInfo(GameInfo gameInfo) throws ApiRemoteCallFailedException;

    /**
     * 根据游戏id获取游戏信息
     * @param gameId
     * @return
     * @throws com.lehecai.core.exception.ApiRemoteCallFailedException
     */
    public GameInfo queryGameInfoById(Integer gameId) throws ApiRemoteCallFailedException;
    /**
     * 根据游戏id获取游戏服务器信息
     * @param gameId
     * @return
     * @throws com.lehecai.core.exception.ApiRemoteCallFailedException
     */
    public List<GameServer> queryGameServerByGameId(Integer gameId) throws ApiRemoteCallFailedException;

    /**
     * 更改游戏服务器信息
     * @param gameServer
     * @throws com.lehecai.core.exception.ApiRemoteCallFailedException
     */
    public void manageGameServer(GameServer gameServer) throws ApiRemoteCallFailedException;

    /**
     * 根据游戏id获取游戏物品信息
     * @param gameId
     * @return
     * @throws com.lehecai.core.exception.ApiRemoteCallFailedException
     */
    public List<GameItem> queryGameItemByGameId(Integer gameId) throws ApiRemoteCallFailedException;

    /**
     * 更改游戏物品信息
     * @param gameItem
     * throws ApiRemoteCallFailedException
     */
    public void manageGameItem(GameItem gameItem) throws ApiRemoteCallFailedException;
}
