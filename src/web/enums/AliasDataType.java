package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class AliasDataType extends IntegerBeanLabelItem{

	private static final long serialVersionUID = 1L;
	
	public static final AliasDataType LEAGUE_BASE = new AliasDataType("联赛基础", 1);
	public static final AliasDataType TEAM_BASE = new AliasDataType("球队基础", 2);
	public static final AliasDataType PLAYER_BASE = new AliasDataType("球员基础", 3);
	public static final AliasDataType MATCH = new AliasDataType("比赛", 4);
	public static final AliasDataType LIVE = new AliasDataType("直播", 5);
	public static final AliasDataType BOOKMAKER = new AliasDataType("赔率公司", 6);
	public static final AliasDataType AREA = new AliasDataType("区域", 7);
	public static final AliasDataType COUNTRY = new AliasDataType("国家", 8);
	public static final AliasDataType CITY = new AliasDataType("城市", 9);
	public static final AliasDataType ODDS_EURO = new AliasDataType("欧赔", 10);
	public static final AliasDataType ODDS_ASIAN = new AliasDataType("亚盘赔率", 11);
	public static final AliasDataType ODDS_TOTAL = new AliasDataType("大小球赔率", 12);
	
	public static final AliasDataType ODDS_EURO_EX = new AliasDataType("交易所欧赔", 13);
	public static final AliasDataType LEAGUE_STAGE = new AliasDataType("联赛阶段", 14);
	public static final AliasDataType LIVE_TYPE = new AliasDataType("直播类型", 15);
	public static final AliasDataType PLAYER_POSITION = new AliasDataType("球员位置", 16);
	public static final AliasDataType PLAYER_TEAM_CUR = new AliasDataType("球员当前球队", 17);
	public static final AliasDataType TEAM_FIELDS = new AliasDataType("球队字段域", 18);
	public static final AliasDataType MATCH_STATISTICS = new AliasDataType("比赛统计", 19);
	public static final AliasDataType LANG = new AliasDataType("语言扩展", 20);
	public static final AliasDataType GROUP = new AliasDataType("小组", 21);
	
	public static final AliasDataType LEAGUE = new AliasDataType("联赛", 22);
	public static final AliasDataType TEAM = new AliasDataType("球队", 23);
	public static final AliasDataType PLAYER = new AliasDataType("球员", 24);

	
	protected AliasDataType(String name, int value) {
		super(AliasDataType.class.getName(),name, value);
	}
	
	public static AliasDataType getItem(int value){
		return (AliasDataType)AliasDataType.getResult(AliasDataType.class.getName(), value);
	}
	
	public static List<AliasDataType> list;
     static{
        list = new ArrayList<AliasDataType>();

    }
}
