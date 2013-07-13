package web.service.process;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.event.Coupon;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 生成彩金卡
 * @author chirowong
 */
public interface CouponGenerateService {
	/**
	 * 
	 * @param coupon 优惠券信息
	 * @param number 数量
	 * @throws ApiRemoteCallFailedException
	 */
	public void couponGenerate(Coupon coupon, int number) throws ApiRemoteCallFailedException;
	
	/**
	 * 根据processId获取彩金卡
	 * @param processId
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> queryCouponList(String processId, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 根据processId激活彩金卡
	 * @param processId
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Coupon enableCouponByProcessId(String processId) throws ApiRemoteCallFailedException;
}
