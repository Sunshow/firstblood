package web.action.openapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.export.QrCodeExport;
import com.lehecai.admin.web.service.openapi.QrCodeService;
import com.lehecai.core.api.openapi.QrCode;
import com.lehecai.core.api.openapi.QrCodeScan;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.util.CoreDateUtils;

/**
 * 2013-05-24
 * 二维码管理
 * @author He Wang
 *
 */
public class QrCodeScanAction extends BaseAction {
	private static final long serialVersionUID = 2513042492248675631L;
	private Logger logger = LoggerFactory.getLogger(QrCodeScanAction.class);

	private QrCodeService qrCodeService;
	private List<QrCodeScan> qrCodeScanList;
	private List<QrCode> qrCodeList;
	private Long qrCodeId;
	private String qrCodeName;
	private Boolean manageFlag;
	private Date beginDate;
	private Date endDate;
	
	//导出
	private InputStream inputStream;
	private String fileName;
	
	/**
	 * 查询二维码
	 * @return
	 */
	public String handle() {
		logger.info("进入查询二维码列表");
		beginDate = super.getDefaultQueryBeginDate(-7);
		endDate = new Date();
		return "list";
	}
	
	/**
	 * 查询二维码
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询二维码扫描记录列表");
		if (qrCodeId == null || qrCodeId <= 0L) {
			super.setErrorMessage("二维码不能为空,请选择");
			return "failure";
		}
		if (beginDate == null || endDate == null) {
			super.setErrorMessage("二维码扫描查询时间区间不能为空");
			return "failure";
		}
		if (CoreDateUtils.diffDays(endDate, beginDate) > 31) {
			super.setErrorMessage("二维码扫描查询时间区间最大跨度为31天");
			return "failure";
		}
		Map<String, Object> map = null;
		try {
			PageBean pageBean = new PageBean();
			pageBean.setPageSize(Integer.MAX_VALUE);
			map = qrCodeService.queryQrCodeScanList(qrCodeId, beginDate, endDate, pageBean);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询二维码扫描记录异常，" , e.getMessage());
			super.setErrorMessage("API查询二维码扫描记录异常，请联系技术人员!" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			qrCodeScanList = (List<QrCodeScan>)map.get(Global.API_MAP_KEY_LIST);
		}
		logger.info("查询二维码扫描记录列表结束");
		return "list";
	}
	
	/**
	 * 查询二维码
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String export() {
		logger.info("进入导出查询二维码扫描记录列表");
		if (qrCodeId == null || qrCodeId <= 0L) {
			super.setErrorMessage("二维码不能为空,请选择");
			return "failure";
		}
		if (beginDate == null || endDate == null) {
			super.setErrorMessage("二维码扫描查询时间区间不能为空");
			return "failure";
		}
		Map<String, Object> map = null;
		try {
			PageBean pageBean = new PageBean();
			pageBean.setPageSize(Integer.MAX_VALUE);
			map = qrCodeService.queryQrCodeScanList(qrCodeId, beginDate, endDate, pageBean);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询二维码扫描记录异常，" , e.getMessage());
			super.setErrorMessage("API查询二维码扫描记录异常，请联系技术人员!" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			qrCodeScanList = (List<QrCodeScan>)map.get(Global.API_MAP_KEY_LIST);
			qrCodeName = qrCodeName == null ? "" : "“" + qrCodeName + "”";
			try {
				Workbook workBook = QrCodeExport.export(qrCodeName, qrCodeScanList);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				workBook.write(os);
				inputStream  = new ByteArrayInputStream(os.toByteArray());
				this.fileName = (new Date()).getTime() + ".xls";
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("文件输出流写入错误");
				return "failure";
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("API返回数据有误，生成excel文件时错误");
				return "failure";
			}
		}
		logger.info("查询二维码扫描记录列表结束");
		return "download";
	}
	

	public void setQrCodeService(QrCodeService qrCodeService) {
		this.qrCodeService = qrCodeService;
	}

	public QrCodeService getQrCodeService() {
		return qrCodeService;
	}

	public void setQrCodeId(Long qrCodeId) {
		this.qrCodeId = qrCodeId;
	}

	public Long getQrCodeId() {
		return qrCodeId;
	}

	public void setManageFlag(Boolean manageFlag) {
		this.manageFlag = manageFlag;
	}

	public Boolean getManageFlag() {
		return manageFlag;
	}

	public void setQrCodeScanList(List<QrCodeScan> qrCodeScanList) {
		this.qrCodeScanList = qrCodeScanList;
	}

	public List<QrCodeScan> getQrCodeScanList() {
		return qrCodeScanList;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setQrCodeList(List<QrCode> qrCodeList) {
		this.qrCodeList = qrCodeList;
	}

	@SuppressWarnings("unchecked")
	public List<QrCode> getQrCodeList() {
		Map<String, Object> map = null;
		try {
			PageBean pageBean = super.getPageBean();
			pageBean.setPageSize(Integer.MAX_VALUE);
			map = qrCodeService.queryQrCodeList(null, pageBean);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询二维码异常，" , e.getMessage());
		}
		if (map != null) {
			qrCodeList = (List<QrCode>)map.get(Global.API_MAP_KEY_LIST);
		}
		return qrCodeList;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setQrCodeName(String qrCodeName) {
		this.qrCodeName = qrCodeName;
	}

	public String getQrCodeName() {
		return qrCodeName;
	}


}
