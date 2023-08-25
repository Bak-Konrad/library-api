package com.example.libraryapi.customer;

import com.example.libraryapi.book.BookRepository;
import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.customer.model.dto.CustomerDto;
import com.example.libraryapi.mapper.GeneralMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final GeneralMapper generalMapper;
    private final BookRepository bookRepository;

    public CustomerDto save(Customer customerToBeSaved) {
        Customer saved = customerRepository.save(customerToBeSaved);
        return generalMapper.mapCustomerToDto(saved);
    }

    public Page<CustomerDto> findAll(Pageable pageable) {
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return customerPage.map(generalMapper::mapCustomerToDto);
    }

    public List<BookDto> getAllBooksForCustomer(Long customerId) {
        List<Book> bookList = bookRepository.findAllByCustomerId(customerId);
        return bookList.stream()
                .map(generalMapper::mapBookToDto)
                .toList();
    }

    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Customer related to id ={0} has not been found ", customerId)));
    }
}
