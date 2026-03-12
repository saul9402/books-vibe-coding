package com.mitocode.service.impl;

import com.mitocode.model.Author;
import com.mitocode.repo.IAuthorRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IAuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl extends CRUDImpl<Author, String> implements IAuthorService {

    private final IAuthorRepo repo;

    @Override
    protected IGenericRepo<Author, String> getRepo() {
        return repo;
    }
}
