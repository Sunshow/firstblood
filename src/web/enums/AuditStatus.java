package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class AuditStatus extends IntegerBeanLabelItem{
	private static final long serialVersionUID = 1L;

	
	private static List<AuditStatus> checkItemsBoolean = new ArrayList<AuditStatus>();
	private static List<AuditStatus> checkItemsInt = new ArrayList<AuditStatus>();
	
	protected AuditStatus(String name, int value, boolean typeFlag) {
		super(AuditStatus.class.getName(),name, value);
		if (typeFlag) {
			checkItemsInt.add(this);
		} else {
			checkItemsBoolean.add(this);
		}
	}
	
	protected AuditStatus(String name, int value) {
		this(name, value, false);
	}
	
	public static AuditStatus getItem(int value){
		return (AuditStatus)AuditStatus.getResult(AuditStatus.class.getName(), value);
	}

	public static List<AuditStatus> getBooleanItemsForCheck() {
		return checkItemsBoolean;
	}
	
	public static List<AuditStatus> getIntItemsForCheck() {
		return checkItemsInt;
	}

	public final static AuditStatus HISTORYCONSUMPTIONLOW = new AuditStatus("消费不到充值的20%",1);
	public final static AuditStatus HIGHFREQUENCY = new AuditStatus("提款频率过高",2);
	public final static AuditStatus BANKHIGHFREQUENCY = new AuditStatus("银行卡提款频率过高",3);
	public final static AuditStatus SUSPICION = new AuditStatus("有钓鱼或者洗钱等嫌疑",4);
	public final static AuditStatus LATESTCONSUMPTIONLOW = new AuditStatus("上次充值后消费不足",5);
	public final static AuditStatus FIRST_WITHDRAW = new AuditStatus("首次提款",6);
	public final static AuditStatus WITHDRAW_SAME_CARD_24HOURS = new AuditStatus("银行卡{card}24小时内提款次数已达{cardNum}次",7,true);
	public final static AuditStatus WITHDRAW_SAME_NAME_24HOURS = new AuditStatus("{name}24小时内提款次数已达{nameNum}次",8,true);
	
	

}
