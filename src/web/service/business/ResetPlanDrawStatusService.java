package web.service.business;

import java.util.List;

import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface ResetPlanDrawStatusService {
	
	/**
	 * 执行重置
	 * @return
	 */
	public void reset(List<String> idList, List<String> successList, List<String> failList) throws ApiRemoteCallFailedException;
}

