package com.mitocode.repo;

import com.mitocode.model.Book;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Flux;

public interface IBookRepo extends IGenericRepo<Book, String> {

    // Busca libros cuya categoría embebida coincida con el nombre (regex, case-insensitive)
    @Query("{ 'category.name': { $regex: ?0, $options: 'i' } }")
    Flux<Book> getBooksByCategory(String name);
}
