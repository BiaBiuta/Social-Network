package com.example.laborator7.repository.paging;


import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Paginator<E> {
    private Pageable pageable;
    private Iterable<E>elements;

    public Paginator(Pageable pageable, Iterable<E> elements) {
        this.pageable = pageable;
        this.elements = elements;
    }
    public Page<E> paginate() {
       Stream<E> result= StreamSupport.stream(elements.spliterator(),false).skip((long) pageable.getPageNumber() *pageable.getPageSize()).limit(pageable.getPageSize());
        //fac un stream din elements, sar peste primele n*pageable.getPageSize() elemente si iau urmatoarele pageable.getPageSize() elemente
        //StreamSupport.stream(elements.spliterator(),false) - creez un stream din elements
        //skip((long) pageable.getPageNumber() *pageable.getPageSize()) - sar peste primele n*pageable.getPageSize() elemente
        //limit(pageable.getPageSize()) - iau urmatoarele pageable.getPageSize() elemente
        return new PageImplementation<>(pageable, result);
    }
}
