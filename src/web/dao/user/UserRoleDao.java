package web.dao.user;

import java.util.List;

import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.domain.user.UserRole;

/**
 * 用户角色数据访问接口
 * @author chirowong
 *
 */
public interface UserRoleDao {
	List<UserRole> getRolesByUser(User user);
	void merge(UserRole userRole);
}
