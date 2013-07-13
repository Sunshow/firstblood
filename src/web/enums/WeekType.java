package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class WeekType extends IntegerBeanLabelItem{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static WeekType SUNDAY = new WeekType("周日", 1);
	public final static WeekType MONDAY = new WeekType("周一", 2);
	public final static WeekType TUESDAY = new WeekType("周二", 3);
	public final static WeekType WEDNESDAY = new WeekType("周三", 4);
	public final static WeekType THURSDAY = new WeekType("周四", 5);
	public final static WeekType FRIDAY = new WeekType("周五", 6);
	public final static WeekType SATURDAY = new WeekType("周六", 7);
	
	protected WeekType(String name, int value) {
		super(WeekType.class.getName(),name, value);
	}
	
	public static WeekType getItem(int value){
		return (WeekType)WeekType.getResult(WeekType.class.getName(), value);
	}
	public static List<WeekType> list;
     static{
        list = new ArrayList <WeekType>();
        list.add(SUNDAY);
        list.add(MONDAY);
        list.add(TUESDAY);
        list.add(WEDNESDAY);
        list.add(THURSDAY);
        list.add(FRIDAY);
        list.add(SATURDAY);
    }
}
