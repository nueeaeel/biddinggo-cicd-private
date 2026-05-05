package com.biddingmate.biddinggo.common.inif;

public interface IMybatisCRUD<T> {
    int insert(T dto);
    int update(T dto);
    T findById(Long id);
}
