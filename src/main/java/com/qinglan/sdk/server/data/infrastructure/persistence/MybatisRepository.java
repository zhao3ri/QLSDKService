package com.qinglan.sdk.server.data.infrastructure.persistence;

import com.qinglan.sdk.server.data.query.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface MybatisRepository {
    String STATEMENT_INSERT = "insert";
    String STATEMENT_DELETE = "delete";
    String STATEMENT_DELETE_LIST = "deleteList";
    String STATEMENT_UPDATE = "update";
    String STATEMENT_FIND_PAGE = "findByPage";
    String STATEMENT_FIND_ALL = "findAll";
    String STATEMENT_FIND_TOTAL = "findTotal";
    String STATEMENT_FIND_ID = "findId";
    String STATEMENT_FIND_ONE = "findOne";

    <T> int save(T entity);

    <T> int delete(Class<T> cls, Serializable id);

    <T> int delete(Class<T> cls, String statement, Object params);

    <T> int deleteList(Class<T> cls, List<? extends Serializable> list);

    <T> int update(T entity);

    <T> int update(Class<T> cls, Object params);

    <T> int update(Class<T> cls, String statement, Object params);

    <T> T findById(Class<T> cls, Serializable id);

    <T> List<T> findAll(Class<T> cls);

    <E, T> Page<E> findPage(Class<T> cls, int pageNo, int pageSize);

    <E, T> Page<E> findPage(Class<T> cls, String statement, Map<String, Object> params, int pageNo, int pageSize);

    <E, T> List<E> findList(Class<T> cls, String statement, Object params);

    <E, T> E findOne(Class<T> cls, String statement, Object params);

    <E, T> E findOne(Class<T> cls, Object params);

    <T> int isExist(Class<T> cls, String statement, Object params);

    <T> int insert(Class<T> cls, String statement, Object params);

    <T> int insert(Class<T> cls, Object params);
}
