package web.service.impl.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.game.GameManageService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.game.GameInfo;
import com.lehecai.core.api.game.GameItem;
import com.lehecai.core.api.game.GameServer;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.game.GameFeature;
import com.lehecai.core.game.GameGroup;
import com.lehecai.core.game.GameSellStatus;
import com.lehecai.core.game.GameStatus;
import com.lehecai.core.game.GameType;
import com.lehecai.core.game.ItemStatus;
import com.lehecai.core.game.ItemType;
import com.lehecai.core.game.ServerRecharge;
import com.lehecai.core.game.ServerStatus;

/**
 * @author jinsheng
 */
public class GameManageServiceImpl implements GameManageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private ApiRequestService gameApiWriteRequestService;
    
    private ApiRequestService gameApiRequestService;
    

    @Override
    public Map<String, Object> queryGameInfoList(GameInfo gameInfo, String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException {
        logger.info("进入调用API查询游戏信息");
        ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_GAME_INFO_QUERY);
       
        if (!StringUtils.isEmpty(gameInfo.getGameId())) {
        	request.setParameter(GameInfo.GAME_ID, gameInfo.getGameId());
        }              
        if (gameInfo.getGameType() != null && gameInfo.getGameType().getValue() != GameType.ALL.getValue()) {
        	request.setParameter(GameInfo.GAME_TYPE, gameInfo.getGameType().getValue() + "");
        }
        if (gameInfo.getGameStatus() != null && gameInfo.getGameStatus().getValue() != GameStatus.ALL.getValue()) {
        	request.setParameter(GameInfo.GAME_STATUS, gameInfo.getGameStatus().getValue() + "");
        }
        if (gameInfo.getGameGroup() != null && gameInfo.getGameGroup().getValue() != GameGroup.ALL.getValue()) {
        	request.setParameter(GameInfo.GAME_GROUP, gameInfo.getGameGroup().getValue() + "");
        }
        if (gameInfo.getGameSellStatus() != null && gameInfo.getGameSellStatus().getValue() != GameSellStatus.ALL.getValue()) {
        	request.setParameter(GameInfo.GAME_SELL_STAUTS, gameInfo.getGameSellStatus().getValue() + "");
        }
        if (gameInfo.getGameRecommend() != null && gameInfo.getGameRecommend().getValue() != YesNoStatus.ALL.getValue()) {
        	request.setParameter(GameInfo.GAME_RECOMMEND, gameInfo.getGameRecommend().getValue() + "");
        }
        if (gameInfo.getGameTpl() != null && gameInfo.getGameTpl().getValue() != YesNoStatus.ALL.getValue()) {
        	request.setParameter(GameInfo.GAME_TPL, gameInfo.getGameTpl().getValue() + "");
        }
        if (!StringUtils.isEmpty(gameInfo.getGameShortName())) {
        	request.setParameter(GameInfo.GAME_SHORT_NAME, gameInfo.getGameShortName());
        }
        if (!StringUtils.isEmpty(gameInfo.getGameSort())) {
            request.setParameter(GameInfo.GAME_SORT, gameInfo.getGameSort());
        }
        if (!StringUtils.isEmpty(orderStr) && !StringUtils.isEmpty(orderView)) {
            request.addOrder(orderStr,orderView);
        } else {
            request.addOrder(GameInfo.GAME_SORT, ApiConstant.API_REQUEST_ORDER_ASC);
        }
        if (pageBean != null) {
            request.setPage(pageBean.getPage());
            request.setPagesize(pageBean.getPageSize());
        }
        logger.info("Request Query String: {}", request.toQueryString());
        ApiResponse response = gameApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
        
        if (response == null) {
            logger.error("调用API查询游戏信息失败");
            throw new ApiRemoteCallFailedException("调用API查询游戏信息失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("调用API查询游戏信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
            throw new ApiRemoteCallFailedException("调用API查询游戏信息请求出错," + response.getMessage());
        }
        logger.info("结束调用查询游戏信息API");
        List<GameInfo> gameOrderList = GameInfo.convertFromJSONArray(response.getData());

        if (pageBean != null && pageBean.isPageFlag()) {
            int totalCount = response.getTotal();
            int pageCount = 0;
            pageBean.setCount(totalCount);
            
            if (pageBean.getPageSize() != 0 ) {
                pageCount = totalCount / pageBean.getPageSize();
                
                if (totalCount % pageBean.getPageSize() != 0) {
                    pageCount ++;
                }
            }
            pageBean.setPageCount(pageCount);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
        map.put(Global.API_MAP_KEY_LIST, gameOrderList);
        return map;
    }

    @Override
    public void manageGameInfo(GameInfo gameInfo) throws ApiRemoteCallFailedException {

        logger.info("进入调用API更新游戏信息");
        ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_GAME_INFO_UPDATE);
        
        if (gameInfo == null) {
            logger.error("游戏信息为空");
            return;
        }
        if (!StringUtils.isEmpty(gameInfo.getGameId())) {
            request.setParameterForUpdate(GameInfo.GAME_ID, gameInfo.getGameId());
        }
        if (gameInfo.getGameType() != null && gameInfo.getGameType().getValue() != GameType.ALL.getValue()) {
            request.setParameterForUpdate(GameInfo.GAME_TYPE, gameInfo.getGameType().getValue() + "");
        }
        if (gameInfo.getGameStatus() != null && gameInfo.getGameStatus().getValue() != GameStatus.ALL.getValue()) {
            request.setParameterForUpdate(GameInfo.GAME_STATUS, gameInfo.getGameStatus().getValue() + "");
        }
        if (gameInfo.getGameSellStatus() != null && gameInfo.getGameSellStatus().getValue() != GameSellStatus.ALL.getValue()) {
            request.setParameterForUpdate(GameInfo.GAME_SELL_STAUTS, gameInfo.getGameSellStatus().getValue() + "");
        }
        if (gameInfo.getGameRecommend() != null && gameInfo.getGameRecommend().getValue() != YesNoStatus.ALL.getValue()) {
            request.setParameterForUpdate(GameInfo.GAME_RECOMMEND, gameInfo.getGameRecommend().getValue() + "");
        }
        if (gameInfo.getGameGroup() != null && gameInfo.getGameGroup().getValue() != GameGroup.ALL.getValue()) {
            request.setParameterForUpdate(GameInfo.GAME_GROUP, gameInfo.getGameGroup().getValue() + "");
        }
        if (gameInfo.getGameFeature() != null && gameInfo.getGameFeature().getValue() != GameFeature.ALL.getValue()) {
            request.setParameterForUpdate(GameInfo.GAME_FEATURE, gameInfo.getGameFeature().getValue() + "");
        }
        if (gameInfo.getGameRechargeCredit() != null) {
            request.setParameterForUpdate(GameInfo.GAME_RECHARGE_CREDIT, gameInfo.getGameRechargeCredit());
        }
        if (gameInfo.getGameName() != null) {
            request.setParameterForUpdate(GameInfo.GAME_NAME, gameInfo.getGameName());
        }
        if (gameInfo.getGameShortName() != null) {
            request.setParameterForUpdate(GameInfo.GAME_SHORT_NAME, gameInfo.getGameShortName());
        }
        if (gameInfo.getGameSort() != null) {
            request.setParameterForUpdate(GameInfo.GAME_SORT, gameInfo.getGameSort());
        }
        if (gameInfo.getGameUrl() != null) {
            request.setParameterForUpdate(GameInfo.GAME_URL, gameInfo.getGameUrl());
        }
        if (gameInfo.getGameTpl() != null && gameInfo.getGameTpl().getValue() != YesNoStatus.ALL.getValue()) {
        	request.setParameterForUpdate(GameInfo.GAME_TPL, gameInfo.getGameTpl().getValue() + "");
        }
        if (gameInfo.getGameCompany() != null) {
            request.setParameterForUpdate(GameInfo.GAME_COMPANY, gameInfo.getGameCompany());
        }
        if (gameInfo.getGameCompanyUrl() != null) {
            request.setParameterForUpdate(GameInfo.GAME_COMPANY_URL, gameInfo.getGameCompanyUrl());
        }
        if (gameInfo.getGameDes() != null) {
            request.setParameterForUpdate(GameInfo.GAME_DES, gameInfo.getGameDes());
        }
        if (gameInfo.getGameRecommendDes() != null) {
            request.setParameterForUpdate(GameInfo.GAME_RECOMMEND_DES, gameInfo.getGameRecommendDes());
        }
        if (gameInfo.getGameCardUrl() != null) {
            request.setParameterForUpdate(GameInfo.GAME_CARD_URL, gameInfo.getGameCardUrl());
        }
        if (gameInfo.getGameBbsUrl() != null) {
            request.setParameterForUpdate(GameInfo.GAME_BBS_URL, gameInfo.getGameBbsUrl());
        }
        if (gameInfo.getGameCmsNotice() != null) {
            request.setParameterForUpdate(GameInfo.GAME_CMS_NOTICE, gameInfo.getGameCmsNotice());
        }
        if (gameInfo.getGameCmsRaider() != null) {
            request.setParameterForUpdate(GameInfo.GAME_CMS_RAIDER, gameInfo.getGameCmsRaider());
        }
        if (gameInfo.getGameCmsData() != null) {
            request.setParameterForUpdate(GameInfo.GAME_CMS_DATA, gameInfo.getGameCmsData());
        }
        if (gameInfo.getGamePlayWidth() != null) {
            request.setParameterForUpdate(GameInfo.GAME_PLAY_WIDTH, gameInfo.getGamePlayWidth());
        }
        if (gameInfo.getGamePlayHeight() != null) {
            request.setParameterForUpdate(GameInfo.GAME_PLAY_HEIGHT, gameInfo.getGamePlayHeight());
        }
        if (gameInfo.getGameCustomerService() != null) {
            request.setParameterForUpdate(GameInfo.GAME_CUSTOMER_SERVICE, gameInfo.getGameCustomerService());
        }
        if (gameInfo.getGameLogoHome() != null) {
            request.setParameterForUpdate(GameInfo.GAME_LOGO_HOME, gameInfo.getGameLogoHome());
        }
        if (gameInfo.getGameLogoRecommend() != null) {
            request.setParameterForUpdate(GameInfo.GAME_LOGO_RECOMMEND, gameInfo.getGameLogoRecommend());
        }
        if (gameInfo.getGameLogoBack() != null) {
            request.setParameterForUpdate(GameInfo.GAME_LOGO_BACK, gameInfo.getGameLogoBack());
        }
        if (gameInfo.getGameLogoScreenshotOne() != null) {
            request.setParameterForUpdate(GameInfo.GAME_LOGO_SCREENSHOT_ONE, gameInfo.getGameLogoScreenshotOne());
        }
        if (gameInfo.getGameLogoScreenshotTwo() != null) {
            request.setParameterForUpdate(GameInfo.GAME_LOGO_SCREENSHOT_TWO, gameInfo.getGameLogoScreenshotTwo());
        }
        if (gameInfo.getGameLogoScreenshotThree() != null) {
            request.setParameterForUpdate(GameInfo.GAME_LOGO_SCREENSHOT_THREE, gameInfo.getGameLogoScreenshotThree());
        }
        logger.info("Request Query String: {}", request.toQueryString());
        ApiResponse response = gameApiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);

        if (response == null) {
            logger.error("调用API更新游戏信息失败");
            throw new ApiRemoteCallFailedException("调用API更新游戏信息失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("调用API更新游戏信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
            throw new ApiRemoteCallFailedException("调用API更新游戏信息请求出错," + response.getMessage());
        }
        logger.info("结束调用更新游戏信息API");
    }
    
    @Override
    public GameInfo queryGameInfoById(Integer gameId) throws ApiRemoteCallFailedException {
    	logger.info("进入调用API查询游戏信息");
    	ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_GAME_INFO_QUERY);

        if (gameId != null && gameId > 0) {
            request.setParameter(GameServer.GAME_ID, gameId + "");
        }
        logger.info("Request Query String: {}", request.toQueryString());
        ApiResponse response = gameApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);

        if (response == null) {
            logger.error("调用API查询游戏信息失败");
            throw new ApiRemoteCallFailedException("调用API查询游戏信息失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("调用API查询游戏信息请求出错, rc={}, message={}", response.getCode(), response.getMessage());
            throw new ApiRemoteCallFailedException("调用API查询游戏信息请求出错," + response.getMessage());
        }
        logger.info("结束调用查询游戏信息API");
        
        if (response.getTotal() == 0) {
        	logger.info("获取游戏基本信息，结果为空");
        	return null;
        } else {
        	return GameInfo.convertFromJSONArray(response.getData()).get(0);
        }
    }

    @Override
    public List<GameServer> queryGameServerByGameId(Integer gameId) throws ApiRemoteCallFailedException {
        logger.info("进入调用API查询游戏服务器");
        ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_GAME_SERVER_QUERY);

        if (gameId != null && gameId > 0) {
            request.setParameter(GameServer.GAME_ID, gameId + "");
        }
        logger.info("Request Query String: {}", request.toQueryString());
        ApiResponse response = gameApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);

        if (response == null) {
            logger.error("调用API查询游戏服务器失败");
            throw new ApiRemoteCallFailedException("调用API查询游戏服务器失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("调用API查询游戏服务器请求出错, rc={}, message={}", response.getCode(), response.getMessage());
            throw new ApiRemoteCallFailedException("调用API查询游戏服务器请求出错," + response.getMessage());
        }
        logger.info("结束调用查询游戏服务器API");
        
        if (response.getTotal() == 0) {
        	logger.info("获取游戏服务器，结果为空");
        	return null;
        } else {
        	return GameServer.convertFromJSONArray(response.getData());
        }        
    }

    @Override
    public void manageGameServer(GameServer gameServer) throws ApiRemoteCallFailedException {
        logger.info("进入调用API更新游戏服务器");
        ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_GAME_SERVER_UPDATE);
        
        if (gameServer == null) {
            logger.error("游戏服务器为空");
            return;
        }
        if (!StringUtils.isEmpty(gameServer.getServerId())) {
        	request.setParameterForUpdate(GameServer.SERVER_ID, gameServer.getServerId());
        }
        if (!StringUtils.isEmpty(gameServer.getGameId())) {
            request.setParameterForUpdate(GameServer.GAME_ID, gameServer.getGameId());
        }
        if (gameServer.getServerStatus() != null && gameServer.getServerStatus().getValue() != ServerStatus.ALL.getValue()) {
        	request.setParameterForUpdate(GameServer.SERVER_STATUS, gameServer.getServerStatus().getValue() + "");
        }
        if (gameServer.getServerSort() != null) {
        	request.setParameterForUpdate(GameServer.SERVER_SORT, gameServer.getServerSort());
        }
        if (gameServer.getServerName() != null) {
        	request.setParameterForUpdate(GameServer.SERVER_NAME, gameServer.getServerName());
        }
        if (gameServer.getServerRecharge() != null && gameServer.getServerRecharge().getValue() != ServerRecharge.ALL.getValue()) {
        	request.setParameterForUpdate(GameServer.SERVER_RECHARGE_OPEN, gameServer.getServerRecharge().getValue() + "");
        }
        if (gameServer.getAuthHost() != null) {
        	request.setParameterForUpdate(GameServer.AUTH_HOST, gameServer.getAuthHost());
        }
        if (gameServer.getApiPlay() != null) {
        	request.setParameterForUpdate(GameServer.API_PLAY, gameServer.getApiPlay());
        }
        if (gameServer.getApiRecharge() != null) {
        	request.setParameterForUpdate(GameServer.API_RECHARGE, gameServer.getApiRecharge());
        }
        if (gameServer.getApiQuery() != null) {
        	request.setParameterForUpdate(GameServer.API_QUERY, gameServer.getApiQuery());
        }
        if (gameServer.getAuthServerId() != null) {
        	request.setParameterForUpdate(GameServer.AUTH_SERVER_ID, gameServer.getAuthServerId());
        }
        if (gameServer.getAuthAppId() != null) {
        	request.setParameterForUpdate(GameServer.AUTH_APP_ID, gameServer.getAuthAppId());
        }
        if (gameServer.getAuthRechargeKey() != null) {
        	request.setParameterForUpdate(GameServer.AUTH_RECHARGE_KEY, gameServer.getAuthRechargeKey());
        }
        if (gameServer.getAuthPlayKey() != null) {
        	request.setParameterForUpdate(GameServer.AUTH_PLAY_KEY, gameServer.getAuthPlayKey());
        }
        if (gameServer.getAuthQueryKey() != null) {
        	request.setParameterForUpdate(GameServer.AUTH_QUERY_KEY, gameServer.getAuthQueryKey());
        }       
        logger.info("Request Query String: {}", request.toQueryString());
        ApiResponse response = gameApiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);

        if (response == null) {
            logger.error("调用API更新游戏服务器失败");
            throw new ApiRemoteCallFailedException("调用API更新游戏服务器失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("调用API更新游戏服务器请求出错, rc={}, message={}", response.getCode(), response.getMessage());
            throw new ApiRemoteCallFailedException("调用API更新游戏服务器请求出错," + response.getMessage());
        }
        logger.info("结束调用API更新游戏服务器");
    }

    @Override
    public List<GameItem> queryGameItemByGameId(Integer gameId) throws ApiRemoteCallFailedException {
        logger.info("进入调用API查询游戏物品");
        ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_GAME_ITEM_QUERY);

        if (gameId != null && gameId > 0) {
            request.setParameter(GameItem.GAME_ID, gameId + "");
        }
        logger.info("Request Query String: {}", request.toQueryString());

        ApiResponse response = gameApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);

        if (response == null) {
            logger.error("调用API查询游戏物品失败");
            throw new ApiRemoteCallFailedException("调用API查询游戏物品失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("调用API查询游戏物品请求出错, rc={}, message={}", response.getCode(), response.getMessage());
            throw new ApiRemoteCallFailedException("调用API查询游戏物品请求出错," + response.getMessage());
        }
        logger.info("结束调用查询游戏物品API");
        
        if (response.getTotal() == 0) {
        	logger.info("获取游戏物品，结果为空");
        	return null;
        } else {
        	return GameItem.convertFromJSONArray(response.getData());
        }        
    }

    @Override
    public void manageGameItem(GameItem gameItem) throws ApiRemoteCallFailedException {
        logger.info("进入调用API更新游戏物品");
        ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_GAME_ITEM_UPDATE);
        
        if (gameItem == null) {
            logger.error("游戏物品为空");
            return;
        }
        if (!StringUtils.isEmpty(gameItem.getGameId())) {
        	request.setParameterForUpdate(GameItem.GAME_ID, gameItem.getGameId());
        }
        if (!StringUtils.isEmpty(gameItem.getItemId())) {
        	request.setParameterForUpdate(GameItem.ITEM_ID, gameItem.getItemId());
        }
        if (gameItem.getItemType() != null && gameItem.getItemType().getValue() != ItemType.ALL.getValue()) {
        	request.setParameterForUpdate(GameItem.ITEM_TYPE, gameItem.getItemType().getValue() + "");
        }
        if (gameItem.getItemStatus() != null && gameItem.getItemStatus().getValue() != ItemStatus.ALL.getValue()) {
        	request.setParameterForUpdate(GameItem.ITEM_STATUS, gameItem.getItemStatus().getValue() + "");
        }
        if (gameItem.getItemSort() != null) {
        	request.setParameterForUpdate(GameItem.ITEM_SORT, gameItem.getItemSort());
        }
        if (gameItem.getItemGroup() != null) {
        	request.setParameterForUpdate(GameItem.ITEM_GROUP, gameItem.getItemGroup());
        }
        if (gameItem.getItemGroupName() != null) {
        	request.setParameterForUpdate(GameItem.ITEM_GROUP_NAME, gameItem.getItemGroupName());
        }
        if (gameItem.getItemName() != null) {
        	request.setParameterForUpdate(GameItem.ITEM_NAME, gameItem.getItemName());
        }
        if (gameItem.getItemDes() != null) {
        	request.setParameterForUpdate(GameItem.ITEM_DES, gameItem.getItemDes());
        }
        if (gameItem.getItemImage() != null) {
        	request.setParameterForUpdate(GameItem.ITEM_IMAGE, gameItem.getItemImage());
        }
        if (gameItem.getItemRateCash() != null) {
        	request.setParameterForUpdate(GameItem.ITEM_RATE_CASH, gameItem.getItemRateCash());
        }
        if (gameItem.getItemRateCredit() != null) {
        	request.setParameterForUpdate(GameItem.ITEM_RATE_CREDIT, gameItem.getItemRateCredit());
        }
        if (gameItem.getItemMinUnit() != null) {
        	request.setParameterForUpdate(GameItem.ITEM_MIN_UNIT, gameItem.getItemMinUnit());
        }
        logger.info("Request Query String: {}", request.toQueryString());

    	ApiResponse response = gameApiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
    	
    	if (response == null) {
            logger.error("调用API更新游戏物品失败");
            throw new ApiRemoteCallFailedException("调用API更新游戏物品失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("调用API更新游戏物品请求出错, rc={}, message={}", response.getCode(), response.getMessage());
            throw new ApiRemoteCallFailedException("调用API更新游戏物品请求出错," + response.getMessage());
        }
        logger.info("结束调用更新游戏物品API");
    }

	public void setGameApiRequestService(ApiRequestService gameApiRequestService) {
		this.gameApiRequestService = gameApiRequestService;
	}

	public ApiRequestService getGameApiRequestService() {
		return gameApiRequestService;
	}

	public void setGameApiWriteRequestService(ApiRequestService gameApiWriteRequestService) {
		this.gameApiWriteRequestService = gameApiWriteRequestService;
	}

	public ApiRequestService getGameApiWriteRequestService() {
		return gameApiWriteRequestService;
	}
}