package web.service.event;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.CouponStatus;
import com.lehecai.core.lottery.CouponType;

public interface CouponService {

	/**
	 * 分页查询所有充值券信息
	 * @param pageBean 分页信息
	 */
	Map<String, Object> findCouponList(PageBean pageBean) throws ApiRemoteCallFailedException;

	/**
	 * 多条件分页查询充值券信息
	 * @param cpId 充值券id
	 * @param type 充值券类型
	 * @param status 充值券状态
	 * @param uid 用户id
	 * @param eventId 活动id
	 * @param pageBean 分页信息
	 */
	Map<String, Object> findCouponListByCondition(Long cpId, CouponType type, CouponStatus status, Long uid, Integer eventId,
                                                  String orderStr, String orderView, PageBean pageBean)  throws ApiRemoteCallFailedException;
	
	/**
	 * 删除指定cpId的充值券
	 * @param cpId 充值券Id
	 */
	void delCoupon(Long cpId, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 启用指定cpId的充值券
	 * @param cpId 充值券Id
	 */
	void enable(Long cpId, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 禁用指定cpId的充值券
	 * @param cpId 充值券Id
	 */
	void disable(Long cpId, PageBean pageBean) throws ApiRemoteCallFailedException;
	/**
	 * 充值券金额查询
	 * @param cpId 充值券id
	 * @param type 充值券类型
	 * @param status 充值券状态
	 * @param uid 用户id
	 * @param eventId 活动id
	 * @param pageBean 分页信息
	 */
	Map<String, Object> getAmount(Long cpId, CouponType type, CouponStatus status, Long uid, Integer eventId,
                                  String orderStr, String orderView) throws ApiRemoteCallFailedException;

}
