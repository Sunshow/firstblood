package web.dao.user;

import java.util.List;

import com.lehecai.admin.web.domain.user.Permission;

public interface PermissionDao {
	void merge(Permission permission);
	List<Permission> list(Permission permission);
	Permission get(Long ID);
	void del(Permission permission);
}