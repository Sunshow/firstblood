package web.action.openapi;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.openapi.QrCodeService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.openapi.QrCode;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 2013-05-24
 * 二维码管理
 * @author He Wang
 *
 */
public class QrCodeAction extends BaseAction {
	private static final long serialVersionUID = 1803042499820675627L;
	private Logger logger = LoggerFactory.getLogger(QrCodeAction.class);

	private QrCodeService qrCodeService;
	private QrCode qrCode;
	private List<QrCode> qrCodeList;
	private Long qrCodeId;
	private Boolean manageFlag;
	
	/**
	 * 查询二维码
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String handle() {
		logger.info("进入查询二维码列表");
		Map<String, Object> map = null;
		try {
			PageBean pageBean = super.getPageBean();
			pageBean.setPageSize(50);
			map = qrCodeService.queryQrCodeList(null, pageBean);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询二维码异常，" , e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			qrCodeList = (List<QrCode>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(ServletActionContext.getRequest(), pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("查询二维码列表结束");
		return "list";
	}
	
	/**
	 * 输入
	 * @return
	 */
	public String input() {
		logger.info("进入输入二维码信息");
		if (qrCodeId != null && qrCodeId != 0L) {
			try {
				qrCode = qrCodeService.getQrCode(qrCodeId);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API查询二维码异常，" , e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
				return "failure";
			}
		} else {
			qrCode = new QrCode();
		}
		logger.info("输入二维码信息结束");
		return "inputForm";
	}
	
	/**
	 * 添加修改
	 * @return
	 */
	public String manage () {
		logger.info("进入添加修改二维码信息");
		if (qrCode == null) {
			logger.error("二维码为空");
			super.setErrorMessage("二维码不能为空");
			return "failure";
		}
		if (StringUtils.isEmpty(qrCode.getName())) {
			logger.error("二维码名称为空");
			super.setErrorMessage("二维码名称不能为空");
			return "failure";
		}
		Long newId = -1L;
		boolean result = false;
		if (qrCode.getId() != null && qrCode.getId() != 0L) {	//编辑
			try {
				result = qrCodeService.updateQrCode(qrCode);
				newId = qrCode.getId();
			} catch (ApiRemoteCallFailedException e) {
				logger.error("更新二维码异常，{}", e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
				return "failure";
			}
		} else {						//添加
			try {
				newId = qrCodeService.addQrCode(qrCode);
				if (newId > 0) {
					result = true;
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error("二维码添加异常，{}", e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
				return "failure";
			}
		}
		
		super.setForwardUrl("/openapi/qrCode.do?action=input&qrCodeId=" + newId);
		if (result) {
			logger.info("操作成功");
			super.setErrorMessage("操作成功");
			return "success";
		} else {
			logger.error("操作失败");
			super.setErrorMessage("操作失败");
			return "failure";
		}
		
	}

	public void setQrCode(QrCode qrCode) {
		this.qrCode = qrCode;
	}

	public QrCode getQrCode() {
		return qrCode;
	}

	public void setQrCodeList(List<QrCode> qrCodeList) {
		this.qrCodeList = qrCodeList;
	}

	public List<QrCode> getQrCodeList() {
		return qrCodeList;
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

}
