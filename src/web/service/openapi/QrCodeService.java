package web.service.openapi;

import java.util.Date;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.openapi.QrCode;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 2013-05-09
 * @author He Wang
 *
 */
public interface QrCodeService {

	
	/**
	 * 按条件查询二维码
	 * @param appStatus
	 * @param appIsOpen
	 * @param appId
	 * @param appType
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	Map<String, Object> queryQrCodeList(Long qrCodeId, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 根据id查询二维码
	 * @param qrCodeId
	 * @return
	 */
	QrCode getQrCode(Long qrCodeId) throws ApiRemoteCallFailedException ;
	
	/**
	 * 添加二维码,需返回id
	 * @param qrCode
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	Long addQrCode(QrCode qrCode) throws ApiRemoteCallFailedException;
	
	/**
	 * 编辑二维码
	 * @param qrCode
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean updateQrCode(QrCode qrCode) throws ApiRemoteCallFailedException;

	/**
	 * 查看扫描记录
	 * @param qrCodeId
	 * @param beginDate
	 * @param endDate
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	Map<String, Object> queryQrCodeScanList(Long qrCodeId, Date beginDate, Date endDate, PageBean pageBean) throws ApiRemoteCallFailedException;
	

}