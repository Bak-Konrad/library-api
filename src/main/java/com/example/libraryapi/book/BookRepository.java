package com.example.libraryapi.book;

import com.example.libraryapi.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Book> findBookByIdAndBlockedNot(Long bookId, boolean blocked);

}

