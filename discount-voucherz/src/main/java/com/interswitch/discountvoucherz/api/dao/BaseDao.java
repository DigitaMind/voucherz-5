package com.interswitch.discountvoucherz.api.dao;

import com.interswitch.discountvoucherz.api.model.Page;
import org.springframework.stereotype.Component;

@Component
public interface BaseDao<T> {

    public boolean update(T model);

    public T get(String code);

    public Page<T> getAll(int pageNumber, int pageSize);

    public boolean delete(T voucher);
}
