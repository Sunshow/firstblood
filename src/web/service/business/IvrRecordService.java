package web.service.business;

import java.util.List;

import com.lehecai.core.api.user.IvrRecord;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface IvrRecordService {

	/**
	 * 根据用户编码查询绑定银行卡
	 */
	List<IvrRecord> findIvrRecordList(Long uid) throws ApiRemoteCallFailedException;
	
	/**
	 * 解绑用户银行卡
	 */
	boolean unlock(IvrRecord record) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改用户银行卡
	 */
	boolean updateBank(IvrRecord record) throws ApiRemoteCallFailedException;
}
