package com.example.libraryapi.book;

import com.example.libraryapi.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findBookByIdAndBlockedNot(Long bookId, boolean blocked);

    List<Book> findAllByAddingDateTimeAfterAndCategoryIn(LocalDateTime timeStamp, List<String> categories);
}
