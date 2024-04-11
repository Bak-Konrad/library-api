package com.example.libraryapi.service;

import com.example.libraryapi.book.BookRepository;
import com.example.libraryapi.book.BookService;
import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.book.model.command.BorrowBookCommand;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.customer.CustomerRepository;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.loan.LoanRepository;
import com.example.libraryapi.loan.model.Loan;
import com.example.libraryapi.loan.model.dto.LoanDto;
import com.example.libraryapi.mapper.GeneralMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private GeneralMapper generalMapper;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    public void testLockBook_ResultsInBookBeingLocked() {
        //given
        Long bookId = 1L;
        Book book = Book.builder().build();
        BookDto bookDto = BookDto.builder().build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(generalMapper.mapBookToDto(book)).thenReturn(bookDto);

        //when
        BookDto lockedBookDto = bookService.lockBook(bookId);
        //then

        assertTrue(book.isBlocked());
        verify(generalMapper, times(1)).mapBookToDto(book);
        assertNotNull(lockedBookDto);
    }

    @Test
    public void testLockBook_ResultsInEntityNotFoundException() {
        //given
        Long bookId = 1L;
        String errorMessage = "Book related to id 1 has not been found";

        //when//then
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> bookService.lockBook(bookId))
                .withMessage(errorMessage);
    }

    @Test
    public void testBorrowBook_resultsInLoanRegistration() {
        // Given
        Long customerId = 1L;
        Long bookId = 2L;
        BorrowBookCommand bookCommand = new BorrowBookCommand();
        bookCommand.setBorrowedSince(LocalDate.now());
        bookCommand.setBorrowedTo(LocalDate.now().plusDays(5));

        Book book = new Book();
        book.setId(bookId);
        book.setBlocked(false);

        Customer customer = new Customer();
        customer.setBookList(new HashSet<>());
        customer.setLoans(new HashSet<>());
        customer.setId(customerId);

        Loan loan = Loan.builder()
                .Id(1L)
                .borrowedSince(LocalDate.now())
                .borrowedTo(LocalDate.now().plusDays(5))
                .customer(customer)
                .book(book)
                .build();

        when(bookRepository.findBookByIdAndBlockedNot(bookId, true)).thenReturn(Optional.of(book));
        when(customerRepository.findWithLockById(customerId)).thenReturn(Optional.of(customer));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        // When
        LoanDto loanDto = bookService.borrowBook(customerId, bookId, bookCommand);

        // Then
        verify(bookRepository).findBookByIdAndBlockedNot(bookId, true);
        verify(customerRepository).findWithLockById(customerId);
        verify(loanRepository).save(any(Loan.class));
        assertEquals(book.getId(), loan.getBook().getId());
        assertEquals(customer.getId(), loan.getCustomer().getId());
        assertEquals(bookCommand.getBorrowedSince(), loan.getBorrowedSince());
        assertEquals(bookCommand.getBorrowedTo(), loan.getBorrowedTo());
    }

    @Test
    public void testBorrowBook_ResultsInBookEntityNotFoundException() {
        //given
        Long customerId = 1L;
        Long bookId = 1L;
        BorrowBookCommand bookCommand = new BorrowBookCommand();
        String errorMessage = "Book related to id =" + bookId + " has not been found or is blocked ";

        // when
        when(bookRepository.findBookByIdAndBlockedNot(bookId, true)).thenReturn(Optional.empty());

        // then
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> bookService.borrowBook(customerId, bookId, bookCommand))
                .withMessage(errorMessage);
    }

    @Test
    public void testBorrowBook_ResultsInCustomerEntityNotFoundException() {
        //given
        Long customerId = 1L;
        Long bookId = 1L;
        BorrowBookCommand bookCommand = new BorrowBookCommand();
        String errorMessage = "Customer related to id =" + customerId + " has not been found ";

        // when //then
        when(bookRepository.findBookByIdAndBlockedNot(bookId, true)).thenReturn(Optional.of(new Book()));
        when(customerRepository.findWithLockById(customerId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> bookService.borrowBook(customerId, bookId, bookCommand))
                .withMessage(errorMessage);
    }

    @Test
    public void testReturnBook_resultsInBookEntityNotFoundException() {
        Long loanId = 1L;
        Long bookId = 1L;
        String errorMessage = "Book related to id =" + bookId + " has not been found ";
        Customer customer = Customer.builder()
                .id(1L)
                .build();

        Book book = Book.builder()
                .id(1L)
                .build();
        Loan loan = Loan.builder()
                .customer(customer)
                .book(book)
                .build();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());


        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> bookService.returnBook(loanId))
                .withMessage(errorMessage);
    }

    @Test
    public void testReturnBook_resultsInLoanEntityNotFoundException() {
        //given
        Long loanId = 1L;
        String errorMessage = "Loan related to id =" + loanId + " has not been found ";

        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());
        // when // then
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> bookService.returnBook(loanId))
                .withMessage(errorMessage);
    }

    @Test
    public void testReturnBook_resultsInCustomerEntityNotFoundException() {
        Long loanId = 1L;
        Long bookId = 1L;
        String errorMessage = "Customer related to id =1 has not been found ";
        Customer customer = Customer.builder()
                .id(1L)
                .build();

        Book book = Book.builder()
                .id(1L)
                .build();

        Loan loan = Loan.builder()
                .customer(customer)
                .book(book)
                .build();

        // Mocking
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Test
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> bookService.returnBook(loanId))
                .withMessage(errorMessage);
    }

    @Test
    public void testFindAllBooks_resultsInPageOfBooksReturn() {
        // Given
        List<Book> bookList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            bookList.add(new Book());
        }
        BookDto bookDto = BookDto.builder().build();
        Page<Book> bookPage = new PageImpl<>(bookList);
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);
        when(generalMapper.mapBookToDto(any())).thenReturn(bookDto);

        // When
        Page<BookDto> bookDtoPage = bookService.findAll(Pageable.unpaged());

        // Then
        assertEquals(10, bookDtoPage.getContent().size());
        verify(generalMapper, times(10)).mapBookToDto(any());

    }

    @Test
    public void testLoanHandlerBookAvailable() {
        // Given
        Long bookId = 1L;
        BorrowBookCommand bookCommand = new BorrowBookCommand();
        bookCommand.setBorrowedSince(LocalDate.now());
        bookCommand.setBorrowedTo(LocalDate.now().plusDays(7));

        when(loanRepository.existsByBookIdAndBorrowedSinceLessThanEqualAndBorrowedToGreaterThanEqual(
                bookId,
                bookCommand.getBorrowedSince(),
                bookCommand.getBorrowedTo())).thenReturn(false);
        when(loanRepository.findLoanByBookId(bookId)).thenReturn(Optional.empty());

        // When
        Loan loan = bookService.loanHandler(bookId, bookCommand);

        // Then
        assertNotNull(loan);

    }

    @Test
    public void testLoanHandlerBookNotAvailable() {
        // Given
        Long bookId = 1L;
        BorrowBookCommand bookCommand = new BorrowBookCommand();
        bookCommand.setBorrowedSince(LocalDate.now());
        bookCommand.setBorrowedTo(LocalDate.now().plusDays(7));
        String errorMessage = "Book is not available in requested period of time";

        when(loanRepository.existsByBookIdAndBorrowedSinceLessThanEqualAndBorrowedToGreaterThanEqual(
                bookId,
                bookCommand.getBorrowedSince(),
                bookCommand.getBorrowedTo())).thenReturn(true);

        // When
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> bookService.loanHandler(bookId, bookCommand))
                .withMessage(errorMessage);
    }


}
