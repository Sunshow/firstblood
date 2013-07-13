package web.dao.user;

import java.util.List;

import com.lehecai.admin.web.domain.user.Menu;

public interface MenuDao {
	void merge(Menu menu);
	List<Menu> list(Menu menu);
	Menu get(Long ID);
	void del(Menu menu);
}