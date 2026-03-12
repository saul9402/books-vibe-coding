package com.mitocode.service;

import com.mitocode.model.Book;

import java.util.List;

public interface IBookService extends ICRUD<Book, Integer> {

    List<Book> getBooksByCategory(String name);
}
