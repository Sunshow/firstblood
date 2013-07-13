/**
 * 
 */
package web.action.customconfig;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.customconfig.CustomFunctionConfig;
import com.lehecai.admin.web.domain.customconfig.FunctionType;
import com.lehecai.admin.web.domain.user.Role;
import com.lehecai.admin.web.service.customconfig.CustomFunctionConfigService;
import com.lehecai.admin.web.service.user.PermissionService;
import com.lehecai.admin.web.utils.PageUtil;

/**
 * @author chirowong
 * 客户自定义权限管理
 */
public class CustomFunctionConfigAction extends BaseAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4260678425602382212L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private PermissionService permissionService;
	private CustomFunctionConfigService customFunctionConfigService;
	private List<Role> roleList;
	private CustomFunctionConfig customFunctionConfig;
	private List<CustomFunctionConfig> customFunctionConfigs;
	private List<Role> roleListForView;
	private int functionTypeId;
	
	public String handle(){
		logger.info("进入客户自定义权限列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		if(customFunctionConfig == null) customFunctionConfig = new CustomFunctionConfig();
		customFunctionConfigs = customFunctionConfigService.list(customFunctionConfig, super.getPageBean());
		PageBean pageBean = customFunctionConfigService.getPageBean(customFunctionConfig,super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		return "list";
	}
	
	public String input(){
		logger.info("添加客户自定义权限");
		Role role = new Role();
		PageBean nopageBean = new PageBean();
		nopageBean.setPageFlag(false);
		roleList = permissionService.listRoles(role);
		if(customFunctionConfig != null && customFunctionConfig.getId() != null){
			customFunctionConfig = customFunctionConfigService.get(customFunctionConfig.getId());
			functionTypeId = customFunctionConfig.getFunctionType().getValue();
		}else{
			customFunctionConfig = new CustomFunctionConfig();
		}
		return "input";
	}
	
	public String view(){
		logger.info("查看客户自定义权限信息");
		if(customFunctionConfig != null && customFunctionConfig.getId() != null){
			customFunctionConfig = customFunctionConfigService.get(customFunctionConfig.getId());
			String roles = customFunctionConfig.getRoles();
			String[] arrTerminals = roles.split(",");
			roleListForView = new ArrayList<Role>();
			for(int i = 0; i < arrTerminals.length; i++){
				roleListForView.add(permissionService.getRole(Long.valueOf(arrTerminals[i].trim())));
			}
		}else{
			logger.info("查看客户自定义权限出错，客户自定义权限编码为空");
			super.setErrorMessage("查看客户自定义权限出错，客户自定义权限编码为空");
			return "failure";
		}
		return "view";
	}
	
	public String del(){
		logger.info("删除客户自定义权限信息");
		if (customFunctionConfig != null && customFunctionConfig.getId() != null) {
			customFunctionConfig = customFunctionConfigService.get(customFunctionConfig.getId());
			customFunctionConfigService.del(customFunctionConfig);
		} else {
			logger.error("删除客户自定义权限，编码为空");
			super.setErrorMessage("删除客户自定义权限，编码不能为空");
			return "failure";
		}
		super.setForwardUrl("/customConfig/customFunctionConfig.do");
		logger.info("删除客户自定义权限结束");
		return "success";
	}
	
	public String manage(){
		logger.info("进入更新客户自定义权限信息");
		if (customFunctionConfig != null) {
			if (functionTypeId == 0) {
				logger.error("添加客户自定义权限，客户自定义权限名为空");
				super.setErrorMessage("客户自定义权限名不能为空");
				return "failure";
			}
			if (customFunctionConfig.getRoles() == null || "".equals(customFunctionConfig.getRoles())) {
				logger.error("添加客户自定义权限，没有选择终端");
				super.setErrorMessage("没有选择终端");
				return "failure";
			}
			customFunctionConfig.setFunctionType(FunctionType.getItem(functionTypeId));
			if(customFunctionConfig.getId() == null){
				List<CustomFunctionConfig> cfcList = customFunctionConfigService.list(customFunctionConfig, null);
				if(cfcList != null && cfcList.size() > 0){
					logger.error("已存在该权限的设置");
					super.setErrorMessage("已存在该权限的设置");
					return "failure";
				}
			}else{
				CustomFunctionConfig oriCustomFunctionConfig = customFunctionConfigService.get(customFunctionConfig.getId());
				if(oriCustomFunctionConfig.getFunctionType().getValue() != customFunctionConfig.getFunctionType().getValue()){
					List<CustomFunctionConfig> cfcList = customFunctionConfigService.list(customFunctionConfig, null);
					if(cfcList != null && cfcList.size() > 0){
						logger.error("已存在该权限的设置");
						super.setErrorMessage("已存在该权限的设置");
						return "failure";
					}
				}
			}
			customFunctionConfigService.manage(customFunctionConfig);
			super.setForwardUrl("/customConfig/customFunctionConfig.do");
			logger.info("更新客户自定义权限信息结束");
			return "success";
		} else {
			logger.error("添加客户自定义权限错误，提交表单为空");
			super.setErrorMessage("添加客户自定义权限错误，提交表单不能为空");
			return "failure";
		}
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

	public List<Role> getRoleListForView() {
		return roleListForView;
	}

	public void setRoleListForView(List<Role> roleListForView) {
		this.roleListForView = roleListForView;
	}

	public CustomFunctionConfigService getCustomFunctionConfigService() {
		return customFunctionConfigService;
	}
	
	public void setCustomFunctionConfigService(
			CustomFunctionConfigService customFunctionConfigService) {
		this.customFunctionConfigService = customFunctionConfigService;
	}

	public CustomFunctionConfig getCustomFunctionConfig() {
		return customFunctionConfig;
	}

	public void setCustomFunctionConfig(CustomFunctionConfig customFunctionConfig) {
		this.customFunctionConfig = customFunctionConfig;
	}

	public List<CustomFunctionConfig> getCustomFunctionConfigs() {
		return customFunctionConfigs;
	}

	public void setCustomFunctionConfigs(List<CustomFunctionConfig> customFunctionConfigs) {
		this.customFunctionConfigs = customFunctionConfigs;
	}
	
	public List<FunctionType> getFunctionTypeList(){
		return FunctionType.getItems();
	}

	public int getFunctionTypeId() {
		return functionTypeId;
	}

	public void setFunctionTypeId(int functionTypeId) {
		this.functionTypeId = functionTypeId;
	}
}
