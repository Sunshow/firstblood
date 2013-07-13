package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class CdnType extends IntegerBeanLabelItem{

	private static final long serialVersionUID = 1L;
	
	public static final CdnType ALL  = new CdnType("全部", -1);
	public static final CdnType CHINACACHE  = new CdnType("蓝汛", 1);
	public static final CdnType CHINANETCENTER = new CdnType("网宿", 2);
	
	
	protected CdnType(String name, int value) {
		super(CdnType.class.getName(),name, value);
	}
	
	public static CdnType getItem(int value){
		return (CdnType)CdnType.getResult(CdnType.class.getName(), value);
	}
	
	public static List<CdnType> list;
     static{
        list = new ArrayList<CdnType>();
        list.add(CdnType.ALL);
        list.add(CdnType.CHINACACHE);
        list.add(CdnType.CHINANETCENTER);
    }
}
