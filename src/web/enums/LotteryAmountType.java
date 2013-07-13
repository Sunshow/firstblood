package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class LotteryAmountType extends IntegerBeanLabelItem{
	private static final long serialVersionUID = 1L;
	public final static LotteryAmountType F0T50 = new LotteryAmountType("0-50",1);
	public final static LotteryAmountType F50T100 = new LotteryAmountType("50-100",2);
	public final static LotteryAmountType F100T200 = new LotteryAmountType("100-200",3);
	public final static LotteryAmountType F200T500 = new LotteryAmountType("200-500",4);
	public final static LotteryAmountType F500T1000 = new LotteryAmountType("500-1000",5);
	public final static LotteryAmountType F1000T10000 = new LotteryAmountType("1000-10000",6);
	public final static LotteryAmountType F10000T100000 = new LotteryAmountType("10000-100000",7);
	public final static LotteryAmountType F100000 = new LotteryAmountType("100000及更多",8);
	
	protected LotteryAmountType(String name, int value) {
		super(LotteryAmountType.class.getName(),name, value);
	}
	
	public static LotteryAmountType getItem(int value){
		return (LotteryAmountType)LotteryAmountType.getResult(LogType.class.getName(), value);
	}
	public static List<LotteryAmountType> list;
     static{
        list = new ArrayList<LotteryAmountType>();
        list.add(F0T50);
        list.add(F50T100);
        list.add(F100T200);
        list.add(F200T500);
        list.add(F500T1000);
        list.add(F1000T10000);
        list.add(F10000T100000);
        list.add(F100000);
    }
}
