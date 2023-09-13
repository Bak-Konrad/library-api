package com.example.libraryapi.customer.model;

import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.loan.model.Loan;
import lombok.*;
import com.example.libraryapi.subscription.model.Subscription;

import javax.persistence.*;
import java.util.Set;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String emailAddress;
    @ManyToMany
    private Set<Book> bookList;
    @ManyToMany
    private Set<Subscription> subscriptions;
    @ManyToMany()
    private Set<Loan> loans;
}
