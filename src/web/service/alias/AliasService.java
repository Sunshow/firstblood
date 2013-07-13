package web.service.alias;

import java.util.List;

import com.lehecai.admin.web.bean.AliasMatchBean;
import com.lehecai.admin.web.enums.AliasDataProvider;

public interface AliasService {

	/**
	 * 根据比赛信息获取别名（足球）
	 * @param aliasDataProvider 数据商
	 * @param matchInfo 要获取别名的比赛场次
	 */
	public List<AliasMatchBean> getAliasFromMatchInfo(AliasDataProvider aliasDataProvider, List<AliasMatchBean> aliasMatchs);
	
	/**
	 * 根据比赛信息获取别名（篮球）
	 * @param aliasDataProvider
	 * @param aliasMatchs
	 * @return
	 */
	public List<AliasMatchBean> getAliasFromBasketballScheduleInfo(AliasDataProvider aliasDataProvider, List<AliasMatchBean> aliasMatchs);
	
	/**
	 * 根据ids足球比赛获取比赛时间
	 * @param matchIds
	 * @return
	 */
	public List<AliasMatchBean> getFootballMatchTimeByIds(String[] matchIds);
}
