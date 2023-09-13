package com.example.libraryapi.loan;

import com.example.libraryapi.book.BookService;
import com.example.libraryapi.book.model.command.BorrowBookCommand;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.loan.model.dto.LoanDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/library/loans")
public class LoanController {
    private final BookService bookService;

    @PatchMapping("/customers/{customerId}/books/{bookId}/borrow")
    @Secured("ROLE_CUSTOMER")
    public ResponseEntity<LoanDto> borrowBook(@PathVariable Long customerId, @PathVariable Long bookId,
                                              @RequestBody BorrowBookCommand bookCommand) {
        return new ResponseEntity<>(bookService.borrowBook(customerId, bookId, bookCommand), HttpStatus.OK);
    }

    @PatchMapping("/{loanId}/return")
    @Secured({"ROLE_CUSTOMER", "ROLE_EMPLOYEE"})
    public ResponseEntity<BookDto> returnBook(@PathVariable Long loanId) {
        return new ResponseEntity<>(bookService.returnBook(loanId), HttpStatus.OK);
    }

}
