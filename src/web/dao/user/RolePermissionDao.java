package web.dao.user;

import java.util.List;

import com.lehecai.admin.web.domain.user.Role;
import com.lehecai.admin.web.domain.user.RolePermission;

public interface RolePermissionDao {
	List<RolePermission> getPermissionsByRole(Role role);
	void delPermissionsByRole(Role role);
	void merge(RolePermission rolePermission);
    void delete(RolePermission rolePermission);
}
