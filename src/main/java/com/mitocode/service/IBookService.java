package com.mitocode.service;

import com.mitocode.model.Book;
import reactor.core.publisher.Flux;

public interface IBookService extends ICRUD<Book, String> {

    Flux<Book> getBooksByCategory(String name);
}
