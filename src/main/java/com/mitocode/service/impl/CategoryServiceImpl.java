package com.mitocode.service.impl;

import com.mitocode.model.Category;
import com.mitocode.repo.ICategoryRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends CRUDImpl<Category, String> implements ICategoryService {

    private final ICategoryRepo repo;

    @Override
    protected IGenericRepo<Category, String> getRepo() {
        return repo;
    }

    
}
