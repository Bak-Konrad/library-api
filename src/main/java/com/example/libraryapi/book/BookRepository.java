package com.example.libraryapi.book;

import com.example.libraryapi.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findBookByIdAndBlocked(Long id, boolean blocked);

    List<Book> findAllByCustomerId(Long customerId);

    boolean existsByCustomerIdAndId(Long customerId, Long id);
}
