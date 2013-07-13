package com.bjgl.web.dao.user;

import java.util.Date;
import java.util.List;

import com.bjgl.web.bean.PageBean;
import com.bjgl.web.entity.user.User;

public interface UserDao {
	void merge(User user);
	User mergePK(User user);
	/**
	 * 多条件分页查询用户
	 * @param userName	用户名
	 * @param name	姓名
	 * @param beginDate	起始创建时间
	 * @param endDate 结束创建时间
	 * @param roleID 角色编号
	 * @param valid	是否有效
	 * @param pageBean
	 * @return
	 */
	List<User> list(String userName, String name, Date beginDate, Date endDate, Long roleID, String valid, PageBean pageBean);
	/**
	 * 封装多条件查询分页信息
	 * @param userName	用户名
	 * @param name	姓名
	 * @param beginDate	起始创建时间
	 * @param endDate 结束创建时间
	 * @param roleID 角色编号
	 * @param valid	是否有效
	 * @param pageBean
	 * @return
	 */
	PageBean getPageBean(String userName, String name, Date beginDate, Date endDate,Long roleID, String valid, PageBean pageBean);
	/**
	 * 根据用户编号查询用户
	 * @param ID 用户编号
	 */
	User get(Long ID);
	void del(User user);
	User getByUserName(String userName);
}