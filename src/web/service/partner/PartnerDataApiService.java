package web.service.partner;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.partner.PartnerDataApi;
import com.lehecai.core.api.partner.PartnerDataApiLottery;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface PartnerDataApiService {
	
	/**
	 * 查询数据项列表
	 * @param partnerDataApi
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> getPartnerDataApiResult(PartnerDataApi partnerDataApi, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 更新合作商数据项
	 * @param partnerDataApi
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean updatePartnerDataApi(PartnerDataApi partnerDataApi) throws ApiRemoteCallFailedException;
	
	/**
	 * 更新合作商数据项
	 * @param partnerDataApi
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean updatePartnerDataApiTemplate(PartnerDataApi partnerDataApi) throws ApiRemoteCallFailedException;

	/**
	 * 创建合作商数据项
	 * @param partnerDataApi
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean createPartnerDataApi(PartnerDataApi partnerDataApi) throws ApiRemoteCallFailedException;
	
	/**
	 * 保存数据项状态(启动/停用)
	 * @param partnerDataApi
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean updatePartnerDataApiStatus(PartnerDataApi partnerDataApi) throws ApiRemoteCallFailedException;
	
	/**
	 * 查询数据项彩种
	 * @param partnerDataApiLottery
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> getPartnerDataApiLotteryResult(PartnerDataApiLottery partnerDataApiLottery, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 保存数据项彩种
	 * @param partnerDataApiLottery
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean updatePartnerDataApiLottery(PartnerDataApiLottery partnerDataApiLottery) throws ApiRemoteCallFailedException;
	
	/**
	 * 保存数据项彩种
	 * @param partnerDataApiLottery
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean createPartnerDataApiLottery(PartnerDataApiLottery partnerDataApiLottery) throws ApiRemoteCallFailedException;
	
	/**
	 * 手动更新
	 * @param partnerDataApi
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean updateManual(PartnerDataApi partnerDataApi) throws ApiRemoteCallFailedException;
	
	/**
	 * 模板预览
	 * @param partnerDataApi
	 * @param userId
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public PartnerDataApi preview(PartnerDataApi partnerDataApi, Long userId) throws ApiRemoteCallFailedException;
	
	/**
	 * 删除彩种
	 * @param partnerDataApiLottery
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public boolean delLottery(PartnerDataApiLottery partnerDataApiLottery) throws ApiRemoteCallFailedException;
}
