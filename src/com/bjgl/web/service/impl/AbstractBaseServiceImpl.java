package com.bjgl.web.service.impl;

import com.bjgl.web.bean.PageBean;
import com.bjgl.web.dao.BaseDao;
import com.bjgl.web.service.BaseService;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.io.Serializable;
import java.util.List;

/**
 * User: sunshow
 * Date: 13-7-14
 * Time: 上午9:44
 */
public abstract class AbstractBaseServiceImpl<T> implements BaseService<T> {

    protected BaseDao<T> dao;

    @Override
    public void save(T entity) {
        this.dao.save(entity);
    }

    @Override
    public void update(T entity) {
        this.dao.update(entity);
    }

    @Override
    public void delete(Serializable id) {
        this.dao.delete(id);
    }

    @Override
    public T findById(Serializable id) {
        return this.dao.findById(id);
    }

    @Override
    public PageBean getPageBean(T example, PageBean pageBean) {
        return this.dao.getPageBean(example, pageBean);
    }

    @Override
    public List<T> findByExample(T example, PageBean pageBean, Order... orders) {
        return this.dao.findByExample(example, pageBean, orders);
    }

    @Override
    public PageBean getPageBean(T example, List<Criterion> criterionList, PageBean pageBean) {
        return this.dao.getPageBean(example, criterionList, pageBean);
    }

    @Override
    public List<T> findByExample(T example, List<Criterion> criterionList, PageBean pageBean, Order... orders) {
        return this.dao.findByExample(example, criterionList, pageBean, orders);
    }

    @Override
    public List<T> findByHQL(String hql, PageBean pageBean, Object... params) {
        return this.dao.findByHQL(hql, pageBean, params);
    }

    public void setDao(BaseDao<T> dao) {
        this.dao = dao;
    }
}
