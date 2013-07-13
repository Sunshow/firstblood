package web.service.business;

import java.util.Date;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.business.PayPassword;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface PayPasswordService {
	/**
	 * 通过id进行查询
	 * @param uid
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public PayPassword get(Long uid) throws ApiRemoteCallFailedException;
	/**
	 * 查看日志
	 * @param uid
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> searchLog(Long uid, Date startDate, Date endDate, Integer passType, PageBean pageBean) throws ApiRemoteCallFailedException;
}
