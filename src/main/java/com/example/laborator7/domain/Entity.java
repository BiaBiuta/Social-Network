package com.example.laborator7.domain;

import java.io.Serializable;
import java.util.Objects;

public class Entity<ID> implements Serializable {
    protected ID id;
    public ID getId(){
        return id;
    }
    public void setId(ID id){
        this.id=id;
    }
    @Override
    public int hashCode(){
        return Objects.hash(getId());
    }
    @Override
    public boolean equals(Object obj){
        if(this==obj){
            return true;
        }
        if(!(obj instanceof Entity)) return false;
        Entity<?> entity=(Entity<?>)obj;
        return getId().equals(entity.getId());
    }
    @Override
    public String toString(){
        return "Entity{" +
                "id=" + id +
                '}';
    }
}


