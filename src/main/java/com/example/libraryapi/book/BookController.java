package com.example.libraryapi.book;

import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.book.model.command.CreateBookCommand;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.mapper.GeneralMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/library/books")
public class BookController {
    private final GeneralMapper mapper;
    private final BookService bookService;

    @PostMapping
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<BookDto> addBook(@RequestBody CreateBookCommand bookCommand) {
        Book bookToBeSaved = mapper.mapBookFromCommand(bookCommand);
        return new ResponseEntity<>(bookService.save(bookToBeSaved), HttpStatus.CREATED);

    }

    @PatchMapping("/{bookId}/lock")
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<BookDto> lockBook(@PathVariable Long bookId) {
        return new ResponseEntity<>(bookService.lockBook(bookId), HttpStatus.OK);
    }

    @GetMapping("/public")
    public ResponseEntity<Page<BookDto>> getAllBooks(@RequestParam(required = false) int page) {
        int PAGE_SIZE = 10;
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<BookDto> books = bookService.findAll(pageable);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }


}
