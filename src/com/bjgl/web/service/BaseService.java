package com.bjgl.web.service;

import com.bjgl.web.bean.PageBean;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.io.Serializable;
import java.util.List;

/**
 * 基础CRUD操作的Service接口
 * User: sunshow
 * Date: 13-7-14
 * Time: 上午9:43
 */
public interface BaseService<T> {

    public void save(T entity);

    public void update(T entity);

    public void delete(Serializable id);

    public T findById(Serializable id);

    public PageBean getPageBean(T example, PageBean pageBean);

    public List<T> findByExample(T example, PageBean pageBean, Order... orders);

    public PageBean getPageBean(T example, List<Criterion> criterionList, PageBean pageBean);

    public List<T> findByExample(T example, List<Criterion> criterionList, PageBean pageBean, Order... orders);

    public List<T> findByHQL(String hql, PageBean pageBean, Object... params);

}
