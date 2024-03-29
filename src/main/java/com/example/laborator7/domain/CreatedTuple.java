package com.example.laborator7.domain;

import java.util.Objects;

public class CreatedTuple<E1,E2>{
    private E1 e1;
    private E2 e2;
    public CreatedTuple(E1 e1 , E2 e2){
        this.e1=e1;
        this.e2=e2;
    }
    public E1 getLeft(){
        return e1;
    }
    public void setLeft(E1 e1){
        this.e1=e1;
    }
    public E2 getRight(){
        return e2;
    }
    public void setRight(E2 e2){
        this.e2=e2;
    }
    @Override
    public boolean equals(Object obj){
        return (this.e1.equals(((CreatedTuple<?, ?>) obj).e1) && this.e2.equals(((CreatedTuple<?, ?>) obj).e2)) || (this.e1.equals(((CreatedTuple<?, ?>)obj).e2)  && this.e2.equals(((CreatedTuple<?, ?>)obj).e1));
    }
    @Override
    public int hashCode(){return Objects.hash(e1,e2);}
}
