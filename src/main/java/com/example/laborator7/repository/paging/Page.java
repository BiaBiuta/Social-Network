package com.example.laborator7.repository.paging;

import java.util.stream.Stream;




public interface Page<E> {
    Pageable getPageable();

    Pageable nextPageable();

    Stream<E> getContent();


}

