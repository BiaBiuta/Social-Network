package com.example.laborator7.repository.paging;

import com.example.laborator7.domain.Entity;
import com.example.laborator7.repository.CrudRepository;

public interface PagingRepository<ID,E extends Entity<ID>> extends CrudRepository<ID,E> {
    Page<E> findAll(Pageable pageable);//pageable e un fel de paginator
    Page<E> findAll(Pageable pageable,Long l);

}
