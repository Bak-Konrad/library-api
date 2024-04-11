package com.example.libraryapi.customer;

import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.customer.model.dto.CustomerDto;
import com.example.libraryapi.mapper.GeneralMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final GeneralMapper generalMapper;

    public CustomerDto save(Customer customerToBeSaved) {
        Customer saved = customerRepository.save(customerToBeSaved);
        return generalMapper.mapCustomerToDto(saved);
    }

    public Page<CustomerDto> findAll(Pageable pageable) {
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return customerPage.map(generalMapper::mapCustomerToDto);
    }

    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Customer related to id ={0} has not been found ", customerId)));
    }
}
