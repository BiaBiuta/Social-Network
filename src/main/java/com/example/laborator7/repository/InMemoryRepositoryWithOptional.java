package org.example.repository;

import org.example.domain.Entity;
import org.example.domain.User;
import org.example.validator.ValidationException;
import org.example.validator.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepositoryWithOptional <ID,E extends Entity<ID>> implements RepositoryWithOptional<ID,E> {
    //T is for indicate type
    //E is for indicate that you use elements
    Map<ID,E> entities;
    private Validator<E> validator;
    public InMemoryRepositoryWithOptional(Validator<E> validator){
        this.validator=validator;
        this.entities=new HashMap<ID,E>();
    }

    @Override
    public Optional<E> findOne(ID id) {
        if(id==null) throw new  IllegalArgumentException("this argument is not valid!");
        Optional<E> e = Optional.ofNullable(entities.get(id));
        return e;
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entityForAdd) {
        if(entityForAdd==null) throw new IllegalArgumentException("entity it must not be null");
        validator.validate(entityForAdd);
        if(entities.get(entityForAdd.getId())!=null){
            return Optional.empty();
        }
        entities.put(entityForAdd.getId(), entityForAdd);
        return Optional.of(entityForAdd);
    }

    @Override
    public Optional<E> delete(ID id) {
        return  Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entityUpdated) throws ValidationException {
        validator.validate(entityUpdated);
        if (entityUpdated == null) {
            throw new IllegalArgumentException("entity must be not null!");
        }
        if (entities.get(entityUpdated.getId())==null){
            return Optional.empty();
        }
        //put() method of HashMap is used to insert a mapping into a map.
        // This means we can insert a specific key and the value it is mapping to into a particular map.
        // If an existing key is passed then the previous value gets replaced by the new value.
        entities.put(entityUpdated.getId(), entityUpdated);
        return Optional.of(entityUpdated);

    }

    @Override
    public ArrayList<String> allFriendsPerMonth(User entity, Integer local) {
        return null;
    }

}
