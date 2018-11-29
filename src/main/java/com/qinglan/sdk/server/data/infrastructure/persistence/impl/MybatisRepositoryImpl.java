package com.qinglan.sdk.server.data.infrastructure.persistence.impl;

import com.qinglan.sdk.server.data.query.Page;
import com.qinglan.sdk.server.data.infrastructure.persistence.MybatisRepository;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisRepositoryImpl extends SqlSessionDaoSupport implements MybatisRepository {
    public MybatisRepositoryImpl() {
    }

    public <T> int save(T entity) {
        return this.getSqlSession().insert(entity.getClass().getName() + ".insert", entity);
    }

    public <T> int delete(Class<T> entityClass, Serializable id) {
        return this.getSqlSession().delete(entityClass.getName() + ".delete", id);
    }

    public <T> int deleteList(Class<T> entityClass, List<? extends Serializable> list) {
        return this.getSqlSession().delete(entityClass.getName() + ".deleteList", list);
    }

    public <T> int delete(Class<T> entityClass, String statement, Object params) {
        return this.getSqlSession().delete(entityClass.getName() + "." + statement, params);
    }

    public <T> int update(T entity) {
        return this.getSqlSession().update(entity.getClass().getName() + ".update", entity);
    }

    public <T> int update(Class<T> entityClass, String statement, Object params) {
        return this.getSqlSession().update(entityClass.getName() + "." + statement, params);
    }

    public <T> T findById(Class<T> entityClass, Serializable id) {
        return this.getSqlSession().selectOne(entityClass.getName() + ".findById", id);
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        return this.getSqlSession().selectList(entityClass.getName() + ".findAll");
    }

    public <E, T> Page<E> findPage(Class<T> entityClass, int pageNo, int pageSize) {
        Page<E> page = new Page(pageSize, pageNo);
        Map<String, Object> params = new HashMap();
        params.put("offset", page.getFirstResult());
        params.put("pageSize", page.getPageSize());
        List<E> pageList = this.getSqlSession().selectList(entityClass.getName() + ".findByPage", params);
        long total = (Long) this.getSqlSession().selectOne(entityClass.getName() + ".findTotal");
        page.setResult(pageList);
        page.setTotalCount(total);
        return page;
    }

    public <E, T> Page<E> findPage(Class<T> entityClass, String statement, Map<String, Object> params, int pageNo, int pageSize) {
        Page<E> page = new Page(pageSize, pageNo);
        params.put("offset", page.getFirstResult());
        params.put("pageSize", page.getPageSize());
        List<E> pageList = this.getSqlSession().selectList(entityClass.getName() + "." + statement, params);
        long total = (Long) this.getSqlSession().selectOne(entityClass.getName() + "." + statement + "Total", params);
        page.setResult(pageList);
        page.setTotalCount(total);
        return page;
    }

    public <E, T> List<E> findList(Class<T> entityClass, String statement, Object params) {
        return this.getSqlSession().selectList(entityClass.getName() + "." + statement, params);
    }

    public <E, T> E findOne(Class<T> entityClass, String statement, Object params) {
        return this.getSqlSession().selectOne(entityClass.getName() + "." + statement, params);
    }

    public <T> int isExist(Class<T> entityClass, String statement, Object params) {
        return (Integer) this.getSqlSession().selectOne(entityClass.getName() + "." + statement, params);
    }

    public <T> int insert(Class<T> entityClass, String statement, Object params) {
        return this.getSqlSession().insert(entityClass.getName() + "." + statement, params);
    }
}