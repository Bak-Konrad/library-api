package com.example.libraryapi.customer;

import com.example.libraryapi.customer.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findWithLockById(Long id);

    Page<Customer> getAllBySubscriptionsIsNotEmpty(Pageable pageable);

}
