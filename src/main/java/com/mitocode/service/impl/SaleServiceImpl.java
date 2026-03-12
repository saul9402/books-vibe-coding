package com.mitocode.service.impl;

import com.mitocode.model.Sale;
import com.mitocode.repo.ISaleRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.ISaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SaleServiceImpl extends CRUDImpl<Sale, String> implements ISaleService {

    private final ISaleRepo repo;

    @Override
    protected IGenericRepo<Sale, String> getRepo() {
        return repo;
    }

   
}
