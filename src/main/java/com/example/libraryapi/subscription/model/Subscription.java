package com.example.libraryapi.subscription.model;

import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.customer.model.Customer;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String bookCategory;
    @ManyToMany(mappedBy = "subscriptions", fetch = FetchType.EAGER)
    private Set<Book> books;
    @ManyToMany(mappedBy = "subscriptions", fetch = FetchType.EAGER)
    private Set<Customer> subscribers;


}
