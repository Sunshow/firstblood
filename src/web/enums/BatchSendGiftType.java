package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

/**
 * 批量赠送彩金模式
 * @author yanweijie
 *
 */
public class BatchSendGiftType extends IntegerBeanLabelItem{
	private static final long serialVersionUID = 1L;
	
	public final static BatchSendGiftType COMPLETETYPE = new BatchSendGiftType("补全模式",1);
	public final static BatchSendGiftType INCREMENTTYPE = new BatchSendGiftType("递增模式",2);
	
	protected BatchSendGiftType(String name, int value) {
		super(BatchSendGiftType.class.getName(),name, value);
	}
	
	public static BatchSendGiftType getItem(int value){
		return (BatchSendGiftType)BatchSendGiftType.getResult(BatchSendGiftType.class.getName(), value);
	}
	public static List<BatchSendGiftType> list;
     static{
        list = new ArrayList <BatchSendGiftType>();
        list.add(COMPLETETYPE);
        list.add(INCREMENTTYPE);
    }
}
