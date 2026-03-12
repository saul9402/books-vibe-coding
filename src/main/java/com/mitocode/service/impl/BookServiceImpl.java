package com.mitocode.service.impl;

import com.mitocode.model.Book;
import com.mitocode.repo.IBookRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl extends CRUDImpl<Book, Integer> implements IBookService {

    private final IBookRepo repo;

    @Override
    protected IGenericRepo<Book, Integer> getRepo() {
        return repo;
    }

    @Override
    public List<Book> getBooksByCategory(String name) {
        return repo.getBooksByCategory(name);
    }
}
