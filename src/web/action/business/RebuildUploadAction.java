package web.action.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.business.RebuildUploadService;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class RebuildUploadAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(RebuildUploadAction.class);
	
	private RebuildUploadService rebuildUploadService;
	private Long id;
	private String data;
	public String handle() {
		return "inputForm";
	}
	
	public String rebuildUpload() {
		logger.info("进入重建上传文件缓存内容");
		if (id == null || id == 0) {
			logger.error("文件方案号为空");
			super.setErrorMessage("文件方案号不能为空");
			return "failure";
		}
		try {
			data = rebuildUploadService.rebuildUpload(id);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("重建上传文件缓存,api调用异常,{}", e.getMessage());
			super.setErrorMessage("api调用异常,请联系技术人员!原因 : " + e.getMessage());
			return "failure";
		}
		return "inputForm";
	}

	public RebuildUploadService getRebuildUploadService() {
		return rebuildUploadService;
	}

	public void setRebuildUploadService(RebuildUploadService rebuildUploadService) {
		this.rebuildUploadService = rebuildUploadService;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
}
