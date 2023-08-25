package com.example.libraryapi.service;

import com.example.libraryapi.book.BookRepository;
import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.customer.CustomerRepository;
import com.example.libraryapi.customer.CustomerService;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.customer.model.dto.CustomerDto;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private GeneralMapper generalMapper;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CustomerService customerService;


    @Test
    public void testSaveCustomer_resultsInCustomerPassedToRepository() {
        // Given
        Customer customerToBeSaved = new Customer();
        Customer savedCustomer = new Customer();
        when(customerRepository.save(customerToBeSaved)).thenReturn(savedCustomer);
        when(generalMapper.mapCustomerToDto(savedCustomer)).thenReturn(CustomerDto.builder().build());

        // When
        CustomerDto savedCustomerDto = customerService.save(customerToBeSaved);

        // Then
        assertNotNull(savedCustomerDto);
        verify(generalMapper, times(1)).mapCustomerToDto(savedCustomer);
    }

    @Test
    public void testFindAllCustomers_resultsInCustomerPageReturned() {
        // Given
        List<Customer> customerList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            customerList.add(new Customer());
        }
        Page<Customer> customerPage = new PageImpl<>(customerList);
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(customerPage);
        when(generalMapper.mapCustomerToDto(any())).thenReturn(CustomerDto.builder().build());

        // When
        Page<CustomerDto> customerDtoPage = customerService.findAll(Pageable.unpaged());

        // Then
        assertEquals(10, customerDtoPage.getContent().size()); // Assuming pageSize is 10
        verify(generalMapper, times(10)).mapCustomerToDto(any());
    }

    @Test
    public void testGetAllBooksForCustomer_resultsInBookListReturned() {
        // Given
        Long customerId = 1L;
        List<Book> bookList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            bookList.add(new Book());
        }
        when(bookRepository.findAllByCustomerId(customerId)).thenReturn(bookList);
        when(generalMapper.mapBookToDto(any())).thenReturn(BookDto.builder().build());

        // When
        List<BookDto> bookDtoList = customerService.getAllBooksForCustomer(customerId);

        // Then
        assertEquals(5, bookDtoList.size());
        verify(generalMapper, times(5)).mapBookToDto(any());
    }

    @Test
    public void testGetCustomerById_resultsInCustomerReturned() {
        // Given
        Long customerId = 1L;
        Customer customer = new Customer();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When
        Customer result = customerService.getCustomerById(customerId);

        // Then
        assertNotNull(result);
    }

    @Test
    public void testGetCustomerById_resultsInEntityNotFoundException() {
        // Given
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> customerService.getCustomerById(customerId))
                .withMessage(MessageFormat.format("Customer related to id ={0} has not been found ", customerId));
    }



}
