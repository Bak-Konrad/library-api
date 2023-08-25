package com.example.libraryapi.book;

import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.book.model.command.BorrowBookCommand;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.customer.CustomerRepository;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.mapper.GeneralMapper;
import com.example.libraryapi.subscription.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final GeneralMapper generalMapper;
    private final CustomerRepository customerRepository;
    private final SubscriptionService subscriptionService;

    public BookDto save(Book book) {
        Book saved = bookRepository.save(book);
        subscriptionService.verifySubscription(book.getCategory());
        subscriptionService.sendNotification(book.getCategory());
        return generalMapper.mapBookToDto(saved);
    }

    public BookDto lockBook(Long bookId) {
        Book toBeUpdated = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Book related to id {0} has not been found", bookId)));
        toBeUpdated.setBlocked(true);
        return generalMapper.mapBookToDto(bookRepository.save(toBeUpdated));

    }

    public BookDto borrowBook(Long customerId, Long bookId, BorrowBookCommand bookCommand) {
        boolean blocked = false;
        Book toBeUpdated = bookRepository.findBookByIdAndBlocked(bookId, blocked)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Book related to id ={0} has not been found or is blocked ", bookId)));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Customer related to id ={0} has not been found ", customerId)));
        toBeUpdated.setBorrowedSince(bookCommand.getBorrowedSince());
        toBeUpdated.setBorrowedTo(bookCommand.getBorrowedTo());
        toBeUpdated.setBlocked(true);
        toBeUpdated.setCustomer(customer);
        customer.getBookList().add(toBeUpdated);
        return generalMapper.mapBookToDto(bookRepository.save(toBeUpdated));

    }

    public BookDto returnBook(Long customerId, Long bookId) {
        boolean blocked = true;
        Book toBeUpdated = bookRepository.findBookByIdAndBlocked(bookId, blocked)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Book related to id ={0} has not been found or is available ", bookId)));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Customer related to id ={0} has not been found ", customerId)));
        toBeUpdated.setBlocked(false);
        toBeUpdated.setBorrowedSince(null);
        toBeUpdated.setBorrowedTo(null);
        customer.getBookList().remove(toBeUpdated);
        return generalMapper.mapBookToDto(bookRepository.save(toBeUpdated));

    }

    public Page<BookDto> findAll(Pageable pageable) {
        Page<Book> bookPage = bookRepository.findAll(pageable);
        return bookPage.map(generalMapper::mapBookToDto);

    }

    public boolean isBookRentedByCustomer(Long customerId, Long bookId) {
        return bookRepository.existsByCustomerIdAndId(customerId, bookId);

    }

}
