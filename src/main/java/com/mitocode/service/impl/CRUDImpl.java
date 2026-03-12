package com.mitocode.service.impl;

import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.ICRUD;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class CRUDImpl<T, ID> implements ICRUD<T, ID> {

    protected abstract IGenericRepo<T, ID> getRepo();

    @Override
    public Mono<T> save(T t) {
        return getRepo().save(t);
    }

    @Override
    public Mono<T> update(ID id, T t) {
        return getRepo().findById(id)
                .switchIfEmpty(Mono.error(new ModelNotFoundException("ID NOT FOUND: " + id)))
                .flatMap(existing -> getRepo().save(t));
    }

    @Override
    public Flux<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public Mono<T> findById(ID id) {
        return getRepo().findById(id)
                .switchIfEmpty(Mono.error(new ModelNotFoundException("ID NOT FOUND: " + id)));
    }

    @Override
    public Mono<Void> delete(ID id) {
        return getRepo().findById(id)
                .switchIfEmpty(Mono.error(new ModelNotFoundException("ID NOT FOUND: " + id)))
                .flatMap(existing -> getRepo().deleteById(id));
    }
}
