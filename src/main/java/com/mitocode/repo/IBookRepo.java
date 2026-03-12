package com.mitocode.repo;

import com.mitocode.model.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IBookRepo extends IGenericRepo<Book, Integer> {


    //SELECT * FROM Book b INNER JOIN Category c ON b.id_category = c.id_category WHERE c.name LIKE %?%
    @Query("FROM Book b WHERE b.category.name LIKE %:name%")
    List<Book> getBooksByCategory(@Param("name") String name);
}
