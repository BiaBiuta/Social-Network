package org.example.repository;

import org.example.domain.Entity;
import org.example.domain.User;
import org.example.validator.Validator;

public abstract class AbstractDataBaseRepository<ID,E extends Entity<ID>> implements RepositoryWithOptional<ID,E>{
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
