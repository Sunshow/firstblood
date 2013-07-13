package web.activiti.entity;

/**
 * @author chirowong
 *
 */
public enum HistoryQueryCondition {
	ADDEDREWARDSROCESS("TASK_ADDED_REWARDS","addedRewardsProcess"),
	RECHARGEROCESS("TASK_RECHARGE","rechargeProcess"),
	GIFTREWARDSROCESS("TASK_GIFTREWARDS","giftRewardsProcess"),
	GIFTCARDSROCESS("TASK_GIFTCARDS","giftCardsProcess"),
	COMMISSIONTASKPROCESS("TASK_COMMISSION","commissionTaskProcess");
	
	private String tableName;
	private String processName;
	
	private HistoryQueryCondition(String tableName,String processName){
		this.tableName = tableName;
		this.processName = processName;
	}
	
	public static String getTableName(String processName){
		for(HistoryQueryCondition c : HistoryQueryCondition.values()){
			if(c.getProcessName().equals(processName)){
				return c.getTableName();
			}
		}
		return null;
	}
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
}
