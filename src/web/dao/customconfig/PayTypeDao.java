/**
 * 
 */
package web.dao.customconfig;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.customconfig.PayType;
/**
 * @author chirowong
 *
 */
public interface PayTypeDao {
	public PayType get(Integer id);
	public void save(PayType payType);
	public void del(PayType payType);
	public PayType merge(PayType payType);
	public List<PayType> list(PayType payType, PageBean pageBean);
	public PageBean getPageBean(PayType payType, PageBean pageBean);
}
