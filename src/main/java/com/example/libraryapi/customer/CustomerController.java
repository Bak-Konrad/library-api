package com.example.libraryapi.customer;

import com.example.libraryapi.book.BookService;
import com.example.libraryapi.book.model.command.BorrowBookCommand;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.customer.model.command.CreateCustomerCommand;
import com.example.libraryapi.customer.model.dto.CustomerDto;
import com.example.libraryapi.mapper.GeneralMapper;
import com.example.libraryapi.subscription.SubscriptionService;
import com.example.libraryapi.subscription.model.Subscription;
import com.example.libraryapi.subscription.model.command.CreateSubscriptionCommand;
import com.example.libraryapi.subscription.model.dto.SubscriptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/library/customers")
public class CustomerController {
    private final BookService bookService;
    private final GeneralMapper generalMapper;
    private final CustomerService customerService;
    private final SubscriptionService subscriptionService;

    @PostMapping("/public")
    public ResponseEntity<CustomerDto> addCustomer(@RequestBody CreateCustomerCommand customerCommand) {
        Customer customerToBeSaved = generalMapper.mapCustomerFromCommand(customerCommand);
        return new ResponseEntity<>(customerService.save(customerToBeSaved), HttpStatus.CREATED);
    }

    @PatchMapping("{customerId}/books/{bookId}/borrow")
    @Secured("ROLE_CUSTOMER")
    public ResponseEntity<BookDto> borrowBook(@PathVariable Long customerId, @PathVariable Long bookId,
                                              @RequestBody BorrowBookCommand bookCommand) {

        return new ResponseEntity<>(bookService.borrowBook(customerId, bookId, bookCommand), HttpStatus.OK);
    }

    @PatchMapping("{customerId}/books/{bookId}/return")
    @Secured({"ROLE_CUSTOMER", "ROLE_EMPLOYEE"})
    public ResponseEntity<BookDto> returnBook(@PathVariable Long customerId, @PathVariable Long bookId) {
        return new ResponseEntity<>(bookService.returnBook(customerId, bookId), HttpStatus.OK);
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
    public ResponseEntity<Page<CustomerDto>> getAllCustomers(@RequestParam(required = false) int page) {
        int PAGE_SIZE = 10;
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<CustomerDto> customers = customerService.findAll(pageable);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping("{customerId}/books")
    @Secured({"ROLE_CUSTOMER", "ROLE_EMPLOYEE"})
    public ResponseEntity<List<BookDto>> getAllBooksForCustomer(@PathVariable Long customerId) {
        return new ResponseEntity<>(customerService.getAllBooksForCustomer(customerId), HttpStatus.OK);

    }

}
