package com.mitocode.service.impl;

import com.mitocode.model.Book;
import com.mitocode.repo.IBookRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class BookServiceImpl extends CRUDImpl<Book, String> implements IBookService {

    private final IBookRepo repo;

    @Override
    protected IGenericRepo<Book, String> getRepo() {
        return repo;
    }

    @Override
    public Flux<Book> getBooksByCategory(String name) {
        return repo.getBooksByCategory(name);
    }
}
