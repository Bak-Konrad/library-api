package com.example.libraryapi.book;

import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.book.model.command.CreateBookCommand;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.mapper.GeneralMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/library/books")
public class BookController {
    private final GeneralMapper mapper;
    private final BookService bookService;

    @PostMapping
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<BookDto> addBook(@RequestBody @Valid CreateBookCommand bookCommand) {
        Book bookToBeSaved = mapper.mapBookFromCommand(bookCommand);
        return new ResponseEntity<>(bookService.save(bookToBeSaved), HttpStatus.CREATED);
    }

    @PatchMapping("/{bookId}/lock")
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<BookDto> lockBook(@PathVariable Long bookId) {
        return new ResponseEntity<>(bookService.lockBook(bookId), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Page<BookDto>> getAllBooks(@PageableDefault(page = 0, size = 5) Pageable pageable) {
        Page<BookDto> books = bookService.findAll(pageable);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

}
