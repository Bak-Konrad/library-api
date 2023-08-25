package com.example.libraryapi.mapper;

import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.book.model.command.CreateBookCommand;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.customer.model.command.CreateCustomerCommand;
import com.example.libraryapi.customer.model.dto.CustomerDto;
import org.springframework.stereotype.Service;
import com.example.libraryapi.subscription.model.Subscription;
import com.example.libraryapi.subscription.model.command.CreateSubscriptionCommand;
import com.example.libraryapi.subscription.model.dto.SubscriptionDto;

import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class GeneralMapper {
    public Book mapBookFromCommand(CreateBookCommand command) {
        return Book.builder()
                .authorFirstName(command.getAuthorFirstName())
                .authorLastName(command.getAuthorLastName())
                .category(command.getCategory())
                .title(command.getTitle())
                .build();
    }

    public BookDto mapBookToDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .category(book.getCategory())
                .authorFirstName(book.getAuthorFirstName())
                .authorLastName(book.getAuthorLastName())
                .title(book.getTitle())
                .blocked(book.isBlocked())
                .borrowedSince(book.getBorrowedSince())
                .borrowedTo(book.getBorrowedTo())
                .build();
    }

    public Customer mapCustomerFromCommand(CreateCustomerCommand command) {
        return Customer.builder()
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .emailAddress(command.getEmailAddress())
                .build();
    }

    public CustomerDto mapCustomerToDto(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .emailAddress(customer.getEmailAddress())
                .build();
    }

    public Subscription mapSubscriptionFromCommand(CreateSubscriptionCommand command) {
        return Subscription.builder()
                .bookCategory(command.getBookCategory())
                .subscribers(new HashSet<>())
                .build();
    }

    public SubscriptionDto mapSubscriptionToDto(Subscription subscription) {
        return SubscriptionDto.builder()
                .bookCategory(subscription.getBookCategory())
                .customersId(subscription.getSubscribers().stream()
                        .map(Customer::getId).collect(Collectors.toSet()))
                .build();
    }

}
