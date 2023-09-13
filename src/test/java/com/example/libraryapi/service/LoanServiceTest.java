package com.example.libraryapi.service;



import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.customer.CustomerRepository;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.email.EmailService;
import com.example.libraryapi.loan.LoanRepository;
import com.example.libraryapi.loan.LoanService;
import com.example.libraryapi.loan.model.Loan;
import com.example.libraryapi.mapper.GeneralMapper;
import com.example.libraryapi.subscription.SubscriptionRepository;
import com.example.libraryapi.subscription.SubscriptionService;
import com.example.libraryapi.subscription.model.Subscription;
import com.example.libraryapi.subscription.model.dto.SubscriptionDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @InjectMocks
    private LoanService loanService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private GeneralMapper generalMapper;

    @Test
    public void testGetAllBooksForCustomer() {
        // Given
        Long customerId = 1L;

        Loan loan1 = new Loan();
        loan1.setId(1L);
        Book book1 = new Book();
        book1.setId(11L);
        loan1.setBook(book1);

        Loan loan2 = new Loan();
        loan2.setId(2L);
        Book book2 = new Book();
        book2.setId(22L);
        loan2.setBook(book2);

        List<Loan> loans = List.of(loan1, loan2);

        when(loanRepository.findAllByCustomerId(customerId)).thenReturn(loans);

        BookDto bookDto1 = BookDto.builder().id(11L).build();

        BookDto bookDto2 = BookDto.builder().id(22L).build();

        when(generalMapper.mapBookToDto(book1)).thenReturn(bookDto1);
        when(generalMapper.mapBookToDto(book2)).thenReturn(bookDto2);

        // When
        List<BookDto> result = loanService.getAllBooksForCustomer(customerId);

        // Then
        assertEquals(2, result.size());
        assertEquals(bookDto1, result.get(0));
        assertEquals(bookDto2, result.get(1));
    }

    @Test
    public void testGetAllBooksForCustomerNoLoans() {
        // Given
        Long customerId = 1L;

        when(loanRepository.findAllByCustomerId(customerId)).thenReturn(Collections.emptyList());

        // When
        List<BookDto> result = loanService.getAllBooksForCustomer(customerId);

        // Then
        assertTrue(result.isEmpty());
    }
}
