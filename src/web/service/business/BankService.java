package web.service.business;

import java.util.Map;
import java.util.Set;

import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface BankService {
	
	/**
	 * 根据银行ID获取银行信息
	 * @throws ApiRemoteCallFailedException 
	 */
	public Map<String, Object> getBankInfoByBankId(Set<String> bankIdSet) throws ApiRemoteCallFailedException;
}
