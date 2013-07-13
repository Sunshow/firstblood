/**
 * 
 */
package web.activiti.delegate.rewards;

import com.lehecai.admin.web.activiti.delegate.AbstractDelegate;
import org.activiti.engine.delegate.DelegateExecution;

/**
 * @author qatang
 * @author chirowong //修改系统不发送邮件的错误，增加发送短信和站内短信功能
 *
 */
public class NotifyHigherFinancialUserAddedRewardsDelegate extends AbstractDelegate {
	protected final static String PROCESS_ID = "addedRewardsProcess";
    protected final static String TASK_ID = "payAddedRewardsTask";

    /*
	@Autowired
	private NotifyService notifyService;
	@Autowired
	private WorkProcessUserService workProcessUserService;
	@Autowired
	private PermissionService permissionService;
	*/
	
	@Override
	protected void doExecution(DelegateExecution execution) throws Exception {
		/*logger.info("财务人员额度不足，通知上级财务人员");
		AddedRewardsTaskForm addedRewardsTaskForm = (AddedRewardsTaskForm)execution.getVariable("addedRewardsTaskForm");
		
		PageBean pageBean = new PageBean();
		pageBean.setPageFlag(false);
		List<WorkProcessUser> workProcessUsers = workProcessUserService.list(PROCESS_ID, TASK_ID, null, pageBean);
		
		Calendar cd = Calendar.getInstance();
		int weekday = cd.get(Calendar.DAY_OF_WEEK);
		
		for (WorkProcessUser workProcessUser : workProcessUsers) {
			boolean flag = false;
			if (workProcessUser.isNotify()) {
				String[] dutyDays = StringUtils.split(workProcessUser.getDutyDate(), ",");
				if (dutyDays != null) {
					for (String dutyday : dutyDays) {
						if (weekday == Integer.valueOf(dutyday)) {
							if (workProcessUser.getNotifyTimes() != null && !workProcessUser.getNotifyTimes().equals("")) {
								JSONArray dutyTimes = JSONArray.fromObject(workProcessUser.getNotifyTimes());
								for (Object dutyTime : dutyTimes) {
									JSONObject time = JSONObject.fromObject(dutyTime);
									String startTime = CoreDateUtils.formatDate(cd.getTime()) + " " + time.getString("starttime") + ":00";
									String endTime = CoreDateUtils.formatDate(cd.getTime()) + " " + time.getString("endtime") + ":00";
									if (cd.getTime().after(CoreDateUtils.parseLongDate(startTime)) && cd.getTime().before(CoreDateUtils.parseLongDate(endTime))) {
										flag = true;
										break;
									}
								}
							} else {
								flag = true;
								break;
							}
						}
					}
				}
			}

			if (flag) {
				logger.info("需要通知高级财务人员"+workProcessUser.getName());
				logger.info("发送内容补充派奖工单支付通知:财务人员额度不足，需要上级财务人员处理：方案编码为["
						+ addedRewardsTaskForm.getAddedRewardsTask()
						.getPlanId()
						+ "]，彩种["
						+ addedRewardsTaskForm.getAddedRewardsTask()
								.getLotteryName() + "]，彩期["
						+ addedRewardsTaskForm.getAddedRewardsTask().getPhase()
						+ "]，税后奖金[" + addedRewardsTaskForm.getAddedRewardsTask().getPosttaxPrize() + "]");
				List<User> usersList = new ArrayList<User>();
				int processUserType = workProcessUser.getProcessUserType().getValue();
				if(processUserType == ProcessUserType.ROLE.getValue()){//如果流程用户类型为角色
					List<User> users = userService.list(null, null, null, null, workProcessUser.getOperateId(), "true", pageBean);
					if(users != null && users.size() > 0){
						usersList.addAll(users);
					}	
				}else{
					User user = userService.get(workProcessUser.getOperateId());
					usersList.add(user);
				}
				if(workProcessUser.isEmailNotify() && usersList.size() > 0){
					List<String> emails = new ArrayList<String>();
					String subject = "补充派奖工单支付通知";
					String text = "财务人员额度不足，需要上级财务人员处理：方案编码为["
						+ addedRewardsTaskForm.getAddedRewardsTask()
						.getPlanId()
						+ "]，彩种["
						+ addedRewardsTaskForm.getAddedRewardsTask()
								.getLotteryName() + "]，彩期["
						+ addedRewardsTaskForm.getAddedRewardsTask().getPhase()
						+ "]，税后奖金[" + addedRewardsTaskForm.getAddedRewardsTask().getPosttaxPrize() + "]";
					for(User user : usersList){
						String email = user.getEmail();
						emails.add(email);
					}
					notifyService.sendEmail(subject, text, emails);
				}
				if(workProcessUser.isSmsNotify() && usersList.size() > 0){
					List<String> contacts = new ArrayList<String>();
					String text = "补充派奖工单支付通知:财务人员额度不足，需要上级财务人员处理：方案编码为["
						+ addedRewardsTaskForm.getAddedRewardsTask()
						.getPlanId()
						+ "]，彩种["
						+ addedRewardsTaskForm.getAddedRewardsTask()
								.getLotteryName() + "]，彩期["
						+ addedRewardsTaskForm.getAddedRewardsTask().getPhase()
						+ "]，税后奖金[" + addedRewardsTaskForm.getAddedRewardsTask().getPosttaxPrize() + "]";
					for(User user : usersList){
						String tel = user.getTel();
						contacts.add(tel);
					}
					notifyService.sendSms(text, contacts);
				}
				if(workProcessUser.isMessageNotify() && usersList.size() > 0){
					List<Long> userIds = new ArrayList<Long>();
					String text = "补充派奖工单支付通知:财务人员额度不足，需要上级财务人员处理：方案编码为["
						+ addedRewardsTaskForm.getAddedRewardsTask()
						.getPlanId()
						+ "]，彩种["
						+ addedRewardsTaskForm.getAddedRewardsTask()
								.getLotteryName() + "]，彩期["
						+ addedRewardsTaskForm.getAddedRewardsTask().getPhase()
						+ "]，税后奖金[" + addedRewardsTaskForm.getAddedRewardsTask().getPosttaxPrize() + "]";
					for(User user : usersList){
						Long userId = user.getId();
						userIds.add(userId);
					}
					notifyService.sendMessage(text, null, userIds);
				}
			}else{
				logger.info("不需要发送提醒"+workProcessUser.getName());
			}
		}*/
	}
}
