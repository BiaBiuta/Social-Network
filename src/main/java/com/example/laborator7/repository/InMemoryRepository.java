package com.example.laborator7.repository;


import com.example.laborator7.domain.Entity;
import com.example.laborator7.validator.Validator;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository <ID,E extends Entity<ID>> implements Repository<ID,E> {
    //T is for indicate type
    //E is for indicate that you use elements
    Map<ID,E> entities;
    private Validator<E> validator;
    public InMemoryRepository(Validator<E> validator){
        this.validator=validator;
        this.entities=new HashMap<ID,E>();
    }

    @Override
    public E findOne(ID id) {
        if(id==null) throw new  IllegalArgumentException("this argument is not valid!");
        return entities.get(id);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public E save(E entityForAdd) {
        if(entityForAdd==null) throw new IllegalArgumentException("entity it must not be null");
        validator.validate(entityForAdd);
        if(entities.get(entityForAdd.getId())!=null){
            return entityForAdd;
        }
        else {
            entities.put(entityForAdd.getId(), entityForAdd);
            return entityForAdd;
        }
    }

    @Override
    public E delete(ID id) {
        E entityForDeleting=findOne(id);
        if(entityForDeleting==null){
            throw new IllegalArgumentException("the entity request for deleting does not exist!");
        }
        else{
            entities.remove(entityForDeleting.getId());
            return entityForDeleting;
        }
    }

    @Override
    public E update(E entityUpdated) {
        if (entityUpdated == null) {
            throw new IllegalArgumentException("entity must be not null!");
        }
        if (entities.get(entityUpdated.getId())==null){
            return entityUpdated;
        }
        //put() method of HashMap is used to insert a mapping into a map.
        // This means we can insert a specific key and the value it is mapping to into a particular map.
        // If an existing key is passed then the previous value gets replaced by the new value.
        entities.put(entityUpdated.getId(), entityUpdated);
        return null;

    }
}
