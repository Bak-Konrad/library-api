package com.example.libraryapi.customer;

import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.customer.model.command.CreateCustomerCommand;
import com.example.libraryapi.customer.model.dto.CustomerDto;
import com.example.libraryapi.loan.LoanService;
import com.example.libraryapi.mapper.GeneralMapper;
import com.example.libraryapi.subscription.SubscriptionService;
import com.example.libraryapi.subscription.model.Subscription;
import com.example.libraryapi.subscription.model.command.CreateSubscriptionCommand;
import com.example.libraryapi.subscription.model.dto.SubscriptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/library/customers")
public class CustomerController {
    private final GeneralMapper generalMapper;
    private final CustomerService customerService;
    private final SubscriptionService subscriptionService;
    private final LoanService loanService;

    @PostMapping()
    public ResponseEntity<CustomerDto> addCustomer(@RequestBody @Valid CreateCustomerCommand customerCommand) {
        Customer customerToBeSaved = generalMapper.mapCustomerFromCommand(customerCommand);
        return new ResponseEntity<>(customerService.save(customerToBeSaved), HttpStatus.CREATED);
    }

    @PatchMapping("/{customerId}/subscribe")
    @Secured({"ROLE_CUSTOMER", "ROLE_EMPLOYEE"})
    public ResponseEntity<SubscriptionDto> registerSubscription(@PathVariable Long customerId,
                                                                @RequestBody CreateSubscriptionCommand command) {
        Subscription toBeSaved = generalMapper.mapSubscriptionFromCommand(command);
        return new ResponseEntity<>(subscriptionService.registerSubscription(customerId, toBeSaved), HttpStatus.OK);
    }

    @GetMapping
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<Page<CustomerDto>> getAllCustomers(@PageableDefault(size = 5) Pageable pageable) {
        Page<CustomerDto> customers = customerService.findAll(pageable);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping("{customerId}/books")
    @Secured({"ROLE_CUSTOMER", "ROLE_EMPLOYEE"})
    public ResponseEntity<List<BookDto>> getAllBooksForCustomer(@PathVariable Long customerId) {
        return new ResponseEntity<>(loanService.getAllBooksForCustomer(customerId), HttpStatus.OK);

    }

}
