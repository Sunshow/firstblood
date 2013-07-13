package web.service.business;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.business.VoiceRechargeAmount;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 语音充值限额业务逻辑层接口
 * @author He Wang
 *
 */
public interface VoiceRechargeAmountService {

	/**
	 * 查询语音充值限额
	 * @param voiceRechargeAmount
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> queryVoiceRechargeAmountList(VoiceRechargeAmount voiceRechargeAmount, PageBean pageBean) throws ApiRemoteCallFailedException ;

	/**
	 * 新增语音充值限额
	 * @param voiceRechargeAmount
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean add(VoiceRechargeAmount voiceRechargeAmount) throws ApiRemoteCallFailedException;
	

	/**
	 * 更改语音充值限额
	 * @param voiceRechargeAmount
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean update(VoiceRechargeAmount voiceRechargeAmount) throws ApiRemoteCallFailedException;
	
	/**
	 * 根据id查询语音充值限额
	 * @param id
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public VoiceRechargeAmount get(Long id) throws ApiRemoteCallFailedException;
	
	/**
	 * 语音充值限额批量操作
	 * @param ids
	 * @param status
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean batchOperate(String[] ids, YesNoStatus status) throws ApiRemoteCallFailedException;

}
