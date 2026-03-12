package com.mitocode.service.impl;

import com.mitocode.model.Book;
import com.mitocode.repo.IBookRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IAuthorService;
import com.mitocode.service.IBookService;
import com.mitocode.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BookServiceImpl extends CRUDImpl<Book, String> implements IBookService {

    private final IBookRepo repo;
    private final IAuthorService authorService;
    private final ICategoryService categoryService;

    @Override
    protected IGenericRepo<Book, String> getRepo() {
        return repo;
    }

    @Override
    public Mono<Book> save(Book book) {
        return populateRelatedEntities(book)
                .flatMap(repo::save);
    }

    @Override
    public Mono<Book> update(String id, Book book) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new com.mitocode.exception.ModelNotFoundException("ID NOT FOUND: " + id)))
                .flatMap(existing -> populateRelatedEntities(book))
                .flatMap(repo::save);
    }

    @Override
    public Flux<Book> getBooksByCategory(String name) {
        return repo.getBooksByCategory(name);
    }

    private Mono<Book> populateRelatedEntities(Book book) {
        return Mono.zip(
                authorService.findById(book.getAuthor().getIdAuthor()),
                categoryService.findById(book.getCategory().getIdCategory())
        ).map(tuple -> {
            book.setAuthor(tuple.getT1());
            book.setCategory(tuple.getT2());
            return book;
        });
    }
}
