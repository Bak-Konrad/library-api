package com.example.libraryapi.service;

import com.example.libraryapi.book.BookRepository;
import com.example.libraryapi.book.BookService;
import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.book.model.command.BorrowBookCommand;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.customer.CustomerRepository;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.mapper.GeneralMapper;
import com.example.libraryapi.subscription.SubscriptionService;
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
    private SubscriptionService subscriptionService;

    @InjectMocks
    private BookService bookService;


    @Test
    public void testSaveBook_ResultsInBookPassedToRepository() {
        //Given
        Book book = Book.builder()
                .category("Action")
                .build();

        BookDto bookDto = BookDto.builder()
                .category("Action")
                .build();

        when(bookRepository.save(book)).thenReturn(book);
        when(generalMapper.mapBookToDto(book)).thenReturn(bookDto);

        //when
        BookDto savedBookDto = bookService.save(book);

        //then

        verify(subscriptionService, times(1)).verifySubscription(book.getCategory());
        verify(subscriptionService, times(1)).sendNotification(book.getCategory());
        verify(generalMapper, times(1)).mapBookToDto(book);
        assertEquals(bookDto, savedBookDto);
    }

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
    public void testBorrowBook_ResultsInBlockingAndSettingDates() {
        //given
        Long customerId = 1L;
        Long bookId = 1L;
        LocalDate borrowedSince = LocalDate.now();
        LocalDate borrowedTo = LocalDate.now().plusDays(14);
        BorrowBookCommand bookCommand = new BorrowBookCommand();
        bookCommand.setBorrowedSince(borrowedSince);
        bookCommand.setBorrowedTo(borrowedTo);
        BookDto bookDto = BookDto.builder().build();

        boolean blocked = false;
        Book book = new Book();
        Customer customer = Customer.builder()
                .bookList(new HashSet<>())
                .build();

        when(bookRepository.findBookByIdAndBlocked(bookId, blocked)).thenReturn(Optional.of(book));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(bookRepository.save(book)).thenReturn(book);
        when(generalMapper.mapBookToDto(book)).thenReturn(bookDto);

        //when
        BookDto borrowedBookDto = bookService.borrowBook(customerId, bookId, bookCommand);

        //then

        assertTrue(book.isBlocked());
        assertEquals(borrowedSince, book.getBorrowedSince());
        assertEquals(borrowedTo, book.getBorrowedTo());
        assertEquals(customer, book.getCustomer());
        assertTrue(customer.getBookList().contains(book));
        verify(generalMapper, times(1)).mapBookToDto(book);
        assertNotNull(borrowedBookDto);
    }

    @Test
    public void testBorrowBook_ResultsInBookEntityNotFoundException() {
        //given
        Long customerId = 1L;
        Long bookId = 1L;
        BorrowBookCommand bookCommand = new BorrowBookCommand();
        String errorMessage = "Book related to id =" + bookId + " has not been found or is blocked ";

        boolean blocked = false;

        // when
        when(bookRepository.findBookByIdAndBlocked(bookId, blocked)).thenReturn(Optional.empty());

        // then
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> bookService.borrowBook(customerId, bookId, bookCommand))
                .withMessage(errorMessage);
    }

    @Test
    public void testBorrowBook_ResultsInCustomerEntityNotFoundException() {
        Long customerId = 1L;
        Long bookId = 1L;
        BorrowBookCommand bookCommand = new BorrowBookCommand();
        String errorMessage = "Customer related to id =" + customerId + " has not been found ";

        boolean blocked = false;
        Book book = new Book();

        // Mocking
        when(bookRepository.findBookByIdAndBlocked(bookId, blocked)).thenReturn(Optional.of(book));
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Test
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> bookService.borrowBook(customerId, bookId, bookCommand))
                .withMessage(errorMessage);
    }

    @Test
    public void testReturnBook_resultsInBookEntityNotFoundException() {
        Long customerId = 1L;
        Long bookId = 1L;
        String errorMessage = "Book related to id =" + bookId + " has not been found or is available ";

        boolean blocked = true;

        // Mocking
        when(bookRepository.findBookByIdAndBlocked(bookId, blocked)).thenReturn(Optional.empty());

        // Test
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> bookService.returnBook(customerId, bookId))
                .withMessage(errorMessage);
    }

    @Test
    public void testReturnBook_resultsInCustomerEntityNotFoundException() {
        Long customerId = 1L;
        Long bookId = 1L;
        String errorMessage = "Customer related to id =" + customerId + " has not been found ";

        boolean blocked = true;
        Book book = new Book();

        // Mocking
        when(bookRepository.findBookByIdAndBlocked(bookId, blocked)).thenReturn(Optional.of(book));
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Test
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> bookService.returnBook(customerId, bookId))
                .withMessage(errorMessage);
    }

    @Test
    public void testReturnBook_resultsInBookBeingUnlocked() {
        //given
        Long customerId = 1L;
        Long bookId = 1L;

        boolean blocked = true;
        Book book = new Book();
        Set<Book> books = new HashSet<>();
        Customer customer = new Customer();
        customer.setBookList(books);
        customer.getBookList().add(book);
        BookDto bookDto = BookDto.builder().build();

        when(bookRepository.findBookByIdAndBlocked(bookId, blocked)).thenReturn(Optional.of(book));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(bookRepository.save(book)).thenReturn(book);
        when(generalMapper.mapBookToDto(book)).thenReturn(bookDto);

        // when
        BookDto returnedBookDto = bookService.returnBook(customerId, bookId);
        //then

        assertFalse(book.isBlocked());
        assertNull(book.getBorrowedSince());
        assertNull(book.getBorrowedTo());
        assertFalse(customer.getBookList().contains(book));
        verify(generalMapper, times(1)).mapBookToDto(book);
        assertNotNull(returnedBookDto);
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
    public void testIsBookRented_ResultsInTrueReturned() {
        // Given
        Long customerId = 1L;
        Long bookId = 1L;
        when(bookRepository.existsByCustomerIdAndId(customerId, bookId)).thenReturn(true);

        // When
        boolean isRented = bookService.isBookRentedByCustomer(customerId, bookId);

        // Then
        assertTrue(isRented);
    }

    @Test
    public void testIsBookRented_resultsInFalseReturned() {
        // Given
        Long customerId = 1L;
        Long bookId = 1L;
        when(bookRepository.existsByCustomerIdAndId(customerId, bookId)).thenReturn(false);

        // When
        boolean isRented = bookService.isBookRentedByCustomer(customerId, bookId);

        // Then
        assertFalse(isRented);
    }


}
