/**
 * 
 */
package web.activiti.delegate.giftcards;

import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.lehecai.admin.web.activiti.delegate.AbstractDelegate;
import com.lehecai.admin.web.activiti.entity.GiftCardsTask;
import com.lehecai.admin.web.activiti.form.GiftCardsTaskForm;
import com.lehecai.admin.web.service.process.CouponGenerateService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.event.Coupon;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.CouponType;

/**
 * @author chirowong
 * 生成彩金卡
 */
public class ExecuteGiftCardsDelegate extends AbstractDelegate {
	@Autowired
	private CouponGenerateService couponGenerateService;
	private static final int DENOMINATOR = 1000;//分组传递
	
	@Override
	protected void doExecution(DelegateExecution execution) throws Exception {
		logger.info("生成彩金卡");
		GiftCardsTaskForm giftCardsTaskForm = (GiftCardsTaskForm)execution.getVariable("giftCardsTaskForm");
		GiftCardsTask giftCardsTask = giftCardsTaskForm.getGiftCardsTask();
		Coupon coupon = new Coupon();
		coupon.setAmount(giftCardsTask.getCardMoney().doubleValue());
		coupon.setExpireTime(DateUtil.parseDate(giftCardsTask.getLiveTime()));
		coupon.setType(CouponType.getItem(giftCardsTask.getCouponType()));
		coupon.setEventId(giftCardsTask.getEventId());
		coupon.setUid(giftCardsTask.getUserId());
		coupon.setProcessId(giftCardsTask.getProcessId());
		int number = giftCardsTask.getCardAmount().intValue();
		String opUserName = giftCardsTask.getInitiator();//工单发起人
		try {
			int times = number / DENOMINATOR;
			int surplus = number % DENOMINATOR;
			for (int i=0; i<times; i++) {
				couponGenerateService.couponGenerate(coupon,DENOMINATOR);
				logger.info("{}申請彩金卡{}张成功", opUserName, DENOMINATOR);
				Thread.sleep(200);
			}
			if (surplus != 0) {
				couponGenerateService.couponGenerate(coupon,surplus);
				logger.info("{}申請彩金卡{}张成功", opUserName, surplus);
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("彩金卡生成api调用异常，{}", e.getMessage());
			logger.info("{}申請彩金卡{}失败", opUserName, number);
		}
	}
}
