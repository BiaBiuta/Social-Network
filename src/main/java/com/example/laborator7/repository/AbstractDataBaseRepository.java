package com.example.laborator7.repository;


import com.example.laborator7.domain.Entity;
import com.example.laborator7.repository.paging.PagingRepository;
import com.example.laborator7.validator.Validator;

public abstract class AbstractDataBaseRepository<ID,E extends Entity<ID>> implements CrudRepository<ID,E> , PagingRepository<ID,E> {
    protected String url;
    protected String username;
    protected String password;
    protected  String tableName;
    protected Validator<E> validator;

    public AbstractDataBaseRepository(String url, String username, String password, String tableName, Validator<E> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.tableName = tableName;
        this.validator= (Validator<E>) validator;
    }
}
